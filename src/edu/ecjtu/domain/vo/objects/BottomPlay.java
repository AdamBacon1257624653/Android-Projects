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
	 * ��ʼ����
	 */
	public void startPlay() {
		binderObject.getPlayBinder().play();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * ��ͣ����
	 */
	public void pausePlay() {
		binderObject.getPlayBinder().pause();
		iv_my_start.setImageResource(R.drawable.start);
	}

	/**
	 * ��һ������
	 */
	public void nextPlay() {
		binderObject.getPlayBinder().playNext();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * ��һ������
	 */
	public void previousPlay() {
		binderObject.getPlayBinder().playPrevious();
		iv_my_start.setImageResource(R.drawable.pause);
	}

	/**
	 * ��ʼ���ײ����ֵĸ�����Ϣ
	 */
	public void updateBottomLayoutViews(Song currentSong, MediaPlayer player) {
		if (currentSong != null) {
			iv_my_start.setEnabled(true);
			iv_my_previous.setEnabled(true);
			iv_my_next.setEnabled(true);
			if (!binderObject.getPlayBinder().getIsPlayOnline()) {// ���ز���ʱ
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
		if (binderObject.getPlayBinder().getIsPlayOnline()) {// �������߲��ŵ�textview�Ͱ�ť����
			tv_my_songname.setText(binderObject.getOnlineMusic().getSongName());
			tv_my_artistname.setText(binderObject.getOnlineMusic()
					.getSingerName());
			iv_my_start.setEnabled(true);
		}
	}

	/**
	 * ˢ�����ֽ���
	 */
	public void infalteMusicProgressBar() {
		final MediaPlayer player = binderObject.getPlayer();
		final Long TimeMillis = System.currentTimeMillis();
		binderObject.setThreadTimeMillis(TimeMillis);
		if (activityInterface != null) {// ע�Ქ��Activity�Ƿ����ٵļ���
			activityInterface
					.setOnActivityDestroyListener(new OnActivityDestroyListener() {

						@Override
						public void onActivityDestroy() {
							isActivityDestroy = true;
						}
					});
		}
		new Thread(new Runnable() {
			private Long threadTimeMillis = TimeMillis;// ��ʶ��ǰ���߳�

			@Override
			public void run() {
				while (!isActivityDestroy) {
					handler.sendEmptyMessage(REFRESH_BOTTOM);// ��ˢ�º��˳�
					if (!this.threadTimeMillis.equals(binderObject
							.getThreadTimeMillis()) || !player.isPlaying()) {// ������һ������һ������ͣʱ,����ѭ����ֹ�߳�,��ʡϵͳ��Դ
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
