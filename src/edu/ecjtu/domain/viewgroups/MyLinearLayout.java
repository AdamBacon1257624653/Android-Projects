package edu.ecjtu.domain.viewgroups;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import edu.ecjtu.domain.viewgroups.DragLayout.Status;

public class MyLinearLayout extends LinearLayout {

	private DragLayout dragLayout;
	private Status status;

	public View getDragLayout() {
		return dragLayout;
	}

	public void setDragLayout(DragLayout dragLayout) {
		this.dragLayout = dragLayout;
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyLinearLayout(Context context) {
		this(context, null);
	}

	private void init() {
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (dragLayout != null) {
			status = dragLayout.getCurrStatus();
		}
		if (status == Status.CLOSE) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (status == Status.CLOSE) {
			return super.onTouchEvent(event);
		} else {
			if (status == Status.OPEN) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					dragLayout.close();
				}
			}
			return true;
		}
	}
}
