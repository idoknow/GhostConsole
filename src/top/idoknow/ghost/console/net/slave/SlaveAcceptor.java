package top.idoknow.ghost.console.net.slave;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.FileIO;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.Debug;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Extends from thread,accepts slave connections in a loop.
 * Initialize SlaveHandler objects after receiving connections.
 */
public class SlaveAcceptor extends Thread{
    public static Subject slaveAcceptorSub=new Subject("slaveAcceptor",Subject.CONSOLE);

    //Store all handler objects of slave connections.
    public static ArrayList<SlaveHandler> slaveHandlers=new ArrayList<>();
    //Sync slaveHandlers on this field
    public final static Boolean slaveHandlersSync=false;

    @Override
    public void run(){
        ServerSocket serverSocket = null;
        try {
            serverSocket=new ServerSocket(Integer.parseInt(ConsoleMain.cfg.getString("slave-port")));
            LogMgr.logMessage(slaveAcceptorSub,"Create","Opened port:"
                    +Integer.parseInt(ConsoleMain.cfg.getString("slave-port"))
                    +" for slave connections.");
        }catch (Exception makingServer){
            makingServer.printStackTrace();
            LogMgr.log(LogMgr.CRASH,slaveAcceptorSub,"Create","Failed to create server socket.");
            ConsoleMain.stopConsole(-1);
        }


        //start accepting
        try {
            if (serverSocket==null){
                throw new Exception("Server socket is null.");
            }
            //Accepts slave connections in a loop.
            while (true){
                Socket slaveSocket=serverSocket.accept();
                if (isBanned(String.valueOf(slaveSocket.getInetAddress()))){
                    try {
                        slaveSocket.close();
                    }catch (Exception ignored){}
                    continue;
                }
                SlaveHandler handler=new SlaveHandler(slaveSocket);
                synchronized (slaveHandlersSync) {
                    slaveHandlers.add(handler);
                }
                handler.start();
            }
        }catch (Exception listening){
            listening.printStackTrace();
            LogMgr.log(LogMgr.CRASH,slaveAcceptorSub,"Listen","Error occurred while accepting slave connections.");
            ConsoleMain.stopConsole(-1);
        }
    }


    /**
     * Send the list of slaves to all terminal.
     * Slave will be labelled "focused" if it focused by any terminal.
     */
    public static void sendSlaveList(){
        Debug.debug(slaveAcceptorSub,"sending slave list;");
        StringBuilder msg=new StringBuilder("!clients");
        for(SlaveHandler handler:slaveHandlers){
            if (handler.getSubject().getIdentity()!=Subject.UNDEFINED) {
                msg.append(" ")
                        .append(handler.getSID())
                        .append(" ")
                        .append(handler.getSubject().getToken())
                        .append(" ")
                        .append(handler.getAuthTime())
                        .append(" ")
                        .append(handler.getPeerTerminal() != null)
                        .append(" ")
                        .append(handler.getVersion())
                        .append(" ")
                        .append(handler.getLaunchTime());
            }
        }
        msg.append("!");
        TerminalAcceptor.sendDataToAllTerminals(msg.toString());
    }

    /**
     * Select a slave handler by a String
     * @param select to index a handler,e.g. 1."test"(select by slave name) 2."&32"(select by slave SID)
     * @return the selected handler
     */
    public static SlaveHandler selectByString(String select){
        if (select.startsWith("&")){//index by SID
            int index=Integer.parseInt(select.substring(1));
            synchronized (SlaveAcceptor.slaveHandlersSync){
                for (SlaveHandler slaveHandler:SlaveAcceptor.slaveHandlers){
                    if (slaveHandler.getSID()==index){
                        return slaveHandler;
                    }
                }
            }
        }else {//index by name
            synchronized (SlaveAcceptor.slaveHandlersSync){
                for (SlaveHandler slaveHandler:SlaveAcceptor.slaveHandlers){
                    if (slaveHandler.getSubject().getToken().startsWith(select)){
                        return slaveHandler;
                    }
                }
            }
        }
        return null;
    }



    private final static ArrayList<String> banList=new ArrayList<>();

    public static ArrayList<String> getBanList(){
        return banList;
    }

    public static void loadBanList()throws Exception{
        banList.clear();

        if (!Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-slave-ban"))){
            return;
        }


        if(new File("banIps.txt").exists()){
            String[] listStr = FileIO.read("banIps.txt").split(";");
            banList.addAll(Arrays.asList(listStr));
        }
    }
    public static String getBannedIpsStr(){
        StringBuilder str=new StringBuilder();
        for(String ip:banList){
            str.append(ip).append(";");
        }
        return str.toString();
    }
    public static boolean isBanned(String ip){
        if (!Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-slave-ban"))){
            return false;
        }
        for(String ips:banList){
            if(Pattern.matches(ips,ip)){
                return true;
            }
        }
        return false;
    }


    public static void saveOnlineSlaves(){

        if (ConsoleMain.cfg.getString("save-online-slave-list-to").equals("")){
            return;
        }
        StringBuilder allOnlineClientList=new StringBuilder();
        synchronized (slaveHandlersSync) {
            for (SlaveHandler slaveHandler : slaveHandlers) {
                //写列表到文件以便rescueServer检测未启动客户端的机器
                if (slaveHandler.getSubject().getIdentity()==Subject.SLAVE) {
                    allOnlineClientList.append(slaveHandler.getSubject().getToken().split("-#")[0]).append(" ");
                }
            }
        }
        try {
            FileIO.write(ConsoleMain.cfg.getString("save-online-slave-list-to"),allOnlineClientList.toString());
        }catch (Exception e){
            LogMgr.log(LogMgr.ERROR,slaveAcceptorSub,"SaveList","Failed to save slave list:"+ConsoleMain.getErrorInfo(e));
        }
    }
}
