package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;

/**
 * Reply a message when receiving a heartbeat from slave
 */
public class CommandAlive extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        handler.getDataProxy().appendMsg("#alive#\n");
//        handler.getDataProxy().flushMsg();
    }
}
