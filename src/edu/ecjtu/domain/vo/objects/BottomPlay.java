package edu.ecjtu.domain.vo.objects;

import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ecjtu.domain.views.MyProgressBar;
import edu.ecjtu.domain.vo.interfaces.ActivityInterface;
import edu.ecjtu.musicplayer.R;

public class BottomPlay {

	private ImageView iv_my_start, iv_my_previous, iv_my_next;
	private TextView tv_my_songname, tv_my_artistname;
	private Handler handler;
	private int REFRESH_BOTTOM = 4000;
	private BinderObject binderObject;
	private boolean isActivityDestroy = false;
	private ActivityInterface activityInterface;

	public BottomPlay(ActivityInterface activityInterface,
			ImageView iv_my_start, ImageView iv_my_previous,
			ImageView iv_my_next, TextView tv_my_songname,
			TextView tv_my_artistname, MyProgressBar mpb_musicplay,
			Handler handler, BinderObject binderObject) {
		super();
		this.activityInterface = activityInterface;
		this.iv_my_start = iv_my_start;
		this.iv_my_previous = iv_my_previous;
		this.iv_my_next = iv_my_next;
		this.tv_my_songname = tv_my_songname;
		this.tv_my_artistname = tv_my_artistname;
		this.binderObject = binderObject;
		this.handler = handler;
	}

	public BottomPlay() {
		super();
	}

	/**
	 * 开始播放
	 */
	public void startPlay() {
		binderObject.getPlayBinder().play();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * 暂停播放
	 */
	public void pausePlay() {
		binderObject.getPlayBinder().pause();
		iv_my_start.setImageResource(R.drawable.start);
	}

	/**
	 * 下一曲播放
	 */
	public void nextPlay() {
		binderObject.getPlayBinder().playNext();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * 上一曲播放
	 */
	public void previousPlay() {
		binderObject.getPlayBinder().playPrevious();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * 初始化底部布局的歌曲信息
	 */
	public void updateBottomLayoutViews(Song currentSong, MediaPlayer player) {
		if (currentSong != null) {
			iv_my_start.setEnabled(true);
			iv_my_previous.setEnabled(true);
			iv_my_next.setEnabled(true);
			if (!binderObject.getPlayBinder().getIsPlayOnline()) {// 本地播放时
				tv_my_songname.setText(currentSong.getSongname());
				tv_my_artistname.setText(currentSong.getArtist()
						.getArtistname());
			}
			if (player.isPlaying()) {
				iv_my_start.setImageResource(R.drawable.pause);
			}
		} else {
			iv_my_start.setEnabled(false);
			iv_my_previous.setEnabled(false);
			iv_my_next.setEnabled(false);
		}
		if (binderObject.getPlayBinder().getIsPlayOnline()) {// 更新在线播放的textview和按钮播放
			tv_my_songname.setText(binderObject.getOnlineMusic().getSongName());
			tv_my_artistname.setText(binderObject.getOnlineMusic()
					.getSingerName());
			iv_my_start.setEnabled(true);
		}
	}

	/**
	 * 刷新音乐进度
	 */
	public void infalteMusicProgressBar() {
		final MediaPlayer player = binderObject.getPlayer();
		final Long TimeMillis = System.currentTimeMillis();
		binderObject.setThreadTimeMillis(TimeMillis);
		if (activityInterface != null) {// 注册播放Activity是否销毁的监听
			activityInterface
					.setOnActivityDestroyListener(new OnActivityDestroyListener() {

						@Override
						public void onActivityDestroy() {
							isActivityDestroy = true;
						}
					});
		}
		new Thread(new Runnable() {
			private Long threadTimeMillis = TimeMillis;// 标识当前的线程

			@Override
			public void run() {
				while (!isActivityDestroy) {
					handler.sendEmptyMessage(REFRESH_BOTTOM);// 先刷新后退出
					if (!this.threadTimeMillis.equals(binderObject
							.getThreadTimeMillis()) || !player.isPlaying()) {// 播放上一曲、下一曲、暂停时,跳出循环终止线程,节省系统资源
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
