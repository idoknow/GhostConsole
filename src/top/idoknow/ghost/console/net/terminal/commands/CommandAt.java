package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.at.TimedTaskMgr;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.ArrayList;

/**
 * Auto task command.
 * @author Rock Chin
 */
public class CommandAt extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!at <ls|add|del>");
        }

        switch (params[1]){
            case "ls":{
                ArrayList<TimedTaskMgr.Task> tasks= ConsoleMain.timedTaskMgr.listTasks();
                int index=0;
                for (TimedTaskMgr.Task t:tasks){
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn(index+++"\t"+t.getPeriod()+"\t"+t.getScheTimeStamp()+"\t"
                            +t.getExecCount()+"\t"+t.getSuccCount()+"\t"+t.getCmd());
                }
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("index\tperiod\tscheTS\texec\tsucc\tcmd");
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all AtTask("+tasks.size()+").").flush();
                break;
            }
            case "add":{
                try {
                    long period=Long.parseLong(params[2]);
                    String c=rawData.substring(7+params[2].length()+2);
                    ConsoleMain.timedTaskMgr.addTimedTask(period,c);
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn("Created new AtTask:period="+period+" cmd="+c);
                    LogMgr.logMessage(handler.getSubject(),"AtTask","Created new AtTask:period="+period+" cmd="+c);
                }catch (Exception e){
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn("Failed to create:"+ConsoleMain.getErrorInfo(e));
                }
                ((IHasWrapper)handler).getWrapper().flush();
                break;
            }
            case "del":{
                try {
                    int i = Integer.parseInt(params[2]);
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn("Delete AtTask index="+i+" result:"+ConsoleMain.timedTaskMgr.stop(i));
                    LogMgr.logMessage(handler.getSubject(),"AtTask","Delete AtTask index="+i+" result:"+ConsoleMain.timedTaskMgr.stop(i));
                }catch (Exception e){
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn("Failed to delete:"+ConsoleMain.getErrorInfo(e));
                }
                ((IHasWrapper)handler).getWrapper().flush();
                break;
            }
        }
    }
}
