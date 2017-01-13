package edu.ecjtu.domain.views;

import java.io.File;
import java.util.List;

import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteDialog extends Dialog implements
		android.view.View.OnClickListener {

	private CheckBox cb_deletedialog;
	private TextView tv_deletedialog_message, tv_dialog_message_sufix;
	private Button btn_cancel, btn_confirm;
	private Song song;
	private FilePath filePath;
	private Artist artist;
	private Album album;
	private ContentResolver resolver;

	public void setSong(Song song) {
		this.song = song;
	}

	public void setFilePath(FilePath filePath) {
		this.filePath = filePath;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public DeleteDialog(Context context) {
		super(context);
	}

	public DeleteDialog(Context context, FilePath filePath) {
		super(context);
	}

	public DeleteDialog(Context context, Artist artist) {
		super(context);
	}

	public DeleteDialog(Context context, Album album) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.deletedialog_layout);

		initViews();
		inflateViews();
		registerListeners();
	}

	private void initViews() {
		this.cb_deletedialog = (CheckBox) findViewById(R.id.cb_deletedialog);
		this.tv_deletedialog_message = (TextView) findViewById(R.id.tv_deletedialog_message);
		this.tv_dialog_message_sufix = (TextView) findViewById(R.id.tv_dialog_message_sufix);
		this.btn_cancel = (Button) findViewById(R.id.btn_cancel);
		this.btn_confirm = (Button) findViewById(R.id.btn_confirm);
	}

	/**
	 * 填充tv_deletedialog_message的数据
	 */
	private void inflateViews() {
		if (song != null) {
			this.tv_deletedialog_message
					.setText("'" + song.getSongname() + "'");
		} else if (filePath != null) {
			setMessages(filePath.getParentname(), "文件夹");
		} else if (artist != null) {
			setMessages(artist.getArtistname(), "歌手");
		} else if (album != null) {
			setMessages(album.getAlbumname(), "专辑");
		}

	}

	/**
	 * 设置删除窗口的文字信息
	 * 
	 * @param message
	 *            message前部分
	 * @param suffixMsg
	 *            后缀部分
	 */
	public void setMessages(String message, String suffixMsg) {
		this.tv_deletedialog_message.setText("'" + message + "'");
		this.tv_dialog_message_sufix.setText(suffixMsg + "下的所有歌曲吗？");
		this.cb_deletedialog.setText("同时删除源文件");
	}

	private void registerListeners() {
		this.btn_confirm.setOnClickListener(this);
		this.btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_confirm:// 确认
			if (song != null) {
				DeleteSong();
			} else if (filePath != null) {
				DeleteFilepath();
			} else if (artist != null) {
				DeleteArtist();
			} else if (album != null) {
				DeleteAlbum();
			}
			break;
		case R.id.btn_cancel:// 取消
			break;
		}
		this.dismiss();
	}

	/**
	 * 删除的是歌曲时
	 */
	private void DeleteSong() {
		resolver = getContext().getContentResolver();
		Uri uri = Media.getContentUriForPath(song.getSongpath());
		boolean systetemResult = resolver.delete(uri, Media._ID + "=?",
				new String[] { song.get_id() }) > 0 ? true : false;
		boolean appResult = resolver.delete(MusicProvider.SONG_URI,
				MusicProvider.TABLE_SONG_ID + "=?",
				new String[] { song.get_id() }) > 0 ? true : false;
		if (cb_deletedialog.isChecked()) {
			File songFile = new File(song.getSongpath());
			if (songFile.exists()) {
				songFile.delete();
			}
		}
		if (appResult && systetemResult) {
			Toast.makeText(getContext(), "歌曲删除成功", 0).show();
			listener.listen(true);
		} else {
			listener.listen(false);
		}
	}

	/**
	 * 删除的是文件时
	 */
	private void DeleteFilepath() {
		List<Song> songList = MediaUtils.getSongList(getContext(),
				"filepath_id=?", new String[] { filePath.get_id() });
		resolver = getContext().getContentResolver();
		int count = resolver.delete(MusicProvider.SONG_URI, "filepath_id=?",
				new String[] { filePath.get_id() });// 删除自己的Song
		resolver.delete(MusicProvider.FILEPATH_URI, "_id=?",
				new String[] { filePath.get_id() });// 删除自己的filepath
		DeleteSystem(songList, count, listener);// 删除手机系统中的歌曲
	}

	/**
	 * 删除的是歌手时
	 */
	private void DeleteArtist() {
		List<Song> songList = MediaUtils.getSongList(getContext(),
				"artist_id=?", new String[] { artist.get_id() });
		resolver = getContext().getContentResolver();
		int count = resolver.delete(MusicProvider.SONG_URI, "artist_id=?",
				new String[] { artist.get_id() });// 删除自己的Song
		resolver.delete(MusicProvider.ARTIST_URI, "_id=?",
				new String[] { artist.get_id() });// 删除自己的artist
		DeleteSystem(songList, count, listener);
	}

	/**
	 * 删除的是专辑时
	 */
	private void DeleteAlbum() {
		List<Song> songList = MediaUtils.getSongList(getContext(),
				"album_id=?", new String[] { album.get_id() });
		resolver = getContext().getContentResolver();
		int count = resolver.delete(MusicProvider.SONG_URI, "album_id=?",
				new String[] { album.get_id() });// 删除自己的Song
		resolver.delete(MusicProvider.ALBUM_URI, "_id=?",
				new String[] { album.get_id() });// 删除自己的artist
		DeleteSystem(songList, count, listener);
	}

	/**
	 * 删除系统的歌曲
	 * 
	 * @param songList
	 * @param count
	 */
	private void DeleteSystem(List<Song> songList, int count,
			MyListener mylistener) {
		for (Song song : songList) {
			String path = song.getSongpath();
			Uri uri = Media.getContentUriForPath(path);
			resolver.delete(uri, Media._ID + "=?",
					new String[] { song.get_id() });// 删除系统Song
		}
		if (cb_deletedialog.isChecked()) {// 删除源文件
			for (Song song : songList) {
				new File(song.getSongpath()).delete();
			}
		}
		if (count == songList.size()) {
			Toast.makeText(getContext(), "成功删除歌曲" + count + "首", 0).show();
			mylistener.listen(true);
		} else {
			mylistener.listen(false);
		}
	}

	/**
	 * 删除监听的定义
	 */
	private MyListener listener;

	public interface MyListener {
		void listen(boolean isDeleted);
	}

	public void setOnListen(MyListener listener) {
		this.listener = listener;
	}
}
