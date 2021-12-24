package top.idoknow.ghost.console.net.terminal;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;
import top.idoknow.ghost.console.util.TimeUtil;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TerminalAcceptor extends Thread{
    public static Subject terminalAcceptorSub=new Subject("terminalAcceptor",Subject.CONSOLE);

    //Store all handler objects of terminal connections.
    public static ArrayList<TerminalHandler> terminalHandlers=new ArrayList<>();
    //Sync terminalHandlers on this field
    public final static Boolean terminalHandlersSync=false;

    @Override
    public void run(){
        ServerSocket serverSocket=null;
        try{
            serverSocket=new ServerSocket(Integer.parseInt(ConsoleMain.cfg.getString("terminal-port")));
            LogMgr.logMessage(terminalAcceptorSub,"Create","Opened port:"
                    +Integer.parseInt(ConsoleMain.cfg.getString("terminal-port"))
                    +" for terminal connections.");
        }catch (Exception makingServer){
            makingServer.printStackTrace();
            LogMgr.log(LogMgr.CRASH,terminalAcceptorSub,"Create","Failed to create server socket.");
            ConsoleMain.stopConsole(-1);
        }

        //start accepting
        try {
            if (serverSocket==null){
                throw new Exception("Server socket is null");
            }
            while (true){
                Socket terminalSocket=serverSocket.accept();
                TerminalHandler handler=new TerminalHandler(terminalSocket);
                synchronized (terminalHandlersSync){
                    terminalHandlers.add(handler);
                }
                handler.start();
            }

        }catch (Exception listening){
            listening.printStackTrace();
            LogMgr.log(LogMgr.CRASH,terminalAcceptorSub,"Listen","Error occurred while accepting slave connections.");
            ConsoleMain.stopConsole(-1);
        }
    }


    /**
     * Send terminal list to all terminals who have attribute "listenerMaster".
     */
    public static synchronized void sendTerminalList(){
        StringBuilder result=new StringBuilder("!msts");
        synchronized (TerminalAcceptor.terminalHandlersSync){
            for(TerminalHandler handler1:TerminalAcceptor.terminalHandlers){
//                msts.append(" "+master.socket.getInetAddress()+":"
//                +master.socket.getPort()+"|"+TimeUtil.millsToMMDDHHmmSS(master.connTime)
//                +"|"+(master.attributes.contains("desktop")?1:0));
                result.append(" ")
                        .append(handler1.getSocket().getInetAddress())
                        .append(":")
                        .append(handler1.getSocket().getPort())
                        .append("|")
                        .append(TimeUtil.millsToMMDDHHmmSS(handler1.getAuthTime()))
                        .append("|")
                        .append(handler1.getAttributes().contains("desktop") ? 1 : 0);
            }
            result.append("!");
        }
        sendDataToSpecificTerminal("listenerMaster",result.toString());
    }

    /**
     * Send data to all logged in terminals
     * @param data data to send
     */
    public static void sendDataToAllTerminals(String data){
        synchronized (terminalHandlersSync){
            for (TerminalHandler handler:terminalHandlers){
                handler.getDataProxy().appendMsg(data);
//                handler.getDataProxy().flushMsg();
            }
        }
    }

    /**
     * Send data to terminals who have the specific attribute
     * @param containedAttribute specific attribute
     * @param data data to be sent
     */
    public static void sendDataToSpecificTerminal(String containedAttribute,String data){
        synchronized (terminalHandlersSync) {
            Debug.debug(terminalAcceptorSub,"Selecting terminal with:"+containedAttribute);
            for (TerminalHandler handler : terminalHandlers) {
                if(handler.getAttributes().contains(containedAttribute)){
                    handler.getDataProxy().appendMsg(data);
//                    handler.getDataProxy().flushMsg();
                }
            }
        }
    }
}
