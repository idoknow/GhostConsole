package top.idoknow.ghost.console.adapter.rft;

import com.rft.core.server.FileInfo;
import com.rft.core.server.TaskEvent;
import top.idoknow.ghost.console.ioutil.LogMgr;

public class FileReceiveEvent implements TaskEvent {
    @Override
    public void taskStarted(String s, FileInfo fileInfo) {
        LogMgr.logMessage(RFTAdapter.rftServerSubject,"RFTEvent"
                ,"Start:"+fileInfo.getName()+"("+fileInfo.getSize()+")"+">"+fileInfo.getSavePath()+"|"+s);
    }

    @Override
    public void taskFinished(String s, FileInfo fileInfo) {
        LogMgr.logMessage(RFTAdapter.rftServerSubject,"RFTEvent"
                ,"Fin:"+fileInfo.getName()+"("+fileInfo.getSize()+")"+">"+fileInfo.getSavePath()+"|"+s);
    }

    @Override
    public void taskInterrupted(String s, FileInfo fileInfo) {
        LogMgr.logMessage(RFTAdapter.rftServerSubject,"RFTEvent"
                ,"Stop:"+fileInfo.getName()+"("+fileInfo.getSize()+")"+">"+fileInfo.getSavePath()+"|"+s);
    }
}
