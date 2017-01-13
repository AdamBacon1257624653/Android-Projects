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
	private int titlePixel;// ���������ARGB
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
	 * ʵ�������
	 */
	private void initViews() {
		iv_preshow = (ImageView) findViewById(R.id.iv_preshow);
		gallery_my = (Gallery) findViewById(R.id.gallery_my);
		rl_back = (LinearLayout) findViewById(R.id.rl_back);
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
	}

	/**
	 * ʵ��������
	 */
	private void initArrs() {
		imageNames = Utils.getStrArrsFromResources(this, R.array.cover_names);
		images = Utils.getResouceIdsFromRescource(this, R.array.cover_images);
	}

	/**
	 * ʵ��������
	 */
	private void initGallary() {
		for (int i = 0; i < images.length; i++) {
			// ʵ����������ĵ���View
			View view = View.inflate(this, R.layout.fragment_gallery, null);
			iv_pic = (MyImageView) view.findViewById(R.id.iv_pic);
			tv_picname = (TextView) view.findViewById(R.id.tv_picname);
			iv_pic.setImageResource(images[i]);
			tv_picname.setText(imageNames[i]);
			// ����view��ͼƬ��Ϣ
			view.setTag(images[i]);
			viewList.add(view);
		}
		gallery_my.setAdapter(new GalleryAdappter(viewList));
	}

	/**
	 * ΪһЩview�����
	 */
	private void inflateViews() {
		inflateImageViews();
	}

	/**
	 * ���淵��Ϊiv_preshow���ͼƬ
	 */
	private void inflateImageViews() {
		Intent intentBg = getIntent();
		if (intentBg != null) {
			int coverResId = intentBg.getIntExtra(Utils.BM_COVER, -1);// ��ȡCoverͼƬ����ԴID
			isFromPhone = intentBg.getBooleanExtra(Utils.FROM_PHONE, false);
			if (coverResId != -1) {
				int preshowResId = Utils.coverToPreshow.get(coverResId);// ��ȡiv_preshowͼƬ����ԴID
				int viewResId = Utils.coverToView.get(coverResId);// ��ȡviewͼƬ����ԴID
				iv_preshow.setImageResource(preshowResId);
				iv_preshow.setTag(preshowResId);
				Item item = ((GalleryAdappter) gallery_my.getAdapter())
						.getItemByImageId(viewResId);
				View view = item.getView();
				MyImageView miv = (MyImageView) view.findViewById(R.id.iv_pic);
				miv.setClick(true);
				miv.invalidate();// ˢ��miv����
				gallery_my.setSelection(item.getPosition());
				rl_bg.setBackgroundColor(Utils.viewToColor.get(view.getTag()));// ���������������ɫ
			} else if (isFromPhone) {
				iv_preshow.setImageBitmap(BitmapFactory.decodeFile(new File(
						getFilesDir(), "result.jpg").getAbsolutePath()));
				SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
				int resId = sp.getInt(titleColor, 0);// ��ȡ���������ARGB
				rl_bg.setBackgroundColor(resId);
			}
		}
	}

	/**
	 * ע������¼�
	 */
	private void registerListeners() {
		rl_back.setOnClickListener(this);
		gallery_my.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Integer viewResId = (Integer) view.getTag();// ��ȡview�е�ͼƬID��Դ
				if (viewResId != R.drawable.camera) {// ���ѡ��Ĳ��Ǵ����
					Integer preshowResId = Utils.viewToPreshow.get(viewResId);// ��ȡiv_preshow�е�ͼƬID��Դ
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
						mImageView.invalidate();// ˢ��mImageView
					}
					rl_bg.setBackgroundColor(Utils.viewToColor.get(viewResId));// ������������ı�����ɫ
					isFromPhone = false;
					saveImageInfo();
					if (preshowResId == R.drawable.zly_preshow
							|| preshowResId == R.drawable.cyx_preshow
							|| preshowResId == R.drawable.ldh_preshow
							|| preshowResId == R.drawable.xs_preshow) {
						Toast.makeText(getApplicationContext(), "����ģʽ�ѳɹ�����", 0)
								.show();
					}
				} else {// �����
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_PICK);
					intent.setType("image/*");
					startActivityForResult(intent, Utils.PHOTO_REQUEST_GALLERY);// �����
					rl_bg.setBackgroundColor(titlePixel);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Utils.PHOTO_REQUEST_GALLERY:// ��᷵��
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
						upBitmap.getHeight() / 2);// �@ȡͼƬ������ARGB��Ϣ
				Bitmap resultBitmap = pasteBitmap(upBitmap, downBitmap);// ͼƬƴ��
				iv_preshow.setImageBitmap(resultBitmap);// ͼƬ�ü�
				rl_bg.setBackgroundColor(titlePixel);
				saveBitmap(upBitmap, "up");// ����coverͼƬ
				saveBitmap(resultBitmap, "result");// ����resultͼƬ
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
	 * �DƬ�ļ���
	 * 
	 * @param uri
	 */
	private void crop(Uri uri) {
		// �����ü�ͼƬIntent
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		// ȷ���ü���ı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		downBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.modeldown);
		int width = downBitmap.getWidth();
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", 250);

		intent.putExtra("outputFormat", "JPEG");// ͼƬ�������ʽ
		intent.putExtra("noFaceDetection", true);// ȡ������ʶ��
		intent.putExtra("return-data", true);// Ҫ�������ֵ
		startActivityForResult(intent, Utils.PHOTO_REQUEST_CUT);
	}

	/**
	 * �DƬ��ƴ��
	 * 
	 * @param upBitmap
	 *            �DƬ���ϲ���
	 * @param downBitmap
	 *            �DƬ���²���
	 * @return ���؈DƬƴ�Ӻ�ĽY��
	 */
	private Bitmap pasteBitmap(Bitmap upBitmap, Bitmap downBitmap) {
		int height = upBitmap.getHeight() + downBitmap.getHeight();// ȷ��ͼƬ�ĸ߶�
		Bitmap resultBitmap = Bitmap.createBitmap(upBitmap.getWidth(), height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(resultBitmap);// ȷ����һ�黭��
		canvas.drawBitmap(upBitmap, 0, 0, null);// �ӻ����ģ�0��0����ʼ����ͼƬ
		canvas.drawBitmap(downBitmap, 0, upBitmap.getHeight(), null);// ��upBitmap��ʼ����ͼƬ
		return resultBitmap;
	}

	/**
	 * ��bitmap����Ϊ��ʽΪJPG��ͼƬ
	 * 
	 * @param bitmap
	 *            ��Ҫ�����bitmap
	 * @param imageName
	 *            �����ͼƬ����
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
	 * ������������ı�����ɫ
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
	 * ���ؼ���ʵ��
	 */
	@Override
	public void onBackPressed() {
		back();
	}

	/**
	 * ���ط���
	 */
	private void back() {
		Intent backIntent = new Intent();
		if (isFromPhone) {// ����Ǐ��֙C�вü�����ĈDƬ
			setResult(Utils.BG_FROMPHONE_RESULTCODE, backIntent);
		} else {
			setResult(Utils.BG_RESULTCODE, backIntent);
		}
		finish();
	}

	/**
	 * ����ͼƬ��һЩ��Ϣ
	 */
	private void saveImageInfo() {
		FileOutputStream fos = null;
		try {
			File infoFile = new File(getFilesDir().getName());
			infoFile.deleteOnExit();// �������ǰ�������
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
