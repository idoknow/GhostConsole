package top.idoknow.ghost.console;

import top.idoknow.ghost.util.Out;

import java.util.ArrayList;
import java.util.Date;

public class CheckAliveMaster extends Thread{
	int time;
	CheckAliveMaster(int time){
		this.time=time;
	}
	public void run(){
		ConsoleMain.manuallyTestConn=true;
		Out.say("CheckAliveMaster","正在测试所有连接..");

		ArrayList<HandleConn> dead = new ArrayList<>();
		for (HandleConn handleConn : ConsoleMain.socketArrayList) {
			Out.sayThisLine(handleConn.hostName+":");
			try {
				handleConn.success = false;
				long stTime=new Date().getTime();
				CheckConnAlive cca = new CheckConnAlive(handleConn.bufferedWriter);
				cca.start();
				sleep(time);
				if (!handleConn.success) {
					Out.sayThisLine("Failed\n");
					dead.add(handleConn);
				}else{
					Out.sayThisLine("Succeeded "+(handleConn.reveiveAliveMsgTime-stTime)+"ms\n");
				}
			} catch (Exception e) {
				Out.sayThisLine("Failed\n");
				dead.add(handleConn);
			}
		}

		Out.say("CheckAliveMaster","检查到"+dead.size()+"个失效连接");
		for (HandleConn d : dead) {
			ConsoleMain.killConn(d);
		}
		System.gc();
		Out.say("CheckAliveMaster","已自动清除");
		ConsoleMain.cmdProcessFinish();
		ConsoleMain.manuallyTestConn=false;
	}
}
