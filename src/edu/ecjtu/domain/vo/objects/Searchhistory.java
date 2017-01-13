package edu.ecjtu.domain.vo.objects;

public class Searchhistory {
	private String content;
	private String timemillies;

	public Searchhistory() {
		super();
	}

	public Searchhistory(String content, String timemillies) {
		super();
		this.content = content;
		this.timemillies = timemillies;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimemillies() {
		return timemillies;
	}

	public void setTimemillies(String timemillies) {
		this.timemillies = timemillies;
	}

	@Override
	public String toString() {
		return "searchhistory [content=" + content + ", timemillies="
				+ timemillies + "]";
	}
}
