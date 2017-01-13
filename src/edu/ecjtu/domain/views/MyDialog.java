package edu.ecjtu.domain.views;

import java.io.File;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings.System;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.views.DeleteDialog.MyListener;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class MyDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button btn_dialog_cancel;
	private TextView tv_dialog_favorite, tv_dialog_songname;
	private RelativeLayout rl_dialog_favorite, rl_dialog_ringtong,
			rl_dialog_delete;
	private ImageView iv_dialog_favorite;
	private ContentResolver resolver;
	private Song song;
	private String operationMsg;
	private String isFavorite;
	private ContentValues values;

	public void setSong(Song song) {
		this.song = song;
	}

	public MyDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.songdialog_layout);

		initViews();
		initData();
		registerListeners();
	}

	@Override
	protected void onStart() {
		super.onStart();
		inflateViews();
	}

	private void initViews() {
		this.btn_dialog_cancel = (Button) findViewById(R.id.btn_dialog_cancel);
		this.rl_dialog_delete = (RelativeLayout) findViewById(R.id.rl_dialog_delete);
		this.rl_dialog_favorite = (RelativeLayout) findViewById(R.id.rl_dialog_favorite);
		this.rl_dialog_ringtong = (RelativeLayout) findViewById(R.id.rl_dialog_ringtong);
		this.iv_dialog_favorite = (ImageView) findViewById(R.id.iv_dialog_favorite);
		this.tv_dialog_favorite = (TextView) findViewById(R.id.tv_dialog_favorite);
		this.tv_dialog_songname = (TextView) findViewById(R.id.tv_dialog_songname);
	}

	private void registerListeners() {
		this.btn_dialog_cancel.setOnClickListener(this);
		this.rl_dialog_favorite.setOnClickListener(this);
		this.rl_dialog_ringtong.setOnClickListener(this);
		this.rl_dialog_delete.setOnClickListener(this);
	}

	private void initData() {
		resolver = getContext().getContentResolver();
		values = new ContentValues();
	}

	private void inflateViews() {
		isFavorite = song.isFavorite();
		// �ж��ղ�״̬
		if (isFavorite.equals("1")) {
			this.iv_dialog_favorite.setImageResource(R.drawable.favorited_icon);
			this.tv_dialog_favorite.setText("ȡ���ղ�");
		} else {
			this.iv_dialog_favorite
					.setImageResource(R.drawable.song_addto_favorite);
			this.tv_dialog_favorite.setText("�ղ�");
		}
	}

	/**
	 * ������ʾ������textview�ĸ�����
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.tv_dialog_songname.setText(text);
	}

	@Override
	public void onClick(View v) {
		boolean isCancel = false;
		boolean isDelete = false;
		operationMsg = "";
		switch (v.getId()) {
		case R.id.rl_dialog_favorite:// ����ղ�
			favoriteOperation();
			break;
		case R.id.rl_dialog_ringtong:// ����Ϊ����
			setSongToBeRingtong();
			break;
		case R.id.rl_dialog_delete:// ɾ��
			DeleteDialog deleteDialog = new DeleteDialog(getContext());
			deleteDialog.setSong(song);
			deleteDialog.setCancelable(true);
			deleteDialog.show();
			deleteDialog.setOnListen(new MyListener() {

				@Override
				public void listen(boolean isDeleted) {
					songDeleteSuccessedListener
							.OnSongDeleteSuccessed(isDeleted);
				}
			});
			isDelete = true;
			break;
		case R.id.btn_dialog_cancel:
			isCancel = true;
			break;
		}
		if (!isCancel && !isDelete) {
			Toast.makeText(getContext(), operationMsg, 0).show();
		}
		this.dismiss();
	}

	/**
	 * ����������Ϊ����
	 */
	private void setSongToBeRingtong() {
		String absolutePath = song.getSongpath();
		File file = new File(absolutePath);
		values.put(MediaColumns.DATA, absolutePath);
		values.put(MediaColumns.TITLE, song.getSongname());
		values.put(MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaColumns.SIZE, file.length());
		values.put(Media._ID, song.get_id());
		values.put(Media.DURATION, song.getDuration());
		values.put(Media.ARTIST, song.getArtist().getArtistname());
		values.put(Media.ALBUM, song.getAlbum().getAlbumname());
		values.put(Media.IS_NOTIFICATION, 0);
		values.put(Media.IS_ALARM, 0);
		values.put(Media.IS_MUSIC, 0);
		values.put(Media.IS_RINGTONE, 1);
		Uri uri = Media.getContentUriForPath(absolutePath);// ִ�����ݿ����uri
		resolver.delete(uri, Media._ID + "=?", new String[] { song.get_id() });// ������ϵͳ���ݿ���ɾ�����׸裬Ȼ�������
		resolver.delete(MusicProvider.SONG_URI, MusicProvider.TABLE_SONG_ID
				+ "=?", new String[] { song.get_id() });// �������ݱ���ɾ�����Ա��ٴ���ӽ����ݿ�
		Uri newUri = resolver.insert(uri, values);// ��ȡ���ݿ��е���Ҫ����������uri
		RingtoneManager.setActualDefaultRingtoneUri(getContext(),
				RingtoneManager.TYPE_RINGTONE, newUri);
		boolean flag = System.putString(resolver, System.RINGTONE,
				newUri.toString());
		if (flag) {
			operationMsg = "�������óɹ�";
		} else {
			operationMsg = "��������ʧ��";
		}
	}

	/**
	 * �ղذ�ť�ķ���
	 */
	private void favoriteOperation() {
		int favoriteState = Integer.valueOf(isFavorite) == 1 ? 0 : 1;
		values.put(MusicProvider.TABLE_SONG_ISFAVORITE, favoriteState);
		resolver.update(MusicProvider.SONG_URI, values,
				MusicProvider.TABLE_SONG_ID + "=?",
				new String[] { song.get_id() });
		listener.OnFavoriteStateChange(!(favoriteState == 0));// ���������м���
		if (favoriteState == 0) {// ȡ���ղص�״̬
			operationMsg = "ȡ���ղسɹ�";
			tv_dialog_favorite.setText("�ղ�");
			iv_dialog_favorite.setImageResource(R.drawable.song_addto_favorite);
		} else {// �ղسɹ�
			operationMsg = "�����ղسɹ�";
			tv_dialog_favorite.setText("ȡ���ղ�");
			iv_dialog_favorite.setImageResource(R.drawable.favorited_icon);
		}
		values.clear();
	}

	private OnFavoriteStateChangeListener listener;

	public interface OnFavoriteStateChangeListener {
		void OnFavoriteStateChange(boolean isFavorite);
	};

	/**
	 * �����Ƿ��ղط���
	 * 
	 * @param listener
	 */
	public void setOnFavoriteStateChangeListener(
			OnFavoriteStateChangeListener listener) {
		this.listener = listener;
	}

	private OnSongDeleteSuccessedListener songDeleteSuccessedListener;

	public interface OnSongDeleteSuccessedListener {
		void OnSongDeleteSuccessed(boolean isDeleted);
	}

	/**
	 * �����Ƿ�ɹ�ɾ������
	 */
	public void setOnSongDeleteSuccessedListener(
			OnSongDeleteSuccessedListener songDeleteSuccessedListener) {
		this.songDeleteSuccessedListener = songDeleteSuccessedListener;
	}
}
