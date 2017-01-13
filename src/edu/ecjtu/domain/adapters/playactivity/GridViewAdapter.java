package edu.ecjtu.domain.adapters.playactivity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ecjtu.domain.vo.objects.Gvitem;
import edu.ecjtu.musicplayer.R;

public class GridViewAdapter extends BaseAdapter {

	private List<Gvitem> gvitems;
	private Context context;

	public GridViewAdapter(List<Gvitem> gvitems, Context context) {
		super();
		this.gvitems = gvitems;
		this.context = context;
	}

	@Override
	public int getCount() {
		return gvitems.size();
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
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = convertView;
		Gvitem item = gvitems.get(position);
		if (view == null) {
			view = View.inflate(context, R.layout.gv_item_layout, null);
		}
		ViewHolder viewHolder = ViewHolder.getViewHolder(view);
		viewHolder.iv_gv.setImageResource(item.getItemResId());
		viewHolder.tv_gv.setText(item.getItemName());
		return view;
	}

	static class ViewHolder {
		ImageView iv_gv;
		TextView tv_gv;

		public static ViewHolder getViewHolder(View view) {
			Object tag = view.getTag();
			if (tag != null) {
				return (ViewHolder) tag;
			} else {
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.iv_gv = (ImageView) view.findViewById(R.id.iv_gv);
				viewHolder.tv_gv = (TextView) view.findViewById(R.id.tv_gv);
				view.setTag(viewHolder);
				return viewHolder;
			}
		}
	}
}
