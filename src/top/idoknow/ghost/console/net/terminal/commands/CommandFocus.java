package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Focus on a slave,if this slave has already been focused by another terminal,
 * this operation will be cancelled and notify another terminal to defocus from this slave.
 * @author Rock Chin
 */
public class CommandFocus extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!focus <slaveSelector>");
        }

        SlaveHandler targetSlave= SlaveAcceptor.selectByString(params[1]);

        if (targetSlave==null){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Slave not found:"+params[1]);
            ((IHasWrapper)handler).getWrapper().flush();
            return;
        }
        if (targetSlave.getPeerTerminal()!=null){//already focused by another terminal
            targetSlave.getPeerTerminal().getWrapper().wrapTimeLn("\n"+handler.getSubject().getText()+" is requesting the slave you are focusing on.");
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("This slave is now focused by "+targetSlave.getPeerTerminal().getSubject().getText()+"("+targetSlave.getSID()+").");
        }else {
            ((TerminalHandler)handler).focus(targetSlave);
            //Output slave's history message
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("====== History Message ======\n"+targetSlave.readHistory());
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("====== History Message of This Slave ======");
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("You are now focusing on "+targetSlave.getSubject().getText());
        }
        ((IHasWrapper)handler).getWrapper().flush();
        SlaveAcceptor.sendSlaveList();
    }
}
