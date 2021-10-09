package top.idoknow.ghost.console.net.terminal.commands;

import com.rft.core.util.FileRW;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.IHasWrapper;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.terminal.CommandSyntaxException;
import top.idoknow.ghost.console.net.terminal.UnauthorizedSessionException;
import top.idoknow.ghost.console.subject.Subject;

import java.util.ArrayList;

/**
 * Manage ban list of slave.
 * @author Rock Chin
 */
public class CommandBan extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) throws Exception {
        if (handler.getSubject().getIdentity()== Subject.UNDEFINED){
            throw new UnauthorizedSessionException("unauthorized terminal session.");
        }
        if (params.length<2){
            throw new CommandSyntaxException("!ban <operation>");
        }

        if(!Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-slave-ban"))){
            ((IHasWrapper)handler).getWrapper().wrapTimeLn("Ban list is unable.").flush();
            return;
        }


        switch (params[1]){
            case "add":{
                if (params.length<3){
                    throw new CommandSyntaxException("!ban add <RegExp>");
                }

                FileRW.write("banIps.txt", SlaveAcceptor.getBannedIpsStr()+params[2]+";");
                SlaveAcceptor.loadBanList();
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("New RegExp ban regulation added:"+params[2]).flush();
                break;
            }
            case "ls":{
                int i=0;
                ArrayList<String> regulations=SlaveAcceptor.getBanList();
                for (String regulation:regulations){
                    ((IHasWrapper)handler).getWrapper().wrapTimeLn(i+++" "+regulation);
                }
                ((IHasWrapper)handler).getWrapper().wrapTimeLn("List all slave banning regulations("+regulations.size()+").").flush();
                break;
            }
            default:{
                throw new CommandSyntaxException("!ban <operation>");
            }
        }
    }
}
