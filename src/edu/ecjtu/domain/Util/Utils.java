package edu.ecjtu.domain.Util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;
import android.widget.Toast;
import edu.ecjtu.musicplayer.R;

public class Utils {
	// 背景圖片修改請求码
	public static final int BG_REQUESTCODE = 0;
	public static final int BG_FROMPHONE_REQUESTCODE = 1;
	// 背景图片的返回结果码
	public static final int BG_RESULTCODE = 0;
	public static final int BG_FROMPHONE_RESULTCODE = 1;
	// 从相册中获取照片的请求码
	public static final int PHOTO_REQUEST_GALLERY = 1000;
	// 打开相片的裁剪技术的请求码
	public static final int PHOTO_REQUEST_CUT = 1001;
	// 定义来自手机图片key值，以便获取该图片的RGB信息
	public static final int PHONEIMAGE_KEY = 100002;
	// 用于Intent的传递的key
	public static final String BM_COVER = "bm_cover";
	// 用于intent的传递key值
	public static final String FROM_PHONE = "from_phone";
	// 保存preshowresid的key值
	public static final String PRESHOW_RESID = "preshowResId";
	// 主题颜色
	public static final int themeColor = 0xFF00B4FF;
	// 数据加载完毕
	public static final int SONG_LOADED = 1;
	public static final int SONGFRAGMENT_CREATED = 2;
	public static final int SONGFRAGMENT_RESUMED = 3;
	public static final int FOLDERFRAGMENT_RESUMED = 4;
	public static final int SINGERFRAGMENT_CREATED = 5;
	public static final int SINGERFRAGMENT_RESUMED = 6;
	public static final int ALBUMFRAGMENT_CREATED = 7;
	public static final int ALBUMFRAGMENT_RESUMED = 8;
	// 执行数据库的插入操作
	public static final int ALBUM_INSERT = 10;
	public static final int SONG_INSERT = 11;
	public static final int ARTIST_INSERT = 12;
	public static final int FILEPATH_INSERT = 13;
	// 扫描文件
	public static final int SCAN_FILEPATH = 0;// 更新文件名称的message.what
	public static final int SONG_SCANNED = 1;// 扫描到一个文件的message.what
	public static final int SCAN_FINISHED = 2;// 文件扫描完毕的message.what
	public static final int SCANNING = 3;// 文件正在扫描的message.what
	// 刷新底部的进度条
	public static final int REFRESH_BOTTOM = 4000;
	// 图片轮播的handler.what
	public static final int UPDATE_IMAGE = 5000;

	public static final String TAG = "Adam";

	// 设置popupwindow的listview的id
	public static final int POPUPWINDOW_LISTVIEW = 90001;
	public static final int OPEN_DRAGLAYOUT = 0;

	// 网络交互完毕
	public static final int RESPONSE_FINISHED = 0;

	//
	public static final int UPDATE_LISTEN = 1001;
	
	
	public static Map<Integer, Integer> coverToPreshow = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> preshowToCover = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> coverToView = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> viewToPreshow = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> viewToColor = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> preshowToMain = new HashMap<Integer, Integer>();
	static {
		/**
		 * 为preshowToCover添加键值对
		 */
		preshowToCover
				.put(R.drawable.default_preshow, R.drawable.default_cover);
		preshowToCover.put(R.drawable.zly_preshow, R.drawable.zly_cover);
		preshowToCover.put(R.drawable.cyx_preshow, R.drawable.cyx_cover);
		preshowToCover.put(R.drawable.ldh_preshow, R.drawable.ldh_cover);
		preshowToCover.put(R.drawable.xs_preshow, R.drawable.xs_cover);
		preshowToCover.put(R.drawable.deer_preshow, R.drawable.deer_cover);
		preshowToCover.put(R.drawable.flower_preshow, R.drawable.flower_cover);
		preshowToCover.put(R.drawable.sightview_preshow,
				R.drawable.sightview_cover);

		/**
		 * 为coverToPreshow添加键值对
		 */
		coverToPreshow
				.put(R.drawable.default_cover, R.drawable.default_preshow);
		coverToPreshow.put(R.drawable.zly_cover, R.drawable.zly_preshow);
		coverToPreshow.put(R.drawable.cyx_cover, R.drawable.cyx_preshow);
		coverToPreshow.put(R.drawable.ldh_cover, R.drawable.ldh_preshow);
		coverToPreshow.put(R.drawable.xs_cover, R.drawable.xs_preshow);
		coverToPreshow.put(R.drawable.deer_cover, R.drawable.deer_preshow);
		coverToPreshow.put(R.drawable.flower_cover, R.drawable.flower_preshow);
		coverToPreshow.put(R.drawable.sightview_cover,
				R.drawable.sightview_preshow);

		/**
		 * 为coverToView添加键值对
		 */
		coverToView.put(R.drawable.default_cover, R.drawable.mydefault);
		coverToView.put(R.drawable.zly_cover, R.drawable.zly);
		coverToView.put(R.drawable.cyx_cover, R.drawable.cyx);
		coverToView.put(R.drawable.ldh_cover, R.drawable.ldh);
		coverToView.put(R.drawable.xs_cover, R.drawable.xs);
		coverToView.put(R.drawable.deer_cover, R.drawable.deer);
		coverToView.put(R.drawable.flower_cover, R.drawable.flower);
		coverToView.put(R.drawable.sightview_cover, R.drawable.sightview);

		/**
		 * 为viewToPreshow添加键值对
		 */
		viewToPreshow.put(R.drawable.mydefault, R.drawable.default_preshow);
		viewToPreshow.put(R.drawable.zly, R.drawable.zly_preshow);
		viewToPreshow.put(R.drawable.cyx, R.drawable.cyx_preshow);
		viewToPreshow.put(R.drawable.ldh, R.drawable.ldh_preshow);
		viewToPreshow.put(R.drawable.xs, R.drawable.xs_preshow);
		viewToPreshow.put(R.drawable.deer, R.drawable.deer_preshow);
		viewToPreshow.put(R.drawable.flower, R.drawable.flower_preshow);
		viewToPreshow.put(R.drawable.sightview, R.drawable.sightview_preshow);

		/**
		 * 为viewToColor添加键值对
		 */
		viewToColor.put(R.drawable.mydefault, 0xFF057DD3);
		viewToColor.put(R.drawable.zly, 0xFFED97B4);
		viewToColor.put(R.drawable.cyx, 0xFF000000);
		viewToColor.put(R.drawable.ldh, 0xFF000852);
		viewToColor.put(R.drawable.xs, 0xFFC64510);
		viewToColor.put(R.drawable.deer, 0xFF95FE63);
		viewToColor.put(R.drawable.flower, 0xFF7C8F01);
		viewToColor.put(R.drawable.sightview, 0xFF61F4FC);

		/**
		 * 为preshowToMain添加键值对
		 */
		preshowToMain.put(R.drawable.default_preshow, R.drawable.enter);// 默认的匹配
		preshowToMain.put(R.drawable.zly_preshow, R.drawable.zly_main);// 张靓颖模式
		preshowToMain.put(R.drawable.cyx_preshow, R.drawable.cyx_main);// 陈奕迅模式
		preshowToMain.put(R.drawable.ldh_preshow, R.drawable.ldh_main);// 刘德华模式
		preshowToMain.put(R.drawable.xs_preshow, R.drawable.xs_main);// 许嵩模式
	}

	/**
	 * 从资源文件中获取字符串数组资源
	 * 
	 * @param context
	 *            上下文对象
	 * @param resId
	 *            资源的name/id
	 * @return 返回查找成功的数组
	 */
	public static String[] getStrArrsFromResources(Context context, int resId) {
		String[] arrs = context.getResources().getStringArray(resId);
		return arrs;
	}

	/**
	 * 获取多媒体资源的ID数组
	 * 
	 * @param context
	 *            上下文对象
	 * @param resId
	 *            资源数组id
	 * @return 资源的id数组
	 */
	public static int[] getResouceIdsFromRescource(Context context, int resId) {
		TypedArray typedArray = context.getResources().obtainTypedArray(resId);
		int len = typedArray.length();
		int[] resIds = new int[len];
		for (int i = 0; i < len; i++) {
			resIds[i] = typedArray.getResourceId(i, 0);
		}
		typedArray.recycle();
		return resIds;
	}

	/**
	 * 在View对象上画圆,并且在圆圈里面画勾
	 * 
	 * @param view
	 */
	public static void drawCircle(View view) {
		Paint paint = new Paint();
		int left = view.getLeft();
		int width = view.getWidth();
		int top = view.getTop();
		int height = view.getHeight();
		paint.setAlpha(0x50000000);
		Canvas canvas = new Canvas();
		canvas.drawRect(left, top, left + width, top + height, paint);// 画半透明底
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(10);
		int cx = left + width / 2;
		int cy = top + height / 2;
		System.out.println(cx + ":" + cy);
		canvas.drawCircle(cx, cy, 100, paint);// 画圆
		Path path = new Path();
		// x:-10，y:-3
		path.moveTo(cx - 10, cy - 3);
		// x:0;y:+7
		path.lineTo(cx, cy + 7);
		// x:+10;y:-8
		path.lineTo(cx + 10, cy - 8);
		canvas.drawPath(path, paint);
	}

	private static Toast mToast;

	/**
	 * 单例toast显示
	 * 
	 * @param context
	 *            上下文对象
	 * @param msg
	 *            显示的文字
	 */
	public static void showToast(Context context, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(context, "", 0);
		}
		mToast.setText(msg);
		mToast.show();
	}
}
