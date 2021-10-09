package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Output terminal list in a readable format.
 * @author Rock Chin
 */
public class CommandLsmst extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        synchronized (TerminalAcceptor.terminalHandlersSync){
            int i=0;
            for (TerminalHandler terminalHandler:TerminalAcceptor.terminalHandlers){
                ((IHasWrapper)handler).getWrapper().wrapTimeLn(i+++"\t"+terminalHandler.getSubject().getText()+"\t"+terminalHandler.getSocket().getInetAddress()
                        +":"+terminalHandler.getSocket().getPort());
            }
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all terminals("+TerminalAcceptor.terminalHandlers.size()+").").flush();
        }
    }
}
