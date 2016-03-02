package newer.com.musicplayer;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    //媒体播放器
    private MediaPlayer mediaPlayer;
    //存放音乐文件的集合
    ArrayList<Song> list = new ArrayList<>();
    //是否加载完全
    private boolean init = false;
    //是否正在播放
    boolean isRunning = false;
    //当前播放歌曲
    int current = 0;
    //是不是第一次就开始按钮
    boolean isFirst = true;

    public MusicService() {
    }

    //创建服务
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    playNext();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        new LoadMusicThread().start();
        Log.d("onCreate","服务创建");

    }

    public void play(int position) throws IOException {
        isFirst = false;
        //正在播放
        isRunning = true;
        //当前播放歌曲
        current = position;
        //回到空闲状态
        mediaPlayer.reset();
        //只有在空闲状态才可以设置数据源
        mediaPlayer.setDataSource(list.get(position).getData());
        //加载解码器相关数据
        mediaPlayer.prepare();
        //开始播放
        mediaPlayer.start();

        Song song = list.get(position);

        Intent intent = new Intent(AppFinal.PLAY_SONG_CURRENT);
        intent.putExtra(AppFinal.EXTRA_SONG, song);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);

    }

    public void play() {
        if (!isFirst) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isRunning = false;
            } else {
                mediaPlayer.start();
                isRunning = true;
            }
        }
    }

    public void playNext() throws IOException {
        if (!isFirst) {
            isRunning = true;
            if (current == list.size() - 1) {
                current = 0;
            } else {
                current++;
            }
            play(current);
        }
    }

    public void playPreview() throws IOException {
        if (!isFirst) {
            isRunning = true;
            if (current == 0) {
                current = list.size() - 1;
            } else {
                current--;
            }
            play(current);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    //销毁服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "服务销毁");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LoadBinder();
    }

    public Song getSong() {
        return list.get(current);
    }

    //新建一个类继承Bingder 里面写一个获得当前服务的方法
    class LoadBinder extends Binder {

        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public boolean getInit() {
        return init;
    }


    /**
     * 加载音乐文件的线程 加载完毕后发出本地广播
     */
    class LoadMusicThread extends Thread {
        @Override
        public void run() {
            super.run();
            Cursor cursor = getContentResolver().query(
                    Media.EXTERNAL_CONTENT_URI,
                    new String[]{Media._ID, Media.DATA, Media.TITLE, Media.ARTIST, Media.DURATION, Media.ALBUM, Media.ALBUM_ID},
                    "is_music != ?",
                    new String[]{"0"},
                    null
            );
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(Media._ID));
                long duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION));
                long album_id = cursor.getLong(cursor.getColumnIndex(Media.ALBUM_ID));
                String data = cursor.getString(cursor.getColumnIndex(Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(Media.ALBUM));

                Song song = new Song(id, duration, album, title, data, artist, album_id);
                list.add(song);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            //加载完成 游标关闭 并且发送广播
            cursor.close();
            init = true;

            Intent intent = new Intent(AppFinal.LOAD_SONG_LIST);
            //用bundle存放音乐集合,需要实现序列化
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppFinal.EXTRA_SONG_LIST, list);
            intent.putExtra(AppFinal.EXTRA_SONG_LIST, bundle);

            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(intent);


        }
    }
}
