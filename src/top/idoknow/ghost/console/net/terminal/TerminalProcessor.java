package top.idoknow.ghost.console.net.terminal;

import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.AbstractProcessor;
import top.idoknow.ghost.console.net.terminal.commands.*;
import top.idoknow.ghost.console.net.terminal.operations.*;
import top.idoknow.ghost.console.net.terminal.operations.CommandLsmst;
import top.idoknow.ghost.console.net.terminal.operations.CommandPw;

public class TerminalProcessor extends AbstractProcessor {

    public TerminalProcessor(AbstractHandler handler) {
        super(handler);

        //Terminal control commands
        register("#alivem#",new CommandAlivem());
        register("#alivems#",new CommandAlivems());
        register("#attri",new CommandAttri());
        register("#close#",new CommandClose());
        register("#lsmst#",new CommandLsmst());
        register("#pw",new CommandPw());
        register("#taglog#",new CommandTaglog());
        register("#login",new CommandLogin());

        //Console commands
        register("!all",new CommandAll());
        register("!at",new CommandAt());
        register("!ban",new CommandBan());
        register("!chname",new CommandChname());
        register("!desc",new CommandDesc());
        register("!dfocus",new CommandDfocus());
        register("!echo",new CommandEcho());
        register("!exit",new CommandExit());
        register("!focus",new CommandFocus());
        register("!help",new CommandHelp());
        register("!hst",new CommandHst());
        register("!jre",new CommandJre());
        register("!list",new CommandList());
        register("!log",new CommandLog());
        register("!lsmst",new top.idoknow.ghost.console.net.terminal.commands.CommandLsmst());
        register("!lstag",new CommandLstag());
        register("!note",new CommandNote());
        register("!pw",new top.idoknow.ghost.console.net.terminal.commands.CommandPw());
        register("!rft",new CommandRft());
        register("!rmtag",new CommandRmtag());
        register("!space",new CommandSpace());
        register("!startup",new CommandStartup());
        register("!stop",new CommandStop());
        register("!close",new CommandStop());
        register("!svfile",new CommandSvfile());
        register("!test",new CommandTest());

    }

    @Override
    public String[] parse(String data) throws Exception {
        return data.split(" ");
    }
}
