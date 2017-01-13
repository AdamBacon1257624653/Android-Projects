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
		// 判断收藏状态
		if (isFavorite.equals("1")) {
			this.iv_dialog_favorite.setImageResource(R.drawable.favorited_icon);
			this.tv_dialog_favorite.setText("取消收藏");
		} else {
			this.iv_dialog_favorite
					.setImageResource(R.drawable.song_addto_favorite);
			this.tv_dialog_favorite.setText("收藏");
		}
	}

	/**
	 * 设置显示歌曲名textview的歌曲名
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
		case R.id.rl_dialog_favorite:// 添加收藏
			favoriteOperation();
			break;
		case R.id.rl_dialog_ringtong:// 设置为铃声
			setSongToBeRingtong();
			break;
		case R.id.rl_dialog_delete:// 删除
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
	 * 将歌曲设置为铃声
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
		Uri uri = Media.getContentUriForPath(absolutePath);// 执行数据库操作uri
		resolver.delete(uri, Media._ID + "=?", new String[] { song.get_id() });// 首先在系统数据库中删除该首歌，然后再添加
		resolver.delete(MusicProvider.SONG_URI, MusicProvider.TABLE_SONG_ID
				+ "=?", new String[] { song.get_id() });// 此条数据必须删除，以便再次添加进数据库
		Uri newUri = resolver.insert(uri, values);// 获取数据库中的需要设置铃声的uri
		RingtoneManager.setActualDefaultRingtoneUri(getContext(),
				RingtoneManager.TYPE_RINGTONE, newUri);
		boolean flag = System.putString(resolver, System.RINGTONE,
				newUri.toString());
		if (flag) {
			operationMsg = "铃声设置成功";
		} else {
			operationMsg = "铃声设置失败";
		}
	}

	/**
	 * 收藏按钮的方法
	 */
	private void favoriteOperation() {
		int favoriteState = Integer.valueOf(isFavorite) == 1 ? 0 : 1;
		values.put(MusicProvider.TABLE_SONG_ISFAVORITE, favoriteState);
		resolver.update(MusicProvider.SONG_URI, values,
				MusicProvider.TABLE_SONG_ID + "=?",
				new String[] { song.get_id() });
		listener.OnFavoriteStateChange(!(favoriteState == 0));// 监听器进行监听
		if (favoriteState == 0) {// 取消收藏的状态
			operationMsg = "取消收藏成功";
			tv_dialog_favorite.setText("收藏");
			iv_dialog_favorite.setImageResource(R.drawable.song_addto_favorite);
		} else {// 收藏成功
			operationMsg = "歌曲收藏成功";
			tv_dialog_favorite.setText("取消收藏");
			iv_dialog_favorite.setImageResource(R.drawable.favorited_icon);
		}
		values.clear();
	}

	private OnFavoriteStateChangeListener listener;

	public interface OnFavoriteStateChangeListener {
		void OnFavoriteStateChange(boolean isFavorite);
	};

	/**
	 * 监听是否收藏方法
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
	 * 监听是否成功删除方法
	 */
	public void setOnSongDeleteSuccessedListener(
			OnSongDeleteSuccessedListener songDeleteSuccessedListener) {
		this.songDeleteSuccessedListener = songDeleteSuccessedListener;
	}
}
