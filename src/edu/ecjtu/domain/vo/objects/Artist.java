package edu.ecjtu.domain.vo.objects;

import java.io.Serializable;

import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.vo.interfaces.PinyinInterface;

public class Artist implements Serializable, PinyinInterface,
		Comparable<Artist> {
	private static final long serialVersionUID = -1383873436534325028L;

	private String _id;
	private String artistname;
	private String count;
	private String pinyin;

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
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

	public String getArtistname() {
		return artistname;
	}

	public void setArtistname(String artistname) {
		this.artistname = artistname;
		this.pinyin = PinyinUtil.getPinyin(artistname);
	}

	public Artist() {
		super();
	}

	public Artist(String _id, String artistname) {
		super();
		this._id = _id;
		this.artistname = artistname;
		this.pinyin = PinyinUtil.getPinyin(artistname);
	}

	@Override
	public String toString() {
		return "Artist [_id=" + _id + ", artistname=" + artistname + ", count="
				+ count + "]";
	}

	@Override
	public int compareTo(Artist another) {
		return PinyinUtil.compareResult(this, another);
	}

}
