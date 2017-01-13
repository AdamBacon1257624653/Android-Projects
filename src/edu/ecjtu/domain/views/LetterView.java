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

public class LetterView extends View {

	private float cellWidth, cellHeight;
	private int moveIndex = -1;

	private String[] letters = { "A", "B", "C", "D", "E",//
			"F", "G", "H", "I", "J",//
			"K", "L", "M", "N", "O",//
			"P", "Q", "R", "S", "T",//
			"U", "V", "W", "X", "Y",//
			"Z", "#", };

	private Paint paint;
	private OnLetterUpdateListener listener;
	private int downIndex = -1;
	private int touchIndex = -1;

	public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public LetterView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LetterView(Context context) {
		this(context, null);
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.GRAY);
		paint.setTextSize(20);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.cellWidth = w;
		this.cellHeight = h * 1.0f / letters.length;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < letters.length; i++) {
			String c = letters[i];
			float textWidth = paint.measureText(c);
			Rect bounds = new Rect();
			paint.getTextBounds(c, 0, c.length(), bounds);
			int textHeight = bounds.height();
			int x = (int) (cellWidth / 2.0f - textWidth / 2.0f);
			int y = (int) (cellHeight / 2.0f + textHeight / 2.0f + i
					* cellHeight);
			paint.setColor((touchIndex == i) ? 0xFF00B4FF : Color.GRAY);
			canvas.drawText(c, x, y, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downIndex = (int) (event.getY() / cellHeight);
			touchIndex = downIndex;
			if (downIndex >= 0 && downIndex < letters.length) {
				if(listener!=null){
					listener.OnLetterUpdate(letters[downIndex]);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			moveIndex = (int) (event.getY() / cellHeight);
			touchIndex = moveIndex;
			if (moveIndex != downIndex && moveIndex >= 0
					&& moveIndex < letters.length) {
				listener.OnLetterUpdate(letters[moveIndex]);
			}
			break;
		case MotionEvent.ACTION_UP:
			touchIndex = -1;
			break;
		}
		invalidate();
		return true;
	}

	public interface OnLetterUpdateListener {
		void OnLetterUpdate(String letter);
	}

	public void setOnLetterUpdateListener(OnLetterUpdateListener listener) {
		this.listener = listener;

	}

}
