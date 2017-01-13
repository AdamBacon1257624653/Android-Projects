package edu.ecjtu.domain.viewgroups;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;

import edu.ecjtu.activities.PlayerActivity;

public class DragLayout extends FrameLayout {

	private View menuView, mainView;
	private ViewDragHelper mDragHelper;
	private int viewWidth;
	private int viewHeight;
	private int dragRange;
	private Status currrStatus = Status.CLOSE;
	private PlayerActivity playerActivity;

	public enum Status {
		OPEN, CLOSE, DRAG
	}

	public Status getCurrStatus() {
		return currrStatus;
	}

	public void setCurrStatus(Status status) {
		this.currrStatus = status;
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		playerActivity = (PlayerActivity) context;
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context) {
		this(context, null);
	}

	private void init() {
		mDragHelper = ViewDragHelper.create(this, callback);
	}

	ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View view, int pointerId) {
			return true;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				left = (int) fixLeft(left);
			}
			return left;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			changeStatus(mainView.getLeft(), xvel);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			float newLeft = mainView.getLeft();
			if (changedView == menuView) {
				newLeft = mainView.getLeft() + dx;
				newLeft = fixLeft(newLeft);
				mainView.layout((int) newLeft, 0, (int) newLeft + viewWidth,
						viewHeight);
			}
			float percent = newLeft * 1.0f / dragRange;
			onDragEvent(percent);
			if (changedView == menuView) {
				changedView.layout(0, 0, (int) dragRange, viewHeight);
			}
		};
	};

	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		if (playerActivity.getPagerPosition() == 0) {
			return mDragHelper.shouldInterceptTouchEvent(ev);
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (playerActivity.getPagerPosition() == 0) {
			mDragHelper.processTouchEvent(event);
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	protected void onDragEvent(float percent) {
		Status preStatus = currrStatus;
		updateStatus(percent);
		if (listener != null) {
			listener.OnDrag(percent);
			dragAnimation(percent);
		}
		if (preStatus != currrStatus) {
			if (currrStatus == Status.OPEN) {
				if (listener != null) {
					listener.OnOpen(percent);
				}
			} else if (currrStatus == Status.CLOSE) {
				if (listener != null) {
					listener.OnClose(percent);
				}
			}
		}
	}

	/**
	 * 更新状态
	 * 
	 * @param percent
	 */
	private void updateStatus(float percent) {
		currrStatus = Status.DRAG;
		if (percent == 1.0f) {
			currrStatus = Status.OPEN;
		} else if (percent == 0) {
			currrStatus = Status.CLOSE;
		}
	}

	/**
	 * 变化动画
	 * 
	 * @param percent
	 */
	protected void dragAnimation(float percent) {
		FloatEvaluator fe = new FloatEvaluator();
		float translationX = fe.evaluate(percent, -0.5f * viewWidth, 0);
		menuView.setTranslationX(translationX);
		float scaleMain = fe.evaluate(percent, 1.0f, 0.8f);
		mainView.setScaleY(scaleMain);
		getBackground().setColorFilter(
				(int) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT),
				Mode.SRC_OVER);
	}

	/**
	 * 颜色逐渐变化
	 * 
	 * @param percent
	 *            移动的百分比
	 * @param startVlue
	 *            开始颜色值
	 * @param endValue
	 *            结束的颜色值
	 * @return
	 */
	public Object evaluateColor(float percent, Object startVlue, Object endValue) {
		int startInt = (int) startVlue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (int) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;
		return (int) ((startA + (int) (percent * (endA - startA))) << 24)
				| (int) ((startR + (int) (percent * (endR - startR))) << 16)
				| (int) ((startG + (int) (percent * (endG - startG))) << 8)
				| (int) (startB + (percent * (endB - startB)));
	}

	protected void changeStatus(int Left, float xvel) {
		if (Left >= dragRange / 2.0f && xvel == 0) {
			open();
		} else if (xvel > 0) {
			open();
		} else {
			close();
		}
	}

	public void open() {
		open(true);
	}

	private void open(boolean isSmooth) {
		int finalLeft = (int) dragRange;
		if (isSmooth) {
			if (mDragHelper.smoothSlideViewTo(mainView, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}
	}

	public void close() {
		close(true);
	}

	private void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			if (mDragHelper.smoothSlideViewTo(mainView, finalLeft, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}

	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	protected float fixLeft(float left) {
		if (left < 0) {
			left = 0;
		} else if (left > dragRange) {
			left = dragRange;
		}
		return left;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		menuView = getChildAt(0);
		mainView = getChildAt(1);
		menuView.layout(0, 0, (int) dragRange, viewHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		viewHeight = h;
		dragRange = (int) (viewWidth * 0.6f);
	}

	private OnStatusChangeListener listener;

	public interface OnStatusChangeListener {
		void OnDrag(float percent);

		void OnOpen(float percent);

		void OnClose(float percent);
	}

	public void setOnStatusChangeListener(OnStatusChangeListener listener) {
		this.listener = listener;
	}
}
