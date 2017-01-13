package edu.ecjtu.domain.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ecjtu.domain.views.DeleteDialog.MyListener;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.musicplayer.R;

public class OtherDialog extends Dialog implements
		android.view.View.OnClickListener {

	private FilePath filePath;
	private Artist artist;
	private Album album;
	private TextView tv_otherdialog_title;
	private RelativeLayout rl_otherdialog_play, rl_otherdialog_delete;
	private Button btn_otherdialog_cancel;

	public OtherDialog(Context context, FilePath filePath) {
		super(context);
		this.filePath = filePath;
	}

	public OtherDialog(Context context, Artist artist) {
		super(context);
		this.artist = artist;
	}

	public OtherDialog(Context context, Album album) {
		super(context);
		this.album = album;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.otherdialog_layout);

		initViews();
		inflateViews();
		registerListener();
	}

	private void initViews() {
		tv_otherdialog_title = (TextView) findViewById(R.id.tv_otherdialog_title);
		rl_otherdialog_play = (RelativeLayout) findViewById(R.id.rl_otherdialog_play);
		rl_otherdialog_delete = (RelativeLayout) findViewById(R.id.rl_otherdialog_delete);
		btn_otherdialog_cancel = (Button) findViewById(R.id.btn_otherdialog_cancel);
	}

	/**
	 * 填充类标题栏
	 */
	private void inflateViews() {
		if (filePath != null) {
			tv_otherdialog_title.setText(filePath.getParentname());
		} else if (artist != null) {
			tv_otherdialog_title.setText(artist.getArtistname());
		} else if (album != null) {
			tv_otherdialog_title.setText(album.getAlbumname());
		}
	}

	private void registerListener() {
		this.rl_otherdialog_play.setOnClickListener(this);
		this.rl_otherdialog_delete.setOnClickListener(this);
		this.btn_otherdialog_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		DeleteDialog dialog = new DeleteDialog(getContext());
		switch (view.getId()) {
		case R.id.rl_otherdialog_play:

			break;
		case R.id.rl_otherdialog_delete:
			if (filePath != null) {
				dialog.setFilePath(filePath);
				dialog.show();
			} else if (artist != null) {
				dialog.setArtist(artist);
				dialog.show();
			} else if (album != null) {
				dialog.setAlbum(album);
				dialog.show();
			}
			dialog.setOnListen(new MyListener() {

				@Override
				public void listen(boolean isDeleted) {
					deleteSuccessedListener.OnDeleteSuccessed(isDeleted);
				}
			});
			break;
		case R.id.btn_otherdialog_cancel:
			break;
		}
		this.dismiss();
	}

	/**
	 * 文件删除监听
	 */
	private OnDeleteSuccessedListener deleteSuccessedListener;

	public interface OnDeleteSuccessedListener {
		void OnDeleteSuccessed(boolean isDeleted);
	}

	public void setOnDeleteSuccessedListener(
			OnDeleteSuccessedListener deleteSuccessedListener) {
		this.deleteSuccessedListener = deleteSuccessedListener;
	}
}
