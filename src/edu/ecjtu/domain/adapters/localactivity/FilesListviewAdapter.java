package edu.ecjtu.domain.adapters.localactivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.ecjtu.domain.views.MyCheckView;
import edu.ecjtu.musicplayer.R;

public class FilesListviewAdapter extends BaseAdapter {

	private Context context;
	private List<String> folderNames;
	private ViewHolder viewHolder;
	private Map<Integer, View> map = new HashMap<Integer, View>();

	public FilesListviewAdapter(Context context, List<String> folderNames) {
		this.context = context;
		this.folderNames = folderNames;
	}

	@Override
	public int getCount() {
		return this.folderNames.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		viewHolder = null;
		String fileName = folderNames.get(position);
		if (map.get(position) == null) {
			view = View.inflate(context, R.layout.lv_files_layout, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = map.get(position);
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_filename.setText(fileName);
		viewHolder.iv_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyCheckView iv_check = ((MyCheckView) map.get(position)
						.findViewById(R.id.iv_check));
				iv_check.setChecked(!iv_check.isChecked());
			}
		});
		map.put(position, view);
		return view;
	}

	private class ViewHolder {
		TextView tv_filename;
		MyCheckView iv_check;

		public ViewHolder(View view) {
			tv_filename = (TextView) view.findViewById(R.id.tv_filename);
			iv_check = (MyCheckView) view.findViewById(R.id.iv_check);
		}
	}

	@Override
	public Object getItem(int position) {
		return map.get(position);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * @param myCheckView
	 */
	public void setChecked(final MyCheckView myCheckView, boolean isChecked) {
		myCheckView.setChecked(isChecked);
		notifyDataSetChanged();
	}

	public Map<Integer, View> getViewMap() {
		return map;
	}

}
