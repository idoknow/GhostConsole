package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * This command is expired.
 * Set welcome-message in properties file to replace note.
 * @author Rock Chin
 */
public class CommandNote extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Command !note is now expired.Please set welcome-message in properties file.");
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Current welcome-message is:"+ ConsoleMain.cfg.getString("welcome-message"));
        ((IHasWrapper)handler).getWrapper().flush();
    }
}
