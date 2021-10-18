package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;

/**
 * Receive the heartbeat response from a slave
 */
public class CommandAlivem extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        ((TerminalHandler)handler).receiveResp();

        TagLogAdapter.getTagLog().addTag("$"+handler.getSubject().getToken().split("-#")[0], ConsoleMain.ALIVE_TAG);
    }
}
