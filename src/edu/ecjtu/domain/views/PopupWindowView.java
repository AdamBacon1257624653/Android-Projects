package edu.ecjtu.domain.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PopupWindowView extends View {

	private String[] playMethods = { "顺序播放", "随机播放", "列表循环播放", "单曲循环播放" };
	private Paint paint;
	private float cellWith;
	private float cellHeight;
	private int clickIndex = 0;
	private int downIndex = -1;
	private int moveIndex = -1;
	private OnPopupCLickListener listener;

	public PopupWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public PopupWindowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopupWindowView(Context context) {
		this(context, null);

	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setTextSize(30);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		for (int i = 0; i < playMethods.length; i++) {
			String playMethod = playMethods[i];
			int x = (int) (cellWith / 2.0f - paint.measureText(playMethod, 0,
					playMethod.length()) / 2.0f);
			Rect bounds = new Rect();
			paint.getTextBounds(playMethod, 0, playMethod.length(), bounds);
			int y = (int) (cellHeight / 2.0f + bounds.height() / 2.0f + i
					* cellHeight);
			paint.setColor(0x33FFFFFF);
			paint.setColor(i == clickIndex ? Color.WHITE : 0x33FFFFFF);
			canvas.drawRect(new Rect(0, (int) cellHeight * i, (int) cellWith,
					(int) (cellHeight + i * cellHeight)), paint);// 设置文字背景框
			paint.setColor(0xFF606060);
			canvas.drawText(playMethod, x, y, paint);
			canvas.drawLine(0, cellHeight * (i + 1), cellWith, cellHeight
					* (i + 1) + 2, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downIndex = (int) (event.getY() / cellHeight);
			clickIndex = downIndex;
			break;
		case MotionEvent.ACTION_MOVE:
			moveIndex = (int) (event.getY() / cellHeight);
			if (downIndex != moveIndex) {
				clickIndex = moveIndex;
			}
			listener.OnPopupClick(playMethods[clickIndex], clickIndex);
			break;
		}
		invalidate();
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		cellWith = w;
		cellHeight = h * 1.0f / playMethods.length;
	}

	public interface OnPopupCLickListener {
		void OnPopupClick(String playMethod, Integer position);
	}

	public void setOnPopupCLickListener(OnPopupCLickListener listener) {
		this.listener = listener;
	}
}
