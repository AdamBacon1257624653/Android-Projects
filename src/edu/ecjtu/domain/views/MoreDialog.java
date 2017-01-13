package edu.ecjtu.domain.views;

import java.io.File;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class MoreDialog extends Dialog implements
		android.view.View.OnClickListener {
	private ContentValues values;
	private ContentResolver resolver;
	private Song currentSong;
	private TextView tv_more_favorite;
	private ImageView iv_more_favorite, iv_more_sound, iv_more_download;
	private SeekBar sb_more_sound;
	private boolean isFavorited = false;
	private TextView tv_volume;
	private OnFavoriteStateUpdateListener listener;
	private AudioManager audioManager;
	private int maxVolume;
	private int currentVolume;
	private int volumePercent;
//	private OnlineMusic music;
	private NetworkInfo networkInfo;
	private String DOWNLOAD_FOLDER_NAME;
	private String DOWNLOAD_FILE_NAME;
	private DownloadManager downloadManager;
//	private long downloadId;

	protected MoreDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MoreDialog(Context context, int theme, Song currentSong) {
		super(context, theme);
		this.currentSong = currentSong;
	}

	public MoreDialog(Context context) {
		super(context);
	}

//	public void setMusic(OnlineMusic music) {
//		this.music = music;
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play_more_layout);
		audioManager = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		initViews();
		updateSoundBar();
		inflateViews();
		registerListeners();
	}

	/**
	 * 更新声音seekbar
	 */
	private void updateSoundBar() {
		/**
		 * 注：AudioManager.STREAM_MUSIC-->表示的是媒体播放音量
		 */
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sb_more_sound.setMax(maxVolume);
		sb_more_sound.setProgress(currentVolume);
		volumePercent = (int) ((currentVolume * 1.0f / maxVolume * 1.0f) * 100);
		tv_volume.setText(volumePercent + "");
		updateVolumeIcon(currentVolume);
	}

	/**
	 * 更新音量图标
	 */
	private void updateVolumeIcon(int volumeValue) {
		int volumeResId = volumeValue == 0 ? R.drawable.mute_icon
				: R.drawable.sound_icon;
		iv_more_sound.setImageResource(volumeResId);
	}

	private void initViews() {
		tv_more_favorite = (TextView) findViewById(R.id.tv_more_favorite);
		iv_more_favorite = (ImageView) findViewById(R.id.iv_more_favorite);
		sb_more_sound = (SeekBar) findViewById(R.id.sb_more_sound);
		tv_volume = (TextView) findViewById(R.id.tv_volume);
		iv_more_sound = (ImageView) findViewById(R.id.iv_more_sound);
		iv_more_download = (ImageView) findViewById(R.id.iv_more_download);
	}

	private void inflateViews() {
		if (!PlayUtils.binderObject.getPlayBinder().getIsPlayOnline()) {
			if (currentSong != null) {
				int resId = currentSong.isFavorite().equals("1") ? R.drawable.more_favorited
						: R.drawable.more_add_to_favorite;
				String textViewMsg = currentSong.isFavorite().equals("1") ? "取消收藏"
						: "收藏";
				iv_more_favorite.setImageResource(resId);
				tv_more_favorite.setText(textViewMsg);
			}
			iv_more_download.setImageResource(R.drawable.more_disable_download);
			iv_more_download.setEnabled(false);
		} else {
			iv_more_download.setImageResource(R.drawable.more_enable_download);
			iv_more_download.setEnabled(true);
		}
	}

	private void registerListeners() {
		iv_more_favorite.setOnClickListener(this);
		iv_more_sound.setOnClickListener(this);
		iv_more_download.setOnClickListener(this);
		sb_more_sound.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				volumePercent = (int) ((progress * 1.0f / maxVolume * 1.0f) * 100);
				tv_volume.setText(volumePercent + "");
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_PLAY_SOUND);
				updateVolumeIcon(progress);
			}
		});
	}

	/**
	 * 修改收藏状态
	 */
	private void updateFavoriteState() {
		values = new ContentValues();
		String toastMsg = "";
		String updateValue = "";
		String textViewMsg = "";
		int drawableResId = -1;
		resolver = getContext().getContentResolver();
		if (currentSong.isFavorite().equals("0")) {// 如果不是收藏状态
			drawableResId = R.drawable.more_favorited;
			textViewMsg = "取消收藏";
			toastMsg = "收藏成功！";
			updateValue = "1";
			isFavorited = true;
		} else {// 如果是收藏状态
			drawableResId = R.drawable.more_add_to_favorite;
			textViewMsg = "收藏";
			toastMsg = "取消收藏成功！";
			updateValue = "0";
			isFavorited = false;
		}
		iv_more_favorite.setImageResource(drawableResId);
		tv_more_favorite.setText(textViewMsg);
		currentSong.setIsFavorite(updateValue);
		values.put(MusicProvider.TABLE_SONG_ISFAVORITE, updateValue);
		resolver.update(MusicProvider.SONG_URI, values,
				MusicProvider.TABLE_SONG_ID + "=?",
				new String[] { currentSong.get_id() });
		values.clear();
		Utils.showToast(getContext(), toastMsg);
		if (listener != null) {
			listener.OnFavoriteStateUpdate(isFavorited);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_more_favorite:
			if (!PlayUtils.binderObject.getPlayBinder().getIsPlayOnline()) {
				if (currentSong != null) {
					updateFavoriteState();
				} else {
					Utils.showToast(getContext(), "当前暂无歌曲可收藏！");
				}
			} else {
				Utils.showToast(getContext(), "当前播放歌曲非本地歌曲，请下载后收藏！");
			}
			break;
		case R.id.iv_more_sound:// 设置静音与否
			if (currentVolume != 0) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);// true:静音模式。false:不是静音模式
			} else {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			}
			updateSoundBar();
			break;
		case R.id.iv_more_download:
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				if (checkNetworkState()) {
					int networkType = networkInfo.getType();
					if (networkType == ConnectivityManager.TYPE_MOBILE) {// 如果是移动流量
						DataWarningDialog dataWarningDialog = new DataWarningDialog(
								getContext(), R.style.dialog_theme);
						dataWarningDialog.setCancelable(true);
						dataWarningDialog.setMoreDialog(this);
						dataWarningDialog.show();
						this.dismiss();
					} else {
						startDownload();
						this.dismiss();
					}
				} else {
					Utils.showToast(getContext(), "网络连接错误，请检查您的网络连接！");
				}
			} else {
				Utils.showToast(getContext(), "SD卡未正确安装，请检查您的SD卡");
			}
			break;
		}
	}

	/**
	 * 开始下载
	 */
	public void startDownload() {
		Utils.showToast(getContext(), "开始下载");
		DOWNLOAD_FOLDER_NAME = "FeiChangMusic";
		DOWNLOAD_FILE_NAME = PlayUtils.binderObject.getOnlineMusic()
				.getSongName() + ".mp3";
		String MP3_URL = PlayUtils.binderObject.getOnlineMusic().getUrlPath();
		File saveFolder = Environment
				.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
		if (!saveFolder.exists() || !saveFolder.isDirectory()) {
			saveFolder.mkdirs();
		}
		downloadManager = (DownloadManager) getContext().getSystemService(
				Context.DOWNLOAD_SERVICE);
		// // 获取下载请求
		Request downloadRequest = new Request(Uri.parse(MP3_URL));
		// 设置下载的路径
		downloadRequest.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,
				DOWNLOAD_FILE_NAME);
		// 设置下载显示信息
		downloadRequest.setTitle(DOWNLOAD_FILE_NAME.subSequence(0,
				DOWNLOAD_FILE_NAME.lastIndexOf(".")));
		downloadRequest.setDescription(DOWNLOAD_FILE_NAME.subSequence(0,
				DOWNLOAD_FILE_NAME.lastIndexOf(".")) + "正在下载");
		downloadRequest
				.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 下载完成后显示通知栏
		downloadRequest.setVisibleInDownloadsUi(true);// 显示在下载UI中
		// downloadRequest.setAllowedOverRoaming(false);//设置漫游状态下是否可以下载
		downloadRequest.allowScanningByMediaScanner();// 用于MediaScanner扫描
		// 设置下载内容的MIMETYPE类型
		downloadRequest.setMimeType("audio/mpeg");
		// 开始下载
		downloadManager.enqueue(downloadRequest);
	}

	/**
	 * 检查网络连接状态
	 * 
	 * @return <li>true:网络已连接<li>false:网络未连接
	 */
	private boolean checkNetworkState() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null
				|| !connectivityManager.getBackgroundDataSetting()) {
			return false;
		}
		return true;
	}

	public interface OnFavoriteStateUpdateListener {
		void OnFavoriteStateUpdate(boolean isFavorited);
	}

	public void setOnFavoriteStateUpdateListener(
			OnFavoriteStateUpdateListener listener) {
		this.listener = listener;

	}
}
