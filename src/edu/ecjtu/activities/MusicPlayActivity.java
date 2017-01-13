package edu.ecjtu.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.services.PlayService.OnMusicOnlinePlayListener;
import edu.ecjtu.domain.services.PlayService.OnMusicPlayListener;
import edu.ecjtu.domain.views.MoreDialog;
import edu.ecjtu.domain.views.MoreDialog.OnFavoriteStateUpdateListener;
import edu.ecjtu.domain.vo.interfaces.PlayInterface;
import edu.ecjtu.domain.vo.objects.BinderObject;
import edu.ecjtu.domain.vo.objects.PlayAsyncTask;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class MusicPlayActivity extends Activity implements OnClickListener {

	private RelativeLayout rl_dropdown;
	private SeekBar sb_play;
	private ImageView iv_play_more, iv_musicplay_previous, iv_musicplay_start,
			iv_musicplay_next, iv_musicplay_backward, iv_musicplay_speed,
			iv_play_favorite;
	private TextView tv_music_play_songname, tv_play_time, tv_play_duration,
			tv_musicplay_singername;
	private Song currentSong;
	private BinderObject binderObject;
	private MediaPlayer player;
	private PlayInterface playBinder;
	private boolean isActivityPaused = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_paly);

		initViews();
		initData();
		registerListeners();
	}

	@Override
	protected void onStart() {
		super.onStart();
		inflateViews();
	}

	@Override
	protected void onResume() {
		isActivityPaused = false;
		super.onResume();
		registerBufferingListener();
	}

	private void initViews() {
		rl_dropdown = (RelativeLayout) findViewById(R.id.rl_dropdown);
		sb_play = (SeekBar) findViewById(R.id.sb_play);
		iv_play_more = (ImageView) findViewById(R.id.iv_play_more);
		iv_musicplay_previous = (ImageView) findViewById(R.id.iv_musicplay_previous);
		iv_musicplay_start = (ImageView) findViewById(R.id.iv_musicplay_start);
		iv_musicplay_next = (ImageView) findViewById(R.id.iv_musicplay_next);
		iv_musicplay_backward = (ImageView) findViewById(R.id.iv_musicplay_backward);
		iv_play_favorite = (ImageView) findViewById(R.id.iv_play_favorite);
		iv_musicplay_speed = (ImageView) findViewById(R.id.iv_musicplay_speed);
		tv_music_play_songname = (TextView) findViewById(R.id.tv_music_play_songname);
		tv_play_time = (TextView) findViewById(R.id.tv_play_time);
		tv_play_duration = (TextView) findViewById(R.id.tv_play_duration);
		tv_musicplay_singername = (TextView) findViewById(R.id.tv_musicplay_singername);
	}

	private void initData() {
		initBinderData();
	}

	private void initBinderData() {
		binderObject = PlayUtils.binderObject;
		if (binderObject.getCurrentSong() != null) {
			currentSong = binderObject.getCurrentSong();
		}
		player = binderObject.getPlayer();
		playBinder = binderObject.getPlayBinder();
	}

	private void inflateViews() {
		if (player != null && player.isPlaying()) {
			iv_musicplay_start.setImageResource(R.drawable.music_play_pause);
		} else {
			iv_musicplay_start.setImageResource(R.drawable.music_play_start);
		}
		// 更新收藏图标
		if (currentSong != null) {
			isFavorited = currentSong.isFavorite().equals("1") ? true : false;
			updateFavoriteImageView(isFavorited);
		}
		// 设置duration
		updateViews();
		updatePlayProgress();
	}

	/**
	 * 初始化进度条的时间状态
	 */
	public String formatTime(String timeStr) {
		int time = Integer.valueOf(timeStr);
		int minutes = time / (1000 * 60);
		String minutesStr = "";
		String secondsStr = "";
		if (minutes < 10) {
			minutesStr = "0" + minutes;
		}
		int seconds = time % (1000 * 60);
		if (seconds < 10000) {// 小于10秒
			secondsStr = "0" + seconds / 1000;
		} else {
			secondsStr = String.valueOf(seconds).substring(0, 2);
		}
		return minutesStr + ":" + secondsStr;
	}

	/**
	 * 更新组件状态信息
	 */
	private void updateViews() {
		currentSong = binderObject.getCurrentSong();
		if (currentSong != null) {
			updateShowViews();
			// 设置可用性
			iv_musicplay_start.setEnabled(true);
			iv_musicplay_previous.setEnabled(true);
			iv_musicplay_next.setEnabled(true);
			iv_musicplay_backward.setEnabled(true);
			iv_musicplay_next.setEnabled(true);
		} else {
			iv_musicplay_start.setEnabled(false);
			iv_musicplay_previous.setEnabled(false);
			iv_musicplay_next.setEnabled(false);
			iv_musicplay_backward.setEnabled(false);
			iv_musicplay_speed.setEnabled(false);
		}
	}

	/**
	 * 更改各大组件的显示信息.eg:textview,seekbar
	 */
	private void updateShowViews() {
		String songName = "";
		String singerName = "";
		String duration = "";
		if (!playBinder.getIsPlayOnline()) {
			sb_play.setSecondaryProgress(0);
			songName = currentSong.getSongname();
			singerName = currentSong.getArtist().getArtistname();
			duration = currentSong.getDuration();
		} else {
			songName = binderObject.getOnlineMusic().getSongName();
			singerName = binderObject.getOnlineMusic().getSingerName();
			duration = player.getDuration() + "";
		}
		duration = formatTime(duration);
		tv_music_play_songname.setText(songName);
		tv_musicplay_singername.setText(singerName);
		tv_play_duration.setText(duration);
	}

	private void updatePlayProgress() {
		if (playBinder.getIsStarted()) {// 如果启动过，则启动该线程
			new PlayAsyncTask(this, sb_play, tv_play_time).execute(player);
		}
	}

	private void registerListeners() {
		rl_dropdown.setOnClickListener(this);
		iv_play_more.setOnClickListener(this);
		iv_musicplay_start.setOnClickListener(this);
		iv_musicplay_next.setOnClickListener(this);
		iv_musicplay_previous.setOnClickListener(this);
		iv_musicplay_speed.setOnClickListener(this);
		iv_musicplay_backward.setOnClickListener(this);
		sb_play.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private boolean fromUser;
			private int progress;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (fromUser) {
					if (currentSong != null) {
						player.seekTo(progress);
					} else {
						seekBar.setProgress(0);
						tv_play_time.setText("00:00");
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				this.fromUser = fromUser;
				if (fromUser) {
					tv_play_time.setText(formatTime(progress + ""));
					this.progress = progress;
				}
			}
		});
		playBinder.setOnMusicPlayListener(new OnMusicPlayListener() {

			@Override
			public void OnMusicPlay() {
				if (!isActivityPaused) {// 如果本播放界面失去焦点时，不启动线程
					updateViews();
					updatePlayProgress();
				}
			}

			@Override
			public void OnMusicPause() {
			}
		});
		playBinder
				.setOnMusicOnlinePlayListener(new OnMusicOnlinePlayListener() {

					@Override
					public void OnMusicOnlinePlay() {
						if (!isActivityPaused) {
							iv_musicplay_start
									.setImageResource(R.drawable.music_play_pause);
						}
					}
				});
	}

	/**
	 * 注册缓冲监听
	 */
	private void registerBufferingListener() {
		player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				if (!isActivityPaused) {
					sb_play.setSecondaryProgress((int) (mp.getDuration() * (percent * 1.0f / 100.0f)));
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_dropdown:// 关闭本Activity的监听
			finish();
			break;
		case R.id.iv_play_more:// ...的监听
			showMoreDialog();
			break;
		case R.id.iv_play_favorite:// 收藏的监听
			break;
		/**
		 * 播放方式
		 */
		case R.id.iv_musicplay_start:// 播放/暂停/恢复
			if (player.isPlaying()) {
				playBinder.pause();
				iv_musicplay_start
						.setImageResource(R.drawable.music_play_start);
			} else {
				playBinder.play();
				iv_musicplay_start
						.setImageResource(R.drawable.music_play_pause);
			}
			break;
		case R.id.iv_musicplay_next:// 下一首
			playBinder.playNext();
			iv_musicplay_start.setImageResource(R.drawable.music_play_pause);
			break;
		case R.id.iv_musicplay_previous:// 上一首
			playBinder.playPrevious();
			iv_musicplay_start.setImageResource(R.drawable.music_play_pause);
			break;
		case R.id.iv_musicplay_speed:// 快进播放
			int speedPosition = player.getCurrentPosition() + 10000;
			playSeekTo(speedPosition);
			break;
		case R.id.iv_musicplay_backward:// 快退播放
			int backwardPosition = player.getCurrentPosition() - 10000;
			playSeekTo(backwardPosition);
			break;
		}
	}

	/**
	 * 追溯到某个点进行播放
	 * 
	 * @param progress
	 */
	private void playSeekTo(int progress) {
		playBinder.playSeekTo(progress);
		if (!player.isPlaying()) {
			playBinder.play();
			playBinder.pause();
		}
	}

	/**
	 * more的dialog显示
	 */
	private void showMoreDialog() {
		MoreDialog moreDialog = new MoreDialog(this, R.style.dialog_theme,
				currentSong);
		moreDialog.setCancelable(true);
		moreDialog.show();
		moreDialog
				.setOnFavoriteStateUpdateListener(new OnFavoriteStateUpdateListener() {

					@Override
					public void OnFavoriteStateUpdate(boolean isFavorited) {
						updateFavoriteImageView(isFavorited);
					}
				});
	}

	/**
	 * 更新收藏状态的图标
	 * 
	 * @param isFavorited
	 */
	private void updateFavoriteImageView(boolean isFavorited) {
		if (!playBinder.getIsPlayOnline()) {
			int drawableResId = isFavorited ? R.drawable.music_play_favorited
					: R.drawable.music_play_favorite;
			iv_play_favorite.setImageResource(drawableResId);
		}
	}

	@Override
	protected void onPause() {
		isActivityPaused = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (listener != null) {
			listener.OnRefreshProgress(true);
		}
		super.onDestroy();
	}

	/**
	 * 设置当前Activity是否销毁的监听
	 */
	private OnRefreshProgressListener listener;
	private boolean isFavorited;

	public interface OnRefreshProgressListener {
		void OnRefreshProgress(boolean isActivityDestroy);
	}

	public void setOnRefreshProgressListener(OnRefreshProgressListener listener) {
		this.listener = listener;
	}
}
