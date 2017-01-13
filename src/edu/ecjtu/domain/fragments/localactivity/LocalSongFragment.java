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
	 * ���ݼ�����Ϻ���ʾ����
	 */
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Utils.SONG_LOADED:
				activity.initBinderData();
				bottomPlay
						.updateBottomLayoutViews(binderObject.getCurrentSong(),
								binderObject.getPlayer());// ����
				initSongListView(songs);

				if (!isLetterListenerRegistered) {
					registerLetterSearchListener();// ע����ĸ��ѯ�����¼�
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
				binderObject.getPlayer());// ����
	}

	/**
	 * ��ʼ������M��
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
	 * ��ʼ���������ں͸����б�
	 */
	private void initData() {
		initDataFromActivity();
	}

	/**
	 * ��Activity�г�ʼ������
	 */
	private void initDataFromActivity() {
		activity = (LocalActivity) getActivity();
		binderObject = activity.getBinderObject();
		bottomPlay = activity.getBottomPlay();
	}

	/**
	 * ��ʼ�������б�,����ʾ��������
	 */
	public void initSongListView(List<Song> songs) {
		if (this.songs == null) {
			this.songs = songs;
		}
		this.ll_loading.setVisibility(View.GONE);// ���ݼ������
		this.lv_local_songs.setAdapter(new SongListviewAdapter(getActivity(),
				songs));
		changeSongsView(songs);
		((SongListviewAdapter) this.lv_local_songs.getAdapter())
				.setOnSongsChangedListener(new OnSongsChangedListener() {// ʵʱ����������Ϣ�ı仯

					@Override
					public void OnSongsChange(List<Song> songs) {
						changeSongsView(songs);
					}
				});
		lv_local_songs.setSelection(binderObject.getCurrentPosition());
	}

	/**
	 * �ı�listview����ʾ״̬��Ϣ
	 * 
	 * @param songs
	 */
	private void changeSongsView(List<Song> songs) {
		if (songs.size() > 0) {
			rl_hassongs.setVisibility(View.VISIBLE);
			rl_local_songs.setVisibility(View.VISIBLE);
			rl_nosong.setVisibility(View.GONE);
			this.tv_local_amount.setText(songs.size() + "�׸���");
		} else {
			rl_hassongs.setVisibility(View.GONE);
			rl_local_songs.setVisibility(View.GONE);
			rl_nosong.setVisibility(View.VISIBLE);
		}
		// ��ʼ���ļ��б���������
		((LocalActivity) getActivity()).handler
				.sendEmptyMessage(Utils.SONGFRAGMENT_RESUMED);
	}

	/**
	 * ģ����ѯ��ʾ���
	 * 
	 * @param songs
	 */
	public void fuzzyInitSongListView(List<Song> songs) {
		this.lv_local_songs.setAdapter(new SongListviewAdapter(getActivity(),
				songs));
		rl_hassongs.setVisibility(View.VISIBLE);
		rl_local_songs.setVisibility(View.VISIBLE);
		rl_nosong.setVisibility(View.GONE);
		this.tv_local_amount.setText(songs.size() + "�׸���");
	}

	/**
	 * ע�������
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
	 * ���ʱ�İ��涯��
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
	 * ע����ĸ��ѯ�ļ����¼�
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
	 * fragment�л��������¼���songlistview
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
	 * ��ʾ��������
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
			public void OnPopupClick(String playMethod, Integer position) {// ����popupwindow�ĵ���¼�
				btn_playmethod.setText(playMethod);
				switch (position) {
				case 0:// ˳�򲥷�
					binderObject.setPlayMethod(PlayMethod.SEQUENCE_PLAY);
					break;
				case 1:// �������
					binderObject.setPlayMethod(PlayMethod.RANDOM_PLAY);
					break;
				case 2:// �б�ѭ��
					binderObject.setPlayMethod(PlayMethod.LIST_LOOP);
					break;
				case 3:// ����ѭ��
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
	 * ��ʼ�����ظ���
	 */
	public void initLocalSong(List<Audio> audios, List<Song> songs) {
		for (int i = 0; i < audios.size(); i++) {
			Integer duration = Integer.valueOf(audios.get(i).getDuration()) / 1000;
			if (duration > 60 && duration < 600) {
				boolean exists = false;// �жϵ�ǰ�ֶ��Ƿ����
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
				// ��ʼ��������
				Album album = null;
				album = initDBAlbum(audios, i, album);
				// ��ʼ�����ֶ���
				Artist artist = null;
				artist = initDBArtist(audios, i, album, artist);
				// ��ʼ���ļ�·������
				FilePath filePath = null;
				filePath = initDBFilePath(audios, i, filePath);
				// ��ʼ������
				Song song = initDBSong(audios, i, album, artist, filePath);
				songs.add(song);
				if (!exists) {
					resolver = getActivity().getContentResolver();
					ContentValues values = new ContentValues();
					// ����Album��
					initDBValues(album, artist, filePath, song, values);
					resolver.insert(MusicProvider.SONG_URI, values);
					values.clear();
				}
			}
		}
		if (this.songs == null || this.songs.size() < 1) {// ���μ���ʱ
			Collections.sort(songs);// ���μ��أ�����
			this.songs = songs;
			PlayUtils.binderObject.setSongs(songs);
			PlayUtils.binderObject.setCurrentSong(songs.get(0));// ����currentSong
		}
		handler.sendEmptyMessage(Utils.SONG_LOADED);// ���ݼ������
	}

	/**
	 * ��ʼ���������ݿ��е�ContentValues
	 * 
	 * @param album
	 *            �������ݿ��ר������
	 * @param artist
	 *            �������ݿ�ĸ��ֶ���
	 * @param filePath
	 *            �������ݿ��Filepath����
	 * @param song
	 *            �������ݿ�ĸ�������
	 * @param values
	 *            ���ڲ������ݿ��ContentValues
	 */
	private void initDBValues(Album album, Artist artist, FilePath filePath,
			Song song, ContentValues values) {
		values.put("_id", album.get_id());
		values.put("albumname", album.getAlbumname());
		resolver.insert(MusicProvider.ALBUM_URI, values);
		values.clear();
		// ����Artist��
		values.put("_id", artist.get_id());
		values.put("artistname", artist.getArtistname());
		resolver.insert(MusicProvider.ARTIST_URI, values);
		values.clear();
		// ����FilePath��
		values.put("_id", filePath.get_id());
		values.put("parentname", filePath.getParentname());
		values.put("absolutepath", filePath.getAbsolutepath());
		resolver.insert(MusicProvider.FILEPATH_URI, values);
		values.clear();
		// ����Song
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
	 * ��ʼ�����ݿ����
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
		song.setDuration(audios.get(i).getDuration());// ��Ƶʱ��
		song.setSongpath(audios.get(i).getFilePath());// �����ļ��ľ���·������.mp3��
		song.setParentpath(audios.get(i).getParentpath());// ���ø��ļ�·��
		return song;
	}

	/**
	 * ��ʼ���Լ����ݿ�Artist
	 * 
	 * @param audios
	 *            �ֻ�ϵͳ��Ƶ����
	 * @param i
	 *            ����
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
		if (!isArtistExist) {// ��������ڲŽ����½�
			artist = new Artist();
			artist.set_id(audios.get(i).getArtist_id());
			artist.setArtistname(audios.get(i).getArtist());
		}
		albums.add(album);
		return artist;
	}

	/**
	 * ��ʼ���Լ����ݿ�Album
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
		if (!isAlbumExist) {// ��������ڲŽ����½�
			album = new Album();
			album.set_id(audios.get(i).getAlbum_id());
			album.setAlbumname(audios.get(i).getAlbum());
		}
		albums.add(album);
		return album;
	}

	/**
	 * ��ʼ���Լ����ݿ�FilePath
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
		if (!isFilePathExsit) {// �趨filepath��Ψһid
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
	 * ��ʼ��FilePath�ļ���
	 * 
	 * @return
	 */
	public List<FilePath> getFilePaths() {
		List<FilePath> filePaths = MediaUtils.getList(FilePath.class,
				getActivity(), MusicProvider.FILEPATH_URI, new String[] {
						"Parentname", "Absolutepath", "Count", "_id" }, null,
				null, null);// ��ȡ�Լ����ݿ�������ļ���Ϣ
		return filePaths;
	}

	/**
	 * ����songlistview��ѡ����
	 */
	public void setSongListViewSelection() {
		lv_local_songs
				.setSelection(PlayUtils.binderObject.getCurrentPosition());
	}

	/**
	 * ��ȡletterview����Activity����
	 */
	public LetterView getLetterView() {
		return this.song_letterview;
	}
}
