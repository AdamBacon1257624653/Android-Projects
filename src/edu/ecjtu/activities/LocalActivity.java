package edu.ecjtu.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.localactivity.LocalViewPagerAdapter;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.fragments.localactivity.LocalAlbumFragment;
import edu.ecjtu.domain.fragments.localactivity.LocalFolderFragment;
import edu.ecjtu.domain.fragments.localactivity.LocalSingerFragment;
import edu.ecjtu.domain.fragments.localactivity.LocalSongFragment;
import edu.ecjtu.domain.services.PlayService.OnPlayListener;
import edu.ecjtu.domain.views.LetterView;
import edu.ecjtu.domain.views.MyProgressBar;
import edu.ecjtu.domain.views.MyStickView;
import edu.ecjtu.domain.vo.interfaces.ActivityInterface;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.Audio;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.BottomPlay;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class LocalActivity extends FragmentActivity implements OnClickListener,
		ActivityInterface {

	private ViewPager vp_local;
	private RelativeLayout rl_local_show, include_layout;
	private LinearLayout ll_search, rl_local_back;
	private ImageView iv_search, iv_scanlocal, iv_my_start, iv_my_next,
			iv_my_previous;
	private MyStickView iv_local_move;
	private TextView tv_cancelsearch, tv_local_folder, tv_local_artist,
			tv_local_album, tv_local_song, tv_my_songname, tv_my_artistname;
	private EditText et_search;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private DisplayMetrics dm;
	private List<Song> songs = new ArrayList<Song>();
	private List<Audio> audios;
	private List<FilePath> filePaths;
	private List<Artist> artists;
	private List<Album> albums;
	private TextView[] textViews;
	private LocalSongFragment songFragment;
	private LocalFolderFragment folderFragment;
	private LocalSingerFragment singerFragment;
	private LocalAlbumFragment albumFragment;
	private boolean shouldReloadSongs = false;// �Ƿ�Ӧ�����¼��ظ����б�
	private boolean shouldReloadFolders = false;// �Ƿ�Ӧ�����¼����ļ��б�
	private boolean isActivityPaused = false;
	private LetterView letterView;
	private OnActivityDestroyListener destroyListener;
	//
	private BinderObject binderObject;
	private MediaPlayer player;
	private Song currentSong;
	private PlayInterface playBinder;
	private BottomPlay bottomPlay;
	private MyProgressBar mpb_musicplay;

	/**
	 * ���ݼ�����Ϻ���ʾ����
	 */
	public Handler handler = new Handler() {
		private boolean isFilePathsNULL = true;
		private boolean isArtistsNULL = true;
		private boolean isAlbumsNULL = true;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// ���ظ���fragment��������Ϣ
			case Utils.SONGFRAGMENT_CREATED:
				if (songs.size() <= 0) {
					songFragment.initLocalSong(audios, songs);// ��ʼ����
				} else {
					songFragment.initSongListView(songs);// ֱ����ʾ
					songFragment.registerLetterSearchListener();
				}
				break;
			// �����ļ�fragment��������Ϣ
			case Utils.SONGFRAGMENT_RESUMED:
				filePaths = songFragment.getFilePaths();// ��ȡ�Լ����ݿ�������ļ���Ϣ
				folderFragment.initLocalFolder(filePaths);
				isFilePathsNULL = false;
				break;
			case Utils.FOLDERFRAGMENT_RESUMED:
				if (!isFilePathsNULL) {
					folderFragment.initLocalFolder(filePaths);
				}
				break;
			// ���ظ���fragment��������Ϣ
			case Utils.SINGERFRAGMENT_CREATED:
				artists = folderFragment.getArtists();
				Collections.sort(artists);
				singerFragment.initLocalSinger(artists);
				isArtistsNULL = false;
				break;
			case Utils.SINGERFRAGMENT_RESUMED:
				if (!isArtistsNULL) {
					singerFragment.initLocalSinger(artists);
				}
				break;
			// ����ר��fragment��������Ϣ
			case Utils.ALBUMFRAGMENT_CREATED:
				albums = singerFragment.getAlbums();
				Collections.sort(albums);
				albumFragment.initLocalAlbums(albums);
				isAlbumsNULL = false;
				break;
			case Utils.ALBUMFRAGMENT_RESUMED:
				if (!isAlbumsNULL) {
					albumFragment.initLocalAlbums(albums);
				}
				break;
			case 4000:
				mpb_musicplay.setCurrProgress(player.getCurrentPosition());
				mpb_musicplay.invalidate();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local);

		initViews();
		initData();
		initMoveView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActivityPaused = false;
		initBinderData();
		inflateViews();
		registerListeners();
	}

	/**
	 * ��ʼ������view�ؼ�
	 */
	private void initViews() {
		this.vp_local = (ViewPager) findViewById(R.id.vp_local);
		this.rl_local_back = (LinearLayout) findViewById(R.id.rl_local_back);
		this.rl_local_show = (RelativeLayout) findViewById(R.id.rl_local_show);
		this.ll_search = (LinearLayout) findViewById(R.id.ll_search);
		this.iv_search = (ImageView) findViewById(R.id.iv_search);
		this.iv_scanlocal = (ImageView) findViewById(R.id.iv_scanlocal);
		this.iv_local_move = (MyStickView) findViewById(R.id.iv_local_move);
		this.tv_cancelsearch = (TextView) findViewById(R.id.tv_cancelsearch);
		this.et_search = (EditText) findViewById(R.id.et_search);
		// �ĸ�pager�ı���
		this.tv_local_folder = (TextView) findViewById(R.id.tv_local_folder);
		this.tv_local_artist = (TextView) findViewById(R.id.tv_local_artist);
		this.tv_local_album = (TextView) findViewById(R.id.tv_local_album);
		this.tv_local_song = (TextView) findViewById(R.id.tv_local_song);
		// bottom�µĲ���
		this.include_layout = (RelativeLayout) findViewById(R.id.include_layout);
		this.tv_my_songname = (TextView) findViewById(R.id.tv_my_songname);
		this.tv_my_artistname = (TextView) findViewById(R.id.tv_my_artistname);
		this.iv_my_start = (ImageView) findViewById(R.id.iv_my_start);
		this.iv_my_previous = (ImageView) findViewById(R.id.iv_my_previous);
		this.iv_my_next = (ImageView) findViewById(R.id.iv_my_next);
		// �Զ��������
		this.mpb_musicplay = (MyProgressBar) findViewById(R.id.mpb_musicplay);
	}

	/**
	 * Ϊviewpager�������
	 */
	private void initData() {
		this.audios = MediaUtils.getAudioList(this);// �@ȡ�ֻ�ϵͳ����
		textViews = new TextView[] { tv_local_song, tv_local_folder,
				tv_local_artist, tv_local_album };
		songFragment = new LocalSongFragment();
		folderFragment = new LocalFolderFragment();
		singerFragment = new LocalSingerFragment();
		albumFragment = new LocalAlbumFragment();
		fragments.add(songFragment);
		fragments.add(folderFragment);
		fragments.add(singerFragment);
		fragments.add(albumFragment);
		vp_local.setAdapter(new LocalViewPagerAdapter(this
				.getSupportFragmentManager(), fragments));
	}

	/**
	 * ��ʼ��iv_local_move
	 */
	private void initMoveView() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.iv_local_move.setPageCount(4);
		this.iv_local_move.setWidth(dm.widthPixels);
		this.iv_local_move.invalidate();
	}

	/**
	 * ��ʼ��Service������
	 */
	public void initBinderData() {
		binderObject = PlayUtils.binderObject;
		if (player == null) {
			player = binderObject.getPlayer();
			playBinder = binderObject.getPlayBinder();
		}
		songs = binderObject.getSongs();
		currentSong = binderObject.getCurrentSong();
		bottomPlay = new BottomPlay(this, iv_my_start, iv_my_previous,
				iv_my_next, tv_my_songname, tv_my_artistname, mpb_musicplay,
				handler, binderObject);

	}

	/**
	 * ��������Ϣ
	 */
	private void inflateViews() {
		mpb_musicplay.setMaxWidth(dm.widthPixels);
		if (currentSong != null) {
			mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
					.getDuration()));
			mpb_musicplay.setCurrProgress(0);
		}
		bottomPlay.updateBottomLayoutViews(currentSong, player);// ���ײ����
		if (player != null && player.isPlaying()) {// �����̨Service���ڲ��ŵĕr��
			iv_my_start.setImageResource(R.drawable.pause);
		} else {
			iv_my_start.setImageResource(R.drawable.start);
		}
		if (player.getCurrentPosition() < player.getDuration()) {
			bottomPlay.infalteMusicProgressBar();
		}
	}

	/**
	 * ע������¼�
	 */
	private void registerListeners() {
		this.tv_local_song.setOnClickListener(this);
		this.tv_local_folder.setOnClickListener(this);
		this.tv_local_artist.setOnClickListener(this);
		this.tv_local_album.setOnClickListener(this);
		this.rl_local_back.setOnClickListener(this);
		this.iv_search.setOnClickListener(this);
		this.tv_cancelsearch.setOnClickListener(this);
		this.iv_scanlocal.setOnClickListener(this);
		// ���ŵļ�������
		this.iv_my_start.setOnClickListener(this);
		this.iv_my_next.setOnClickListener(this);
		this.iv_my_previous.setOnClickListener(this);
		this.include_layout.setOnClickListener(this);
		this.playBinder.setOnPlayListener(new OnPlayListener() {

			@Override
			public void OnPlay() {
				if (!isActivityPaused) {// ���������ʧȥ���㣬�����������߳�
					initBinderData();
					mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
							.getDuration()));
					bottomPlay.updateBottomLayoutViews(currentSong, player);// ���ײ����
					listener.OnSongPositionChange();// ����λ�ü���
					songFragment.setSongListViewSelection();// �ı�SongListView��ѡ����
					bottomPlay.infalteMusicProgressBar();
				}
			}
		});
		this.vp_local.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				iv_local_move.onTouchEvent(event);
				hideSearch();
				return false;
			}
		});
		this.vp_local.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					iv_local_move.setLeftX(0d);
					break;
				case 1:
					iv_local_move.setLeftX(dm.widthPixels / 4d);
					break;
				case 2:
					iv_local_move.setLeftX(dm.widthPixels / 2d);
					break;
				case 3:
					iv_local_move.setLeftX(dm.widthPixels * (0.75));
					break;
				}
				iv_local_move.setPosition(position);
				iv_local_move.invalidate();
				changeTextColor(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		this.et_search.addTextChangedListener(new TextWatcher() {

			/**
			 * ���������е�״̬
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				switch (vp_local.getCurrentItem()) {
				case 0:// ģ����ѯ����
					letterView = songFragment.getLetterView();
					List<Song> songWordsList = MediaUtils.getList(Song.class,
							getApplicationContext(),
							MusicProvider.SONG_LIKE_URI, new String[] {
									"Songname", "Artist", "Album", "Duration",
									"Songpath" }, null,
							new String[] { s.toString() }, null);
					songFragment.fuzzyInitSongListView(songWordsList);
					break;
				case 1:// ģ����ѯ�ļ���
					List<FilePath> filePathWordsList = MediaUtils.getList(
							FilePath.class, getApplicationContext(),
							MusicProvider.FILEPATH_LIKE_URI, new String[] {
									"Parentname", "Count", "Absolutepath",
									"_id" }, null,
							new String[] { s.toString() }, null);
					folderFragment.fuzzyInitLocalFolder(filePathWordsList);

					break;
				case 2:// ģ����ѯ����
					letterView = singerFragment.getLetterView();
					List<Artist> artistWordsList = MediaUtils.getList(
							Artist.class, getApplicationContext(),
							MusicProvider.ARTIST_LIKE_URI, new String[] {
									"Artistname", "Count" }, null,
							new String[] { s.toString() }, null);
					singerFragment.fuzzyInitLocalSinger(artistWordsList);
					break;
				case 3:// ģ����ѯר��
					letterView = albumFragment.getLetterView();
					List<Album> albumWordsList = MediaUtils.getList(
							Album.class, getApplicationContext(),
							MusicProvider.ALBUM_LIKE_URI, new String[] {
									"Albumname", "Count" }, null,
							new String[] { s.toString() }, null);
					albumFragment.fuzzyInitLocalAlbums(albumWordsList);
					break;
				}
				if (letterView != null) {
					letterView.setVisibility(View.GONE);
				}
			}

			/**
			 * ��������ǰ��״̬
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			/**
			 * �������ֺ��״̬
			 */
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		this.et_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {// ��ʱȥ����ʱ���ع�ԭ����ģ��
					include_layout.setVisibility(View.VISIBLE);
					switch (vp_local.getCurrentItem()) {
					case 0:// ģ����ѯ����
						songFragment.fuzzyInitSongListView(songs);
						break;
					case 1:// ģ����ѯ�ļ���
						folderFragment.fuzzyInitLocalFolder(filePaths);
						break;
					case 2:// ģ����ѯ����
						singerFragment.fuzzyInitLocalSinger(artists);
						break;
					case 3:// ģ����ѯר��
						albumFragment.fuzzyInitLocalAlbums(albums);
						break;
					}
					if (letterView != null) {
						letterView.setVisibility(View.VISIBLE);
					}
				} else {
					include_layout.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_local_back:// ����
			finish();
			break;
		case R.id.iv_search:// ����
			showSearch();
			break;
		case R.id.tv_cancelsearch:// ȡ������
			hideSearch();
			break;
		case R.id.iv_scanlocal:// ɨ������
			Intent intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			break;
		// pager�ı�
		case R.id.tv_local_song:
			vp_local.setCurrentItem(0);
			break;
		case R.id.tv_local_folder:
			vp_local.setCurrentItem(1);
			break;
		case R.id.tv_local_artist:
			vp_local.setCurrentItem(2);
			break;
		case R.id.tv_local_album:
			vp_local.setCurrentItem(3);
			break;
		// �ײ����Ÿı�
		case R.id.iv_my_start:// ��ʼ/��ͣ/�ָ�����
			if (!player.isPlaying()) {
				bottomPlay.startPlay();
			} else {
				bottomPlay.pausePlay();
			}
			break;
		case R.id.iv_my_next:// ��һ�ײ���
			bottomPlay.nextPlay();
			break;
		case R.id.iv_my_previous:// ��һ�ײ���
			bottomPlay.previousPlay();
			break;
		// �ײ����ļ����¼�:
		case R.id.include_layout:
			Intent playIntent = new Intent(this, MusicPlayActivity.class);
			startActivity(playIntent);
			break;
		}
	}

	/**
	 * ��ʾ������
	 */
	private void showSearch() {
		this.ll_search.setVisibility(View.VISIBLE);
		this.rl_local_show.setVisibility(View.GONE);
	}

	/**
	 * ����������
	 */
	private void hideSearch() {
		this.ll_search.setVisibility(View.GONE);
		this.rl_local_show.setVisibility(View.VISIBLE);
	}

	/**
	 * �ı�������ɫ
	 * 
	 * @param position
	 */
	private void changeTextColor(int position) {
		for (int i = 0; i < textViews.length; i++) {
			if (i == position) {
				textViews[i].setTextColor(0xFF3481D3);
			} else {
				textViews[i].setTextColor(0xFF000000);
			}
		}
	};

	/**
	 * ɾ���ļ�/ר��/����ʱ����
	 */
	public void reloadSongs() {
		songs = MediaUtils.getList(Song.class, getApplicationContext(),
				MusicProvider.SONG_MULTI_URI, new String[] { "Songname",
						"Artist", "Album", "Duration", "Songpath" }, null,
				null, null);
		handler.sendEmptyMessage(Utils.SONGFRAGMENT_CREATED);
		shouldReloadSongs = false;
	}

	/**
	 * ɾ��ר��/����ʱ����
	 */
	public void reloadFolders() {
		filePaths = MediaUtils.getList(FilePath.class, this,
				MusicProvider.FILEPATH_URI, new String[] { "Parentname",
						"Absolutepath", "Count", "_id" }, null, null, null);// ��ȡ�Լ����ݿ�������ļ���Ϣ
		shouldReloadSongs = true;
		shouldReloadFolders = false;
		handler.sendEmptyMessage(Utils.FOLDERFRAGMENT_RESUMED);
	}

	/**
	 * ɾ������ʱ����
	 */
	public void reloadArtists() {
		artists = MediaUtils.getList(Artist.class, this,
				MusicProvider.ARTIST_URI, new String[] { "_id", "Artistname",
						"Count" }, null, null, null);// ��ȡ�Լ����ݿ��Artist��Ϣ
		shouldReloadFolders = true;
		shouldReloadSongs = true;
		handler.sendEmptyMessage(Utils.SINGERFRAGMENT_RESUMED);
	}

	public boolean isShouldReloadSongs() {
		return shouldReloadSongs;
	}

	public boolean isShouldReloadFolders() {
		return shouldReloadFolders;
	}

	/**
	 * ��ǰ��������λ�ñ仯����
	 */
	private OnSongPositionChangeListener listener;

	public interface OnSongPositionChangeListener {
		void OnSongPositionChange();
	}

	public void setOnSongPositionChangeListener(
			OnSongPositionChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * ��ȡBinderObject��ר��Fragment�ĵ���
	 * 
	 * @return
	 */
	public BinderObject getBinderObject() {
		return this.binderObject;
	}

	public BottomPlay getBottomPlay() {
		return this.bottomPlay;
	}

	@Override
	protected void onPause() {
		super.onPause();
		isActivityPaused = true;
		if (destroyListener != null) {
			destroyListener.onActivityDestroy();
		}
	}

	@Override
	public void setOnActivityDestroyListener(OnActivityDestroyListener listener) {
		this.destroyListener = listener;
	}
}
