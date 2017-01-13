package edu.ecjtu.domain.services;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.Song;

public class PlayService extends Service {

	private List<Song> songList;
	private MediaPlayer player = new MediaPlayer();
	private int currentSongPosition = 0;// 记录当前播放歌曲的位置
	private Song currentSong;// 当前正在播放的歌曲
	private boolean isPausing = false;
	private boolean isStarted = false;// 标识当前程序是否启动过
	private boolean isPlayOnline = false;
	public OnMusicOnlinePlayListener musicOnlinePlayListener;

	@Override
	public IBinder onBind(Intent data) {
		MyBinder myBinder = new MyBinder();
		initData(myBinder);
		return myBinder;
	}

	/**
	 * 初始化数据
	 */
	private void initData(PlayInterface playBinder) {
		songList = MediaUtils.getList(Song.class, getApplicationContext(),
				MusicProvider.SONG_MULTI_URI,
				new String[] { "_id", "Songname", "Artist", "Album",
						"Songpath", "Duration", "IsFavorite" }, null, null,
				null);
		Collections.sort(songList);
		if (songList.size() > 0) {
			currentSong = songList.get(currentSongPosition);
		}
		initBinderObject(playBinder);
	}

	/**
	 * 音乐播放
	 */
	private void doPlay() {
		try {
			if (!isPausing) {// 没有暂停播放时
				currentSong = songList.get(currentSongPosition);
				PlayUtils.binderObject.setCurrentSong(currentSong);// 更新当前播放的歌曲
				PlayUtils.binderObject.setCurrentPosition(currentSongPosition);
				player.reset();
				player.setDataSource(currentSong.getSongpath());
				player.prepare();
				isPlayOnline = false;// 设置为非网络播放
				updateRecentPlayTime();// 更新最近播放时间
			}
			player.start();
			if (isPausing) {// 从暂停恢复到播放时
				isPausing = false;
			}
			listen();// 执行各种播放的监听
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新最近播放的时间
	 */
	private void updateRecentPlayTime() {
		resolver = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(MusicProvider.TABLE_SONG_LASTPLAYTIMEMILLIES,
				System.currentTimeMillis());
		resolver.update(MusicProvider.SONG_URI, values,
				MusicProvider.TABLE_SONG_ID + "=?",
				new String[] { currentSong.get_id() });
	}

	/**
	 * 播放网络资源
	 * 
	 * @param url
	 */
	private void doPlayOnline(String url) {
		player.reset();
		try {
			player.setDataSource(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		player.prepareAsync();
		isStarted = true;
		isPlayOnline = true;
		player.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				if (onOnlinePlayStartListener != null) {
					onOnlinePlayStartListener.OnOnlinePlayStart();
				}
				if (musicOnlinePlayListener != null) {
					musicOnlinePlayListener.OnMusicOnlinePlay();
				}
				listen();
			}
		});
	}

	/**
	 * 执行播放的监听
	 */
	private void listen() {
		if (!isStarted) {
			isStarted = true;
		}
		if (listener != null) {
			listener.OnPlay();// 播放监听
		}
		if (musicListener != null) {
			musicListener.OnMusicPlay();// 音乐播放界面的监听
		}
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer player) {
				doPlayNext();
			}
		});
	}

	/**
	 * 音乐暂停
	 */
	private void doPause() {
		player.pause();
		isPausing = true;
	}

	/**
	 * 播放下一曲
	 */
	private void doPlayNext() {// 播放下一首
		switchSongWithDirection(1);
	}

	/**
	 * 播放上一曲
	 */
	private void doPlayPrevious() {// 播放上一首
		switchSongWithDirection(-1);
	}

	/**
	 * 带方向性的切歌。即：上一首、下一首
	 * 
	 * @param direction
	 *            <li>大于等于0：下一首<li>小于0：上一首
	 */
	private void switchSongWithDirection(int direction) {
		isPausing = false;
		switch (binderObject.getPlayMethod()) {
		case SEQUENCE_PLAY:// 顺序上一首播放
			if (direction < 0) {// 上一首
				if (currentSongPosition == 0) {
					Utils.showToast(getApplicationContext(), "当前是第一首歌曲!");
				} else {
					currentSongPosition--;
				}
			} else {// 下一首
				if (currentSongPosition == songList.size() - 1) {
					Utils.showToast(getApplicationContext(), "当前是最后一首歌曲!");
				} else {
					currentSongPosition++;
				}
			}
			break;
		case RANDOM_PLAY:// 随机播放
			Random random = new Random();
			currentSongPosition = random.nextInt(songList.size());
			break;
		case LIST_LOOP:// 列表循环播放
			if (direction < 0) {// 上一首
				if (currentSongPosition == 0) {// 注：这儿需要判断播放方式（待完成）
					currentSongPosition = songList.size() - 1;
				} else {
					currentSongPosition--;
				}
			} else {// 下一首
				if (currentSongPosition == songList.size() - 1) {// 注：这儿需要判断播放方式（待完成）
					currentSongPosition = 0;
				} else {
					currentSongPosition++;
				}
			}
			break;
		case SINGLE_LOOP:// 单曲循环播放
			currentSongPosition = currentSongPosition - 0;
			break;
		}
		doPlay();
	}

	/**
	 * 根据歌曲位置直接切歌
	 * 
	 * @param position
	 *            歌曲位置
	 */
	private void doSwitchSong(int position) {
		isPausing = false;
		currentSongPosition = position;
		doPlay();
	}

	/**
	 * 音乐播放接口
	 */
	class MyBinder extends Binder implements PlayInterface {
		@Override
		public void play() {
			doPlay();
		}

		@Override
		public void playOnline(String url) {
			doPlayOnline(url);
		}

		@Override
		public void pause() {
			doPause();
		}

		@Override
		public void playNext() {
			doPlayNext();
		}

		@Override
		public void playPrevious() {
			doPlayPrevious();
		}

		@Override
		public void switchSong(int position) {
			doSwitchSong(position);
		}

		/**
		 * 快进/快退/拖动进度条播放
		 */
		@Override
		public void playSeekTo(int playPosition) {
			player.seekTo(playPosition);
		}

		@Override
		public void setOnPlayListener(OnPlayListener listener) {
			PlayService.this.listener = listener;
		}

		@Override
		public void setOnMusicPlayListener(OnMusicPlayListener musicListener) {
			PlayService.this.musicListener = musicListener;
		}

		@Override
		public void setOnMusicOnlinePlayListener(
				OnMusicOnlinePlayListener musicOnlinePlayListener) {
			PlayService.this.musicOnlinePlayListener = musicOnlinePlayListener;
		}

		public void setOnOnlinePlayStartListener(
				OnOnlinePlayStartListener onOnlinePlayStartListener) {
			PlayService.this.onOnlinePlayStartListener = onOnlinePlayStartListener;
		}

		@Override
		public boolean getIsStarted() {
			return PlayService.this.isStarted;
		}

		@Override
		public boolean getIsPlayOnline() {
			return PlayService.this.isPlayOnline;
		}
	}

	/**
	 * 播放监听
	 */
	private OnPlayListener listener;

	public interface OnPlayListener {
		void OnPlay();
	}

	/**
	 * 播放界面的播放监听
	 */
	private OnMusicPlayListener musicListener;
	private BinderObject binderObject;
	private OnOnlinePlayStartListener onOnlinePlayStartListener;
	private ContentResolver resolver;

	public interface OnMusicPlayListener {
		void OnMusicPlay();

		void OnMusicPause();
	}

	public interface OnMusicOnlinePlayListener {
		void OnMusicOnlinePlay();
	}

	public interface OnOnlinePlayStartListener {
		void OnOnlinePlayStart();
	}

	/**
	 * 初始化绑定Service所要传递的对象
	 * 
	 * @return
	 */
	private BinderObject initBinderObject(PlayInterface playBinder) {
		binderObject = new BinderObject();
		binderObject.setSongs(songList);
		binderObject.setCurrentSong(currentSong);
		binderObject.setPlayer(player);
		binderObject.setPlayBinder(playBinder);
		binderObject.setCurrentPosition(currentSongPosition);
		binderObject.setPlayMethod(PlayMethod.SEQUENCE_PLAY);
		PlayUtils.binderObject = binderObject;
		return binderObject;
	}

	@Override
	public void onDestroy() {
		if (player != null) {
			super.onDestroy();
			player.stop();
			player.release();
			player = null;
		}
	}

	/**
	 * 定义音乐播放方式
	 */
	public enum PlayMethod {
		SEQUENCE_PLAY, RANDOM_PLAY, LIST_LOOP, SINGLE_LOOP
	}
}
