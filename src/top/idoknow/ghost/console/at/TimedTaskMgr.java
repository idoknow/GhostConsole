package top.idoknow.ghost.console.at;

import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimedTaskMgr {

    public static AtTaskHandler atTaskHandler=new AtTaskHandler();
    public static AtCommandProcessor atCommandProcessor=new AtCommandProcessor(atTaskHandler);


    public static class Task extends TimerTask{
        long period;
        String cmd;
        String scheTimeStamp;
        long execCount=0,succCount=0;
        private final Timer proxyTimer;
        @Override
        public void run(){
            try {
                atCommandProcessor.run(cmd);
                succCount++;
            }catch (Exception e){
                LogMgr.log(LogMgr.ERROR,atTaskHandler.getSubject(),"AtTask","Exception while issue:"+cmd+"\n"+e.getMessage());
            }
            execCount++;
        }
        public Task(long period,String cmd){
            this.period=period;
            this.cmd=cmd;
            proxyTimer=new Timer();
            proxyTimer.schedule(this,5000,period);
            scheTimeStamp= TimeUtil.millsToMMDDHHmmSS(new Date().getTime());
        }
        public void stop(){
            proxyTimer.cancel();
        }

        public long getPeriod() {
            return period;
        }

        public String getCmd() {
            return cmd;
        }

        public String getScheTimeStamp() {
            return scheTimeStamp;
        }

        public long getExecCount() {
            return execCount;
        }

        public long getSuccCount() {
            return succCount;
        }
    }

    private final ArrayList<Task> runningTasks=new ArrayList<>();
    public void addTimedTask(long period,String cmd){
        synchronized (runningTasks) {
            runningTasks.add(new Task(period, cmd));
        }
    }
    public ArrayList<Task> listTasks(){
        return runningTasks;
    }
    public boolean stop(int index){
        try {
            synchronized (runningTasks) {
                runningTasks.get(index).stop();
                runningTasks.remove(index);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
