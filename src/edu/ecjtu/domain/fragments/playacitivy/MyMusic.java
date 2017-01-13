package edu.ecjtu.domain.fragments.playacitivy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ecjtu.activities.CustomBgAcitivty;
import edu.ecjtu.activities.DownloadedActivity;
import edu.ecjtu.activities.FavoriteActivity;
import edu.ecjtu.activities.LocalActivity;
import edu.ecjtu.activities.RecentPlayActivity;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.playactivity.MyListviewAdapter;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class MyMusic extends Fragment {
	private int[] resIds;
	private String[] arrs;
	private View view;
	private ListView lv_my;
	private ImageView iv_stars;// iv_my_start, iv_my_previous, iv_my_next;
	private Intent intentBg;
	private boolean isFromPhone = false;// ��¼cover�����Ƿ�Ϊ�Զ����
	private Map<String, Object> map = null;
	private RelativeLayout rl_favorite;// rl_play;
	private TextView tv_favorite_amount;
	// tv_my_songname, tv_my_artistname;
	private List<Song> favoriteSongs, mySongs;
	private MyListviewAdapter myListviewAdapter;
//	private PlayerActivity activity;
	// private OnActivityDestroyListener listener;
	// private boolean isAcitivityPaused = false;

	// private Song currentSong;
	// private PlayInterface playBinder;
	private BinderObject binderObject;
	// private MediaPlayer player;
	// private BottomPlay bottomPlay;
	// private MyProgressBar mpb_musicplay;
	// private DisplayMetrics dm = new DisplayMetrics();

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case 4000:// ���ڲ���,ˢ���Զ��������
			// mpb_musicplay.setCurrProgress(player.getCurrentPosition());
			// mpb_musicplay.invalidate();
			// break;
			}
		};
	};
	private List<Song> recentPlaySongs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_my, null);
		initViews();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		initData();
		initBinderData();
		inflateViews();
		registerListeners();
	}

	/**
	 * ʵ�������
	 */
	private void initViews() {
		this.lv_my = (ListView) view.findViewById(R.id.lv_my);
		this.iv_stars = (ImageView) view.findViewById(R.id.iv_stars);
		this.rl_favorite = (RelativeLayout) view.findViewById(R.id.rl_favorite);
		this.tv_favorite_amount = (TextView) view
				.findViewById(R.id.tv_favorite_amount);
	}

	/**
	 * ʵ��������
	 */
	private void initData() {
		arrs = Utils.getStrArrsFromResources(this.getActivity(),
				R.array.my_strarrs);
		resIds = Utils.getResouceIdsFromRescource(this.getActivity(),
				R.array.my_images);
		// ��ʼ���ղظ���
		favoriteSongs = MediaUtils.getList(Song.class, this.getActivity(),
				MusicProvider.SONG_MULTI_URI, new String[] {
						MusicProvider.TABLE_SONG_ID, "Songname", "Artist",
						"Album", "Duration", "Songpath" },
				MusicProvider.TABLE_SONG_ISFAVORITE + "=?",
				new String[] { "1" }, null);

		updateRecentPlaySongs();
	}

	/**
	 * ��ȡService�İ�����
	 */
	private void initBinderData() {
		binderObject = PlayUtils.binderObject;
		mySongs = binderObject.getSongs();
	}

	/**
	 * ������
	 */
	private void inflateViews() {
		if (this.favoriteSongs.size() > 0) {
			this.tv_favorite_amount.setText(this.favoriteSongs.size() + "��");
		} else {
			this.tv_favorite_amount.setText("0��");
		}
		myListviewAdapter = new MyListviewAdapter(this.getActivity(),
				this.arrs, this.resIds);
		myListviewAdapter.setSongs(mySongs, recentPlaySongs);
		lv_my.setAdapter(myListviewAdapter);
		inflateImageView();
	}

	/**
	 * activity����ʱ,���cover�DƬ�Ͳ���״̬ͼƬ
	 */
	private void inflateImageView() {
		getImageInfoFromPhone();
		if (map != null) {
			isFromPhone = Boolean.valueOf(map.get(Utils.FROM_PHONE).toString());
		} else {
			isFromPhone = false;
		}
		if (!isFromPhone) {
			inflateCoverNotFromPhone();
		} else {
			inflateCoverFromPhone();
		}
	}

	/**
	 * Ϊ���ע���¼�
	 */
	private void registerListeners() {
		this.iv_stars.setOnClickListener(new MyClickListener());
		this.rl_favorite.setOnClickListener(new MyClickListener());
		this.lv_my.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					Intent intent = new Intent(getActivity(),
							LocalActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent recentPlayIntent = new Intent(getActivity(),
							RecentPlayActivity.class);
					startActivity(recentPlayIntent);
					break;
				case 2:
					Intent downloadIntent = new Intent(getActivity(),
							DownloadedActivity.class);
					startActivity(downloadIntent);
					break;
				}
			}
		});
	}

	/**
	 * ����������Ÿ���
	 */
	private void updateRecentPlaySongs() {
		// ��ʼ��������Ÿ��� 86400000
		long yesterdayTimemillies = System.currentTimeMillis() - 120000;// ����һ����������
		recentPlaySongs = MediaUtils.getList(Song.class, getActivity(),
				MusicProvider.SONG_URI, new String[] { "Songname", "Artist",
						"Album" }, MusicProvider.TABLE_SONG_LASTPLAYTIMEMILLIES
						+ ">=?", new String[] { yesterdayTimemillies + "" },
				MusicProvider.TABLE_SONG_LASTPLAYTIMEMILLIES);
	}

	/**
	 * ʵʱ�����������,��PlayActivity����
	 */
	public void invalidateRecentPlay() {
		updateRecentPlaySongs();
		// ʵʱ����������Ÿ�����Ŀ
		myListviewAdapter.setSongs(mySongs, recentPlaySongs);
		myListviewAdapter.notifyDataSetChanged();
		lv_my.invalidate();
	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.iv_stars:// �Զ�������cover����ת
				intentBg = new Intent(MyMusic.this.getActivity(),
						CustomBgAcitivty.class);
				if (!isFromPhone) {
					intentBg.putExtra(Utils.BM_COVER,
							(Integer) iv_stars.getTag());
				} else {
					intentBg.putExtra(Utils.FROM_PHONE, isFromPhone);
				}
				startActivityForResult(intentBg, Utils.BG_REQUESTCODE);
				break;
			case R.id.rl_favorite:// �ղظ赥����ת
				Intent intent = new Intent(getActivity(),
						FavoriteActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	/**
	 * activity��������ͼƬ
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// cover_imageview�Ļ���
		getImageInfoFromPhone();
		if (map != null) {
			isFromPhone = Boolean.valueOf(map.get(Utils.FROM_PHONE).toString());
		} else {
			isFromPhone = false;
		}
		if (requestCode == Utils.BG_REQUESTCODE
				&& resultCode == Utils.BG_RESULTCODE) {
			inflateCoverNotFromPhone();
		} else if (requestCode == Utils.BG_REQUESTCODE
				&& resultCode == Utils.BG_FROMPHONE_RESULTCODE) {
			inflateCoverFromPhone();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ���ֻ��л�ȡisFromPhone��PreshowImageId
	 */
	private void getImageInfoFromPhone() {
		FileInputStream fis = null;
		try {
			String fileDirName = this.getActivity().getFilesDir().getName();
			if (fileDirName != null && !fileDirName.isEmpty()) {
				fis = this.getActivity().openFileInput(fileDirName);
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(fis));
				String string = "";
				map = new HashMap<String, Object>();
				while ((string = bReader.readLine()) != null
						&& !string.isEmpty()) {
					String[] arr = string.split(":");
					map.put(arr[0], arr[1].trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �����֙C���xȡ�DƬ�����cover��iamgeview
	 */
	private void inflateCoverNotFromPhone() {
		if (!isFromPhone) {
			if (map != null) {
				int preshowResId = Integer.valueOf(map.get(Utils.PRESHOW_RESID)
						.toString());
				Integer resId = Utils.preshowToCover.get(preshowResId);
				if (resId == null) {
					iv_stars.setTag(R.drawable.default_cover);
				} else {
					iv_stars.setTag(resId);
					iv_stars.setImageResource(resId);
				}
			} else {
				iv_stars.setTag(R.drawable.default_cover);// ����iv_starsͼƬ����ԴID
			}
		}
	}

	/**
	 * ���ֻ��ж�ȡͼƬ�����cover��imageview
	 */
	private void inflateCoverFromPhone() {
		Bitmap bitmap = BitmapFactory.decodeFile(new File(getActivity()
				.getFilesDir(), "up.jpg").getAbsolutePath());
		iv_stars.setImageBitmap(bitmap);
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}