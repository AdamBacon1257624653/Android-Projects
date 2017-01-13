package edu.ecjtu.domain.adapters.playactivity;

import java.util.List;

import android.app.DownloadManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.vo.objects.Song;
import edu.ecjtu.musicplayer.R;

public class MyListviewAdapter extends BaseAdapter {

	private Context context;
	private String[] arrs;
	private int[] resIds;
	private List<Song> mysongs;
	private List<Song> recentplaySongs;

	public MyListviewAdapter(Context context, String[] arrs, int[] resIds) {
		this.context = context;
		this.arrs = arrs;
		this.resIds = resIds;
	}

	@Override
	public int getCount() {
		return this.arrs.length;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		ViewHolder viewHolder = null;
		if (convertView == null) {
			view = View.inflate(context, R.layout.lv_my, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.iv_myicon.setImageResource(resIds[position]);
		viewHolder.tv_mymusic.setText(arrs[position]);
		if (position == 0) {
			viewHolder.tv_amount.setText(mysongs.size() + "首");
		} else if (position == 1) {
			viewHolder.tv_amount.setText(recentplaySongs.size() + "首");
		} else {
			viewHolder.tv_amount.setText(MediaUtils.queryByDownloadStatus(
					context, DownloadManager.STATUS_SUCCESSFUL).size()
					+ "首");
		}
		return view;
	}

	private class ViewHolder {
		ImageView iv_myicon;
		TextView tv_mymusic;
		TextView tv_amount;

		public ViewHolder(View view) {
			iv_myicon = (ImageView) view.findViewById(R.id.iv_myicon);
			tv_mymusic = (TextView) view.findViewById(R.id.tv_mymusic);
			tv_amount = (TextView) view.findViewById(R.id.tv_amount);
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
	 * 获取歌曲的总数
	 * 
	 * @param songList
	 */
	public void setSongs(List<Song> songList, List<Song> recentPlaySongs) {
		this.mysongs = songList;
		this.recentplaySongs = recentPlaySongs;
	}
}
