package edu.ecjtu.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import edu.ecjtu.musicplayer.R;

public class ScanActivity extends Activity implements OnClickListener {
	private LinearLayout rl_scan_back;
	private Button btn_scanall, btn_choosefile_scan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scansongs);

		initViews();
		registerListeners();
	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		this.rl_scan_back = (LinearLayout) findViewById(R.id.rl_scan_back);
		this.btn_scanall = (Button) findViewById(R.id.btn_scanall);
		this.btn_choosefile_scan = (Button) findViewById(R.id.btn_choosefile_scan);
	}

	/**
	 * 注册监听器
	 */
	private void registerListeners() {
		this.rl_scan_back.setOnClickListener(this);
		this.btn_scanall.setOnClickListener(this);
		this.btn_choosefile_scan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_scan_back:// 返回
			finish();
			break;
		case R.id.btn_scanall:// 全盘扫描
			Intent intent = new Intent(this, ScanningActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_choosefile_scan:// 自定义扫描
			Intent intentChoose = new Intent(this, CustomScanActivity.class);
			startActivity(intentChoose);
			break;
		}
	}
}
