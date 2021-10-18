package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.ArrayList;

/**
 * Kick clave.
 * @author Rock Chin
 */
public class CommandExit extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!exit <slaveStartWith|-m|-lv|-a>");
        }

        ArrayList<SlaveHandler> kill = new ArrayList<>();

        synchronized (SlaveAcceptor.slaveHandlersSync) {


            //select match handlers.
            if (params[1].equalsIgnoreCase("-m")) {
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Kill redundant slaves.").flush();
                ArrayList<String> alreadyScanHostNames = new ArrayList<>();
                for (SlaveHandler slaveHandler : SlaveAcceptor.slaveHandlers) {
                    String slaveName = slaveHandler.getSubject().getToken().split("-#")[0];
                    if (alreadyScanHostNames.contains(slaveName)) {
                        kill.add(slaveHandler);
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Kill:"+slaveHandler.getSubject().getToken());
                    }else {
                        alreadyScanHostNames.add(slaveName);
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Keep:"+slaveHandler.getSubject().getToken());
                    }
                }
                ((IHasWrapper)handler).getWrapper().flush();
            }else if (params[1].equalsIgnoreCase("-a")){
                kill.addAll(SlaveAcceptor.slaveHandlers);
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Kill all.").flush();
            }else if ((params[1].equalsIgnoreCase("-lv"))){//low version
                i:for (int i=0;i<SlaveAcceptor.slaveHandlers.size();i++){
                    SlaveHandler conn=SlaveAcceptor.slaveHandlers.get(i);
                    try {
                        long ver0=Long.parseLong(conn.getVersion().substring(1));
                        for (int j=0;j<SlaveAcceptor.slaveHandlers.size();j++) {
                            try {
                                SlaveHandler conn1=SlaveAcceptor.slaveHandlers.get(j);
                                if (conn==conn1||!conn.getSubject().getToken().split("-#")[0].equals(conn1.getSubject().getToken().split("-#")[0]))
                                    continue;
                                long ver1=Long.parseLong(conn1.getVersion().substring(1));
                                if (ver0<ver1){
                                    kill.add(conn);
                                    ((IHasWrapper)handler).getWrapper().wrapTimeLn("Kill:"+conn.getSubject().getToken());
                                    continue i;
                                }
                            }catch (Exception ignored){}
                        }
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Keep:"+conn.getSubject().getToken());
                    }catch (Exception e){
                        ((IHasWrapper)handler).getWrapper().wrapTimeLn(ConsoleMain.getErrorInfo(e));
                    }
                }
                ((IHasWrapper)handler).getWrapper().flush();
            }
        }
        //Summary and execute kick task
        if (kill.size()==0){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("No slave to kick.").flush();
            return;
        }
        for (SlaveHandler slaveHandler:kill){
            slaveHandler.getDataProxy().appendMsg("!!exit "+slaveHandler.getSubject().getToken().split("-#")[0]);
//            slaveHandler.getDataProxy().flushMsg();
            slaveHandler.dispose();
        }
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Executing kill slaves count:"+kill.size()).flush();
    }
}
