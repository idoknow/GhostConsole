package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveHandler;

/**
 * Receive a response of heartbeat msg sent to slave
 * @author Rock Chin
 */
public class CommandAlives extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        ((SlaveHandler)handler).receiveResp();
        TagLogAdapter.getTagLog().addTag(handler.getSubject().getToken().split(" #")[0], ConsoleMain.ALIVE_TAG);
    }
}
