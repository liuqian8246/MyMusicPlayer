package newer.com.musicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentMusicControl.Callback{

    //UI控键
    private ListView listView;
    private ProgressBar progressBar;

    //绑定的服务
    private MusicService musicService;

    //用来存储音乐的集合
    private ArrayList<Song> songList;

    //片段
    FragmentMusicControl frag_music_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    //初始化
    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        FragmentManager fm = getSupportFragmentManager();
        frag_music_control = (FragmentMusicControl) fm.findFragmentById(R.id.fragment_music_control);
        frag_music_control.setCallback(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //绑定服务
        bindService(
                new Intent(getApplicationContext(), MusicService.class),
                conn,
                BIND_AUTO_CREATE
        );

        //接收本地广播,动态注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppFinal.LOAD_SONG_LIST);
        filter.addAction(AppFinal.PLAY_SONG_CURRENT);
        LocalBroadcastManager.getInstance(MainActivity.this).
                registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //解除绑定
        unbindService(conn);
        //注销广播
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }


    /**
     * 创建服务连接，用来监视服务状态
     */
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MusicService.LoadBinder loadBinder = (MusicService.LoadBinder) binder;
            //获得服务的引用
            musicService = loadBinder.getMusicService();

            if (musicService.getInit()) {
                //如果已经加载完成 就直接赋值，避免没有接收到广播，否则就等接收广播复制
                if (songList == null) {
                    songList = musicService.list;
                    Log.d("HAHA ", "从服务引用的方法获得音乐列表");
                }
                //在listView中显示
                showList();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            songList = null;
        }
    };

    /**
     * 广播接收器
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (songList == null) {
                Bundle bundle = intent.getBundleExtra(AppFinal.EXTRA_SONG_LIST);
                songList = (ArrayList<Song>) bundle.getSerializable(AppFinal.EXTRA_SONG_LIST);
                Log.d("哈哈", "从广播中接收数据" + songList.size());
                showList();
            }
        }
    };

    private void showList() {
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(new MyListAdapter(songList, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    musicService.play(position);
                    frag_music_control.buttonPlay.setText(
                            musicService.isRunning ? "暂停" : "播放"
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
//----------------------------回调接口
    @Override
    public boolean play() {
        if(musicService != null) {
            musicService.play();
            return musicService.isRunning;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        if(musicService != null) {
            try {
                musicService.playNext();
                return musicService.isRunning;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean playPreview() {
        if(musicService != null) {
            try {
                musicService.playPreview();
                return musicService.isRunning;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService != null) {
            if(musicService.isFirst) {
                return 0;
            }
            Log.d("当前位置", String.valueOf(musicService.getCurrentPosition()));
            return musicService.getCurrentPosition();
        }
        return -1;
    }

    @Override
    public Song getSong() {
        if(musicService != null) {
            return musicService.getSong();
        }
        return null;
    }

//----------------------------------------------
}
