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
	private int currentSongPosition = 0;// ��¼��ǰ���Ÿ�����λ��
	private Song currentSong;// ��ǰ���ڲ��ŵĸ���
	private boolean isPausing = false;
	private boolean isStarted = false;// ��ʶ��ǰ�����Ƿ�������
	private boolean isPlayOnline = false;
	public OnMusicOnlinePlayListener musicOnlinePlayListener;

	@Override
	public IBinder onBind(Intent data) {
		MyBinder myBinder = new MyBinder();
		initData(myBinder);
		return myBinder;
	}

	/**
	 * ��ʼ������
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
	 * ���ֲ���
	 */
	private void doPlay() {
		try {
			if (!isPausing) {// û����ͣ����ʱ
				currentSong = songList.get(currentSongPosition);
				PlayUtils.binderObject.setCurrentSong(currentSong);// ���µ�ǰ���ŵĸ���
				PlayUtils.binderObject.setCurrentPosition(currentSongPosition);
				player.reset();
				player.setDataSource(currentSong.getSongpath());
				player.prepare();
				isPlayOnline = false;// ����Ϊ�����粥��
				updateRecentPlayTime();// �����������ʱ��
			}
			player.start();
			if (isPausing) {// ����ͣ�ָ�������ʱ
				isPausing = false;
			}
			listen();// ִ�и��ֲ��ŵļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����������ŵ�ʱ��
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
	 * ����������Դ
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
	 * ִ�в��ŵļ���
	 */
	private void listen() {
		if (!isStarted) {
			isStarted = true;
		}
		if (listener != null) {
			listener.OnPlay();// ���ż���
		}
		if (musicListener != null) {
			musicListener.OnMusicPlay();// ���ֲ��Ž���ļ���
		}
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer player) {
				doPlayNext();
			}
		});
	}

	/**
	 * ������ͣ
	 */
	private void doPause() {
		player.pause();
		isPausing = true;
	}

	/**
	 * ������һ��
	 */
	private void doPlayNext() {// ������һ��
		switchSongWithDirection(1);
	}

	/**
	 * ������һ��
	 */
	private void doPlayPrevious() {// ������һ��
		switchSongWithDirection(-1);
	}

	/**
	 * �������Ե��и衣������һ�ס���һ��
	 * 
	 * @param direction
	 *            <li>���ڵ���0����һ��<li>С��0����һ��
	 */
	private void switchSongWithDirection(int direction) {
		isPausing = false;
		switch (binderObject.getPlayMethod()) {
		case SEQUENCE_PLAY:// ˳����һ�ײ���
			if (direction < 0) {// ��һ��
				if (currentSongPosition == 0) {
					Utils.showToast(getApplicationContext(), "��ǰ�ǵ�һ�׸���!");
				} else {
					currentSongPosition--;
				}
			} else {// ��һ��
				if (currentSongPosition == songList.size() - 1) {
					Utils.showToast(getApplicationContext(), "��ǰ�����һ�׸���!");
				} else {
					currentSongPosition++;
				}
			}
			break;
		case RANDOM_PLAY:// �������
			Random random = new Random();
			currentSongPosition = random.nextInt(songList.size());
			break;
		case LIST_LOOP:// �б�ѭ������
			if (direction < 0) {// ��һ��
				if (currentSongPosition == 0) {// ע�������Ҫ�жϲ��ŷ�ʽ������ɣ�
					currentSongPosition = songList.size() - 1;
				} else {
					currentSongPosition--;
				}
			} else {// ��һ��
				if (currentSongPosition == songList.size() - 1) {// ע�������Ҫ�жϲ��ŷ�ʽ������ɣ�
					currentSongPosition = 0;
				} else {
					currentSongPosition++;
				}
			}
			break;
		case SINGLE_LOOP:// ����ѭ������
			currentSongPosition = currentSongPosition - 0;
			break;
		}
		doPlay();
	}

	/**
	 * ���ݸ���λ��ֱ���и�
	 * 
	 * @param position
	 *            ����λ��
	 */
	private void doSwitchSong(int position) {
		isPausing = false;
		currentSongPosition = position;
		doPlay();
	}

	/**
	 * ���ֲ��Žӿ�
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
		 * ���/����/�϶�����������
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
	 * ���ż���
	 */
	private OnPlayListener listener;

	public interface OnPlayListener {
		void OnPlay();
	}

	/**
	 * ���Ž���Ĳ��ż���
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
	 * ��ʼ����Service��Ҫ���ݵĶ���
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
	 * �������ֲ��ŷ�ʽ
	 */
	public enum PlayMethod {
		SEQUENCE_PLAY, RANDOM_PLAY, LIST_LOOP, SINGLE_LOOP
	}
}
