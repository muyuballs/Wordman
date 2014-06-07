package info.breezes.wordman.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jianxingqiao on 14-6-7.
 */
public class DateUtils {
    public static String format(Date date,String format){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        return  simpleDateFormat.format(date);
    }
}
