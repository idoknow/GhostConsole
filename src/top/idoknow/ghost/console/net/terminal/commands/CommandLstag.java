package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.adapter.taglog.TagLog;
import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

/**
 * List all tags
 * @author Rock Chin
 */
public class CommandLstag extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (!TagLog.isEnable()){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("TagLog is unable.").flush();
            return;
        }
        int i=0;
        for(String owner: TagLogAdapter.getTagLog().getAllOwner().keySet()){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn(i+++"\t"+owner);
        }
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all tag("+TagLogAdapter.getTagLog().getAllOwner().size()+").").flush();
    }
}
