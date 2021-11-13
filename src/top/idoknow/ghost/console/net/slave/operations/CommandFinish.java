package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;

public class CommandFinish extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {

        TerminalAcceptor.sendDataToAllTerminals("!finish!");
    }
}
