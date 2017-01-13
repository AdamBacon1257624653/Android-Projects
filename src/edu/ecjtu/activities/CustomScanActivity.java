package edu.ecjtu.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import edu.ecjtu.domain.adapters.localactivity.FilesListviewAdapter;
import edu.ecjtu.domain.views.MyCheckView;
import edu.ecjtu.musicplayer.R;

public class CustomScanActivity extends Activity implements OnClickListener {
	private LinearLayout rl_cutom_scan_back;
	private LinearLayout ll_allcheck;
	private ListView lv_files;
	private File SDFile = Environment.getExternalStorageDirectory();
	private List<String> folderNames = new ArrayList<String>();
	private MyCheckView iv_allcheck;
	private FilesListviewAdapter fAdapter;
	private FilesListviewAdapter filesListviewAdapter;
	private Map<Integer, View> viewMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_scan);

		initViews();
		initData();
		inflateViews();
		registerListeners();
	}

	/**
	 * 初始化组件
	 */
	private void initViews() {
		this.ll_allcheck = (LinearLayout) findViewById(R.id.ll_allcheck);
		this.rl_cutom_scan_back = (LinearLayout) findViewById(R.id.rl_custom_scan_back);
		this.lv_files = (ListView) findViewById(R.id.lv_files);
		this.iv_allcheck = (MyCheckView) findViewById(R.id.iv_allcheck);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File[] files = SDFile.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					folderNames.add(file.getName());
				}
			}

		} else {
			Toast.makeText(getApplicationContext(), "SD卡为正确安装，请检查您的SD卡", 0)
					.show();
		}
	}

	/**
	 * 将数据填充至组件
	 */
	private void inflateViews() {
		filesListviewAdapter = new FilesListviewAdapter(this, folderNames);
		this.lv_files.setAdapter(filesListviewAdapter);
		viewMap = filesListviewAdapter.getViewMap();

	}

	/**
	 * 注册监听器
	 */
	private void registerListeners() {
		this.rl_cutom_scan_back.setOnClickListener(this);
		this.ll_allcheck.setOnClickListener(this);
	}

	/**
	 * 
	 */

	private void getViews() {
		fAdapter = (FilesListviewAdapter) this.lv_files.getAdapter();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_custom_scan_back:
			finish();
			break;
		case R.id.ll_allcheck:
			getViews();
			boolean isCheck = !iv_allcheck.isChecked();
			iv_allcheck.setChecked(isCheck);
			for (View view : viewMap.values()) {
				MyCheckView iv_check = (MyCheckView) view
						.findViewById(R.id.iv_check);
				fAdapter.setChecked(iv_check, isCheck);
			}
			break;
		}
	}
}
