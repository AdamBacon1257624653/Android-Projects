package edu.ecjtu.domain.adapters.recentplayactivity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class RecentPlaySongListviewAdapter extends BaseAdapter {

	private Context context;
	private List<Song> songs;

	public RecentPlaySongListviewAdapter(Context context, List<Song> songs) {
		this.context = context;
		this.songs = songs;
	}

	@Override
	public int getCount() {
		return this.songs.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		ViewHolder viewHolder = null;
		Song song = songs.get(position);
		if (convertView == null) {
			view = View.inflate(context, R.layout.lv_recentplay_item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_recentplay_songname.setText(song.getSongname());
		viewHolder.tv_recentplay_artist.setText(song.getArtist()
				.getArtistname());
		viewHolder.tv_recentplay_albumname.setText("¡ª"
				+ song.getAlbum().getAlbumname());
		return view;
	}

	private class ViewHolder {
		TextView tv_recentplay_songname, tv_recentplay_artist,
				tv_recentplay_albumname;

		public ViewHolder(View view) {
			tv_recentplay_songname = (TextView) view
					.findViewById(R.id.tv_recentplay_songname);
			tv_recentplay_artist = (TextView) view
					.findViewById(R.id.tv_recentplay_artist);
			tv_recentplay_albumname = (TextView) view
					.findViewById(R.id.tv_recentplay_albumname);
		}
	}

	@Override
	public Object getItem(int index) {
		return null;
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}

}
