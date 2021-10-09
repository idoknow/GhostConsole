package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Actively lose focus from a slave.
 * @author Rock Chin
 */
public class CommandDfocus extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (((TerminalHandler)handler).getFocusedSlave()==null){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("No slave focused.").flush();
            return;
        }
        ((TerminalHandler)handler).defocus();

        ((IHasWrapper)handler).getWrapper().flush();

        SlaveAcceptor.sendSlaveList();
    }
}
