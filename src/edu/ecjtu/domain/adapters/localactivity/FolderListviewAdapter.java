package edu.ecjtu.domain.adapters.localactivity;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ecjtu.domain.views.OtherDialog;
import edu.ecjtu.domain.views.OtherDialog.OnDeleteSuccessedListener;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.musicplayer.R;

public class FolderListviewAdapter extends BaseAdapter {

	private Context context;
	List<FilePath> filePaths;

	public FolderListviewAdapter(Context context, List<FilePath> filePaths) {
		this.context = context;
		this.filePaths = filePaths;
	}

	@Override
	public int getCount() {
		return this.filePaths.size();
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		View view = null;
		ViewHolder viewHolder = null;
		FilePath filePath = filePaths.get(position);
		if (convertView == null) {
			view = View.inflate(context, R.layout.folders_lv_layout, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_parentname.setText(filePath.getParentname());
		viewHolder.tv_foldercount.setText(filePath.getCount() + "首");
		viewHolder.tv_absolutepath.setText(filePath.getAbsolutepath());
		viewHolder.iv_foloder_operation
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showDialog(position);
					}
				});
		return view;
	}

	public void showDialog(final int position) {
		OtherDialog dialog = new OtherDialog(context, filePaths.get(position));
		dialog.show();
		dialog.setOnDeleteSuccessedListener(new OnDeleteSuccessedListener() {

			@Override
			public void OnDeleteSuccessed(boolean isDeleted) {
				if (isDeleted) {
					filePaths.remove(position);
					notifyDataSetChanged();
				}
				listener.OnFilePathsChange(filePaths);
			}
		});
	}

	private class ViewHolder {
		TextView tv_parentname, tv_foldercount, tv_absolutepath;
		ImageView iv_foloder_operation;

		public ViewHolder(View view) {
			tv_parentname = (TextView) view.findViewById(R.id.tv_parentname);
			tv_foldercount = (TextView) view.findViewById(R.id.tv_foldercount);
			tv_absolutepath = (TextView) view
					.findViewById(R.id.tv_absolutepath);
			iv_foloder_operation = (ImageView) view
					.findViewById(R.id.iv_foloder_operation);
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
	 * 监听FilePathList的改变
	 */
	private OnFilePathsChangedListener listener;

	public interface OnFilePathsChangedListener {
		void OnFilePathsChange(List<FilePath> filePaths);
	}

	public void setOnFilePathsChangedListener(
			OnFilePathsChangedListener listener) {
		this.listener = listener;
	}
}
