package edu.ecjtu.domain.fragments.localactivity;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.ecjtu.activities.LocalActivity;
import edu.ecjtu.domain.Util.MediaUtils;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.localactivity.FolderListviewAdapter;
import edu.ecjtu.domain.adapters.localactivity.FolderListviewAdapter.OnFilePathsChangedListener;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.domain.vo.objects.FilePath;
import edu.ecjtu.musicplayer.R;

public class LocalFolderFragment extends Fragment {

	private ListView lv_local_folders;
	private RelativeLayout rl_nofolder, rl_local_folders;
	private TextView tv_folder_amount;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.local_fragment_folders,
				null);
		initViews();
		registerListeners();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		LocalActivity lActivity = ((LocalActivity) getActivity());
		if (lActivity.isShouldReloadFolders()) {
			lActivity.reloadFolders();
		} else {
			((LocalActivity) getActivity()).handler
					.sendEmptyMessage(Utils.FOLDERFRAGMENT_RESUMED);
		}
	}

	private void initViews() {
		rl_local_folders = (RelativeLayout) view
				.findViewById(R.id.rl_local_folders);
		rl_nofolder = (RelativeLayout) view.findViewById(R.id.rl_nofolder);
		lv_local_folders = (ListView) view.findViewById(R.id.lv_local_folders);
		this.tv_folder_amount = (TextView) view
				.findViewById(R.id.tv_folder_amount);
	}

	/**
	 * 注册监听器
	 */
	private void registerListeners() {
		this.lv_local_folders
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parentView,
							View v, int position, long arg3) {
						((FolderListviewAdapter) parentView.getAdapter())
								.showDialog(position);
						return false;
					}
				});
	}

	/**
	 * @param filePaths
	 */
	public void initLocalFolder(List<FilePath> filePaths) {
		if (filePaths.size() > 0) {
			rl_local_folders.setVisibility(View.VISIBLE);
			lv_local_folders.setAdapter(new FolderListviewAdapter(
					getActivity(), filePaths));
			this.tv_folder_amount.setText(filePaths.size() + "个文件夹");
			rl_nofolder.setVisibility(View.GONE);
		} else {
			rl_nofolder.setVisibility(View.VISIBLE);
			rl_local_folders.setVisibility(View.GONE);
		}
		if (lv_local_folders.getAdapter() != null) {
			((FolderListviewAdapter) lv_local_folders.getAdapter())
					.setOnFilePathsChangedListener(new OnFilePathsChangedListener() {

						@Override
						public void OnFilePathsChange(List<FilePath> filepaths) {
							initLocalFolder(filepaths);
							((LocalActivity) getActivity()).reloadSongs();
						}
					});
		}
	};

	/**
	 * 模糊初始化文件路径listview
	 * 
	 * @param filePaths
	 *            填充的数据
	 */
	public void fuzzyInitLocalFolder(List<FilePath> filePaths) {
		lv_local_folders.setAdapter(new FolderListviewAdapter(getActivity(),
				filePaths));
		this.tv_folder_amount.setText(filePaths.size() + "个文件夹");
	}

	/**
	 * 获取歌手信息
	 * 
	 * @return 返回歌手集合
	 */
	public List<Artist> getArtists() {
		List<Artist> artists = MediaUtils.getList(Artist.class, getActivity(),
				MusicProvider.ARTIST_URI, new String[] { "_id", "Artistname",
						"Count" }, null, null, null);
		return artists;
	}

}
