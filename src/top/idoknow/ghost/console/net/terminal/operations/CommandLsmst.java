package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.TimeUtil;

public class CommandLsmst extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        StringBuilder result=new StringBuilder("!msts");
        synchronized (TerminalAcceptor.terminalHandlersSync){
            for(TerminalHandler handler1:TerminalAcceptor.terminalHandlers){
                result.append(" "+handler1.getSocket().getInetAddress()+":"
                        +handler1.getSocket().getPort()+"|"+ TimeUtil.millsToMMDDHHmmSS(handler1.getAuthTime())
                        +"|"+(handler1.getAttributes().contains("desktop")?1:0));
            }
            result.append("!");
            handler.getDataProxy().appendMsg(result +"");
        }
    }
}
