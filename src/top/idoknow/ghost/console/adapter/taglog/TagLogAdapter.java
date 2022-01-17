package top.idoknow.ghost.console.adapter.taglog;

import top.idoknow.ghost.console.core.ConsoleMain;

import java.io.File;

/**
 * This class is an adapter between TagLog lib and Console proj.
 * @author Rock Chin
 */
public class TagLogAdapter {
    private TagLogAdapter(){
    }
    private static TagLog tagLog=new TagLog();
    public static TagLog getTagLog(){
        return tagLog;
    }

    public static synchronized void init()throws Exception{

        TagLog.enable= Boolean.parseBoolean(ConsoleMain.cfg.getString("enable-tag-log"));
        if (new File("taglog.txt").exists()){
            tagLog.load();
        }
    }

    public TagLog getContext(){
        return tagLog;
    }

}
