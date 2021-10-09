package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.ioutil.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

public class CommandEcho extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        LogMgr.logMessage(handler.getSubject(),"Echo"," "+rawData.substring(6));
        TerminalAcceptor.sendDataToAllTerminal(((IHasWrapper)handler).getWrapper()
                .wrapTimeLn("["+handler.getSubject().getText()+"] "+rawData.substring(6))
                .getBuffer());
    }
}
