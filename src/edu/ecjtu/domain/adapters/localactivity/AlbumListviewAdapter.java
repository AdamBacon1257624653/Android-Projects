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
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.musicplayer.R;

public class AlbumListviewAdapter extends BaseAdapter {

	private Context context;
	List<Album> albums;
	private Map<Integer, View> viewMap = new HashMap<Integer, View>();

	public AlbumListviewAdapter(Context context, List<Album> albums) {
		this.context = context;
		this.albums = albums;
	}

	@Override
	public int getCount() {
		return this.albums.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		ViewHolder viewHolder = null;
		Album album = albums.get(position);
		if (viewMap.get(position) == null) {
			view = View.inflate(context, R.layout.albums_lv_layout, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = viewMap.get(position);
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_albumname.setText(album.getAlbumname());
		viewHolder.tv_albumcount.setText(album.getCount() + "首");
		viewHolder.iv_album_operation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(position);
			}
		});
		// 设置字母
		PinyinUtil.initLetterTextView(position, albums,
				viewHolder.tv_album_letter);
		// 設置底部
		PinyinUtil.initBottom(position, albums, viewHolder.tv_albumamount,
				viewHolder.ll_album_bottom, "张专辑");
		viewMap.put(position, view);
		return view;
	}

	public void showDialog(final int position) {
		OtherDialog dialog = new OtherDialog(context, albums.get(position));
		dialog.show();
		dialog.setOnDeleteSuccessedListener(new OnDeleteSuccessedListener() {

			@Override
			public void OnDeleteSuccessed(boolean isDeleted) {
				if (isDeleted) {
					albums.remove(position);
					notifyDataSetChanged();
				}
				listener.OnAlbumsChange(albums);
			}
		});
	}

	private class ViewHolder {
		TextView tv_albumname, tv_albumcount, tv_albumamount, tv_album_letter;
		ImageView iv_album_operation;
		LinearLayout ll_album_bottom;

		public ViewHolder(View view) {
			tv_albumname = (TextView) view.findViewById(R.id.tv_albumname);
			tv_albumcount = (TextView) view.findViewById(R.id.tv_albumcount);
			iv_album_operation = (ImageView) view
					.findViewById(R.id.iv_album_operation);
			tv_albumamount = (TextView) view.findViewById(R.id.tv_albumamount);
			ll_album_bottom = (LinearLayout) view
					.findViewById(R.id.ll_album_bottom);
			tv_album_letter = (TextView) view
					.findViewById(R.id.tv_album_letter);
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
	 * 监听Albums的改变
	 */
	private OnAlbumsChangedListener listener;

	public interface OnAlbumsChangedListener {
		void OnAlbumsChange(List<Album> albums);
	}

	public void setOnAlbumsChangedListener(OnAlbumsChangedListener listener) {
		this.listener = listener;
	}
}
