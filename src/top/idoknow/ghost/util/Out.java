package top.idoknow.ghost.util;

import top.idoknow.ghost.console_old.AcceptMaster;
import top.idoknow.ghost.console_old.HandleMaster;
import top.idoknow.ghost.console_old.ConsoleMain;

import java.io.File;
import java.util.Date;

public class Out {
    public static boolean isPromptEnd=false;
    public static StringBuffer history=new StringBuffer();
    public static StringBuffer loggedHistory=new StringBuffer();
    public static void say(String msg){
        System.out.print(msg+"\n");
        history.append(msg).append("\n");
        loggedHistory.append(msg).append("\n");
        ConsoleMain.sendMsgToAllMasterIgnoreException(msg+"\n");
        checkHistory();
        isPromptEnd=false;
    }
    public static void say(String sub,String msg){
        Date d=new Date();
        say((isPromptEnd ? "\n" : "") +d.getDate()+"."+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds()+"["+sub+"]"+msg);
    }
    public static void putPrompt(){
        if(ConsoleMain.focusedConn!=null)
            sayThisLine("@server|client:"+ ConsoleMain.focusedConn.hostName+">");
        else
            sayThisLine("@server>");
        isPromptEnd=true;
    }

    public static void noRecordSay(String msg){
        try {
           /* if(!ServerMain.handleMaster.available || ServerMain.handleMaster.outputStreamWriter == null)
                return;*/
            for(HandleMaster master: AcceptMaster.masters) {
                master.addMsg( msg );
            }
        }catch (Exception e){
            ;
        }
    }
    public static void sayThisLine(char msg){

        System.out.print((isPromptEnd?"\n":"")+msg);
        history.append((isPromptEnd?"\n":"")+msg);
        loggedHistory.append((isPromptEnd?"\n":"")+msg);
        ConsoleMain.sendMsgToAllMasterIgnoreException((isPromptEnd ? "\n" : "") + msg );
        checkHistory();
        isPromptEnd=false;
    }
    public static void sayThisLine(String msg){
        System.out.print((isPromptEnd?"\n":"")+msg);
        history.append((isPromptEnd?"\n":"")+msg);
        loggedHistory.append((isPromptEnd?"\n":"")+msg);
        try {
           /* if(!ServerMain.handleMaster.available || ServerMain.handleMaster.outputStreamWriter == null)
                return;*/
            for(HandleMaster master: AcceptMaster.masters) {
                master.addMsg((isPromptEnd ? "\n" : "") + msg);
            }
        }catch (Exception e){
            ;
        }
        checkHistory();
        isPromptEnd=false;
    }

    static final int HISTORY_CHAR_LEN=1000;
    public static final int LOGGED_HISTORY_BUFFER_LEN=8192;
    public static void checkHistory(){
        if(history.length()>HISTORY_CHAR_LEN){
            history=new StringBuffer(history.substring(history.length()-HISTORY_CHAR_LEN,history.length()));
        }
        if(loggedHistory.length()>LOGGED_HISTORY_BUFFER_LEN){
            checkLogDir();
            FileRW.write("log/log-auto-"+TimeUtil.millsToMMDDHHmmSS(new Date().getTime()).replaceAll(":","-").replaceAll(",","_")+".log",loggedHistory.toString());
            loggedHistory=new StringBuffer();
        }
    }
    public static void flushLoggedHistoryBuffer(){
        checkLogDir();
        FileRW.write("log/log-man-"+TimeUtil.millsToMMDDHHmmSS(new Date().getTime()).replaceAll(":","-").replaceAll(",","_")+".log",loggedHistory.toString());
        loggedHistory=new StringBuffer();
    }
    public static void checkLogDir(){
        File dir=new File("log");
        if(!dir.isDirectory()){
            dir.mkdirs();
        }
    }
}
