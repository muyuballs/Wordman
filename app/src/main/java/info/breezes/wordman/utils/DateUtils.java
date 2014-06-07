package info.breezes.wordman.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jianxingqiao on 14-6-7.
 */
public class DateUtils {
    public static String format(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }


    public static String formatHHMMSS(long milliseconds) {
        long time=milliseconds/1000;
        int sec=(int)(time%60);
        time=time/60;
        int min=(int)(time%60);
        time=time/60;
        int hour=(int)(time%60);
        return String.format("%02d:%02d:%02d",hour,min,sec);
    }
}
