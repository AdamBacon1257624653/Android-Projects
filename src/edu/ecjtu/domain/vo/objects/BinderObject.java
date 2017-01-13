package edu.ecjtu.domain.vo.objects;

import java.io.Serializable;
import java.util.List;

import android.media.MediaPlayer;
import edu.ecjtu.domain.services.PlayService.PlayMethod;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;

public class BinderObject implements Serializable {
	private static final long serialVersionUID = 5746945463001061025L;

	private Song currentSong;// 当前正在播放的歌曲
	private List<Song> songs;
	private MediaPlayer player;
	private PlayInterface playBinder;
	private int currentPosition;
	private Long threadTimeMillis;
	private PlayMethod playMethod;
	private OnlineMusic onlineMusic;

	public BinderObject() {
		super();
	}

	public BinderObject(Song currentSong, List<Song> songs, MediaPlayer player,
			PlayInterface playBinder, int currentPosition) {
		super();
		this.currentSong = currentSong;
		this.songs = songs;
		this.player = player;
		this.playBinder = playBinder;
		this.currentPosition = currentPosition;
	}

	public OnlineMusic getOnlineMusic() {
		return onlineMusic;
	}

	public void setOnlineMusic(OnlineMusic onlineMusic) {
		this.onlineMusic = onlineMusic;
	}

	public PlayMethod getPlayMethod() {
		return playMethod;
	}

	public void setPlayMethod(PlayMethod playMethod) {
		this.playMethod = playMethod;
	}

	public Long getThreadTimeMillis() {
		return threadTimeMillis;
	}

	public void setThreadTimeMillis(Long threadTimeMillis) {
		this.threadTimeMillis = threadTimeMillis;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public PlayInterface getPlayBinder() {
		return playBinder;
	}

	public void setPlayBinder(PlayInterface playBinder) {
		this.playBinder = playBinder;
	}

	public Song getCurrentSong() {
		return currentSong;
	}

	public void setCurrentSong(Song currentSong) {
		this.currentSong = currentSong;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

	public MediaPlayer getPlayer() {
		return player;
	}

	public void setPlayer(MediaPlayer player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return "BinderObject [currentSong=" + currentSong + ", songs=" + songs
				+ ", player=" + player + ", playBinder=" + playBinder
				+ ", currentPosition=" + currentPosition
				+ ", threadTimeMillis=" + threadTimeMillis + ", playMethod="
				+ playMethod + ", onlineMusic=" + onlineMusic + "]";
	}
}
