package top.idoknow.ghost.console.ioutil.log;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.FileIO;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.TimeUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Classifies log message.
 * Output log message above a specific level.
 * Level type: Message,Warning,Error,Breakdown
 */
public class LogMgr {
    public static final int DEBUG=0,INFO=1,WARNING=2,ERROR=3,CRASH=4;
    //Get from configuration file,any message type num less than this will be logged in mute.
    static int logOutputLevel=0;
    static{
        try{
            logOutputLevel=Integer.parseInt(ConsoleMain.cfg.getString("log-output-level"));
        }catch (Exception ignored){}
    };
    /**
     * Defines attributes of a message
     */
    public static class Log{
        int type=INFO;//default type
        String title;
        String content;
        Subject source;
        String time;

        /**
         * Initialize a log message as default type with provided subject and content.
         * @param source defines this message from which subject
         * @param title title of this msg
         * @param content content of this msg
         */
        public Log(Subject source,String title,String content){
            this.title=title;
            this.content=content;
            this.source=source;
            this.time=TimeUtil.nowFormattedMMDDHHmmSS();
        }

        /**
         * Initialize a log message as provided type
         * @param type the type of this msg
         * @param source defines this message from which subject
         * @param title title of this msg
         * @param content content of this msg
         */
        public Log(int type,Subject source,String title,String content) {
            this.title = title;
            this.content = content;
            this.source = source;
            this.type = type;
            this.time=TimeUtil.nowFormattedMMDDHHmmSS();
        }


        public String getText(){

            return time+" "+getTypeText(type)+" ["+source.getText()+"|"+title+"] "+content;
        }
        public static String getTypeText(int type){

            String typeText="";
            switch (type){
                case DEBUG:
                    typeText="debug";
                    break;
                case INFO:
                    typeText="info ";
                    break;
                case WARNING:
                    typeText="warning";
                    break;
                case ERROR:
                    typeText="error";
                    break;
                case CRASH:
                    typeText="crash";
                    break;
            }
            return typeText;
        }
    }

    //Stores logs until next writing to file.
    private static final ArrayList<Log> logBuffer=new ArrayList<>();
    public static int getBufferCurrentSize(){
        return logBuffer.size();
    }
    //Sync logBuffer on this field.
    private static final Boolean logBufferSync=false;

    private static final ArrayList<Log> dbBuffer=new ArrayList<>();
    public static int getDBBufferCurrentSize(){
        return dbBuffer.size();
    }
    private static final Boolean dbBufferSync=false;

    //Auto flush if size of logBuffer larger than <bufferSize>.

    //Auto flush every <flushTime>mills.
    private static long flushTime=30000;

    //Auto flush task timer
    private static Timer flushTaskTimer=new Timer();

    private static final TimerTask flushTask=new TimerTask() {
        @Override
        public void run() {
            flush(ConsoleMain.cfg.getString("log-file"));
        }
    };

    /**
     * Cancel already scheduled task and scheduled new task with provided flushTime.
     * @param flushTime flushing period
     */
    public static void scheduleAutoFlushTask(long flushTime){
        flushTaskTimer=new Timer();
        flushTaskTimer.schedule(flushTask,flushTime,flushTime);
        LogMgr.flushTime=flushTime;
    }


    public static Log log(int type,Subject source,String title,String content){
        synchronized (logBufferSync){
            Log log=new Log(type,source,title,content);

            if (type>=logOutputLevel) {
                System.out.println(log.getText());
            }
            logBuffer.add(log);
            //check buffer current size
            if (ConsoleMain.cfg==null){
                return log;
            }
            if (LogMySQL.isEnable()&&type!=DEBUG){
                dbBuffer.add(log);
                if (LogMySQL.isReady()){
                    flushDBBuffer();
                }
            }
            if (logBuffer.size()>=Integer.parseInt(ConsoleMain.cfg.getString("log-buffer-size"))){
                flush(ConsoleMain.cfg.getString("log-file"));
            }
            return log;
        }
    }

    /**
     * Logs a message type log.
     * @param source from which subject
     * @param title title of this log
     * @param content content of this log
     * @return
     */
    public static Log logMessage(Subject source,String title,String content){
        return log(INFO,source,title,content);
    }

    public static synchronized void flush(String file){
        if (file==null||"".equals(file)){
            return;
        }
        synchronized (logBufferSync){
            if (logBuffer.size()==0)
                return;
            StringBuilder appendStr=new StringBuilder();
            for (Log log:logBuffer){
                appendStr.append(log.getText());
                appendStr.append("\n");
            }

            try {
                FileIO.write(file, appendStr.toString(), true);
            }catch (Exception e) {
                e.printStackTrace();
            }

            logBuffer.clear();
        }
    }
    public static synchronized void flushDBBuffer(){
        synchronized (dbBufferSync){
            for (Log log:dbBuffer){
                try {
                    boolean succ=LogMySQL.getStmt().execute("INSERT INTO logs (time,type,subject,title,content) " +
                            "VALUES ('"+log.time+"','"+rawToEscape(Log.getTypeText(log.type))+"','"
                            +rawToEscape(log.source.getText())
                            +"','"+rawToEscape(log.title)
                            +"','"+rawToEscape(log.content)+"')");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            dbBuffer.clear();
        }
    }

    public static String rawToEscape(String raw){
        String str1 = raw.replaceAll("\\\\", "\\\\\\\\");
        return str1.replaceAll("'", "\\\\'");
    }
}
