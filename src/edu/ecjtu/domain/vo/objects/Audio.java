package edu.ecjtu.domain.vo.objects;

import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import edu.ecjtu.domain.Util.MediaUtils;

public class Audio {
	private String title, artist, album, filePath;
	private String _id, album_id, artist_id;
	private boolean isRingtone = false, isAlarm = false, isMusic;
	private String parentpath;
	private String duration;

	public Audio(Bundle bundle) {
		_id = bundle.getString(Media._ID);
		album_id = bundle.getString(Media.ALBUM_ID);
		artist_id = bundle.getString(Media.ARTIST_ID);
		title = bundle.getString(Media.TITLE);// 歌曲名
		artist = bundle.getString(Media.ARTIST);// 歌手
		album = bundle.getString(Media.ALBUM);// 专辑
		filePath = bundle.getString(Media.DATA);// 歌曲文件路径
		duration = bundle.getString(Media.DURATION);
		parentpath = bundle.getString(MediaUtils.PARENTPATH);// 歌曲下载时间
		isMusic = bundle.getString(Media.IS_MUSIC).equals("1");
		isRingtone = bundle.getString(Media.IS_RINGTONE).equals("1");
		isAlarm = bundle.getString(Media.IS_ALARM).equals("1");
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getAlbum_id() {
		return album_id;
	}

	public void setAlbum_id(String album_id) {
		this.album_id = album_id;
	}

	public String getArtist_id() {
		return artist_id;
	}

	public void setArtist_id(String artist_id) {
		this.artist_id = artist_id;
	}

	public boolean isRington() {
		return isRingtone;
	}

	public void setRington(boolean isRington) {
		this.isRingtone = isRington;
	}

	public boolean isAlarm() {
		return isAlarm;
	}

	public void setAlarm(boolean isAlarm) {
		this.isAlarm = isAlarm;
	}

	public boolean isMusic() {
		return isMusic;
	}

	public void setMusic(boolean isMusic) {
		this.isMusic = isMusic;
	}

	public String getParentpath() {
		return parentpath;
	}

	public void setCreateDate(String parentpath) {
		this.parentpath = parentpath;
	}

	@Override
	public String toString() {
		return "Audio [title=" + title + ", artist=" + artist + ", album="
				+ album + ", filePath=" + filePath + ", _id=" + _id
				+ ", album_id=" + album_id + ", artist_id=" + artist_id
				+ ", isRington=" + isRingtone + ", isAlarm=" + isAlarm
				+ ", isMusic=" + isMusic + ", createDate=" + parentpath
				+ ", duration=" + duration + "]";
	}

}
