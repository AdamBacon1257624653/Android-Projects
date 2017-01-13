package edu.ecjtu.domain.fragments.localactivity;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
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
import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.localactivity.SingerListviewAdapter;
import edu.ecjtu.domain.adapters.localactivity.SingerListviewAdapter.OnArtistsChangedListener;
import edu.ecjtu.domain.database.MusicProvider;
import edu.ecjtu.domain.views.LetterView;
import edu.ecjtu.domain.views.LetterView.OnLetterUpdateListener;
import edu.ecjtu.domain.vo.interfaces.FragmentInterface;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.domain.vo.objects.Artist;
import edu.ecjtu.musicplayer.R;

public class LocalSingerFragment extends Fragment implements FragmentInterface {

	private RelativeLayout rl_local_artists, rl_noartist;
	private ListView lv_local_artists;
	private View view;
	private LetterView singer_letterview;
	private TextView tv_singer_show_letter;
	private List<Artist> artists;
	private Handler handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.local_fragment_singers,
				null);
		initViews();
		this.handler = ((LocalActivity) getActivity()).handler;
		handler.sendEmptyMessage(Utils.SINGERFRAGMENT_CREATED);
		registerListeners();
		return view;
	}

	private void initViews() {
		this.rl_local_artists = (RelativeLayout) view
				.findViewById(R.id.rl_local_artists);
		this.rl_noartist = (RelativeLayout) view.findViewById(R.id.rl_noartist);
		this.lv_local_artists = (ListView) view
				.findViewById(R.id.lv_local_artists);
		this.singer_letterview = (LetterView) view
				.findViewById(R.id.singer_letterview);
		this.tv_singer_show_letter = (TextView) view
				.findViewById(R.id.tv_singer_show_letter);
	}

	@Override
	public void onResume() {
		super.onResume();
		((LocalActivity) getActivity()).handler
				.sendEmptyMessage(Utils.SINGERFRAGMENT_RESUMED);
	}

	/**
	 * 注册监听器
	 */
	private void registerListeners() {
		singer_letterview
				.setOnLetterUpdateListener(new OnLetterUpdateListener() {

					@Override
					public void OnLetterUpdate(String letter) {
						PinyinUtil.letterQuickIndex(tv_singer_show_letter,
								handler, letter, artists, lv_local_artists);
					}
				});
	}

	/**
	 * 初始化歌手列表
	 * 
	 * @param artists
	 *            歌手集合
	 */
	public void initLocalSinger(List<Artist> artists) {
		this.artists = artists;
		if (artists.size() > 0) {
			this.lv_local_artists.setAdapter(new SingerListviewAdapter(
					getActivity(), artists));
			this.rl_local_artists.setVisibility(View.VISIBLE);
			this.rl_noartist.setVisibility(View.GONE);
		} else {
			this.rl_noartist.setVisibility(View.VISIBLE);
			this.rl_local_artists.setVisibility(View.GONE);
		}
		if (lv_local_artists.getAdapter() != null) {
			((SingerListviewAdapter) lv_local_artists.getAdapter())
					.setOnArtistsChangedListener(new OnArtistsChangedListener() {// 删除监听

						@Override
						public void OnArtistsChange(List<Artist> artists) {
							initLocalSinger(artists);
							((LocalActivity) getActivity()).reloadFolders();// 重新加载文件
						}
					});
			lv_local_artists
					.setOnItemLongClickListener(new OnItemLongClickListener() {// 长按监听

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int position, long arg3) {
							((SingerListviewAdapter) lv_local_artists
									.getAdapter()).showDialog(position);
							return false;
						}
					});
		}
	}

	public void fuzzyInitLocalSinger(List<Artist> artists) {
		this.lv_local_artists.setAdapter(new SingerListviewAdapter(
				getActivity(), artists));

	}

	/**
	 * 获取自己数据库的专辑
	 * 
	 * @return 返回专辑集合
	 */
	public List<Album> getAlbums() {
		return MediaUtils.getList(Album.class, getActivity(),
				MusicProvider.ALBUM_URI, new String[] { "Albumname", "Count",
						"_id" }, null, null, null);
	}

	/**
	 * 供activity访问
	 * 
	 * @return
	 */
	public LetterView getLetterView() {
		return this.singer_letterview;
	}
}
