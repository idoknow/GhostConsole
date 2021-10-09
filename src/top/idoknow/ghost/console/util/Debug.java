package top.idoknow.ghost.console.util;

import top.idoknow.ghost.console.core.ConsoleMain;

public class Debug {
    public static void debug(String s){
        if (Boolean.parseBoolean(ConsoleMain.cfg.getString("debug-mode")))
            System.out.println(s);
    }
}
