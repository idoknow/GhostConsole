package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.LogMgr;

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
    //Sync pending msg on this field
    private final Boolean pendingMsgSync=false;


    private final Boolean waitFromMsgToSend=false;

    private final Thread msgSender=new Thread(()->{
        try {
            while (true){//check size of pending msg arraylist in a while loop.
                //lock pending msg array list
                synchronized (pendingMsgSync){
                    if (pendingMsg.size()>0){//check size
                        //start a loop which is for sending pending msg one by one.
                        while (pendingMsg.size()>0){
                            try {
                                byte[] msg=pendingMsg.remove(0);
                                outputStream.write(msg);
                                outputStream.flush();
                            }catch (IOException failedToSend){
                                //catch the exception of sending msg
                                //output a log of failure of sending msg
                                //then throw this exception to next catcher to dispose current conn
                                LogMgr.log(LogMgr.ERROR,handler.getSubject(),"SendMsg","Failed to send msg to peer.");
                                throw failedToSend;
                            }
                        }
                    }
                }
                //already sent all pending msg and clear arraylist
                //wait for next msg
                synchronized (waitFromMsgToSend){
                    waitFromMsgToSend.wait();
                }
            }
        }catch (Exception msgSenderThreadDown){
            msgSenderThreadDown.printStackTrace();
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


    public void flushMsg(){
        synchronized (waitFromMsgToSend){
            waitFromMsgToSend.notify();
        }
    }

    /**
     * Clear array list of pending messages without sending them.
     */
    public void clearPendingMsg(){
        synchronized (pendingMsgSync) {
            pendingMsg.clear();
        }
    }

    /**
     * Caution:Please call flushMsg(); explicitly.
     * @param bytes data provided in byte[] type.
     */
    public void appendMsg(byte[] bytes){
        synchronized (pendingMsgSync) {
            pendingMsg.add(bytes);
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
