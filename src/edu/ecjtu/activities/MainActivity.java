package edu.ecjtu.activities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.musicplayer.R;

public class MainActivity extends Activity {

	private ImageView iv_main;
	private Map<String, Object> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		// 自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		initViews();
		inflateViews();
	}

	private void initViews() {
		this.iv_main = (ImageView) findViewById(R.id.iv_main);
	}

	/**
	 * 填充图片信息
	 */
	private void inflateViews() {
		getImageInfoFromPhone();
		if (map != null) {
			boolean isFromPhone = Boolean.valueOf(map.get(Utils.FROM_PHONE)
					.toString());
			if (!isFromPhone) {
				int preshowResId = Integer.valueOf(map.get(Utils.PRESHOW_RESID)
						.toString());
				Integer mainResId = null;
				if (preshowResId == R.drawable.zly_preshow
						|| preshowResId == R.drawable.cyx_preshow
						|| preshowResId == R.drawable.ldh_preshow
						|| preshowResId == R.drawable.xs_preshow) {
					mainResId = Utils.preshowToMain.get(preshowResId);
				}
				if (mainResId != null) {
					iv_main.setImageResource(mainResId);
				}
			}
		}
	}

	/**
	 * 从手机中获取isFromPhone和PreshowImageId
	 */
	private void getImageInfoFromPhone() {
		FileInputStream fis = null;
		try {
			String fileDirName = this.getFilesDir().getName();
			if (fileDirName != null && !fileDirName.isEmpty()) {
				fis = this.openFileInput(fileDirName);
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(fis));
				String string = "";
				map = new HashMap<String, Object>();
				while ((string = bReader.readLine()) != null
						&& !string.isEmpty()) {
					String[] arr = string.split(":");
					map.put(arr[0], arr[1].trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void enter(View v) {
		Intent intent = new Intent(this, PlayerActivity.class);
		startActivity(intent);
		this.finish();
	}
}
