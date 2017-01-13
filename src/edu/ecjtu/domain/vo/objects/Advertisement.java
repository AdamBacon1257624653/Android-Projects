package edu.ecjtu.domain.vo.objects;

public class Advertisement {

	private int imageResId;
	private String description;

	public Advertisement() {
		super();
	}

	public Advertisement(int imageResId, String description) {
		super();
		this.imageResId = imageResId;
		this.description = description;
	}

	public int getImageResId() {
		return imageResId;
	}

	public void setImageResId(int imageResId) {
		this.imageResId = imageResId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Advertisement [imageResId=" + imageResId + ", description="
				+ description + "]";
	}
}
