package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.subject.Subject;

public class CommandName extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        handler.updateSubject(new Subject(params[1]+" #"+((SlaveHandler)handler).getSID(),Subject.SLAVE ));
        SlaveAcceptor.sendSlaveList();
        TagLogAdapter.getTagLog().addTag(handler.getSubject().getToken().split(" #")[0], ConsoleMain.LOGIN_TAG);
        TagLogAdapter.getTagLog().addTag(handler.getSubject().getToken().split(" #")[0], ConsoleMain.ALIVE_TAG);
        //save online clients list
        SlaveAcceptor.saveOnlineSlaves();
    }
}
