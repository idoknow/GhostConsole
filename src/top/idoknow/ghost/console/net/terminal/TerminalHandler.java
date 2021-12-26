package top.idoknow.ghost.console.net.terminal;

import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.protocol.*;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.slave.SlaveHandler;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class TerminalHandler extends AbstractHandler implements IHasWrapper {

    private static final long ALIVE_WAIT_TIMEOUT=5000;
    private static long tidIndex=0;


    /**
     * Time stamp of successfully authed
     */
    private long authTime=0;
    public void authSuccessfully(){
        authTime=new Date().getTime();
        //things after terminal authed
        TagLogAdapter.getTagLog().addTag("$"+getSubject().getToken().split("-#")[0], ConsoleMain.LOGIN_TAG);
        TagLogAdapter.getTagLog().addTag("$"+getSubject().getToken().split("-#")[0], ConsoleMain.ALIVE_TAG);

        TerminalAcceptor.sendTerminalList();

        SlaveAcceptor.sendSlaveList();

        if (!ConsoleMain.cfg.getString("welcome-message").equals("")){
            getWrapper().wrapTimeLn("[Welcome] "+ConsoleMain.cfg.getString("welcome-message")).flush();
        }

    }
    public long getAuthTime(){
        return authTime;
    }
    /**
     * Return current tidIndex,set tidIndex=tidIndex+1.
     * @return tidIndex before auto-increase
     */
    public static long nextIndex(){
        return tidIndex++;
    }

    private final long TID=nextIndex();
    public long getTID(){
        return TID;
    }


    /**
     * Attribute list of this handler,
     * describes this handler.
     */
    private final ArrayList<String> attributes=new ArrayList<>();
    public ArrayList<String> getAttributes(){
        return attributes;
    }
    public void addAttribute(String attribute){
        Debug.debug(getSubject(),"attribute added:"+attribute);
        attributes.add(attribute);
    }

    private SlaveHandler focusedSlave=null;

    /**
     * Focus a specific slave.
     * Caution:this method will NOT check if other terminal has already focused on this slave.
     * @param slaveHandler the specific slave to be focused
     */
    public void focus(SlaveHandler slaveHandler){
        if (focusedSlave!=null){
            focusedSlave.removePeerTerminal();
        }
        this.focusedSlave=slaveHandler;
        this.focusedSlave.setPeerTerminal(this);
        LogMgr.logMessage(getSubject(),"Focus","Focusing:"+focusedSlave.getSubject().getText());
    }
    public void defocus(){
        if (focusedSlave!=null){
            focusedSlave.removePeerTerminal();
            getWrapper().wrapTimeLn("Defocus from slave:"
                    +getFocusedSlave().getSubject().getToken()+"|"
                    +getFocusedSlave().getSID()).flush();
            LogMgr.logMessage(getSubject(),"Focus","Defocus:"+focusedSlave.getSubject().getText());
        }
        this.focusedSlave=null;
    }
    public SlaveHandler getFocusedSlave(){
        return focusedSlave;
    }
    public boolean tellPeer(String data){
        if (focusedSlave!=null){
            focusedSlave.getDataProxy().appendMsg(data);
            return true;
        }
        return false;
    }


    private MessageWrapper wrapper;
    @Override
    public MessageWrapper getWrapper(){
        return wrapper;
    }


    public TerminalHandler(Socket socket){
        this.socket=socket;
        this.subject=new Subject("UndefTerminal:"+TID,Subject.UNDEFINED);
        bindProcessor(new TerminalProcessor(this));
    }



    @Override
    public void run(){
        //init
        try{
            InputStreamReader inputStream=new InputStreamReader(getSocket().getInputStream(), "GBK");
            OutputStreamWriter outputStream=new OutputStreamWriter(getSocket().getOutputStream(),"GBK");
            this.dataProxy=new DataProxy(inputStream,outputStream,this);
            this.wrapper=new MessageWrapper(this.dataProxy);
        }catch (Exception createStream){
            createStream.printStackTrace();
            LogMgr.log(LogMgr.ERROR,this.subject,"Stream","Cannot create streams of socket of terminal handler:"+TID);
            dispose();
        }
        //reading loop
        try {
            int data;
            while (true){
                //Read single line of the data
                StringBuilder line=new StringBuilder();
                while ((data=dataProxy.getInputStreamReader().read())!=-1){
                    if ((char)data=='\n'){
                        break;
                    }
                    line.append((char)data);
                }
                Debug.debug(getSubject(),"Recv:"+line);
                //process
                try {
                    this.getProcessor().run(line.toString().replaceAll(""+(char)13,""));
                }catch (AbstractProcessor.CommandNotFoundException e){
                    terminalMessage(line.toString());
                }catch (Exception runningACommand){
//                    runningACommand.printStackTrace();
                    LogMgr.log(LogMgr.ERROR,this.subject,"Process","Error occurred while processing data from terminal.\n"
                            +ConsoleMain.getErrorInfo(runningACommand));
                    getWrapper().wrapTimeLn("Failed to exec:"+line);
                    getWrapper().wrapTimeLn( runningACommand.getClass().getSimpleName()+":"+runningACommand.getMessage()).flush();
                }
            }
        }catch (IOException readAndProcessMsg){
//            readAndProcessMsg.printStackTrace();
            LogMgr.log(LogMgr.ERROR,this.subject,"Read","Terminal disconnected.");
            dispose();
        }
    }

    /**
     * Process messages which are not operation commands from terminal
     * @param data msg from terminal,a whole line.
     */
    private void terminalMessage(String data){
        if (this.getSubject().getIdentity()==Subject.UNDEFINED){
            this.getWrapper().append("!relogin!").flush();
            this.dispose();
        }
        if (getFocusedSlave()==null){
            getWrapper().wrapTimeLn("No slave focused by this session.").flush();
            return;
        }
//        Debug.debug("send to peer");
        tellPeer(data+"\n");
    }

    private boolean disposed=false;
    private final Boolean disposeSync=false;

    @Override
    public void dispose() {
        synchronized (disposeSync){
            if (disposed){
                return;
            }
            disposed=true;
        }
        synchronized (TerminalAcceptor.terminalHandlersSync){
            TerminalAcceptor.terminalHandlers.remove(this);
        }
        this.defocus();
        this.stop();
        try {
            this.socket.close();
        }catch (Exception ignored){}
        LogMgr.logMessage(this.subject,"Dispose","Disposed terminal handler:"+TID);
    }


    private boolean receiveAliveResp=false;
    public void receiveResp(){
        this.receiveAliveResp=true;
    }
    public synchronized void heartbeat(){
        receiveAliveResp=false;

        new Thread(()-> getDataProxy().appendMsg("!alivem!")).start();

        try {
            Thread.sleep(ALIVE_WAIT_TIMEOUT);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (!receiveAliveResp){
            dispose();
        }
    }
}
