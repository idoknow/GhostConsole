package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;

public class CommandInfo extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {

        handler.updateSubject(new Subject(params[1]+"-#"+((SlaveHandler)handler).getSID(),Subject.SLAVE));
//        LogMgr.logMessage(handler.getSubject(),"Login","New slave logged in:"+handler.getSubject().getText());
        ((SlaveHandler)handler).updateVersion(params[2]);
        ((SlaveHandler)handler).updateLaunchTime(Long.parseLong(params[3]));
        ((SlaveHandler)handler).updateInstallTime(params.length>4?Long.parseLong(params[4]):0);
        SlaveAcceptor.sendSlaveList();
        //save online clients list
        SlaveAcceptor.saveOnlineSlaves();
        LogMgr.logMessage(handler.getSubject(),"Login","New slave connected:"+handler.getSubject().getText());


        //TODO check rename task
        TagLogAdapter.getTagLog().addTag(handler.getSubject().getToken().split("-#")[0], ConsoleMain.LOGIN_TAG);
        TagLogAdapter.getTagLog().addTag(handler.getSubject().getToken().split("-#")[0], ConsoleMain.ALIVE_TAG);
    }
}
