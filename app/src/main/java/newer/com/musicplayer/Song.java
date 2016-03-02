package newer.com.musicplayer;

import java.io.Serializable;

/**
 * Created by lenovo on 2015/12/26.
 */
public class Song implements Serializable {
    private long id;
    private String data;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private long album_id;

    public Song(long id, long duration, String album, String title, String data, String artist,long album_id) {
        this.id = id;
        this.duration = duration;
        this.album = album;
        this.title = title;
        this.data = data;
        this.artist = artist;
        this.album_id = album_id;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                '}';
    }
}
