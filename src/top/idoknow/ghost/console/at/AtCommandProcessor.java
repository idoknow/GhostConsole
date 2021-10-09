package top.idoknow.ghost.console.at;

import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.AbstractProcessor;
import top.idoknow.ghost.console.net.terminal.commands.*;

public class AtCommandProcessor extends AbstractProcessor {
    public AtCommandProcessor(AbstractHandler handler) {
        super(handler);

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
    protected String[] parse(String data) throws Exception {
        return data.split(" ");
    }
}
