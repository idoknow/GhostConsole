package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.TimeUtil;

/**
 * List all online slaves.
 * @author Rock Chin
 */
public class CommandList extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        synchronized (SlaveAcceptor.slaveHandlersSync){
            for (SlaveHandler slave:SlaveAcceptor.slaveHandlers){
                ((IHasWrapper)handler).getWrapper().wrapTimeLn(slave.getSID()+"\t"
                        +slave.getSubject().getToken()+"\t"
                        + TimeUtil.millsToMMDDHHmmSS(slave.getAuthTime())+"\t"
                        +(slave.getPeerTerminal()==null?".....":"f:"+slave.getPeerTerminal().getSubject().getToken())+"\t"
                        +slave.getVersion()+"\t"
                        +TimeUtil.millsToMMDDHHmmSS(slave.getInstallTime()));
            }
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("index\tname\tauthTime\tstate\tversion\tinstallTime");
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("List slaves done.("+SlaveAcceptor.slaveHandlers.size()+")");
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Console launched at:"+TimeUtil.millsToMMDDHHmmSS(ConsoleMain.consoleStartTime));
            ((IHasWrapper)handler).getWrapper().flush();
        }
    }
}
