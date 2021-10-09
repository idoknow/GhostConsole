package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Slave startup task related command.
 * In old console slave startup command can be set in console runtime.
 * But in this version of console this command can only be set in properties file.
 * @author Rock Chin
 */
public class CommandStartup extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {

        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }

        ((IHasWrapper)handler).getWrapper().wrapTimeLn("*Please set slave startup command in properties file.");
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Current command is:"+ ConsoleMain.cfg.getString("slave-startup"))
                .flush();
    }
}
