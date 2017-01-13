package edu.ecjtu.activities;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;

import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.playactivity.MyPageAdapter;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.fragments.playacitivy.Musicdb;
import edu.ecjtu.domain.fragments.playacitivy.MyMusic;
import edu.ecjtu.domain.services.PlayService;
import edu.ecjtu.domain.services.PlayService.OnPlayListener;
import edu.ecjtu.domain.viewgroups.DragLayout;
import edu.ecjtu.domain.viewgroups.DragLayout.OnStatusChangeListener;
import edu.ecjtu.domain.viewgroups.DragLayout.Status;
import edu.ecjtu.domain.viewgroups.MyLinearLayout;
import edu.ecjtu.domain.views.MyProgressBar;
import edu.ecjtu.domain.views.MyStickView;
import edu.ecjtu.domain.vo.interfaces.ActivityInterface;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.BottomPlay;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.domain.vo.objects.User;
import edu.ecjtu.musicplayer.R;

public class PlayerActivity extends FragmentActivity implements
		OnClickListener, ActivityInterface {

	private List<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager pager;
	private TextView tv_my, tv_mdb, tv_left_username;
	private MyStickView iv_move;
	private List<TextView> textViews = new ArrayList<TextView>();
	private DisplayMetrics dm = new DisplayMetrics();
	private Bitmap bm_iv_move;
	private ImageView iv_player_icon, iv_left_user_icon, iv_choosehead;
	private DragLayout draglayout;
	private RelativeLayout left_drawer, rl_user;
	private Bitmap headbm;
	private User user = null;
	private MyLinearLayout mll;
	private Intent serviceIntent;
	private final int SERVICE_BIND_FINISH = 0;
	private ImageView iv_head;
	private Musicdb musicdb;
	private MyMusic myMusic;

	// ////////////////////////////////////
	private ImageView iv_my_start, iv_my_previous, iv_my_next;
	private TextView tv_my_songname, tv_my_artistname;
	private Song currentSong;
	private PlayInterface playBinder;
	private BinderObject binderObject;
	private MediaPlayer player;
	private BottomPlay bottomPlay;
	private MyProgressBar mpb_musicplay;
	private RelativeLayout rl_play;
	private OnActivityDestroyListener onActivityDestroyListener;
	private boolean isAcitivityPaused = false;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SERVICE_BIND_FINISH:
				initData();
				inflateViews();
				registerListeners();
				break;
			case Utils.UPDATE_IMAGE:// ���´�ͼ�ֲ�
				musicdb.updatePage();
				handler.sendEmptyMessageDelayed(Utils.UPDATE_IMAGE, 3000);
				break;
			case 4000:// ���ڲ���,ˢ���Զ��������
				mpb_musicplay.setCurrProgress(player.getCurrentPosition());
				mpb_musicplay.invalidate();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���崰�������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_player);
		initViews();
		initServices();
	}

	/**
	 * Ϊ�������ʼ��
	 */
	private void initViews() {
		iv_move = (MyStickView) findViewById(R.id.iv_move);
		pager = (ViewPager) findViewById(R.id.pager);
		tv_my = (TextView) findViewById(R.id.tv_my);
		tv_mdb = (TextView) findViewById(R.id.tv_mdb);
		iv_player_icon = (ImageView) findViewById(R.id.iv_player_icon);
		draglayout = (DragLayout) findViewById(R.id.drawerlayout);
		left_drawer = (RelativeLayout) findViewById(R.id.left_drawer);
		rl_user = (RelativeLayout) findViewById(R.id.rl_user);
		iv_left_user_icon = (ImageView) findViewById(R.id.iv_left_user_icon);
		tv_left_username = (TextView) findViewById(R.id.tv_left_username);
		iv_choosehead = (ImageView) findViewById(R.id.iv_choosehead);
		mll = (MyLinearLayout) findViewById(R.id.mll);
		iv_head = (ImageView) findViewById(R.id.iv_player_icon);
		// �ײ���ʼ��
		this.rl_play = (RelativeLayout) this
				.findViewById(R.id.rl_playactivity_music_play);
		iv_my_start = (ImageView) findViewById(R.id.iv_my_start);
		iv_my_previous = (ImageView) findViewById(R.id.iv_my_previous);
		iv_my_next = (ImageView) findViewById(R.id.iv_my_next);
		tv_my_songname = (TextView) findViewById(R.id.tv_my_songname);
		tv_my_artistname = (TextView) findViewById(R.id.tv_my_artistname);
		this.mpb_musicplay = (MyProgressBar) this
				.findViewById(R.id.mpb_musicplay);
	}

	/**
	 * ��������
	 */
	private void initServices() {
		serviceIntent = new Intent(PlayerActivity.this, PlayService.class);
		startService(serviceIntent);
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
		}

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder binder) {
			handler.sendEmptyMessage(SERVICE_BIND_FINISH);
		}
	};

	private void initData() {
		mll.setDragLayout(draglayout);
		initBinderData();
	}

	private void inflateViews() {
		initViewPager();
		initTextViews();
		initMoveView();
		updateBottomViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isAcitivityPaused) {// ���״ν����ʱ�����ע�Ქ�ż�����
			initBinderData();
			updateBottomViews();
			playRegister();
		}
		isAcitivityPaused = false;
		if (draglayout.getCurrStatus() != Status.CLOSE) {
			draglayout.close();
		}
		initUserInfo();
	}

	/**
	 * ���µײ�����
	 */
	private void updateBottomViews() {
		mpb_musicplay.setMaxWidth(dm.widthPixels);
		bottomPlay.updateBottomLayoutViews(binderObject.getCurrentSong(),
				binderObject.getPlayer());
		if (currentSong != null) {
			mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
					.getDuration()));
		}
		mpb_musicplay.setCurrProgress(0);
		if (player.getCurrentPosition() < player.getDuration()) {
			bottomPlay.infalteMusicProgressBar();
		}
	}

	private void initBinderData() {
		this.binderObject = PlayUtils.binderObject;
		if (playBinder == null && player == null) {
			playBinder = binderObject.getPlayBinder();
			player = binderObject.getPlayer();
		}
		currentSong = binderObject.getCurrentSong();
		bottomPlay = new BottomPlay(this, iv_my_start, iv_my_previous,
				iv_my_next, tv_my_songname, tv_my_artistname, mpb_musicplay,
				handler, binderObject);
	}

	/**
	 * ���ViewPager
	 */
	private void initViewPager() {
		musicdb = new Musicdb();
		myMusic = new MyMusic();
		fragments.add(myMusic);
		fragments.add(musicdb);

		pager.setAdapter(new MyPageAdapter(getSupportFragmentManager(),
				fragments));
	}

	/**
	 * ��ʼ��textviews����
	 */
	private void initTextViews() {
		textViews.add(tv_my);
		textViews.add(tv_mdb);
		textViews.get(0).setTextColor(0xFF00B4FF);
	}

	/**
	 * ��ʼ��iv_move
	 */
	private void initMoveView() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		bm_iv_move = BitmapFactory
				.decodeResource(getResources(), R.drawable.a2);
		iv_move.setLeftX(20d);
		iv_move.setWidth(dm.widthPixels);
		iv_move.setPageCount(2);
		iv_move.invalidate();
	}

	/**
	 * ��ʼ�� ���ֵ���¼�
	 * 
	 * @param v
	 */
	public void layoutClick(View v) {
		switch (v.getId()) {
		case R.id.mylayout:
			pager.setCurrentItem(0);
			textViews.get(0).setTextColor(0xFF00B4FF);
			textViews.get(1).setTextColor(Color.WHITE);
			break;

		case R.id.mdblayout:
			pager.setCurrentItem(1);
			textViews.get(1).setTextColor(0xFF00B4FF);
			textViews.get(0).setTextColor(Color.WHITE);
			break;
		default:
			break;
		}
	}

	/**
	 * ע������¼�
	 */
	private void registerListeners() {
		iv_player_icon.setOnClickListener(this);
		rl_user.setOnClickListener(this);
		iv_choosehead.setOnClickListener(this);
		this.iv_my_start.setOnClickListener(this);
		this.iv_my_previous.setOnClickListener(this);
		this.iv_my_next.setOnClickListener(this);
		this.rl_play.setOnClickListener(this);
		draglayout.setOnStatusChangeListener(new OnStatusChangeListener() {

			@Override
			public void OnDrag(float percent) {
				iv_head.setAlpha(1 - percent);
			}

			@Override
			public void OnOpen(float percent) {

			}

			@Override
			public void OnClose(float percent) {
				ObjectAnimator oa = ObjectAnimator.ofFloat(iv_head,
						"translationX", 15);
				oa.setDuration(1500);
				oa.setInterpolator(new CycleInterpolator(10));
				oa.start();
			}

		});
		left_drawer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		pager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				iv_move.onTouchEvent(event);
				return false;
			}
		});
		playRegister();
		pager.setOnPageChangeListener(new MyPageChangeLinstener());
	}

	/**
	 * ע�Ქ�ż���
	 */
	private void playRegister() {
		this.playBinder.setOnPlayListener(new OnPlayListener() {

			@Override
			public void OnPlay() {
				if (!isAcitivityPaused) {// ��������Ž����˳�ʱ���������߳�
					initBinderData();
					mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
							.getDuration()));
					bottomPlay.updateBottomLayoutViews(
							binderObject.getCurrentSong(),
							binderObject.getPlayer());
					bottomPlay.infalteMusicProgressBar();
					myMusic.invalidateRecentPlay();
				}
			}
		});
	}

	class MyPageChangeLinstener implements OnPageChangeListener {

		/**
		 * arg0 ==1��ʱ���ʾ���ڻ����� arg0==2��ʱ���ʾ��������ˣ� arg0==0��ʱ���ʾʲô��û��������ͣ���ǡ�
		 */
		@Override
		public void onPageScrollStateChanged(int state) {
		}

		/**
		 * ��ʾ��ǰһ��ҳ�滬������һ��ҳ���ʱ����ǰһ��ҳ�滬��ǰ���õķ����� arg0 :��ǰҳ�棬������������ҳ��
		 * arg1:��ǰҳ��ƫ�Ƶİٷֱ� arg2:��ǰҳ��ƫ�Ƶ�����λ��
		 */
		@Override
		public void onPageScrolled(int postion, float percent, int arg2) {

		}

		/**
		 * ��ǰѡ�� ��ҳ�� ���¼�������ҳ����ת��ϵ�ʱ����õġ�
		 */
		@Override
		public void onPageSelected(int position) {
			if (position == 0) {
				iv_move.setLeftX(20d);
			} else {
				iv_move.setLeftX(dm.widthPixels - bm_iv_move.getWidth() - 20d);
			}
			iv_move.setPosition(position);
			iv_move.invalidate();
			for (int i = 0; i < textViews.size(); i++) {
				if (position == i) {
					textViews.get(i).setTextColor(0xFF0084FF);
				} else {
					textViews.get(i).setTextColor(Color.WHITE);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_player_icon:
			draglayout.open();
			break;
		case R.id.rl_user:
			Intent intent = new Intent(PlayerActivity.this,
					LoginOrRegisterActivity.class);
			if (user != null) {
				intent.putExtra(MusicProvider.TABLE_USER_USERNAME,
						user.getUsername());
			}
			startActivityForResult(intent, Utils.OPEN_DRAGLAYOUT);
			break;
		case R.id.iv_choosehead:
			if (user != null) {
				Intent headIntent = new Intent();
				headIntent.setAction(Intent.ACTION_PICK);
				headIntent.setType("image/*");
				startActivityForResult(headIntent, Utils.PHOTO_REQUEST_GALLERY);
			} else {
				Toast.makeText(getApplicationContext(), "����δ��¼,���ȵ�¼", 1).show();
			}
			break;
		case R.id.iv_my_start:// ��ʼ/��ͣ����
			if (!player.isPlaying()) {
				bottomPlay.startPlay();
			} else {
				bottomPlay.pausePlay();
			}
			break;
		case R.id.iv_my_previous:// ��һ�ײ���
			bottomPlay.previousPlay();
			iv_my_start.setImageResource(R.drawable.pause);
			break;
		case R.id.iv_my_next:// ��һ�ײ���
			bottomPlay.nextPlay();
			iv_my_start.setImageResource(R.drawable.pause);
			break;
		case R.id.rl_playactivity_music_play:
			Intent playIntent = new Intent(this, MusicPlayActivity.class);
			startActivity(playIntent);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Utils.PHOTO_REQUEST_GALLERY:// ����ͼ���
			if (data != null) {
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		case Utils.PHOTO_REQUEST_CUT:// �ü�ͼƬ��
			switch (resultCode) {
			case Activity.RESULT_OK:
				if (data != null) {
					headbm = data.getParcelableExtra("data");// ��ȡ�ü���Ķ���
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					headbm.compress(CompressFormat.JPEG, 100, baos);
					iv_left_user_icon.setImageBitmap(headbm);
					iv_player_icon.setImageBitmap(headbm);
					ContentValues values = new ContentValues();
					values.put(MusicProvider.TABLE_USER_HEADPHOTO,
							baos.toByteArray());
					int count = getContentResolver().update(
							MusicProvider.USER_URI, values,
							MusicProvider.TABLE_USER_USERNAME + "=?",
							new String[] { user.getUsername() });
					if (count > 0) {
						Toast.makeText(getApplicationContext(), "ͷ������ɹ�", 0)
								.show();
					}
				}
				break;
			case Activity.RESULT_CANCELED:
				break;
			}
			if (data != null) {
			}
			break;
		case Utils.OPEN_DRAGLAYOUT:
			if (draglayout.getCurrStatus() != Status.CLOSE) {
				draglayout.close();
			}
			break;
		}
	}

	/**
	 * �ü�ͼƬ
	 * 
	 * @param uri
	 */
	private void crop(Uri uri) {
		// �����ü�Intent
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		// ȷ���ü�����: width:height
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.user_login_icon);
		int width = bm.getWidth();
		int height = bm.getHeight();
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);
		// ȷ�����ͼƬ�Ĵ�С
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		// ȷ��ͼƬ�������ʽ
		intent.putExtra("outputFormat", "JPEG");
		// ȷ��ͼƬ������ʶ��
		intent.putExtra("noFaceDetection", false);
		// Ҫ�������ֵ
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Utils.PHOTO_REQUEST_CUT);
	}

	/**
	 * ��ʼ���û���Ϣ
	 */
	private void initUserInfo() {
		Cursor cursor = getContentResolver().query(MusicProvider.USER_URI,
				null, MusicProvider.TABLE_USER_ISLOGIN + "=?",
				new String[] { "1" }, null);
		user = null;
		if (cursor.moveToNext()) {
			user = new User();
			user.setUsername(cursor.getString(cursor
					.getColumnIndex(MusicProvider.TABLE_USER_USERNAME)));
			user.setHeadphoto(cursor.getBlob(cursor
					.getColumnIndex(MusicProvider.TABLE_USER_HEADPHOTO)));
		}
		if (user != null) {
			tv_left_username.setText(user.getUsername());
			byte[] bytes = user.getHeadphoto();
			if (bytes != null) {
				Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0,
						bytes.length);
				iv_left_user_icon.setImageBitmap(bm);
				iv_player_icon.setImageBitmap(bm);
			} else {
				iv_player_icon.setImageResource(R.drawable.user_login_icon);// ������ʾ����iv��ͼ��
				iv_left_user_icon.setImageResource(R.drawable.user_login_icon);// ��������iv��ͼ��
			}
		} else {
			tv_left_username.setText("��¼/ע��");
			iv_player_icon.setImageResource(R.drawable.user_login_icon);// ������ʾ����iv��ͼ��
			iv_left_user_icon.setImageResource(R.drawable.user_login_icon);// ��������iv��ͼ��
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isAcitivityPaused = true;
		if (onActivityDestroyListener != null) {
			onActivityDestroyListener.onActivityDestroy();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * ��������
	 */
	protected void onDestroy() {
		super.onDestroy();
		unbindService(conn);
	}

	/**
	 * ��ȡHandler,��Fragment����
	 * 
	 * @return
	 */
	public Handler getHandler() {
		return this.handler;
	}

	/**
	 * ��ȡ��ǰViewPager��λ��,��draglayout����
	 * 
	 * @return ��ǰViewPager��λ��
	 */
	public int getPagerPosition() {
		return pager.getCurrentItem();
	}

	@Override
	public void setOnActivityDestroyListener(OnActivityDestroyListener listener) {
		this.onActivityDestroyListener = listener;

	}
}
