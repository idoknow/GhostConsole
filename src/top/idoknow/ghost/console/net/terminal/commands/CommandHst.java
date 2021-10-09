package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Output history message to terminal.
 * This command is expired.
 * @author Rock Chin
 */
public class CommandHst extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Command !hst is now expired and unavailable.").flush();
    }
}
