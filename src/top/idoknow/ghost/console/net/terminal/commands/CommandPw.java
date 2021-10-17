package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.authorize.TerminalAccountMgr;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Change the password of current account.
 * @author Rock Chin
 */
public class CommandPw extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!pw <newPassword>");
        }
        if (TerminalAccountMgr.updatePassword(handler.getSubject().getToken(),params[1])){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Successfully changed password.").flush();
            LogMgr.logMessage(handler.getSubject(),"Password","Changed password.");
        }else {
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Failed to change password.").flush();
        }
    }
}
