package edu.ecjtu.domain.fragments.localactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ecjtu.activities.LocalActivity;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.localactivity.SongListviewAdapter;
import edu.ecjtu.domain.adapters.localactivity.SongListviewAdapter.OnSongsChangedListener;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.services.PlayService.PlayMethod;
import edu.ecjtu.domain.views.LetterView;
import edu.ecjtu.domain.views.LetterView.OnLetterUpdateListener;
import edu.ecjtu.domain.views.PopupWindowView;
import edu.ecjtu.domain.views.PopupWindowView.OnPopupCLickListener;
import edu.ecjtu.domain.vo.interfaces.FragmentInterface;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.Audio;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.BottomPlay;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class LocalSongFragment extends Fragment implements OnClickListener,
		FragmentInterface {

	private View view;
	private ImageView iv_dropdown, iv_musicicon;
	private Button btn_playmethod;
	private ListView lv_local_songs;
	private PopupWindow popupWindow;
	private RelativeLayout rl_hassongs, rl_nosong, rl_local_songs;
	private LinearLayout ll_loading;
	private TextView tv_local_amount, tv_show_letter;
	private ContentResolver resolver;
	private List<Song> songs;
	private List<FilePath> filePaths = new ArrayList<FilePath>();
	private List<Album> albums = new ArrayList<Album>();
	private List<Artist> artists = new ArrayList<Artist>();
	private LocalActivity activity;
	private BinderObject binderObject;
	private BottomPlay bottomPlay;
	private LetterView song_letterview;
	private boolean isLetterListenerRegistered = false;
	// private

	/**
	 * 数据加载完毕后显示数据
	 */
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Utils.SONG_LOADED:
				activity.initBinderData();
				bottomPlay
						.updateBottomLayoutViews(binderObject.getCurrentSong(),
								binderObject.getPlayer());// 回现
				initSongListView(songs);

				if (!isLetterListenerRegistered) {
					registerLetterSearchListener();// 注册字母查询监听事件
				}
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.local_fragment_songs, null);
		initViews();
		initData();
		ReLoadSongListview();
		registerListeners();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		activity.initBinderData();
		bottomPlay.updateBottomLayoutViews(binderObject.getCurrentSong(),
				binderObject.getPlayer());// 回现
	}

	/**
	 * 初始化各大組件
	 */
	private void initViews() {
		this.btn_playmethod = (Button) view.findViewById(R.id.btn_playmethod);
		this.iv_dropdown = (ImageView) view.findViewById(R.id.iv_dropdown);
		this.lv_local_songs = (ListView) view.findViewById(R.id.lv_local_songs);
		this.rl_hassongs = (RelativeLayout) view.findViewById(R.id.rl_hassongs);
		this.rl_nosong = (RelativeLayout) view.findViewById(R.id.rl_nosong);
		this.ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
		this.rl_local_songs = (RelativeLayout) view
				.findViewById(R.id.rl_local_songs);
		this.tv_local_amount = new TextView(getActivity());
		this.song_letterview = (LetterView) view
				.findViewById(R.id.song_letterview);
		this.tv_show_letter = (TextView) view.findViewById(R.id.tv_show_letter);
		this.iv_musicicon = (ImageView) view.findViewById(R.id.iv_musicicon);
	}

	/**
	 * 初始化弹出窗口和歌曲列表
	 */
	private void initData() {
		initDataFromActivity();
	}

	/**
	 * 从Activity中初始化数据
	 */
	private void initDataFromActivity() {
		activity = (LocalActivity) getActivity();
		binderObject = activity.getBinderObject();
		bottomPlay = activity.getBottomPlay();
	}

	/**
	 * 初始化歌曲列表,并显示歌曲数量
	 */
	public void initSongListView(List<Song> songs) {
		if (this.songs == null) {
			this.songs = songs;
		}
		this.ll_loading.setVisibility(View.GONE);// 数据加载完毕
		this.lv_local_songs.setAdapter(new SongListviewAdapter(getActivity(),
				songs));
		changeSongsView(songs);
		((SongListviewAdapter) this.lv_local_songs.getAdapter())
				.setOnSongsChangedListener(new OnSongsChangedListener() {// 实时监听歌曲信息的变化

					@Override
					public void OnSongsChange(List<Song> songs) {
						changeSongsView(songs);
					}
				});
		lv_local_songs.setSelection(binderObject.getCurrentPosition());
	}

	/**
	 * 改变listview的显示状态信息
	 * 
	 * @param songs
	 */
	private void changeSongsView(List<Song> songs) {
		if (songs.size() > 0) {
			rl_hassongs.setVisibility(View.VISIBLE);
			rl_local_songs.setVisibility(View.VISIBLE);
			rl_nosong.setVisibility(View.GONE);
			this.tv_local_amount.setText(songs.size() + "首歌曲");
		} else {
			rl_hassongs.setVisibility(View.GONE);
			rl_local_songs.setVisibility(View.GONE);
			rl_nosong.setVisibility(View.VISIBLE);
		}
		// 初始化文件列表的填充数据
		((LocalActivity) getActivity()).handler
				.sendEmptyMessage(Utils.SONGFRAGMENT_RESUMED);
	}

	/**
	 * 模糊查询显示结果
	 * 
	 * @param songs
	 */
	public void fuzzyInitSongListView(List<Song> songs) {
		this.lv_local_songs.setAdapter(new SongListviewAdapter(getActivity(),
				songs));
		rl_hassongs.setVisibility(View.VISIBLE);
		rl_local_songs.setVisibility(View.VISIBLE);
		rl_nosong.setVisibility(View.GONE);
		this.tv_local_amount.setText(songs.size() + "首歌曲");
	}

	/**
	 * 注册监听器
	 */
	private void registerListeners() {
		this.iv_dropdown.setOnClickListener(this);
		this.btn_playmethod.setOnClickListener(this);
		if (this.songs != null) {
			registerLetterSearchListener();
		}
		this.lv_local_songs
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parentView,
							View view, int position, long id) {
						((SongListviewAdapter) parentView.getAdapter())
								.showDialog(position);
						return false;
					}
				});
		this.lv_local_songs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long relatvePosition) {
				clickAnimation(view);
				binderObject.getPlayBinder().switchSong(position);
			}
		});
	}

	/**
	 * 点击时的伴随动画
	 * 
	 * @param view
	 */
	private void clickAnimation(View view) {
		iv_musicicon.setVisibility(View.VISIBLE);
		TranslateAnimation ta = new TranslateAnimation(Animation.ABSOLUTE,
				20.0f, Animation.ABSOLUTE, 20.0f, Animation.ABSOLUTE,
				view.getY(), Animation.RELATIVE_TO_PARENT, 0.98f);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(ta);
		animationSet.setDuration(1000);
		iv_musicicon.startAnimation(animationSet);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation anim) {

			}

			@Override
			public void onAnimationRepeat(Animation anim) {

			}

			@Override
			public void onAnimationEnd(Animation anim) {
				iv_musicicon.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 注册字母查询的监听事件
	 */
	public void registerLetterSearchListener() {
		this.song_letterview
				.setOnLetterUpdateListener(new OnLetterUpdateListener() {

					@Override
					public void OnLetterUpdate(String letter) {
						PinyinUtil.letterQuickIndex(tv_show_letter, handler,
								letter, songs, lv_local_songs);
					}
				});
		isLetterListenerRegistered = true;
	}

	/**
	 * fragment切换回来重新加载songlistview
	 */
	private void ReLoadSongListview() {
		if (activity.isShouldReloadSongs()) {
			activity.reloadSongs();
		} else {
			activity.handler.sendEmptyMessage(Utils.SONGFRAGMENT_CREATED);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_playmethod:
			showPopup();
			break;
		case R.id.iv_dropdown:
			showPopup();
			break;
		}
	}

	/**
	 * 显示弹出窗口
	 */
	private void showPopup() {
		final View view = View.inflate(activity, R.layout.popupwindow_layout,
				null);
		final PopupWindowView popupWindowView = (PopupWindowView) view
				.findViewById(R.id.popupWindowView);
		view.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						view.measure(0, 0);
						popupWindow.setHeight(view.getMeasuredHeight());
						popupWindow.showAsDropDown(btn_playmethod, 0, 10);
					}
				});
		popupWindowView.setOnPopupCLickListener(new OnPopupCLickListener() {

			@Override
			public void OnPopupClick(String playMethod, Integer position) {// 监听popupwindow的点击事件
				btn_playmethod.setText(playMethod);
				switch (position) {
				case 0:// 顺序播放
					binderObject.setPlayMethod(PlayMethod.SEQUENCE_PLAY);
					break;
				case 1:// 随机播放
					binderObject.setPlayMethod(PlayMethod.RANDOM_PLAY);
					break;
				case 2:// 列表循环
					binderObject.setPlayMethod(PlayMethod.LIST_LOOP);
					break;
				case 3:// 单曲循环
					binderObject.setPlayMethod(PlayMethod.SINGLE_LOOP);
					break;
				}
				popupWindow.dismiss();
			}
		});
		if (popupWindow == null) {
			popupWindow = new PopupWindow(view, btn_playmethod.getWidth(),
					view.getMeasuredHeight());
		}
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(btn_playmethod, 0, 10);
	}

	/**
	 * 初始化本地歌曲
	 */
	public void initLocalSong(List<Audio> audios, List<Song> songs) {
		for (int i = 0; i < audios.size(); i++) {
			Integer duration = Integer.valueOf(audios.get(i).getDuration()) / 1000;
			if (duration > 60 && duration < 600) {
				boolean exists = false;// 判断当前字段是否存在
				if (songs.size() > 0) {
					for (Song song : songs) {
						if (song.get_id().equals(audios.get(i).get_id())) {
							exists = true;
							break;
						}
					}
					if (exists) {
						break;
					}
				}
				// 初始化相册对象
				Album album = null;
				album = initDBAlbum(audios, i, album);
				// 初始化歌手对象
				Artist artist = null;
				artist = initDBArtist(audios, i, album, artist);
				// 初始化文件路径对象
				FilePath filePath = null;
				filePath = initDBFilePath(audios, i, filePath);
				// 初始化歌曲
				Song song = initDBSong(audios, i, album, artist, filePath);
				songs.add(song);
				if (!exists) {
					resolver = getActivity().getContentResolver();
					ContentValues values = new ContentValues();
					// 插入Album表
					initDBValues(album, artist, filePath, song, values);
					resolver.insert(MusicProvider.SONG_URI, values);
					values.clear();
				}
			}
		}
		if (this.songs == null || this.songs.size() < 1) {// 初次加载时
			Collections.sort(songs);// 初次加载，排序
			this.songs = songs;
			PlayUtils.binderObject.setSongs(songs);
			PlayUtils.binderObject.setCurrentSong(songs.get(0));// 设置currentSong
		}
		handler.sendEmptyMessage(Utils.SONG_LOADED);// 数据加载完毕
	}

	/**
	 * 初始化插入数据库中的ContentValues
	 * 
	 * @param album
	 *            插入数据库的专辑对象
	 * @param artist
	 *            插入数据库的歌手对象
	 * @param filePath
	 *            插入数据库的Filepath对象
	 * @param song
	 *            插入数据库的歌曲对象
	 * @param values
	 *            用于插入数据库的ContentValues
	 */
	private void initDBValues(Album album, Artist artist, FilePath filePath,
			Song song, ContentValues values) {
		values.put("_id", album.get_id());
		values.put("albumname", album.getAlbumname());
		resolver.insert(MusicProvider.ALBUM_URI, values);
		values.clear();
		// 插入Artist表
		values.put("_id", artist.get_id());
		values.put("artistname", artist.getArtistname());
		resolver.insert(MusicProvider.ARTIST_URI, values);
		values.clear();
		// 插入FilePath表
		values.put("_id", filePath.get_id());
		values.put("parentname", filePath.getParentname());
		values.put("absolutepath", filePath.getAbsolutepath());
		resolver.insert(MusicProvider.FILEPATH_URI, values);
		values.clear();
		// 插入Song
		values.put("_id", song.get_id());
		values.put("songname", song.getSongname());
		values.put("artist_id", song.getArtist().get_id());
		values.put("album_id", song.getAlbum().get_id());
		values.put("filepath_id", song.getFilepath().get_id());
		values.put("parentpath", song.getParentpath());
		values.put("isFavorite", song.isFavorite());
		values.put(MusicProvider.TABLE_SONG_DURATION, song.getDuration());
		values.put(MusicProvider.TABLE_SONG_SONGPATH, song.getSongpath());
	}

	/**
	 * 初始化数据库歌曲
	 * 
	 * @param audios
	 * @param i
	 * @param album
	 * @param artist
	 * @param filePath
	 * @return
	 */
	private Song initDBSong(List<Audio> audios, int i, Album album,
			Artist artist, FilePath filePath) {
		Song song = new Song();
		song.set_id(audios.get(i).get_id());
		song.setSongname(audios.get(i).getTitle());
		song.setAlbum(album);
		song.setArtist(artist);
		song.setFilepath(filePath);
		song.setIsFavorite("0");
		song.setDuration(audios.get(i).getDuration());// 音频时长
		song.setSongpath(audios.get(i).getFilePath());// 歌曲文件的绝对路径，带.mp3的
		song.setParentpath(audios.get(i).getParentpath());// 设置父文件路径
		return song;
	}

	/**
	 * 初始化自己数据库Artist
	 * 
	 * @param audios
	 *            手机系统音频对象
	 * @param i
	 *            参数
	 * @param album
	 * @param artist
	 * @return
	 */
	private Artist initDBArtist(List<Audio> audios, int i, Album album,
			Artist artist) {
		boolean isArtistExist = false;
		for (Artist ar : artists) {
			if (ar.get_id().equals(audios.get(i).getArtist_id())) {
				isArtistExist = true;
				artist = ar;
				break;
			}
		}
		if (!isArtistExist) {// 如果不存在才进行新建
			artist = new Artist();
			artist.set_id(audios.get(i).getArtist_id());
			artist.setArtistname(audios.get(i).getArtist());
		}
		albums.add(album);
		return artist;
	}

	/**
	 * 初始化自己数据库Album
	 * 
	 * @param audios
	 * @param i
	 * @param album
	 * @return
	 */
	private Album initDBAlbum(List<Audio> audios, int i, Album album) {
		boolean isAlbumExist = false;
		for (Album al : albums) {
			if (al.get_id().equals(audios.get(i).getAlbum_id())) {
				isAlbumExist = true;
				album = al;
				break;
			}
		}
		if (!isAlbumExist) {// 如果不存在才进行新建
			album = new Album();
			album.set_id(audios.get(i).getAlbum_id());
			album.setAlbumname(audios.get(i).getAlbum());
		}
		albums.add(album);
		return album;
	}

	/**
	 * 初始化自己数据库FilePath
	 * 
	 * @param audios
	 * @param i
	 * @param filePath
	 * @return
	 */
	private FilePath initDBFilePath(List<Audio> audios, int i, FilePath filePath) {
		boolean isFilePathExsit = false;
		for (FilePath fPath : filePaths) {
			if (fPath.getAbsolutepath().equals(
					new File(audios.get(i).getFilePath()).getParentFile()
							.getAbsolutePath())) {
				isFilePathExsit = true;
				filePath = fPath;
				break;
			}
		}
		if (!isFilePathExsit) {// 设定filepath的唯一id
			filePath = new FilePath();
			filePath.setAbsolutepath(new File(audios.get(i).getFilePath())
					.getParentFile().getAbsolutePath());
			filePath.setParentname(new File(audios.get(i).getFilePath())
					.getParentFile().getName());
			filePath.set_id(System.currentTimeMillis() + "");
			filePaths.add(filePath);
		}
		return filePath;
	}

	/**
	 * 初始化FilePath的集合
	 * 
	 * @return
	 */
	public List<FilePath> getFilePaths() {
		List<FilePath> filePaths = MediaUtils.getList(FilePath.class,
				getActivity(), MusicProvider.FILEPATH_URI, new String[] {
						"Parentname", "Absolutepath", "Count", "_id" }, null,
				null, null);// 获取自己数据库的音乐文件信息
		return filePaths;
	}

	/**
	 * 设置songlistview的选择项
	 */
	public void setSongListViewSelection() {
		lv_local_songs
				.setSelection(PlayUtils.binderObject.getCurrentPosition());
	}

	/**
	 * 获取letterview，供Activity调用
	 */
	public LetterView getLetterView() {
		return this.song_letterview;
	}
}
