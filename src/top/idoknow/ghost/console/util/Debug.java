package top.idoknow.ghost.console.util;

import top.idoknow.ghost.console.core.ConsoleMain;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.subject.Subject;

public class Debug {
    public static void debug(Subject source,String s){
        if (Boolean.parseBoolean(ConsoleMain.cfg.getString("debug-mode")))
            LogMgr.log(LogMgr.DEBUG,source,"Debug",s);
    }
}
