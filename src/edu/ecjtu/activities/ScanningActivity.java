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
	private int songCount = 0;// 音乐个数
	private int clipCount = 0;// 音频个数
	private long fileSize = 0;
	private File SDFile = Environment.getExternalStorageDirectory();
	private boolean exit = false;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String obj = (String) msg.obj;
			switch (msg.what) {
			case Utils.SCAN_FILEPATH:// 更新文件名
				tv_scanfilename.setText(obj);
				break;
			case Utils.SONG_SCANNED:
				tv_scaninfo.setText("已扫描出" + obj + "首歌曲");
				break;
			case Utils.SCAN_FINISHED:
				tv_scaninfo.setText("已扫描出" + songCount + "首歌曲");
				tv_scanfilename.setText("扫描完毕,过滤了" + clipCount + "条音频片段");
				btn_state.setText("完成");
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
			Toast.makeText(this, "SD卡为正确安装，请检查您的SD卡", 1).show();
		}
	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		this.myprogressbar = (MyProgressBar) findViewById(R.id.myprogressbar);
		this.tv_scaninfo = (TextView) findViewById(R.id.tv_scaninfo);
		this.tv_scanfilename = (TextView) findViewById(R.id.tv_scanfilename);
		this.btn_state = (Button) findViewById(R.id.btn_state);
	}

	/**
	 * 为自定义进度条填充数据
	 */
	private void initData() {
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.myprogressbar.setMaxWidth(dm.widthPixels);
		this.myprogressbar.setMaxProgress(1000000000f);
		this.myprogressbar.invalidate();
	}

	/**
	 * 注册监听
	 */
	private void registerListeners() {
		this.btn_state.setOnClickListener(this);
	}

	/**
	 * 扫描音乐文件
	 */
	private void scanFileAsync(File file, int seconds) {
		if (!exit) {
			dispatchMessage(file.getAbsolutePath(), Utils.SCAN_FILEPATH);
			if (file.isDirectory()) {// 如果是文件夹
				if (file.listFiles() != null) {
					File[] files = file.listFiles();
					for (File f : files) {// 遍历子文件
						scanFileAsync(f, seconds);// 继续遍历
					}
				}
			} else {// 如果是文件
				fileSize += file.length();
				dispatchMessage(fileSize + "", Utils.SCANNING);
				String fileName = file.getName();
				if (fileName.endsWith(".mp3") //
						|| fileName.endsWith(".aac")) {// 如果是音乐文件
					if (file.length() > 1500000 && file.length() < 10000000) {// 音乐文件1.5M~10M
						dispatchMessage(++songCount + "", Utils.SONG_SCANNED);
					} else {
						clipCount++;
					}
				}
			}
		}
	}

	/**
	 * 发送message
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
