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
	 * ��������seekbar
	 */
	private void updateSoundBar() {
		/**
		 * ע��AudioManager.STREAM_MUSIC-->��ʾ����ý�岥������
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
	 * ��������ͼ��
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
				String textViewMsg = currentSong.isFavorite().equals("1") ? "ȡ���ղ�"
						: "�ղ�";
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
	 * �޸��ղ�״̬
	 */
	private void updateFavoriteState() {
		values = new ContentValues();
		String toastMsg = "";
		String updateValue = "";
		String textViewMsg = "";
		int drawableResId = -1;
		resolver = getContext().getContentResolver();
		if (currentSong.isFavorite().equals("0")) {// ��������ղ�״̬
			drawableResId = R.drawable.more_favorited;
			textViewMsg = "ȡ���ղ�";
			toastMsg = "�ղسɹ���";
			updateValue = "1";
			isFavorited = true;
		} else {// ������ղ�״̬
			drawableResId = R.drawable.more_add_to_favorite;
			textViewMsg = "�ղ�";
			toastMsg = "ȡ���ղسɹ���";
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
					Utils.showToast(getContext(), "��ǰ���޸������ղأ�");
				}
			} else {
				Utils.showToast(getContext(), "��ǰ���Ÿ����Ǳ��ظ����������غ��ղأ�");
			}
			break;
		case R.id.iv_more_sound:// ���þ������
			if (currentVolume != 0) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);// true:����ģʽ��false:���Ǿ���ģʽ
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
					if (networkType == ConnectivityManager.TYPE_MOBILE) {// ������ƶ�����
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
					Utils.showToast(getContext(), "�������Ӵ������������������ӣ�");
				}
			} else {
				Utils.showToast(getContext(), "SD��δ��ȷ��װ����������SD��");
			}
			break;
		}
	}

	/**
	 * ��ʼ����
	 */
	public void startDownload() {
		Utils.showToast(getContext(), "��ʼ����");
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
		// // ��ȡ��������
		Request downloadRequest = new Request(Uri.parse(MP3_URL));
		// �������ص�·��
		downloadRequest.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,
				DOWNLOAD_FILE_NAME);
		// ����������ʾ��Ϣ
		downloadRequest.setTitle(DOWNLOAD_FILE_NAME.subSequence(0,
				DOWNLOAD_FILE_NAME.lastIndexOf(".")));
		downloadRequest.setDescription(DOWNLOAD_FILE_NAME.subSequence(0,
				DOWNLOAD_FILE_NAME.lastIndexOf(".")) + "��������");
		downloadRequest
				.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// ������ɺ���ʾ֪ͨ��
		downloadRequest.setVisibleInDownloadsUi(true);// ��ʾ������UI��
		// downloadRequest.setAllowedOverRoaming(false);//��������״̬���Ƿ��������
		downloadRequest.allowScanningByMediaScanner();// ����MediaScannerɨ��
		// �����������ݵ�MIMETYPE����
		downloadRequest.setMimeType("audio/mpeg");
		// ��ʼ����
		downloadManager.enqueue(downloadRequest);
	}

	/**
	 * �����������״̬
	 * 
	 * @return <li>true:����������<li>false:����δ����
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
