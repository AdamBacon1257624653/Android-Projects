package edu.ecjtu.domain.vo.objects;

public class OnlineMusic {
	private String songName = "";
	private String singerName = "";
	private String urlPath;// 普通音乐品质的Url路径
	private String HQUrlPath;// 高清音乐品质的Url路径

	public OnlineMusic(String songName, String singerName, String urlPath,
			String hQUrlPath) {
		super();
		this.songName = songName;
		this.singerName = singerName;
		this.urlPath = urlPath;
		HQUrlPath = hQUrlPath;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public String getHQUrlPath() {
		return HQUrlPath;
	}

	public void setHQUrlPath(String hQUrlPath) {
		HQUrlPath = hQUrlPath;
	}

	public OnlineMusic() {
		super();
	}

	@Override
	public String toString() {
		return "OnlineMusic [songName=" + songName + ", singerName="
				+ singerName + ", urlPath=" + urlPath + ", HQUrlPath="
				+ HQUrlPath + "]";
	}
}
