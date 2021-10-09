package top.idoknow.ghost.console.net.terminal.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;

public class CommandClose extends AbstractCommand {
    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        handler.dispose();
    }
}
