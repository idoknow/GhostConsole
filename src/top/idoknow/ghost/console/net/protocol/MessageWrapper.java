package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;
import top.idoknow.ghost.console.util.TimeUtil;

/**
 * Provides methods to wrap message sent to terminal.
 * Make messages easy to read.
 * @author Rock Chin
 */
public class MessageWrapper {
    private DataProxy dataProxy = null;
    //TODO allow caller to create a new wrapper to wrap a message section which should not be divide.
    public MessageWrapper(DataProxy dataProxy){
        this.dataProxy=dataProxy;
    }

    private Subject subject;

    /**
     * For AtTaskHost to create a wrapper that can flush message to log
     * @param subject subject of AtTaskHost
     */
    public MessageWrapper(Subject subject){
        this.subject=subject;
    }

    private StringBuilder buffer=new StringBuilder();
    public String getBuffer(){
        return buffer.toString();
    }

    public synchronized MessageWrapper append(String msg){
        buffer.append(msg);
        return this;
    }
    public MessageWrapper appendLn(String msg){
        return append(msg+"\n");
    }

    public MessageWrapper wrapTime(String msg){
        append(TimeUtil.nowFormattedMMDDHHmmSS()).append(" | ").append(msg);
        return this;
    }
    public MessageWrapper wrapTimeLn(String msg){
        return wrapTime(msg+"\n");
    }

    public synchronized void flush(){
//        Debug.debug("Flushing messages......");
        if (dataProxy!=null) {
            dataProxy.appendMsg(buffer.toString());
//            Debug.debug("#### pendingsize:"+dataProxy.pendingSize);
//            dataProxy.flushMsg();
//            Debug.debug("#### 2222222222pendingSize:"+dataProxy.pendingSize);
        }else if (subject!=null){
            LogMgr.logMessage(subject,"Wrapper","Log by wrapper with subject:\n"+buffer.toString().replaceAll("\n","\n        "));
        }
        buffer=new StringBuilder();
    }

}
