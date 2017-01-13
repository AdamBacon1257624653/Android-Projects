package edu.ecjtu.domain.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.musicplayer.R;

public class MyProgressBar extends View {

	private float maxProgress, currProgress, secondaryProgress = 0;
	private int maxWidth = 100;

	public float getSecondaryProgress() {
		return secondaryProgress;
	}

	public void setSecondaryProgress(float secondaryProgress) {
		this.secondaryProgress = secondaryProgress;
		this.invalidate();
	}

	public float getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(float maxProgress) {
		this.maxProgress = maxProgress;
	}

	public float getCurrProgress() {
		return currProgress;
	}

	public void setCurrProgress(float currProgress) {
		this.currProgress = currProgress;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.MyProgressBar);
		int propertyCount = ta.getIndexCount();
		for (int i = 0; i < propertyCount; i++) {
			int property = ta.getIndex(i);
			switch (property) {
			case R.styleable.MyProgressBar_maxProgress:
				maxProgress = ta.getInteger(property, 0);
				break;
			case R.styleable.MyProgressBar_currProgress:
				currProgress = ta.getInteger(property, 0);
				break;
			}
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(maxWidth, 10);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Utils.themeColor);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(5);
		float width = 10;
		width = ((float) (currProgress / maxProgress)) * maxWidth;
		canvas.drawRect(0, 0, width, 10, paint);
		paint.setColor(0x5000B4DD);
		float secondaryWidth = ((float) (secondaryProgress / maxProgress))
				* maxWidth;
		canvas.drawRect(0, 0, secondaryWidth, 10, paint);
	}
}
