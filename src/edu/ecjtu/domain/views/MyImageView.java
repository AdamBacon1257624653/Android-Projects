package edu.ecjtu.domain.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	private boolean isClick=false;
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public boolean isClick() {
		return isClick;
	}

	public void setClick(boolean isClick) {
		this.isClick = isClick;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isClick){
			Paint paint=new Paint();
			int left=getLeft();
			int width=getWidth();
			int top=getTop();
			int height=getHeight();
			int cx=left+width/2;
			int cy=top+height/2;
			paint.setColor(0x70000000);
			canvas.drawRect(left, top, left+width, top+height, paint);//ª≠∞ÎÕ∏√˜µ◊
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);//…Ë÷√ª≠± ¥÷œ∏
			canvas.drawCircle(cx, cy, 15, paint);//ª≠‘≤
			Path path=new Path();
			/**
			 * 
		 		x:x1-7£¨y:y1-8
				x:cx-3;y:cy+7
				x:x1+12;y:y1-13
			 */
			path.moveTo(cx-10, cy-1);
			path.lineTo(cx-3,cy+7);//’€µ„
			path.lineTo(cx+9, cy-6);
			canvas.drawPath(path, paint);
		}
	}

}
