package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLog;
import top.idoknow.ghost.console.ioutil.FileIO;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * Request taglog data
 */
public class CommandTaglog extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (!TagLog.isEnable()){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("TagLog is disabled.").flush();
            return;
        }

        String  write="!taglog " + FileIO.read("tagLog.txt") + "!";
        ((IHasWrapper)handler).getWrapper().append(write).flush();
    }
}
