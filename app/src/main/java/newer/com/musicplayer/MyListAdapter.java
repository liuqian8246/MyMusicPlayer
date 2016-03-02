package newer.com.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lenovo on 2015/12/26.
 */
public class MyListAdapter extends BaseAdapter {

    private ArrayList<Song> list;
    private Context context;
    private LayoutInflater inflater;

    public MyListAdapter(ArrayList<Song> list,Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Song getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(
                    R.layout.music_list_item,
                    parent,
                    false
            );
            holder = new MyHolder();
            holder.initView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyHolder) convertView.getTag();
        }

        Song song = list.get(position);
        holder.setContent(song);


        return convertView;
    }


    class MyHolder {
        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewAuthor;
        private TextView textViewDuration;

        public void initView(View v) {
            imageView = (ImageView) v.findViewById(R.id.imageView);
            textViewTitle = (TextView) v.findViewById(R.id.textView_title);
            textViewAuthor = (TextView) v.findViewById(R.id.textView_author);
            textViewDuration = (TextView) v.findViewById(R.id.textView_duration);
        }

        public void setContent(Song song) {
            textViewTitle.setText(song.getTitle());
            textViewAuthor.setText(song.getArtist());
            textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));

        }
    }
}
