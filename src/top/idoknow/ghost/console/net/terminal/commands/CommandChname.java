package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Change the name of a slave
 */
public class CommandChname extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<3){
            throw new CommandSyntaxException("!chname <slaveSelector> <newName>");
        }
        SlaveHandler targetSlave= SlaveAcceptor.selectByString(params[1]);
        if (targetSlave==null){
            //TODO rename plan
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Slave not found:"+params[1]);
            ((IHasWrapper)handler).getWrapper().flush();
            return;
        }
        targetSlave.getDataProxy().appendMsg("!!name "+params[2]+"\n");
        targetSlave.getDataProxy().appendMsg("!!cfg write\n");
        targetSlave.updateSubject(new Subject(params[2]+" #"+targetSlave.getSID(),Subject.SLAVE));

        TagLogAdapter.getTagLog().addTag(targetSlave.getSubject().getToken().split(" #")[0], ConsoleMain.LOGIN_TAG);
        TagLogAdapter.getTagLog().addTag(targetSlave.getSubject().getToken().split(" #")[0], ConsoleMain.ALIVE_TAG);

        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Slave name changed.").flush();
        SlaveAcceptor.sendSlaveList();
    }
}
