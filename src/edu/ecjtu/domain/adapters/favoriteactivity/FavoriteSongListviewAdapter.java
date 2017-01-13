package edu.ecjtu.domain.adapters.favoriteactivity;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class FavoriteSongListviewAdapter extends BaseAdapter {

	private Context context;
	private List<Song> songs;
	private Song song;
	private ViewHolder viewHolder;
	private View view;
	private ContentResolver resolver;

	public FavoriteSongListviewAdapter(Context context, List<Song> songs) {
		this.context = context;
		this.songs = songs;
		this.resolver = context.getContentResolver();
	}

	@Override
	public int getCount() {
		return this.songs.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		view = null;
		viewHolder = null;
		song = songs.get(position);
		if (convertView == null) {
			view = View.inflate(context, R.layout.favorited_songs_lv_layout,
					null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_favorited_songname.setText(song.getSongname());
		viewHolder.tv_favorited_artist
				.setText(song.getArtist().getArtistname());
		viewHolder.tv_favorited_albumname.setText("—"
				+ song.getAlbum().getAlbumname());
		viewHolder.tv_favorite_duration.setText(song.getDuration());
		viewHolder.tv_favorite_songpath.setText(song.getSongpath());
		viewHolder.iv_favorited_options
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final Song song = songs.get(position);
						Builder builder = new Builder(context);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										updateFavorite(position, song);
									}

									/**
									 * @param position
									 * @param song
									 */
									private void updateFavorite(
											final int position, final Song song) {
										ContentValues values = new ContentValues();
										values.put(
												MusicProvider.TABLE_SONG_ISFAVORITE,
												false);
										resolver.update(MusicProvider.SONG_URI,
												values,
												MusicProvider.TABLE_SONG_ID
														+ "=?",
												new String[] { song.get_id() });
										songs.remove(position);
										for (Song s : PlayUtils.binderObject
												.getSongs()) {
											if (s.get_id()
													.equals(song.get_id())) {
												s.setIsFavorite("0");
												break;
											}
										}
										listener.onFavoriteSongsChange(songs
												.size() > 0 ? false : true);// 监听listview是否为空
										notifyDataSetChanged();// 更新数据
									}
								})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}

										})
								.setTitle("取消收藏")
								.setMessage(
										"您确认要将名为:" + song.getSongname()
												+ "的歌曲从收藏栏移除吗？").show();
					}
				});
		return view;
	}

	private OnFavoriteSongsChangeListener listener;

	public interface OnFavoriteSongsChangeListener {
		void onFavoriteSongsChange(boolean isFavoriteSongsNull);
	};

	public void setOnFavoriteSongsChange(OnFavoriteSongsChangeListener listener) {
		this.listener = listener;
	}

	private class ViewHolder {
		TextView tv_favorited_songname, tv_favorited_artist,
				tv_favorited_albumname, tv_favorite_songpath,
				tv_favorite_duration;
		ImageView iv_favorited_options;

		public ViewHolder(View view) {
			tv_favorited_songname = (TextView) view
					.findViewById(R.id.tv_favorited_songname);
			tv_favorited_artist = (TextView) view
					.findViewById(R.id.tv_favorited_artist);
			tv_favorited_albumname = (TextView) view
					.findViewById(R.id.tv_favorited_albumname);
			iv_favorited_options = (ImageView) view
					.findViewById(R.id.iv_favorited_options);
			tv_favorite_songpath = (TextView) view
					.findViewById(R.id.tv_favorite_songpath);
			tv_favorite_duration = (TextView) view
					.findViewById(R.id.tv_favorite_duration);

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
