package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;

public class CommandVersion extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        ((SlaveHandler)handler).updateVersion(params[1]);
        SlaveAcceptor.sendSlaveList();
    }
}
