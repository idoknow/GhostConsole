package top.idoknow.ghost.console.net.terminal.commands;

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
 * Check all slave connections and dispose disconnected slave.
 * @author Rock Chin
 */
public class CommandTest extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        long time=200;
        if (params.length>1){
            try {
                time=Long.parseLong(params[1]);
            }catch (Exception e){
                throw new CommandSyntaxException("!test <timeout:long>");
            }
        }
        ArrayList<SlaveHandler> died=new ArrayList<>();
        synchronized (SlaveAcceptor.slaveHandlersSync){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Testing all slaves with timeout "+time).flush();
            for (SlaveHandler slaveHandler:SlaveAcceptor.slaveHandlers){
                ((IHasWrapper)handler).getWrapper().wrapTime("Checking "+slaveHandler.getSubject().getText()+":").flush();
                if (!slaveHandler.heartbeat(time)){
                    ((IHasWrapper)handler).getWrapper().append("false\n").flush();
                    died.add(slaveHandler);
                }else {
                    ((IHasWrapper)handler).getWrapper().append("true\n").flush();
                }
            }
        }
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Died count:"+died.size()).flush();
        for (SlaveHandler diedSlave:died){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Killing:"+diedSlave.getSubject().getText()).flush();
            diedSlave.dispose();
        }
        SlaveAcceptor.sendSlaveList();
    }
}
