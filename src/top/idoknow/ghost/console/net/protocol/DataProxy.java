package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.util.Debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Provides some elementary methods for handlers to send/receive data.
 * @author Rock Chin
 */
public class DataProxy {
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private AbstractHandler handler;

    /**
     * Store pending msgs waiting to be sent.
     */
    private final ArrayList<byte[]> pendingMsg=new ArrayList<>();
    public int pendingSize=0;


    private final Boolean waitFromMsgToSend=false;

    private final Thread msgSender=new Thread(()->{
        try {
            while (true){//check size of pending msg arraylist in a while loop.
                //lock pending msg array list
                synchronized (pendingMsg){
                    if (pendingMsg.size()>0) {
                        outputStream.write(pendingMsg.remove(0));
                        outputStream.flush();
                    }else {
                        pendingMsg.wait();
                    }
                }
            }
        }catch (Exception msgSenderThreadDown){
//            msgSenderThreadDown.printStackTrace();
            LogMgr.log(LogMgr.ERROR,handler.getSubject(),"MsgSender","Msg sender is now down:\n"
                    + ConsoleMain.getErrorInfo(msgSenderThreadDown));
            handler.dispose();
        }
    });


    /**
     * Initialize DataProxy instance with provided stream objects.
     * @param inputStream created input stream obj
     * @param outputStream created output stream obj
     * @param handler slave handler who hold this dataProxy
     */
    public DataProxy(DataInputStream inputStream, DataOutputStream outputStream, AbstractHandler handler){
        this.inputStream=inputStream;
        this.outputStream=outputStream;
        this.handler=handler;
        this.msgSender.start();
    }


    /**
     * Clear array list of pending messages without sending them.
     */
    public void clearPendingMsg(){
        synchronized (pendingMsg) {
            pendingMsg.clear();
        }
    }

    /**
     * @param bytes data provided in byte[] type.
     */
    public void appendMsg(byte[] bytes){
        synchronized (pendingMsg) {
            pendingMsg.add(bytes);
            pendingSize++;
            pendingMsg.notify();
//            Debug.debug("###########appending message#######:"+new String(bytes)+"$$$$$$$$$$$$size:"+pendingSize);
        }
    }

    /**
     * Caution:Please call flushMsg(); explicitly.
     * @param msg msg provided in String type
     */
    public void appendMsg(String msg){
        appendMsg(msg.getBytes(StandardCharsets.UTF_8));
    }

    public void dispose(){
        this.msgSender.stop();
    }

    public DataInputStream getInputStream(){
        return this.inputStream;
    }

}
