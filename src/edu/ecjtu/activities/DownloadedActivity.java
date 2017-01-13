package edu.ecjtu.activities;

import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.adapters.downloadactivity.DownloadedAdapter;
import edu.ecjtu.domain.vo.objects.BytesAndStatus;
import edu.ecjtu.musicplayer.R;

public class DownloadedActivity extends Activity {

	private List<BytesAndStatus> bytesAndStatuseList;
	private ListView lv_downloaded_song;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloaded_layout);
		lv_downloaded_song = (ListView) findViewById(R.id.lv_downloaded_song);
		findViewById(R.id.ll_download_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		;
		initData();
	}

	private void initData() {
		bytesAndStatuseList = MediaUtils.queryByDownloadStatus(this,
				DownloadManager.STATUS_SUCCESSFUL);
		lv_downloaded_song.setAdapter(new DownloadedAdapter(
				bytesAndStatuseList, this));
	}

}
