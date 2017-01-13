package edu.ecjtu.domain.vo.objects;

public class Gvitem {
	private int itemResId;
	private String itemName;

	public int getItemResId() {
		return itemResId;
	}

	public void setItemResId(int itemResId) {
		this.itemResId = itemResId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Gvitem() {
		super();
	}

	public Gvitem(int itemResId, String itemName) {
		super();
		this.itemResId = itemResId;
		this.itemName = itemName;
	}

	@Override
	public String toString() {
		return "gvitem [itemResId=" + itemResId + ", itemName=" + itemName
				+ "]";
	}
}
