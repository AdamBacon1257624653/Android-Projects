package edu.ecjtu.domain.vo.objects;

import java.io.Serializable;

import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.vo.interfaces.PinyinInterface;

public class Song implements Serializable, Comparable<Song>, PinyinInterface {

	private static final long serialVersionUID = -6764398054295163800L;

	private String _id;
	private String songname;
	private Artist artist;
	private Album album;
	private FilePath filepath;
	private String isFavorite = "0";
	private String duration = "0";
	private String songpath;// 保存歌曲的绝对路径
	private String parentpath;
	private String pinyin;

	public Song(String _id, String songname, Artist artist, Album album,
			FilePath filepath, String isFavorite, String duration,
			String songpath, String parentpath) {
		super();
		this._id = _id;
		this.songname = songname;
		this.artist = artist;
		this.album = album;
		this.filepath = filepath;
		this.isFavorite = isFavorite;
		this.duration = duration;
		this.songpath = songpath;
		this.parentpath = parentpath;
		this.pinyin = PinyinUtil.getPinyin(songname);
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getParentpath() {
		return parentpath;
	}

	public void setParentpath(String parentpath) {
		this.parentpath = parentpath;
	}

	public String getSongpath() {
		return songpath;
	}

	public void setSongpath(String songPath) {
		this.songpath = songPath;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String isFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
	}

	public Song(String _id, String songname, Long createdate, Artist artist,
			Album album, FilePath filepath) {
		super();
		this._id = _id;
		this.songname = songname;
		this.artist = artist;
		this.album = album;
		this.filepath = filepath;
		this.pinyin = PinyinUtil.getPinyin(songname);
	}

	public Song(String _id, String songname, Artist artist, Album album,
			FilePath filepath, String isFavorite) {
		super();
		this._id = _id;
		this.songname = songname;
		this.artist = artist;
		this.album = album;
		this.filepath = filepath;
		this.isFavorite = isFavorite;
		this.pinyin = PinyinUtil.getPinyin(songname);
	}

	public Song(String _id, String songname, Artist artist, Album album,
			FilePath filepath, String isFavorite, String duration) {
		super();
		this._id = _id;
		this.songname = songname;
		this.artist = artist;
		this.album = album;
		this.filepath = filepath;
		this.isFavorite = isFavorite;
		this.duration = duration;
		this.pinyin = PinyinUtil.getPinyin(songname);
	}

	public Song() {
		super();
	}

	@Override
	public String toString() {
		return "Song [_id=" + _id + ", songname=" + songname + ", artist="
				+ artist + ", album=" + album + ", filepath=" + filepath
				+ ", isFavorite=" + isFavorite + ", duration=" + duration
				+ ", songpath=" + songpath + ", parentpath=" + parentpath
				+ ", pinyin=" + pinyin + "]";
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSongname() {
		return songname;
	}

	public void setSongname(String songname) {
		this.songname = songname;
		this.pinyin = PinyinUtil.getPinyin(songname);
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public FilePath getFilepath() {
		return filepath;
	}

	public void setFilepath(FilePath filepath) {
		this.filepath = filepath;
	}

	@Override
	public int compareTo(Song another) {
		return PinyinUtil.compareResult(this, another);
	}
}
