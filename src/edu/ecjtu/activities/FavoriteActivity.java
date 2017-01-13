package edu.ecjtu.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.adapters.favoriteactivity.FavoriteSongListviewAdapter;
import edu.ecjtu.domain.adapters.favoriteactivity.FavoriteSongListviewAdapter.OnFavoriteSongsChangeListener;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class FavoriteActivity extends Activity implements OnClickListener {

	private ListView lv_favorited_songs;
	private List<Song> songList;
	private LinearLayout rl_favorite_back;
	private TextView tv_nofavoritesong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_layout);

		initViews();
		initData();
		inflateViews();
		registerListeners();
	}

	private void initViews() {
		this.lv_favorited_songs = (ListView) findViewById(R.id.lv_favorited_songs);
		this.rl_favorite_back = (LinearLayout) findViewById(R.id.rl_favorite_back);
		this.tv_nofavoritesong = (TextView) findViewById(R.id.tv_nofavoritesong);
	}

	/**
	 * ³õÊ¼»¯Êý¾Ý
	 */
	private void initData() {
		songList = MediaUtils.getList(Song.class, this,
				MusicProvider.SONG_MULTI_URI, new String[] {
						MusicProvider.TABLE_SONG_ID, "Songname", "Artist",
						"Album", "Duration", "Songpath" },
				MusicProvider.TABLE_SONG_ISFAVORITE + "=?",
				new String[] { "1" }, null);
	}

	/**
	 * Ìî³älistview
	 */
	private void inflateViews() {
		if (songList.size() > 0) {
			this.lv_favorited_songs.setVisibility(View.VISIBLE);
			this.lv_favorited_songs.setAdapter(new FavoriteSongListviewAdapter(
					this, songList));
			this.tv_nofavoritesong.setVisibility(View.GONE);
		} else {
			this.tv_nofavoritesong.setVisibility(View.VISIBLE);
			this.lv_favorited_songs.setVisibility(View.GONE);
		}
	}

	/**
	 * ×¢²á¼àÌýÆ÷
	 */
	private void registerListeners() {
		this.rl_favorite_back.setOnClickListener(this);
		if (this.songList.size() > 0) {
			((FavoriteSongListviewAdapter) this.lv_favorited_songs.getAdapter())// ¼àÌýÊÕ²Ø¸èÇúÊÇ·ñÎª¿Õ
					.setOnFavoriteSongsChange(new OnFavoriteSongsChangeListener() {

						@Override
						public void onFavoriteSongsChange(
								boolean isFavoriteSongsNull) {
							if (isFavoriteSongsNull) {
								tv_nofavoritesong.setVisibility(View.VISIBLE);
								lv_favorited_songs.setVisibility(View.GONE);
							}
						}
					});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_favorite_back:
			finish();
			break;
		}
	}
}
