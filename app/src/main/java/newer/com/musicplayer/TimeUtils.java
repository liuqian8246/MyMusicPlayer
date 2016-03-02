package newer.com.musicplayer;

/**
 * Created by lenovo on 2015/12/26.
 */
public class TimeUtils {
    //将long值时间转化为String
    public static String formatDuration(long duration) {
        long t = duration / 1000;
        int m = (int) (t / 60);
        int s = (int) (t % 60);

        return String.format("%02d:%02d",m,s);
    }
}
