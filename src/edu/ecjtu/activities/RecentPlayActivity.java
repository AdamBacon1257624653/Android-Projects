package edu.ecjtu.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.adapters.recentplayactivity.RecentPlaySongListviewAdapter;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class RecentPlayActivity extends Activity implements OnClickListener {
	private LinearLayout ll_recentplay_back;
	private ListView lv_recentplay;
	private List<Song> recentPlaySongs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityrecentplay_layout);

		initViews();
		initData();
		inflateViews();
		registerListeners();
	}

	private void initViews() {
		ll_recentplay_back = (LinearLayout) findViewById(R.id.ll_recentplay_back);
		lv_recentplay = (ListView) findViewById(R.id.lv_recentplay);
	}

	private void initData() {
		long yesterdayTimemillies = System.currentTimeMillis() - 120000;// 过滤一天的最近播放
		recentPlaySongs = MediaUtils.getList(Song.class, this,
				MusicProvider.SONG_MULTI_URI, new String[] { "Songname", "Artist",
						"Album" }, MusicProvider.TABLE_SONG_LASTPLAYTIMEMILLIES
						+ ">=?", new String[] { yesterdayTimemillies + "" },
				MusicProvider.TABLE_SONG_LASTPLAYTIMEMILLIES+" DESC");
	}

	private void inflateViews() {
		lv_recentplay.setAdapter(new RecentPlaySongListviewAdapter(this,
				recentPlaySongs));
	}

	private void registerListeners() {
		ll_recentplay_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_recentplay_back:
			finish();
			break;
		}
	}
}
