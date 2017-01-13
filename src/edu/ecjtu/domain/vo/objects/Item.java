package edu.ecjtu.domain.vo.objects;

import android.view.View;

public class Item {
	private View view;
	private Integer position;

	public Item() {
		super();
	}

	public Item(View view, Integer position) {
		super();
		this.view = view;
		this.position = position;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "Item [view=" + view + ", position=" + position + "]";
	}

}
