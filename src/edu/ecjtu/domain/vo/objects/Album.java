package edu.ecjtu.domain.vo.objects;

import java.io.Serializable;

import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.vo.interfaces.PinyinInterface;

public class Album implements Serializable, Comparable<Album>, PinyinInterface {
	private static final long serialVersionUID = 847797094845347165L;

	private String _id;
	private String albumname;
	private String count;
	private String Pinyin;

	public Album() {
		super();
	}

	public String getPinyin() {
		return Pinyin;
	}

	public void setPinyin(String pinyin) {
		Pinyin = pinyin;
	}

	public Album(String _id, String albumname) {
		super();
		this._id = _id;
		this.albumname = albumname;
		this.Pinyin = PinyinUtil.getPinyin(albumname);
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getAlbumname() {
		return albumname;
	}

	public void setAlbumname(String albumname) {
		this.albumname = albumname;
		this.Pinyin = PinyinUtil.getPinyin(albumname);
	}

	@Override
	public String toString() {
		return "Album [_id=" + _id + ", albumname=" + albumname + ", count="
				+ count + "]";
	}

	@Override
	public int compareTo(Album another) {
		return PinyinUtil.compareResult(this, another);
	}

}
