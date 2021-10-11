package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Receive a data to auth root account.
 * This is a expired command as new classic
 * protocol after refactor will use token to auth terminal.
 * This command can only auth root account.
 */
public class CommandPw extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {

        if (ConsoleMain.cfg.getString("root-token").equals(params[1])){
            handler.updateSubject(new Subject("root",Subject.TERMINAL));
            LogMgr.log(LogMgr.INFO,handler.getSubject(),"AuthRoot","Successfully auth root from ip:"+handler.getSocket().getInetAddress());
            ((TerminalHandler)handler).getWrapper().wrapTimeLn("Successfully auth root from ip:"+handler.getSocket().getInetAddress()).flush();

            ((TerminalHandler)handler).authSuccessfully();
        }else {
            ((TerminalHandler)handler).getWrapper().wrapTimeLn("Auth failed for root token:"+params[1]);
            ((TerminalHandler)handler).getWrapper().append("!passErr!").flush();
            handler.dispose();
        }
    }
}
