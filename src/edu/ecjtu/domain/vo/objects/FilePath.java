package edu.ecjtu.domain.vo.objects;

import java.io.Serializable;

public class FilePath implements Serializable{
	private static final long serialVersionUID = -3140722809874269184L;
	
	private String parentname;
	private String absolutepath;
	private String count;
	private String _id;

	public FilePath(String parentname, String absolutepath) {
		super();
		this.parentname = parentname;
		this.absolutepath = absolutepath;
	}

	public FilePath(String parentname, String absolutepath, String count) {
		super();
		this.parentname = parentname;
		this.absolutepath = absolutepath;
		this.count = count;
	}

	public FilePath(String parentname, String absolutepath, String count,
			String _id) {
		super();
		this.parentname = parentname;
		this.absolutepath = absolutepath;
		this.count = count;
		this._id = _id;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getParentname() {
		return parentname;
	}

	public void setParentname(String parentname) {
		this.parentname = parentname;
	}

	public String getAbsolutepath() {
		return absolutepath;
	}

	public void setAbsolutepath(String absolutepath) {
		this.absolutepath = absolutepath;
	}

	public FilePath() {
		super();
	}

	@Override
	public String toString() {
		return "FilePath [parentname=" + parentname + ", absolutepath="
				+ absolutepath + ", count=" + count + ", _id=" + _id + "]";
	}
}
