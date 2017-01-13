package edu.ecjtu.domain.adapters.downloadactivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.ecjtu.domain.vo.objects.BytesAndStatus;
import edu.ecjtu.musicplayer.R;

public class DownloadedAdapter extends BaseAdapter {

	private Context context;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();
	private List<BytesAndStatus> bytesAndStatusList;

	public DownloadedAdapter(List<BytesAndStatus> bytesAndStatusList,
			Context context) {
		super();
		this.context = context;
		this.bytesAndStatusList = bytesAndStatusList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = viewMap.get(position);
		BytesAndStatus bytesAndStatus = bytesAndStatusList.get(position);
		if (view == null) {
			view = View.inflate(context, R.layout.lv_downloaded_item, null);
		}
		ViewHolder viewHolder = ViewHolder.getViewHolder(view);
		viewHolder.tv_downloaded_songname.setText(bytesAndStatus.getTitle());
		int totalSize = bytesAndStatus.getTotalSize();
		String totalSizeString = formatSize(totalSize);
		viewHolder.tv_downloaded_size.setText(totalSizeString);
		return view;
	}

	private String formatSize(int totalSize) {
		String totalSizeString = null;
		double size = totalSize * 1.0f / (1024 * 1024);
		DecimalFormat decimalFormat = new DecimalFormat("###.00");
		totalSizeString = decimalFormat.format(size) + "M";
		return totalSizeString;
	}

	static class ViewHolder {
		TextView tv_downloaded_songname, tv_downloaded_size;

		public static ViewHolder getViewHolder(View view) {
			Object tag = view.getTag();
			if (tag != null) {
				return (ViewHolder) tag;
			} else {
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tv_downloaded_songname = (TextView) view
						.findViewById(R.id.tv_downloaded_songname);
				viewHolder.tv_downloaded_size = (TextView) view
						.findViewById(R.id.tv_downloaded_size);
				view.setTag(viewHolder);
				return viewHolder;
			}
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getCount() {
		return bytesAndStatusList.size();
	}
}
