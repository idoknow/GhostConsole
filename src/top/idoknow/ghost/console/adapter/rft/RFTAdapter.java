package top.idoknow.ghost.console.adapter.rft;

import com.rft.core.server.BufferedFileReceiver;
import com.rft.core.server.FileServer;
import com.rft.core.server.ParallelFileServer;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.subject.Subject;

/**
 * This is a adapter between RFT lib and Console proj.
 * @author Rock Chin
 */
public class RFTAdapter {
    public static BufferedFileReceiver rftReceiver;
    public static FileServer rftServer;

    public static final Subject rftServerSubject=new Subject("RFTServer",Subject.CONSOLE);

    private RFTAdapter(){}
    public static synchronized void init()throws Exception{
        rftReceiver=new BufferedFileReceiver();
        rftReceiver.setRootPath("");
        rftServer=new ParallelFileServer(Integer.parseInt(ConsoleMain.cfg.getString("rft-server-port")),rftReceiver);
        rftReceiver.setFileServer(rftServer);

        rftServer.start();
    }
}
