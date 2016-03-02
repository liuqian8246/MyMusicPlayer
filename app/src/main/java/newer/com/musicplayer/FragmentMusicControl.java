package newer.com.musicplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMusicControl extends Fragment {

    private SeekBar seekBar;
    private TextView textViewPosition;
    private TextView textViewDuration;
    private Button buttonNext;
    private Button buttonPreview;
    public Button buttonPlay;
    private TextView textViewTitle;
    private Callback callback;
    private MyMusicPlayer musicPlayer;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public FragmentMusicControl() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_music_control, container, false);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar);
        textViewTitle = (TextView) v.findViewById(R.id.textView_ti);
        textViewDuration = (TextView) v.findViewById(R.id.textView_duration);
        textViewPosition = (TextView) v.findViewById(R.id.textView_position);
        buttonNext = (Button) v.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRunning = callback.playNext();
                buttonPlay.setText(isRunning ? "暂停" : "播放");
            }
        });
        buttonPlay = (Button) v.findViewById(R.id.button_play);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRunning = callback.play();
                buttonPlay.setText(isRunning ? "暂停" : "播放");
            }
        });
        buttonPreview = (Button) v.findViewById(R.id.button_before);
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRunning = callback.playPreview();
                buttonPlay.setText(isRunning ? "暂停" : "播放");
            }
        });

        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(receiver, new IntentFilter(AppFinal.PLAY_SONG_CURRENT));
        return v;
    }

    @Override
    public void onResume() {
        Log.d("onResume", "启动了");
        super.onResume();
        musicPlayer = new MyMusicPlayer();
        musicPlayer.start();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "启动了");
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(receiver);
        musicPlayer.isRunning = false;

    }

    //注册广播接收器
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Song song = (Song) intent.getSerializableExtra(AppFinal.EXTRA_SONG);
            textViewTitle.setText(song.getTitle());
            textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
            textViewPosition.setText("00:00");
            seekBar.setMax((int) song.getDuration());
        }
    };

    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppFinal.MSG:
                    int position = msg.arg1;
                    textViewPosition.setText(TimeUtils.formatDuration(position));
                    seekBar.setProgress(position);
                    Song song = (Song) msg.obj;
                    if(!textViewTitle.getText().equals(song.getTitle())) {
                        textViewTitle.setText(song.getTitle());
                        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
                        seekBar.setMax((int) song.getDuration());
                    }
                    break;
            }
        }
    };

    class MyMusicPlayer extends Thread {
        boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                int position = callback.getCurrentPosition();
                if (position != -1) {
                    Song song = callback.getSong();
                    Message msg = Message.obtain();
                    msg.what = AppFinal.MSG;
                    msg.arg1 = position;
                    msg.obj = song;
                    uiHandler.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    //回调接口
    public interface Callback {
        boolean play();

        boolean playNext();

        boolean playPreview();

        int getCurrentPosition();

        Song getSong();
    }
}
