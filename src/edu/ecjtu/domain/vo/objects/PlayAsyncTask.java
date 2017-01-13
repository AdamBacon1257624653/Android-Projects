package edu.ecjtu.domain.vo.objects;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.SeekBar;
import android.widget.TextView;
import edu.ecjtu.activities.MusicPlayActivity;
import edu.ecjtu.activities.MusicPlayActivity.OnRefreshProgressListener;

public class PlayAsyncTask extends AsyncTask<Object, Integer, String> {

	private SeekBar seekBar;
	private TextView tv_progress;
	private MusicPlayActivity mpActivity;
	private boolean isActivityDestroy = false;
	private int duration;
	private MediaPlayer player;

	public PlayAsyncTask(MusicPlayActivity mpActivity, SeekBar seekBar,
			TextView tv_progress) {
		super();
		this.seekBar = seekBar;
		this.tv_progress = tv_progress;
		this.mpActivity = mpActivity;
		init();
	}

	private void init() {
		mpActivity
				.setOnRefreshProgressListener(new OnRefreshProgressListener() {

					@Override
					public void OnRefreshProgress(boolean isActivityDestroy) {
						PlayAsyncTask.this.isActivityDestroy = isActivityDestroy;
					}
				});
	}

	@Override
	protected String doInBackground(Object... params) {
		player = (MediaPlayer) params[0];
		duration = player.getDuration();
		this.seekBar.setMax(player.getDuration());
		while (true) {
			int currPosition = player.getCurrentPosition();
			publishProgress(currPosition);
			if (currPosition == duration || this.isActivityDestroy
					|| !player.isPlaying()) {
				break;
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Integer currPosition = values[0];
		updateProgress(currPosition);
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	/**
	 * 刷新进度信息
	 * 
	 * @param currPosition
	 */
	private void updateProgress(Integer currPosition) {
		String progress = mpActivity.formatTime(currPosition + "");
		seekBar.setProgress(currPosition);
		seekBar.invalidate();
		tv_progress.setText(progress);
	}
}
