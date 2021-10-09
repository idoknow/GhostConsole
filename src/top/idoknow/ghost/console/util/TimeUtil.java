package top.idoknow.ghost.console.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static String millsToMMDDHHmmSS(long mills){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return (calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+
                ","+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
    }
    public static String nowMMDDHHmmSS(){
        return millsToMMDDHHmmSS(new Date().getTime());
    }
    public static String millsToFileNameValidMMDDHHmmSS(long mills){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return (calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+
                "_"+calendar.get(Calendar.HOUR_OF_DAY)+"-"+calendar.get(Calendar.MINUTE)+"-"+calendar.get(Calendar.SECOND);
    }
    public static String nowFileNameValidMMDDHHmmSS(){
        return millsToFileNameValidMMDDHHmmSS(new Date().getTime());
    }

}
