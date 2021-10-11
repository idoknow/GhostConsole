package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.authorize.AccountOperationException;
import top.idoknow.ghost.console.authorize.TerminalAccountMgr;
import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Login to an account with token
 * @author Rock Chin
 */
public class CommandLogin extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (params.length<3){
            throw new CommandSyntaxException("failed to auth.");
        }
        try {
            if (TerminalAccountMgr.authorize(params[1], params[2])) {
                handler.updateSubject(new Subject(params[1], Subject.TERMINAL));
                ((TerminalHandler) handler).authSuccessfully();
                LogMgr.log(LogMgr.INFO, handler.getSubject(), "Auth", "Successfully auth " + params[1] + " from ip:" + handler.getSocket().getInetAddress());
                ((TerminalHandler) handler).getWrapper().wrapTimeLn("Successfully auth " + params[1] + " from ip:" + handler.getSocket().getInetAddress()).flush();
                TerminalAcceptor.sendTerminalList();
            }
        }catch (AccountOperationException e){
            LogMgr.logMessage(handler.getSubject(),"Auth","Auth "+params[1]+" failed from ip:"+handler.getSocket().getInetAddress());

            ((TerminalHandler)handler).getWrapper().wrapTimeLn("Auth failed for "+params[1]+" token:"+params[1]).flush();
            ((TerminalHandler)handler).getWrapper().append("!passErr!").flush();
            handler.dispose();
            throw e;
        }
    }
}
