package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.adapter.taglog.TagLog;
import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.ArrayList;

/**
 * Remove a tag in taglog.
 * @author Rock Chin
 */
public class CommandRmtag extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (!TagLog.isEnable()){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("TagLog is disabled.").flush();
            return;
        }

        if (params.length<2){
            throw new CommandSyntaxException("!rmtag <tagOwnerStartWith>");
        }

        ArrayList<String> delete=new ArrayList<>();

        for(String owner: TagLogAdapter.getTagLog().getAllOwner().keySet()){
            if(owner.startsWith(params[1])){
                delete.add(owner);
            }
        }
        if(delete.size()!=0){
            for(String dk:delete){
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("Delete tag owner:"+dk);
                LogMgr.logMessage(handler.getSubject(),"TagLog","Delete tag owner:"+dk);
                TagLogAdapter.getTagLog().getAllOwner().remove(dk);
            }
            TagLogAdapter.getTagLog().pack();
        }else {
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("No tag matched.");
        }
        ((IHasWrapper)handler).getWrapper().flush();
    }
}
