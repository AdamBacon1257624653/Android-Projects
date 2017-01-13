package edu.ecjtu.activities;

import android.app.Activity;
import android.view.View.OnClickListener;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.playactivity.GalleryAdappter;
import edu.ecjtu.domain.views.MyImageView;
import edu.ecjtu.domain.vo.objects.Item;
import edu.ecjtu.musicplayer.R;

public class CustomBgAcitivty extends Activity implements OnClickListener {
	private ImageView iv_preshow;
	private MyImageView iv_pic;
	private TextView tv_picname;
	private Gallery gallery_my;
	private String[] imageNames;
	private List<View> viewList = new ArrayList<View>();
	private int[] images;
	private RelativeLayout rl_bg;
	private LinearLayout rl_back;
	private Bitmap downBitmap;
	private Bitmap upBitmap = null;
	private boolean isFromPhone;
	private int titlePixel;// 类标题栏的ARGB
	private String titleColor = "titleARGB";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custombackground);

		initViews();
		initArrs();
		initGallary();
		inflateViews();
		registerListeners();
	}

	/**
	 * 实例化组件
	 */
	private void initViews() {
		iv_preshow = (ImageView) findViewById(R.id.iv_preshow);
		gallery_my = (Gallery) findViewById(R.id.gallery_my);
		rl_back = (LinearLayout) findViewById(R.id.rl_back);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
	}

	/**
	 * 实例化数组
	 */
	private void initArrs() {
		imageNames = Utils.getStrArrsFromResources(this, R.array.cover_names);
		images = Utils.getResouceIdsFromRescource(this, R.array.cover_images);
	}

	/**
	 * 实例化画廊
	 */
	private void initGallary() {
		for (int i = 0; i < images.length; i++) {
			// 实例化画廊里的单个View
			View view = View.inflate(this, R.layout.fragment_gallery, null);
			iv_pic = (MyImageView) view.findViewById(R.id.iv_pic);
			tv_picname = (TextView) view.findViewById(R.id.tv_picname);
			iv_pic.setImageResource(images[i]);
			tv_picname.setText(imageNames[i]);
			// 保留view的图片信息
			view.setTag(images[i]);
			viewList.add(view);
		}
		gallery_my.setAdapter(new GalleryAdappter(viewList));
	}

	/**
	 * 为一些view做填充
	 */
	private void inflateViews() {
		inflateImageViews();
	}

	/**
	 * 界面返回为iv_preshow填充图片
	 */
	private void inflateImageViews() {
		Intent intentBg = getIntent();
		if (intentBg != null) {
			int coverResId = intentBg.getIntExtra(Utils.BM_COVER, -1);// 获取Cover图片的资源ID
			isFromPhone = intentBg.getBooleanExtra(Utils.FROM_PHONE, false);
			if (coverResId != -1) {
				int preshowResId = Utils.coverToPreshow.get(coverResId);// 获取iv_preshow图片的资源ID
				int viewResId = Utils.coverToView.get(coverResId);// 获取view图片的资源ID
				iv_preshow.setImageResource(preshowResId);
				iv_preshow.setTag(preshowResId);
				Item item = ((GalleryAdappter) gallery_my.getAdapter())
						.getItemByImageId(viewResId);
				View view = item.getView();
				MyImageView miv = (MyImageView) view.findViewById(R.id.iv_pic);
				miv.setClick(true);
				miv.invalidate();// 刷新miv界面
				gallery_my.setSelection(item.getPosition());
				rl_bg.setBackgroundColor(Utils.viewToColor.get(view.getTag()));// 设置类标题栏背景色
			} else if (isFromPhone) {
				iv_preshow.setImageBitmap(BitmapFactory.decodeFile(new File(
						getFilesDir(), "result.jpg").getAbsolutePath()));
				SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
				int resId = sp.getInt(titleColor, 0);// 获取类标题栏的ARGB
				rl_bg.setBackgroundColor(resId);
			}
		}
	}

	/**
	 * 注册监听事件
	 */
	private void registerListeners() {
		rl_back.setOnClickListener(this);
		gallery_my.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Integer viewResId = (Integer) view.getTag();// 获取view中的图片ID资源
				if (viewResId != R.drawable.camera) {// 如果选择的不是打开相册
					Integer preshowResId = Utils.viewToPreshow.get(viewResId);// 获取iv_preshow中的图片ID资源
					iv_preshow.setImageResource(preshowResId);
					iv_preshow.setTag(preshowResId);
					for (View v2 : viewList) {
						MyImageView mImageView = (MyImageView) v2
								.findViewById(R.id.iv_pic);
						if (view.equals(v2)) {
							mImageView.setClick(true);
						} else {
							mImageView.setClick(false);
						}
						mImageView.invalidate();// 刷新mImageView
					}
					rl_bg.setBackgroundColor(Utils.viewToColor.get(viewResId));// 设置类标题栏的背景颜色
					isFromPhone = false;
					saveImageInfo();
					if (preshowResId == R.drawable.zly_preshow
							|| preshowResId == R.drawable.cyx_preshow
							|| preshowResId == R.drawable.ldh_preshow
							|| preshowResId == R.drawable.xs_preshow) {
						Toast.makeText(getApplicationContext(), "明星模式已成功开启", 0)
								.show();
					}
				} else {// 打开相册
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, Utils.PHOTO_REQUEST_GALLERY);// 打开相册
					rl_bg.setBackgroundColor(titlePixel);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Utils.PHOTO_REQUEST_GALLERY:// 相册返回
			if (data != null) {
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		case Utils.PHOTO_REQUEST_CUT:
			switch (resultCode) {
			case Activity.RESULT_OK:
				upBitmap = data.getParcelableExtra("data");
				titlePixel = upBitmap.getPixel(upBitmap.getWidth() / 2,
						upBitmap.getHeight() / 2);// 獲取图片的像素ARGB信息
				Bitmap resultBitmap = pasteBitmap(upBitmap, downBitmap);// 图片拼接
				iv_preshow.setImageBitmap(resultBitmap);// 图片裁剪
				rl_bg.setBackgroundColor(titlePixel);
				saveBitmap(upBitmap, "up");// 保存cover图片
				saveBitmap(resultBitmap, "result");// 保存result图片
				saveBackgroundColor();
				isFromPhone = true;
				saveImageInfo();
				break;
			default:
				break;
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 圖片的剪切
	 * 
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 创建裁剪图片Intent
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		// 确定裁剪框的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		downBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.modeldown);
		int width = downBitmap.getWidth();
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", 250);

		intent.putExtra("outputFormat", "JPEG");// 图片的输出格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// 要求带返回值
		startActivityForResult(intent, Utils.PHOTO_REQUEST_CUT);
	}

	/**
	 * 圖片的拼接
	 * 
	 * @param upBitmap
	 *            圖片的上部分
	 * @param downBitmap
	 *            圖片的下部分
	 * @return 返回圖片拼接后的結果
	 */
	private Bitmap pasteBitmap(Bitmap upBitmap, Bitmap downBitmap) {
		int height = upBitmap.getHeight() + downBitmap.getHeight();// 确定图片的高度
		Bitmap resultBitmap = Bitmap.createBitmap(upBitmap.getWidth(), height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(resultBitmap);// 确定好一块画布
		canvas.drawBitmap(upBitmap, 0, 0, null);// 从画布的（0，0）开始绘制图片
		canvas.drawBitmap(downBitmap, 0, upBitmap.getHeight(), null);// 从upBitmap开始绘制图片
		return resultBitmap;
	}

	/**
	 * 将bitmap保存为格式为JPG的图片
	 * 
	 * @param bitmap
	 *            将要保存的bitmap
	 * @param imageName
	 *            保存的图片名字
	 */
	private void saveBitmap(Bitmap bitmap, String imageName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(getFilesDir(), imageName
					+ ".jpg"));
			bitmap.compress(CompressFormat.JPEG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存类标题栏的背景颜色
	 */
	private void saveBackgroundColor() {
		Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
		editor.putInt(titleColor, titlePixel);
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			back();
			break;
		}
	}

	/**
	 * 返回键的实现
	 */
	@Override
	public void onBackPressed() {
		back();
	}

	/**
	 * 返回方法
	 */
	private void back() {
		Intent backIntent = new Intent();
		if (isFromPhone) {// 如果是從手機中裁剪出來的圖片
			setResult(Utils.BG_FROMPHONE_RESULTCODE, backIntent);
		} else {
			setResult(Utils.BG_RESULTCODE, backIntent);
		}
		finish();
	}

	/**
	 * 保存图片的一些信息
	 */
	private void saveImageInfo() {
		FileOutputStream fos = null;
		try {
			File infoFile = new File(getFilesDir().getName());
			infoFile.deleteOnExit();// 先清除以前存的数据
			fos = openFileOutput(getFilesDir().getName(), Context.MODE_PRIVATE);
			fos.write((Utils.FROM_PHONE + ":" + isFromPhone + "\r\n")
					.getBytes("UTF-8"));
			fos.write((Utils.PRESHOW_RESID + ":" + iv_preshow.getTag())
					.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
