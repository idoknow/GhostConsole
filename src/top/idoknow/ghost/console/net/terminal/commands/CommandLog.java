package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Manage log or flush manually.
 * @author Rock Chin
 */
public class CommandLog extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!log <flush|len>");
        }

        if (params[1].equalsIgnoreCase("len")){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Log buffer(current/max/autoFlushTime):"+ LogMgr.getBufferCurrentSize()
                    +"/"+ ConsoleMain.cfg.getString("log-buffer-size")+"/"+ConsoleMain.cfg.getString("log-flush-time")).flush();
        }else if (params[1].equalsIgnoreCase("flush")){
            LogMgr.flush(ConsoleMain.cfg.getString("log-file"));
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Flush log to:"+ConsoleMain.cfg.getString("log-file")).flush();
        }
    }
}
