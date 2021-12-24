package top.idoknow.ghost.console.net.protocol;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides some elementary methods for handlers to send/receive data.
 * @author Rock Chin
 */
public class DataProxy {
    private OutputStreamWriter outputStreamWriter;
    private InputStreamReader inputStreamReader;

    private AbstractHandler handler;

    /**
     * Store pending msgs waiting to be sent.
     */
    private final ArrayList<String> pendingMsg=new ArrayList<>();
    public int pendingSize=0;


    private final Boolean waitFromMsgToSend=false;

    private final Thread msgSender=new Thread(()->{
        try {
            while (true){//check size of pending msg arraylist in a while loop.
                //lock pending msg array list
                synchronized (pendingMsg){
                    if (pendingMsg.size()>0) {
                        outputStreamWriter.write(pendingMsg.remove(0));
                        outputStreamWriter.flush();
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
     * @param inputStreamReader created input stream obj
     * @param outputStreamWriter created output stream obj
     * @param handler slave handler who hold this dataProxy
     */
    public DataProxy(InputStreamReader inputStreamReader, OutputStreamWriter outputStreamWriter, AbstractHandler handler){
        this.inputStreamReader =inputStreamReader;
        this.outputStreamWriter = outputStreamWriter;
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
     * @param bytes data.
     */
    public void appendMsg(String bytes){
        synchronized (pendingMsg) {
            pendingMsg.add(bytes);
            pendingSize++;
            pendingMsg.notify();
//            Debug.debug("###########appending message#######:"+new String(bytes)+"$$$$$$$$$$$$size:"+pendingSize);
        }
    }

    public void dispose(){
        this.msgSender.stop();
    }

    public InputStreamReader getInputStreamReader(){
        return this.inputStreamReader;
    }

}
