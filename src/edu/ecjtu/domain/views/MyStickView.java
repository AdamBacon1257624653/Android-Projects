package edu.ecjtu.domain.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import edu.ecjtu.musicplayer.R;

public class MyStickView extends ImageView {

	private float downX;
	private float moveX;
	private double left = 0;
	private Bitmap bitmap;
	private int width = 0;// 窗体宽
	private int position = 0;
	private boolean isMoving = false;
	private int pageCount = 0;// 记录fragment的页数

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public void setLeftX(Double left) {
		this.left = left;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public MyStickView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a2);
		if (pageCount == 2) {
			bitmap = BitmapFactory
					.decodeResource(getResources(), R.drawable.a2);
		} else if (pageCount == 4) {
			bitmap = BitmapFactory
					.decodeResource(getResources(), R.drawable.a4);
		}
		setMeasuredDimension(width, bitmap.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, (int) left, 0, null);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int distance = 0;
		switch (position) {
		case 0:
			if (pageCount != 2) {
				left = 0;
			} else {
				left = 20;
			}
			break;
		case 1:
			if (pageCount != 2) {
				left = width / 4;
			} else {
				left = width - bitmap.getWidth() - 20;
			}
			break;
		case 2:
			left = width / 2;
			break;
		case 3:
			left = width * (0.75);
			break;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if (!isMoving) {// 记录downX
				downX = event.getX();
			} else {
				moveX = event.getX();
				distance = (int) Math.abs(moveX - downX);
				switch (position) {
				case 0:
					if (moveX > downX) {// 向右滑动时
						if (pageCount != 2) {
							left = 0;
						} else {
							left = 20;
						}

					} else {// 向左滑动时
						if (pageCount != 2) {
							left = left + distance / 2;
							if (left >= width / 4) {
								left = width / 4;
							}
						} else {
							left = left + distance;
							if (left >= width - bitmap.getWidth() - 20) {// 如果右边界超出padding的边界,则不动
								left = width - bitmap.getWidth() - 20;
							}
						}
					}
					break;
				case 1:
					if (moveX < downX) {// 向左
						if (pageCount != 2) {
							left = left + distance / 2;
							if (left >= width / 2) {
								left = width / 2;
							}
						} else {
							left = width - bitmap.getWidth() - 20;
						}
					} else {// 向右
						if (pageCount != 2) {
							left = left - distance / 2;
							if (left <= 0) {// 如果左边界超出padding的边界,则不动
								left = 0;
							}
						} else {
							left = left - distance;
							if (left <= 20) {// 如果左边界超出padding的边界,则不动
								left = 20;
							}
						}
					}
					break;
				case 2:
					if (moveX < downX) {// 向左
						left = left + distance / 2;
						if (left >= width * (0.75)) {// 如果右边界超出屏幕边界，则不动
							left = width * 0.75;
						}
					} else {// 向右
						left = left - distance / 2;
						if (left <= width / 4) {// 如果左边界超出左边界，则不动
							left = width / 4;
						}
					}
					break;
				case 3:
					if (moveX < downX) {// 向左
						left = width * (0.75);
					} else {// 向右
						left = left - distance / 2;
						if (left <= width / 2) {// 如果左边界超出左边界，则不动
							left = width / 2;
						}
					}
					break;
				}
			}
			isMoving = true;
			break;
		case MotionEvent.ACTION_UP:
			isMoving = false;
			break;
		}
		invalidate();
		return true;// 是否向下分发事件.true：是，false:不是
	}
}
