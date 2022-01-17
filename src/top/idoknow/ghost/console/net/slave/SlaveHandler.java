package top.idoknow.ghost.console.net.slave;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.AbstractHandler;
import top.idoknow.ghost.console.net.protocol.AbstractProcessor;
import top.idoknow.ghost.console.net.protocol.DataProxy;
import top.idoknow.ghost.console.net.terminal.TerminalHandler;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Initialized by SlaveAcceptor.
 * @author Rock Chin
 */
public class SlaveHandler extends AbstractHandler {

    //General index of SID
    private static long sidIndex=0;

    //information
    private long launchTime=0;
    public long getLaunchTime(){
        return launchTime;
    }
    public void updateLaunchTime(long launchTime){
        this.launchTime=launchTime;
    }

    private long authTime=0;
    public long getAuthTime(){
        return authTime;
    }

    private long installTime=0;
    public long getInstallTime(){
        return installTime;
    }
    public void updateInstallTime(long installTime){
        this.installTime=installTime;
    }

    @Override
    public void updateSubject(Subject subject){
        super.updateSubject(subject);
        this.authTime=new Date().getTime();
    }

    private String version="unknown";
    public String getVersion(){
        return version;
    }
    public void updateVersion(String version){
        this.version=version;
    }

    /**
     * Return current sidIndex,set sidIndex=sidIndex+1.
     * @return sidIndex before auto-increase.
     */
    public static long nextIndex(){
        return sidIndex++;
    }


    //Unique SID of this SlaveHandler
    private final long SID=nextIndex();
    public long getSID(){
        return SID;
    }


    private TerminalHandler peerTerminal=null;

    /**
     * Set the peer terminal which is focusing on this slave.
     * This method should ONLY be called by the terminal focusing on this slave.
     * @param terminal terminal focused on this slave
     */
    public void setPeerTerminal(TerminalHandler terminal){
        this.peerTerminal=terminal;
    }
    public void removePeerTerminal(){
        peerTerminal=null;
    }
    public TerminalHandler getPeerTerminal(){
        return peerTerminal;
    }

    /**
     * Send data to peer terminal
     * @param data data to be sent
     */
    public boolean tellPeer(String data){
        if (peerTerminal!=null){
            peerTerminal.getDataProxy().appendMsg(data);
            return true;
        }
        return false;
    }

    public SlaveHandler(Socket socket){
        this.socket=socket;
        this.subject=new Subject("UndefSlave:"+SID,Subject.UNDEFINED);
        //create a SlaveProcessor object and bind to this handler
        bindProcessor(new SlaveProcessor(this));
    }
    @Override
    public void run(){
        //initialize
        try {
            //IDK why I can solve this problem by change it to UTF-8,but when it can run in some puzzling way,DO NOT touch it again.
            InputStreamReader inputStream = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream(),"GBK");
            this.dataProxy=new DataProxy(inputStream,outputStream,this);
        }catch (Exception createStream){
//            createStream.printStackTrace();
            LogMgr.log(LogMgr.ERROR,this.subject,"Stream","Cannot create streams of socket of slave handler:"+SID);
            dispose();
        }
        //Auto kick task

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!heartbeat(3000)) {
                        LogMgr.log(LogMgr.WARNING,getSubject(),"AutoKick","Response time out.");
                        dispose();
                    }
                }catch (Exception ignored){}
            }
        }, 3000, 30000);


        //reading loop
        try {
            int data;
            while ((data=dataProxy.getInputStreamReader().read())!=-1){
                if ((char)data=='!'){//if this is the start of a command,continue reading till the end.
                    StringBuilder cmd=new StringBuilder("!");
                    while((data=dataProxy.getInputStreamReader().read())!=-1){
                        if((char)data=='!') {
                            cmd.append("!");
                            break;
                        }
                        if ((char)data=='\n') {
                            cmd.append("\n");
                            break;
                        }
                        cmd.append((char)data);

                    }
                    Debug.debug(getSubject(),"slave recv command:"+cmd);
                    //read whole command data
                    try {
                        this.getProcessor().run(cmd.toString().replaceAll(""+(char)13,"").substring(0,cmd.length()-1));
                        continue;
                    }catch (AbstractProcessor.CommandNotFoundException e){
                        //name not found,transfer to next step
                        slaveMessage(cmd.toString());
                        continue;
                    }catch (Exception runningACommand){
//                        runningACommand.printStackTrace();
                        LogMgr.log(LogMgr.ERROR,this.subject,"Process","Error occurred while processing data from slave.\n"
                                +ConsoleMain.getErrorInfo(runningACommand));
                        continue;
                    }
                }
                Debug.debug(getSubject(),"slave recv message:"+(char)data);
                //end command check
                // transfer to next step
                slaveMessage((char)data+"");
            }
        }catch (IOException readAndProcessMsg){
//            readAndProcessMsg.printStackTrace();
            LogMgr.log(LogMgr.ERROR,this.subject,"Read","Failed to read from peer.\n");
            dispose();
        }
    }

    private boolean disposed=false;
    private final Boolean disposeSync=false;

    /**
     * Try to close socket,stop thread and remove this instance from the arrayList of SlaveHandlers.
     */
    @Override
    public synchronized void dispose(){
        synchronized (disposeSync){
            if (disposed){
                return;
            }
            disposed=true;
        }
        Debug.debug(getSubject(),"slave defocusing.");
        try {
            this.socket.close();
        }catch (Exception ignored){}
        synchronized (SlaveAcceptor.slaveHandlersSync) {
            SlaveAcceptor.slaveHandlers.remove(this);
        }
        if (this.getPeerTerminal()!=null){
            this.getPeerTerminal().defocus();
        }

        LogMgr.logMessage(this.subject,"Dispose","Disposed slave handler:"+SID);
        this.stop();
    }


    private StringBuffer messageHistory=new StringBuffer();
    private static final int HISTORY_SIZE=Integer.parseInt(ConsoleMain.cfg.getString("slave-message-history-size"));

    /**
     * Append message to message history and cut previous message if length is larger than HISTORY_SIZE
     * @param s new message
     */
    public void recordHistory(String s){
        if (HISTORY_SIZE<1){
            return;
        }
        messageHistory.append(s);
        if (messageHistory.length()>HISTORY_SIZE){
            messageHistory=new StringBuffer(messageHistory.substring(messageHistory.length()-HISTORY_SIZE,messageHistory.length()));
        }
    }

    public String readHistory(){
        return messageHistory.toString();
    }

    /**
     * Process messages from slave if this is not an operation command.
     * @param data msg from slave(maybe single character)
     */
    private boolean slaveMessage(String data){
        if (this.getSubject().getIdentity()==Subject.UNDEFINED){
            this.dispose();
        }
        recordHistory(data);
        return tellPeer(data);
    }
    private boolean receiveAliveResp=false;
    public void receiveResp(){
        this.receiveAliveResp=true;
    }


    public synchronized boolean heartbeat(long wait){
        receiveAliveResp=false;

        getDataProxy().appendMsg("#alives#\n");

        try {
            Thread.sleep(wait);
        }catch (Exception e){
            e.printStackTrace();
        }
        return receiveAliveResp;
    }

}
