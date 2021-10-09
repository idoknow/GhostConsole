package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;

public class CommandAlivems extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        handler.getDataProxy().appendMsg("!alivems!\n");
        handler.getDataProxy().flushMsg();

    }
}
