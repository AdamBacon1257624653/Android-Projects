package edu.ecjtu.domain.adapters.onlinesearchactivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.ecjtu.activities.OnLineSearchActivity;
import edu.ecjtu.domain.Util.PlayUtils;
import edu.ecjtu.domain.vo.objects.OnlineMusic;
import edu.ecjtu.musicplayer.R;

public class SearchResultAdapter extends BaseAdapter {

	private List<OnlineMusic> musics;
	private OnLineSearchActivity context;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();

	public SearchResultAdapter(List<OnlineMusic> musics,
			OnLineSearchActivity context) {
		super();
		this.musics = musics;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = viewMap.get(position);
		final OnlineMusic music = musics.get(position);
		if (view == null) {
			view = View.inflate(context, R.layout.lv_searchresult_item, null);
		}
		ViewHolder viewHolder = ViewHolder.getViewHolder(view);
		viewHolder.tv_url_songname.setText(music.getSongName() + "   "
				+ music.getSingerName());
		if (music.getHQUrlPath() != null && !music.getHQUrlPath().isEmpty()) {
			viewHolder.tv_hqurl_songname.setText(music.getSongName()
					+ "   (高品质)   " + music.getSingerName());
		} else {
			viewHolder.v_searchresult_devide.setVisibility(View.GONE);
			viewHolder.tv_hqurl_songname.setVisibility(View.GONE);
		}
		viewHolder.tv_url_songname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayUtils.binderObject.getPlayBinder().playOnline(
						music.getUrlPath());
				initBinderOnlineSong(music);
			}
		});
		viewHolder.tv_hqurl_songname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PlayUtils.binderObject.getPlayBinder().playOnline(
						music.getHQUrlPath());
				initBinderOnlineSong(music);
			}
		});
		return view;
	}

	/**
	 * 初始化网络歌曲的名称
	 * 
	 * @param music
	 *            网络歌曲
	 */
	private void initBinderOnlineSong(final OnlineMusic music) {
		PlayUtils.binderObject.setOnlineMusic(music);
		context.updateOnlineBottom(music.getSongName(), music.getSingerName());
	}

	static class ViewHolder {
		TextView tv_url_songname, tv_hqurl_songname;
		View v_searchresult_devide;

		public static ViewHolder getViewHolder(View view) {
			Object tag = view.getTag();
			if (tag != null) {
				return (ViewHolder) tag;
			} else {
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tv_url_songname = (TextView) view
						.findViewById(R.id.tv_url_songname);
				viewHolder.tv_hqurl_songname = (TextView) view
						.findViewById(R.id.tv_hqurl_songname);
				viewHolder.v_searchresult_devide = view
						.findViewById(R.id.v_searchresult_devide);
				view.setTag(viewHolder);
				return viewHolder;
			}
		}
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
