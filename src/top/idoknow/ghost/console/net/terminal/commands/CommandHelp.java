package top.idoknow.ghost.console.net.terminal.commands;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.HashMap;

public class CommandHelp extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData)throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        HashMap<String,AbstractCommand> commands=handler.getProcessor().getCommandsCopy();
        int i=0;
        for (String name:commands.keySet()){
            ((IHasWrapper)handler).getWrapper().append(name+"\t");
            if (++i%4==0){
                ((IHasWrapper)handler).getWrapper().append("\n");
            }
        }
        ((IHasWrapper)handler).getWrapper().append("\n");
        ((IHasWrapper)handler).getWrapper().wrapTimeLn("Listed commands("+commands.keySet().size()+").");
        ((IHasWrapper)handler).getWrapper().flush();
    }
}
