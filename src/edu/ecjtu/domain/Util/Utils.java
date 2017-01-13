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
	// �����DƬ�޸�Ո����
	public static final int BG_REQUESTCODE = 0;
	public static final int BG_FROMPHONE_REQUESTCODE = 1;
	// ����ͼƬ�ķ��ؽ����
	public static final int BG_RESULTCODE = 0;
	public static final int BG_FROMPHONE_RESULTCODE = 1;
	// ������л�ȡ��Ƭ��������
	public static final int PHOTO_REQUEST_GALLERY = 1000;
	// ����Ƭ�Ĳü�������������
	public static final int PHOTO_REQUEST_CUT = 1001;
	// ���������ֻ�ͼƬkeyֵ���Ա��ȡ��ͼƬ��RGB��Ϣ
	public static final int PHONEIMAGE_KEY = 100002;
	// ����Intent�Ĵ��ݵ�key
	public static final String BM_COVER = "bm_cover";
	// ����intent�Ĵ���keyֵ
	public static final String FROM_PHONE = "from_phone";
	// ����preshowresid��keyֵ
	public static final String PRESHOW_RESID = "preshowResId";
	// ������ɫ
	public static final int themeColor = 0xFF00B4FF;
	// ���ݼ������
	public static final int SONG_LOADED = 1;
	public static final int SONGFRAGMENT_CREATED = 2;
	public static final int SONGFRAGMENT_RESUMED = 3;
	public static final int FOLDERFRAGMENT_RESUMED = 4;
	public static final int SINGERFRAGMENT_CREATED = 5;
	public static final int SINGERFRAGMENT_RESUMED = 6;
	public static final int ALBUMFRAGMENT_CREATED = 7;
	public static final int ALBUMFRAGMENT_RESUMED = 8;
	// ִ�����ݿ�Ĳ������
	public static final int ALBUM_INSERT = 10;
	public static final int SONG_INSERT = 11;
	public static final int ARTIST_INSERT = 12;
	public static final int FILEPATH_INSERT = 13;
	// ɨ���ļ�
	public static final int SCAN_FILEPATH = 0;// �����ļ����Ƶ�message.what
	public static final int SONG_SCANNED = 1;// ɨ�赽һ���ļ���message.what
	public static final int SCAN_FINISHED = 2;// �ļ�ɨ����ϵ�message.what
	public static final int SCANNING = 3;// �ļ�����ɨ���message.what
	// ˢ�µײ��Ľ�����
	public static final int REFRESH_BOTTOM = 4000;
	// ͼƬ�ֲ���handler.what
	public static final int UPDATE_IMAGE = 5000;

	public static final String TAG = "Adam";

	// ����popupwindow��listview��id
	public static final int POPUPWINDOW_LISTVIEW = 90001;
	public static final int OPEN_DRAGLAYOUT = 0;

	// ���罻�����
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
		 * ΪpreshowToCover��Ӽ�ֵ��
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
		 * ΪcoverToPreshow��Ӽ�ֵ��
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
		 * ΪcoverToView��Ӽ�ֵ��
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
		 * ΪviewToPreshow��Ӽ�ֵ��
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
		 * ΪviewToColor��Ӽ�ֵ��
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
		 * ΪpreshowToMain��Ӽ�ֵ��
		 */
		preshowToMain.put(R.drawable.default_preshow, R.drawable.enter);// Ĭ�ϵ�ƥ��
		preshowToMain.put(R.drawable.zly_preshow, R.drawable.zly_main);// ����ӱģʽ
		preshowToMain.put(R.drawable.cyx_preshow, R.drawable.cyx_main);// ����Ѹģʽ
		preshowToMain.put(R.drawable.ldh_preshow, R.drawable.ldh_main);// ���»�ģʽ
		preshowToMain.put(R.drawable.xs_preshow, R.drawable.xs_main);// ����ģʽ
	}

	/**
	 * ����Դ�ļ��л�ȡ�ַ���������Դ
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param resId
	 *            ��Դ��name/id
	 * @return ���ز��ҳɹ�������
	 */
	public static String[] getStrArrsFromResources(Context context, int resId) {
		String[] arrs = context.getResources().getStringArray(resId);
		return arrs;
	}

	/**
	 * ��ȡ��ý����Դ��ID����
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param resId
	 *            ��Դ����id
	 * @return ��Դ��id����
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
	 * ��View�����ϻ�Բ,������ԲȦ���滭��
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
		canvas.drawRect(left, top, left + width, top + height, paint);// ����͸����
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(10);
		int cx = left + width / 2;
		int cy = top + height / 2;
		System.out.println(cx + ":" + cy);
		canvas.drawCircle(cx, cy, 100, paint);// ��Բ
		Path path = new Path();
		// x:-10��y:-3
		path.moveTo(cx - 10, cy - 3);
		// x:0;y:+7
		path.lineTo(cx, cy + 7);
		// x:+10;y:-8
		path.lineTo(cx + 10, cy - 8);
		canvas.drawPath(path, paint);
	}

	private static Toast mToast;

	/**
	 * ����toast��ʾ
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param msg
	 *            ��ʾ������
	 */
	public static void showToast(Context context, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(context, "", 0);
		}
		mToast.setText(msg);
		mToast.show();
	}
}
