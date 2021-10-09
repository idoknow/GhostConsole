package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;

public class CommandStartup extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        if (!ConsoleMain.cfg.getString("slave-startup").equals("")){
            handler.getDataProxy().appendMsg(ConsoleMain.cfg.getString("slave-startup")+"\n");
            handler.getDataProxy().flushMsg();
        }
    }
}
