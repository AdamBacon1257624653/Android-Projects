package edu.ecjtu.domain.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.ecjtu.musicplayer.R;

public class DataWarningDialog extends Dialog implements OnClickListener {

	private Button btn_continuedownload, btn_canceldownload;
	private MoreDialog moreDialog;

	public DataWarningDialog(Context context, int theme) {
		super(context, theme);
	}

	public void setMoreDialog(MoreDialog moreDialog) {
		this.moreDialog = moreDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datadarningdialog_layout);

		initViews();
		registerListeners();
	}

	private void initViews() {
		btn_continuedownload = (Button) findViewById(R.id.btn_continuedownload);
		btn_canceldownload = (Button) findViewById(R.id.btn_canceldownload);
	}

	/**
	 * 注册监听
	 */
	private void registerListeners() {
		this.btn_continuedownload.setOnClickListener(this);
		this.btn_canceldownload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_continuedownload:
			if (moreDialog != null) {
				moreDialog.startDownload();
			}
			break;
		case R.id.btn_canceldownload:
			break;
		}
		this.dismiss();
	}
}
