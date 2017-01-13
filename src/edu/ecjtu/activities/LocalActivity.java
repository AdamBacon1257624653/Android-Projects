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
	private boolean shouldReloadSongs = false;// 是否应该重新加载歌曲列表
	private boolean shouldReloadFolders = false;// 是否应该重新加载文件列表
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
	 * 数据加载完毕后显示数据
	 */
	public Handler handler = new Handler() {
		private boolean isFilePathsNULL = true;
		private boolean isArtistsNULL = true;
		private boolean isAlbumsNULL = true;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// 加载歌曲fragment的数据信息
			case Utils.SONGFRAGMENT_CREATED:
				if (songs.size() <= 0) {
					songFragment.initLocalSong(audios, songs);// 开始加载
				} else {
					songFragment.initSongListView(songs);// 直接显示
					songFragment.registerLetterSearchListener();
				}
				break;
			// 加载文件fragment的数据信息
			case Utils.SONGFRAGMENT_RESUMED:
				filePaths = songFragment.getFilePaths();// 获取自己数据库的音乐文件信息
				folderFragment.initLocalFolder(filePaths);
				isFilePathsNULL = false;
				break;
			case Utils.FOLDERFRAGMENT_RESUMED:
				if (!isFilePathsNULL) {
					folderFragment.initLocalFolder(filePaths);
				}
				break;
			// 加载歌手fragment的数据信息
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
			// 加载专辑fragment的数据信息
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
	 * 初始化各个view控件
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
		// 四个pager的标题
		this.tv_local_folder = (TextView) findViewById(R.id.tv_local_folder);
		this.tv_local_artist = (TextView) findViewById(R.id.tv_local_artist);
		this.tv_local_album = (TextView) findViewById(R.id.tv_local_album);
		this.tv_local_song = (TextView) findViewById(R.id.tv_local_song);
		// bottom下的布局
		this.include_layout = (RelativeLayout) findViewById(R.id.include_layout);
		this.tv_my_songname = (TextView) findViewById(R.id.tv_my_songname);
		this.tv_my_artistname = (TextView) findViewById(R.id.tv_my_artistname);
		this.iv_my_start = (ImageView) findViewById(R.id.iv_my_start);
		this.iv_my_previous = (ImageView) findViewById(R.id.iv_my_previous);
		this.iv_my_next = (ImageView) findViewById(R.id.iv_my_next);
		// 自定义进度条
		this.mpb_musicplay = (MyProgressBar) findViewById(R.id.mpb_musicplay);
	}

	/**
	 * 为viewpager填充数据
	 */
	private void initData() {
		this.audios = MediaUtils.getAudioList(this);// 獲取手机系统音乐
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
	 * 初始化iv_local_move
	 */
	private void initMoveView() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.iv_local_move.setPageCount(4);
		this.iv_local_move.setWidth(dm.widthPixels);
		this.iv_local_move.invalidate();
	}

	/**
	 * 初始化Service的数据
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
	 * 填充组件信息
	 */
	private void inflateViews() {
		mpb_musicplay.setMaxWidth(dm.widthPixels);
		if (currentSong != null) {
			mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
					.getDuration()));
			mpb_musicplay.setCurrProgress(0);
		}
		bottomPlay.updateBottomLayoutViews(currentSong, player);// 填充底部组件
		if (player != null && player.isPlaying()) {// 如果后台Service正在播放的時候
			iv_my_start.setImageResource(R.drawable.pause);
		} else {
			iv_my_start.setImageResource(R.drawable.start);
		}
		if (player.getCurrentPosition() < player.getDuration()) {
			bottomPlay.infalteMusicProgressBar();
		}
	}

	/**
	 * 注册监听事件
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
		// 播放的监听定义
		this.iv_my_start.setOnClickListener(this);
		this.iv_my_next.setOnClickListener(this);
		this.iv_my_previous.setOnClickListener(this);
		this.include_layout.setOnClickListener(this);
		this.playBinder.setOnPlayListener(new OnPlayListener() {

			@Override
			public void OnPlay() {
				if (!isActivityPaused) {// 如果本界面失去焦点，则不启动播放线程
					initBinderData();
					mpb_musicplay.setMaxProgress(Float.valueOf(currentSong
							.getDuration()));
					bottomPlay.updateBottomLayoutViews(currentSong, player);// 填充底部组件
					listener.OnSongPositionChange();// 歌曲位置监听
					songFragment.setSongListViewSelection();// 改变SongListView的选择项
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
			 * 输入文字中的状态
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				switch (vp_local.getCurrentItem()) {
				case 0:// 模糊查询歌曲
					letterView = songFragment.getLetterView();
					List<Song> songWordsList = MediaUtils.getList(Song.class,
							getApplicationContext(),
							MusicProvider.SONG_LIKE_URI, new String[] {
									"Songname", "Artist", "Album", "Duration",
									"Songpath" }, null,
							new String[] { s.toString() }, null);
					songFragment.fuzzyInitSongListView(songWordsList);
					break;
				case 1:// 模糊查询文件夹
					List<FilePath> filePathWordsList = MediaUtils.getList(
							FilePath.class, getApplicationContext(),
							MusicProvider.FILEPATH_LIKE_URI, new String[] {
									"Parentname", "Count", "Absolutepath",
									"_id" }, null,
							new String[] { s.toString() }, null);
					folderFragment.fuzzyInitLocalFolder(filePathWordsList);

					break;
				case 2:// 模糊查询歌手
					letterView = singerFragment.getLetterView();
					List<Artist> artistWordsList = MediaUtils.getList(
							Artist.class, getApplicationContext(),
							MusicProvider.ARTIST_LIKE_URI, new String[] {
									"Artistname", "Count" }, null,
							new String[] { s.toString() }, null);
					singerFragment.fuzzyInitLocalSinger(artistWordsList);
					break;
				case 3:// 模糊查询专辑
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
			 * 输入文字前的状态
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			/**
			 * 输入文字后的状态
			 */
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		this.et_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {// 当时去焦点时，回归原来的模样
					include_layout.setVisibility(View.VISIBLE);
					switch (vp_local.getCurrentItem()) {
					case 0:// 模糊查询歌曲
						songFragment.fuzzyInitSongListView(songs);
						break;
					case 1:// 模糊查询文件夹
						folderFragment.fuzzyInitLocalFolder(filePaths);
						break;
					case 2:// 模糊查询歌手
						singerFragment.fuzzyInitLocalSinger(artists);
						break;
					case 3:// 模糊查询专辑
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
		case R.id.rl_local_back:// 返回
			finish();
			break;
		case R.id.iv_search:// 搜索
			showSearch();
			break;
		case R.id.tv_cancelsearch:// 取消搜索
			hideSearch();
			break;
		case R.id.iv_scanlocal:// 扫描音乐
			Intent intent = new Intent(this, ScanActivity.class);
			startActivity(intent);
			break;
		// pager改变
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
		// 底部播放改变
		case R.id.iv_my_start:// 开始/暂停/恢复播放
			if (!player.isPlaying()) {
				bottomPlay.startPlay();
			} else {
				bottomPlay.pausePlay();
			}
			break;
		case R.id.iv_my_next:// 下一首播放
			bottomPlay.nextPlay();
			break;
		case R.id.iv_my_previous:// 上一首播放
			bottomPlay.previousPlay();
			break;
		// 底层代码的监听事件:
		case R.id.include_layout:
			Intent playIntent = new Intent(this, MusicPlayActivity.class);
			startActivity(playIntent);
			break;
		}
	}

	/**
	 * 显示搜索栏
	 */
	private void showSearch() {
		this.ll_search.setVisibility(View.VISIBLE);
		this.rl_local_show.setVisibility(View.GONE);
	}

	/**
	 * 隐藏搜索栏
	 */
	private void hideSearch() {
		this.ll_search.setVisibility(View.GONE);
		this.rl_local_show.setVisibility(View.VISIBLE);
	}

	/**
	 * 改变字体颜色
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
	 * 删除文件/专辑/歌手时调用
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
	 * 删除专辑/歌手时调用
	 */
	public void reloadFolders() {
		filePaths = MediaUtils.getList(FilePath.class, this,
				MusicProvider.FILEPATH_URI, new String[] { "Parentname",
						"Absolutepath", "Count", "_id" }, null, null, null);// 获取自己数据库的音乐文件信息
		shouldReloadSongs = true;
		shouldReloadFolders = false;
		handler.sendEmptyMessage(Utils.FOLDERFRAGMENT_RESUMED);
	}

	/**
	 * 删除歌手时调用
	 */
	public void reloadArtists() {
		artists = MediaUtils.getList(Artist.class, this,
				MusicProvider.ARTIST_URI, new String[] { "_id", "Artistname",
						"Count" }, null, null, null);// 获取自己数据库的Artist信息
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
	 * 当前歌曲播放位置变化监听
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
	 * 获取BinderObject，专供Fragment的调用
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
