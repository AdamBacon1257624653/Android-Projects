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
	private int width = 0;// �����
	private int position = 0;
	private boolean isMoving = false;
	private int pageCount = 0;// ��¼fragment��ҳ��

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
			if (!isMoving) {// ��¼downX
				downX = event.getX();
			} else {
				moveX = event.getX();
				distance = (int) Math.abs(moveX - downX);
				switch (position) {
				case 0:
					if (moveX > downX) {// ���һ���ʱ
						if (pageCount != 2) {
							left = 0;
						} else {
							left = 20;
						}

					} else {// ���󻬶�ʱ
						if (pageCount != 2) {
							left = left + distance / 2;
							if (left >= width / 4) {
								left = width / 4;
							}
						} else {
							left = left + distance;
							if (left >= width - bitmap.getWidth() - 20) {// ����ұ߽糬��padding�ı߽�,�򲻶�
								left = width - bitmap.getWidth() - 20;
							}
						}
					}
					break;
				case 1:
					if (moveX < downX) {// ����
						if (pageCount != 2) {
							left = left + distance / 2;
							if (left >= width / 2) {
								left = width / 2;
							}
						} else {
							left = width - bitmap.getWidth() - 20;
						}
					} else {// ����
						if (pageCount != 2) {
							left = left - distance / 2;
							if (left <= 0) {// �����߽糬��padding�ı߽�,�򲻶�
								left = 0;
							}
						} else {
							left = left - distance;
							if (left <= 20) {// �����߽糬��padding�ı߽�,�򲻶�
								left = 20;
							}
						}
					}
					break;
				case 2:
					if (moveX < downX) {// ����
						left = left + distance / 2;
						if (left >= width * (0.75)) {// ����ұ߽糬����Ļ�߽磬�򲻶�
							left = width * 0.75;
						}
					} else {// ����
						left = left - distance / 2;
						if (left <= width / 4) {// �����߽糬����߽磬�򲻶�
							left = width / 4;
						}
					}
					break;
				case 3:
					if (moveX < downX) {// ����
						left = width * (0.75);
					} else {// ����
						left = left - distance / 2;
						if (left <= width / 2) {// �����߽糬����߽磬�򲻶�
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
		return true;// �Ƿ����·ַ��¼�.true���ǣ�false:����
	}
}
