package edu.ecjtu.domain.adapters.localactivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.views.OtherDialog;
import edu.ecjtu.domain.views.OtherDialog.OnDeleteSuccessedListener;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.musicplayer.R;

public class SingerListviewAdapter extends BaseAdapter {

	private Context context;
	List<Artist> artists;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();

	public SingerListviewAdapter(Context context, List<Artist> artists) {
		this.context = context;
		this.artists = artists;
	}

	@Override
	public int getCount() {
		return this.artists.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		ViewHolder viewHolder = null;
		Artist artist = artists.get(position);
		if (viewMap.get(position) == null) {
			view = View.inflate(context, R.layout.singers_lv_layout, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = viewMap.get(position);
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_artistname.setText(artist.getArtistname());
		viewHolder.tv_artistcount.setText(artist.getCount() + "首");
		viewHolder.iv_artist_operation
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showDialog(position);
					}
				});
		// 设置字母
		PinyinUtil.initLetterTextView(position, artists,
				viewHolder.tv_singer_letter);
		// 设置底部
		PinyinUtil.initBottom(position, artists, viewHolder.tv_singercount,
				viewHolder.ll_singer_bottom, "位歌手");
		viewMap.put(position, view);
		return view;
	}

	public void showDialog(final int position) {
		OtherDialog dialog = new OtherDialog(context, artists.get(position));
		dialog.show();
		dialog.setOnDeleteSuccessedListener(new OnDeleteSuccessedListener() {

			@Override
			public void OnDeleteSuccessed(boolean isDeleted) {
				if (isDeleted) {
					artists.remove(position);
					notifyDataSetChanged();
				}
				listener.OnArtistsChange(artists);
			}
		});
	}

	public class ViewHolder {
		TextView tv_artistname, tv_artistcount, tv_singercount,
				tv_singer_letter;
		ImageView iv_artist_operation;
		LinearLayout ll_singer_bottom;

		public ViewHolder(View view) {
			tv_artistname = (TextView) view.findViewById(R.id.tv_artistname);
			tv_artistcount = (TextView) view.findViewById(R.id.tv_artistcount);
			iv_artist_operation = (ImageView) view
					.findViewById(R.id.iv_artist_operation);
			tv_singercount = (TextView) view.findViewById(R.id.tv_singercount);
			ll_singer_bottom = (LinearLayout) view
					.findViewById(R.id.ll_singer_bottom);
			tv_singer_letter = (TextView) view
					.findViewById(R.id.tv_singer_letter);
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

	/**
	 * 监听Artists的改变
	 */
	private OnArtistsChangedListener listener;

	public interface OnArtistsChangedListener {
		void OnArtistsChange(List<Artist> artists);
	}

	public void setOnArtistsChangedListener(OnArtistsChangedListener listener) {
		this.listener = listener;
	}
}
