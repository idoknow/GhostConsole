package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Send message to all unfocused slaves.
 * @author Rock Chin
 */
public class CommandAll extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        for (SlaveHandler slaveHandler:SlaveAcceptor.slaveHandlers){
            if (slaveHandler.getPeerTerminal()==null||slaveHandler.getPeerTerminal()== handler)
                slaveHandler.getDataProxy().appendMsg(rawData.substring(5));
        }
        LogMgr.logMessage(handler.getSubject(),"All"," Issued command on all unfocused slaves:"+rawData.substring(5));

    }
}
