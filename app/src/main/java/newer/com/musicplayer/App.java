package newer.com.musicplayer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lenovo on 2015/12/27.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, MusicService.class));
    }
}
