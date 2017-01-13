package edu.ecjtu.domain.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import edu.ecjtu.musicplayer.R;

public class MyBracketView extends View {

	// 顶点坐标
	private Integer startX = 0;
	private Integer startY = 0;
	private Integer bracketWidth = 0;
	private Integer bracketHeight = 0;
	// 画笔的颜色
	private Integer lineColor = 0;

	public MyBracketView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.MyBracketView);
		int propertyCount = typedArray.getIndexCount();
		for (int i = 0; i < propertyCount; i++) {
			int attr = typedArray.getIndex(i);// 获取属性名
			switch (attr) {
			case R.styleable.MyBracketView_startX:
				startX = typedArray.getDimensionPixelSize(
						R.styleable.MyBracketView_startX, 0);
				break;
			case R.styleable.MyBracketView_startY:
				startY = typedArray.getDimensionPixelSize(
						R.styleable.MyBracketView_startY, 0);
				break;
			case R.styleable.MyBracketView_bracketHeight:
				bracketHeight = typedArray.getDimensionPixelSize(
						R.styleable.MyBracketView_bracketHeight, 0);
				break;
			case R.styleable.MyBracketView_bracketWidth:
				bracketWidth = typedArray.getDimensionPixelSize(
						R.styleable.MyBracketView_bracketWidth, 0);
				break;
			case R.styleable.MyBracketView_lineColor:
				lineColor = typedArray.getColor(
						R.styleable.MyBracketView_lineColor, 0);
				break;
			}
		}
		typedArray.recycle();// 释放资源
	}

	public Integer getStartX() {
		return startX;
	}

	public void setStartX(Integer startX) {
		this.startX = startX;
	}

	public Integer getStartY() {
		return startY;
	}

	public void setStartY(Integer startY) {
		this.startY = startY;
	}

	public Integer getBracketWidth() {
		return bracketWidth;
	}

	public void setBracketWidth(Integer bracketWidth) {
		this.bracketWidth = bracketWidth;
	}

	public Integer getBracketHeight() {
		return bracketHeight;
	}

	public void setBracketHeight(Integer bracketHeight) {
		this.bracketHeight = bracketHeight;
	}

	public Integer getLineColor() {
		return lineColor;
	}

	public void setLineColor(Integer lineColor) {
		this.lineColor = lineColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(3);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setColor(lineColor);
		canvas.drawLine(startX, startY, startX + bracketWidth / 2, startY
				+ bracketHeight / 2, paint);
		canvas.drawLine(startX, startY, startX + bracketWidth / 2, startY
				- bracketHeight / 2, paint);
	}
}
