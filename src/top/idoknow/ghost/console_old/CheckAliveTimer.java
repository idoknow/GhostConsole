package top.idoknow.ghost.console_old;

import top.idoknow.ghost.util.FileRW;
import top.idoknow.ghost.util.Out;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.TimerTask;

public class CheckAliveTimer extends TimerTask {
    public void run(){
        if(!ConsoleMain.manuallyTestConn) {
            try {
                ArrayList<HandleConn> dead = new ArrayList<>();
                for (HandleConn handleConn : ConsoleMain.socketArrayList) {
                    try {
                        handleConn.success = false;
                        CheckConnAlive cca = new CheckConnAlive(handleConn.bufferedWriter);
                        cca.start();
                        Thread.sleep(100);
                        if (!handleConn.success)
                            dead.add(handleConn);
                    } catch (Exception e) {
                        dead.add(handleConn);
                    }
                }
                //Out.say("CheckAliveTimer","检查到"+dead.size()+"个失效连接");
                for (HandleConn d : dead) {
                    try {
                        ConsoleMain.killConn(d);
                    } catch (Exception e) {
                        Out.say("CheckAliveTimer", "清除失效连接时出现问题");
                        e.printStackTrace();
                    }
                }
                StringBuffer allOnlineClientList=new StringBuffer();
                for(HandleConn client: ConsoleMain.socketArrayList){
                    //写taglog
                    //写列表到文件以便rescueServer检测未启动客户端的机器
                    if(client.avai) {
                        ConsoleMain.tagLog.addTag(client.hostName, "alive");
                        allOnlineClientList.append("r"+client.hostName+" ");
                    }
                }
                FileRW.write("rescue"+ File.separatorChar+"onlineClients.txt",allOnlineClientList.toString());
                ConsoleMain.tagLog.addTag(".Server","alive");
                ConsoleMain.tagLog.pack();
                System.gc();

            }catch (ConcurrentModificationException e){
                ;
            } catch (Exception e){
                Out.say("CheckAliveTimer","检测连接时出错");
                e.printStackTrace();
            }
        }
    }
}
