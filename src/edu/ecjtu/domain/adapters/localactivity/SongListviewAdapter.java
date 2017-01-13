package edu.ecjtu.domain.adapters.localactivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.ecjtu.activities.LocalActivity;
import edu.ecjtu.activities.LocalActivity.OnSongPositionChangeListener;
import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.views.MyDialog;
import edu.ecjtu.domain.views.MyDialog.OnFavoriteStateChangeListener;
import edu.ecjtu.domain.views.MyDialog.OnSongDeleteSuccessedListener;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class SongListviewAdapter extends BaseAdapter {

	private Context context;
	private List<Song> songs;
	private DisplayMetrics dm = new DisplayMetrics();
	private Song song;
	private ViewHolder viewHolder;
	private View view;
	private Map<Integer, View> map = new HashMap<Integer, View>();

	public SongListviewAdapter(Context context, List<Song> songs) {
		this.context = context;
		this.songs = songs;
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		dialog = new MyDialog(context, true, null);
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
		if (map.get(position) == null) {
			view = View.inflate(context, R.layout.songs_lv_layout, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = map.get(position);
			viewHolder = (ViewHolder) view.getTag();
		}
		// ѡ��ѡ�еĸ���
		if (position == PlayUtils.binderObject.getCurrentPosition()) {
			viewHolder.tv_songname.setTextColor(0xFF00B4FF);
			viewHolder.tv_artist.setTextColor(0xFF00B4FF);
			viewHolder.tv_albumname.setTextColor(0xFF00B4FF);
		}
		// ������ĸ
		PinyinUtil.initLetterTextView(position, songs, viewHolder.tv_letter);
		// ���õײ�
		PinyinUtil.initBottom(position, songs, viewHolder.tv_songcount,
				viewHolder.ll_song_bottom, "�׸���");
		viewHolder.tv_songname.setText(song.getSongname());
		viewHolder.tv_artist.setText(song.getArtist().getArtistname());
		viewHolder.tv_albumname.setText("��" + song.getAlbum().getAlbumname());
		viewHolder.tv_duration.setText(song.getDuration());
		viewHolder.tv_songpath.setText(song.getSongpath());
		viewHolder.iv_song_options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(position);
			}
		});
		((LocalActivity) context)
				.setOnSongPositionChangeListener(new OnSongPositionChangeListener() {

					@Override
					public void OnSongPositionChange() {
						for (View v : map.values()) {
							TextView tv_songname = (TextView) v
									.findViewById(R.id.tv_songname);
							TextView tv_artist = (TextView) v
									.findViewById(R.id.tv_artist);
							TextView tv_albumname = (TextView) v
									.findViewById(R.id.tv_albumname);
							if (v == map.get(PlayUtils.binderObject
									.getCurrentPosition())) {
								tv_songname.setTextColor(0xFF00B4FF);
								tv_artist.setTextColor(0xFF00B4FF);
								tv_albumname.setTextColor(0xFF00B4FF);
							} else {
								tv_songname.setTextColor(0xFF303030);
								tv_artist.setTextColor(0xFFA0A0A0);
								tv_albumname.setTextColor(0xFFA0A0A0);
							}
						}
						notifyDataSetChanged();// ����listview
					}
				});
		map.put(position, view);
		return view;
	}

	/**
	 * ��ʾ�����򴰿�
	 * 
	 * @param position
	 */
	public void showDialog(final int position) {
		dialog.setSong(songs.get(position));
		dialog.show();
		dialog.setText(songs.get(position).getSongname());
		dialog.setOnFavoriteStateChangeListener(new OnFavoriteStateChangeListener() {// �����ղ�״̬�ĸı�

			@Override
			public void OnFavoriteStateChange(boolean isFavorite) {
				songs.get(position).setIsFavorite(isFavorite ? "1" : "0");
			}
		});
		dialog.setOnSongDeleteSuccessedListener(new OnSongDeleteSuccessedListener() {// ����ɾ��״̬�ĸı�

			@Override
			public void OnSongDeleteSuccessed(boolean isDeleted) {
				if (isDeleted) {
					songs.remove(position);
					notifyDataSetChanged();
				}
				listener.OnSongsChange(songs);
			}
		});
	}

	private class ViewHolder {
		TextView tv_songname, tv_artist, tv_albumname, tv_duration,
				tv_songpath, tv_songcount, tv_letter;
		ImageView iv_song_options;
		LinearLayout ll_song_bottom;

		public ViewHolder(View view) {
			tv_songname = (TextView) view.findViewById(R.id.tv_songname);
			tv_artist = (TextView) view.findViewById(R.id.tv_artist);
			tv_albumname = (TextView) view.findViewById(R.id.tv_albumname);
			tv_duration = (TextView) view.findViewById(R.id.tv_duration);
			tv_songpath = (TextView) view.findViewById(R.id.tv_songpath);
			iv_song_options = (ImageView) view
					.findViewById(R.id.iv_song_options);
			tv_songcount = (TextView) view.findViewById(R.id.tv_songcount);
			tv_letter = (TextView) view.findViewById(R.id.tv_letter);
			ll_song_bottom = (LinearLayout) view
					.findViewById(R.id.ll_song_bottom);
		}
	}

	@Override
	public Object getItem(int position) {
		return map.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * ����songlist�ĸı�
	 */
	private OnSongsChangedListener listener;
	private MyDialog dialog;

	public interface OnSongsChangedListener {
		void OnSongsChange(List<Song> songs);
	}

	public void setOnSongsChangedListener(OnSongsChangedListener listener) {
		this.listener = listener;
	}
}
