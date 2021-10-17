package top.idoknow.ghost.console.ioutil.log;

import top.idoknow.ghost.console.core.ConsoleMain;

import java.sql.*;

/**
 * Output log to MySQL
 * @author Rock Chin
 */
public class LogMySQL {


    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection conn = null;
    public static Statement stmt = null;

    public static boolean isEnable(){
        return !ConsoleMain.cfg.getString("log-to-mysql-address").equals("");
    }
    private static boolean ready=false;

    public static boolean isReady(){
        return ready;
    }
    public static Statement getStmt(){
        return stmt;
    }
    public static boolean init()throws Exception{
        if (!isEnable())
            return false;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection("jdbc:mysql://" + ConsoleMain.cfg.getString("log-to-mysql-address")
                + "/" + ConsoleMain.cfg.getString("mysql-database")
                , ConsoleMain.cfg.getString("mysql-user")
                , ConsoleMain.cfg.getString("mysql-password"));
        //create data table if not exist
        stmt = conn.createStatement();
        String sql="CREATE TABLE IF NOT EXISTS `logs`(\n" +
                "   `id` INT UNSIGNED AUTO_INCREMENT,\n" +
                "   `time` VARCHAR(25) NOT NULL,\n" +
                "   `type` VARCHAR(10) NOT NULL,\n" +
                "   `subject` VARCHAR(256) NOT NULL,\n" +
                "   `title` VARCHAR(128) NOT NULL,\n" +
                "   `content` VARCHAR(2048),\n" +
                "   PRIMARY KEY ( `id` )\n" +
                ")ENGINE=InnoDB;";
        boolean rs=stmt.execute(sql);
        ready=true;
        return rs;

    }
}
