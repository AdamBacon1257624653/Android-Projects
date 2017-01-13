package edu.ecjtu.domain.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;
import edu.ecjtu.musicplayer.R;

public class MyCheckView extends ImageView {

	private boolean isChecked = false;

	public MyCheckView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
		invalidate();
	}

	public boolean isChecked() {
		return isChecked;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isChecked) {
			Bitmap bm_circle = BitmapFactory.decodeResource(getResources(),
					R.drawable.file_circlecheck);
			int radius = bm_circle.getHeight() / 2;
			int width = getWidth();
			int height = getHeight();
			int left = getLeft();
			int top = getTop();
			int cx = width / 2;
			int cy = height / 2;
			Paint paint = new Paint();
			paint.setColor(0xff00b4ff);
			canvas.drawCircle(cx, cy, radius, paint);
			paint.setStrokeWidth(3f);
			paint.setStyle(Style.STROKE);
			paint.setColor(0xffffffff);
			Path path = new Path();
			/**
			 * 
			 x:x1-7£¨y:y1-8 x:cx-3;y:cy+7 x:x1+12;y:y1-13
			 */
			path.moveTo(cx - 12, cy - 3);
			path.lineTo(cx - 3, cy + 7);// ’€µ„
			path.lineTo(cx + 15, cy - 10);
			canvas.drawPath(path, paint);
		}
	}
}
