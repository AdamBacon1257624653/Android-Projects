package edu.ecjtu.activities;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.views.MyProgressBar;
import edu.ecjtu.musicplayer.R;

public class ScanningActivity extends Activity implements OnClickListener {

	private static MyProgressBar myprogressbar;
	private static TextView tv_scaninfo;
	private static TextView tv_scanfilename;
	private Button btn_state;
	private DisplayMetrics dm = new DisplayMetrics();
	private int songCount = 0;// ���ָ���
	private int clipCount = 0;// ��Ƶ����
	private long fileSize = 0;
	private File SDFile = Environment.getExternalStorageDirectory();
	private boolean exit = false;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String obj = (String) msg.obj;
			switch (msg.what) {
			case Utils.SCAN_FILEPATH:// �����ļ���
				tv_scanfilename.setText(obj);
				break;
			case Utils.SONG_SCANNED:
				tv_scaninfo.setText("��ɨ���" + obj + "�׸���");
				break;
			case Utils.SCAN_FINISHED:
				tv_scaninfo.setText("��ɨ���" + songCount + "�׸���");
				tv_scanfilename.setText("ɨ�����,������" + clipCount + "����ƵƬ��");
				btn_state.setText("���");
				btn_state
						.setBackgroundResource(R.drawable.scanningcomplete_btn_bg);
				myprogressbar.setCurrProgress(1000000000f);
				break;
			case Utils.SCANNING:
				float currProgress = (Float.valueOf(obj) / 9891366951f) * 1000000000f;
				myprogressbar.setCurrProgress(currProgress);
				myprogressbar.invalidate();
				break;
			}
		};
	};
	private Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanningall_layout);

		initViews();
		initData();
		registerListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			thread = new Thread(new Runnable() {

				@Override
				public void run() {
					scanFileAsync(SDFile, 60);
					if (!exit) {
						dispatchMessage(clipCount + "", Utils.SCAN_FINISHED);
					}
				}
			});
			thread.start();
		} else {
			Toast.makeText(this, "SD��Ϊ��ȷ��װ����������SD��", 1).show();
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initViews() {
		this.myprogressbar = (MyProgressBar) findViewById(R.id.myprogressbar);
		this.tv_scaninfo = (TextView) findViewById(R.id.tv_scaninfo);
		this.tv_scanfilename = (TextView) findViewById(R.id.tv_scanfilename);
		this.btn_state = (Button) findViewById(R.id.btn_state);
	}

	/**
	 * Ϊ�Զ���������������
	 */
	private void initData() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.myprogressbar.setMaxWidth(dm.widthPixels);
		this.myprogressbar.setMaxProgress(1000000000f);
		this.myprogressbar.invalidate();
	}

	/**
	 * ע�����
	 */
	private void registerListeners() {
		this.btn_state.setOnClickListener(this);
	}

	/**
	 * ɨ�������ļ�
	 */
	private void scanFileAsync(File file, int seconds) {
		if (!exit) {
			dispatchMessage(file.getAbsolutePath(), Utils.SCAN_FILEPATH);
			if (file.isDirectory()) {// ������ļ���
				if (file.listFiles() != null) {
					File[] files = file.listFiles();
					for (File f : files) {// �������ļ�
						scanFileAsync(f, seconds);// ��������
					}
				}
			} else {// ������ļ�
				fileSize += file.length();
				dispatchMessage(fileSize + "", Utils.SCANNING);
				String fileName = file.getName();
				if (fileName.endsWith(".mp3") //
						|| fileName.endsWith(".aac")) {// ����������ļ�
					if (file.length() > 1500000 && file.length() < 10000000) {// �����ļ�1.5M~10M
						dispatchMessage(++songCount + "", Utils.SONG_SCANNED);
					} else {
						clipCount++;
					}
				}
			}
		}
	}

	/**
	 * ����message
	 * 
	 * @param
	 */
	private void dispatchMessage(String obj, int what) {
		Message msg = handler.obtainMessage();
		msg.obj = obj;
		msg.what = what;
		handler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_state:
			exit = true;
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		exit = true;
		super.onBackPressed();
	}
}
