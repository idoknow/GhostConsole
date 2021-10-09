package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

public class CommandAttri extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        ((TerminalHandler)handler).getAttributes().add(params[1]);
        TerminalAcceptor.sendTerminalList();
    }
}
