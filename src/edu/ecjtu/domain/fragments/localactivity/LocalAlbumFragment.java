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
import edu.ecjtu.domain.Util.PinyinUtil;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.localactivity.AlbumListviewAdapter;
import edu.ecjtu.domain.adapters.localactivity.AlbumListviewAdapter.OnAlbumsChangedListener;
import edu.ecjtu.domain.views.LetterView;
import edu.ecjtu.domain.views.LetterView.OnLetterUpdateListener;
import edu.ecjtu.domain.vo.interfaces.FragmentInterface;
import edu.ecjtu.domain.vo.objects.Album;
import edu.ecjtu.musicplayer.R;

public class LocalAlbumFragment extends Fragment implements FragmentInterface {

	private RelativeLayout rl_local_albums, rl_noalbum;
	private ListView lv_local_albums;
	private View view;
	private LetterView album_letterview;
	private TextView tv_album_show_letter;
	private Handler handler;
	private List<Album> albums;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = View
				.inflate(getActivity(), R.layout.local_fragment_albums, null);
		initViews();
		this.handler = ((LocalActivity) getActivity()).handler;
		this.handler.sendEmptyMessage(Utils.ALBUMFRAGMENT_CREATED);
		registerListeners();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.handler.sendEmptyMessage(Utils.ALBUMFRAGMENT_RESUMED);
	}

	private void initViews() {
		rl_local_albums = (RelativeLayout) view
				.findViewById(R.id.rl_local_albums);
		rl_noalbum = (RelativeLayout) view.findViewById(R.id.rl_noalbum);
		lv_local_albums = (ListView) view.findViewById(R.id.lv_local_albums);
		tv_album_show_letter = (TextView) view
				.findViewById(R.id.tv_album_show_letter);
		album_letterview = (LetterView) view
				.findViewById(R.id.album_letterview);
	}

	private void registerListeners() {
		album_letterview
				.setOnLetterUpdateListener(new OnLetterUpdateListener() {

					@Override
					public void OnLetterUpdate(String letter) {
						PinyinUtil.letterQuickIndex(tv_album_show_letter,
								handler, letter, albums, lv_local_albums);
					}
				});
	}

	public void initLocalAlbums(List<Album> albums) {
		this.albums = albums;
		if (albums.size() > 0) {
			this.lv_local_albums.setAdapter(new AlbumListviewAdapter(
					getActivity(), albums));
			this.rl_local_albums.setVisibility(View.VISIBLE);
			this.rl_noalbum.setVisibility(View.GONE);
			((AlbumListviewAdapter) lv_local_albums.getAdapter())
					.setOnAlbumsChangedListener(new OnAlbumsChangedListener() {

						@Override
						public void OnAlbumsChange(List<Album> albums) {
							initLocalAlbums(albums);
							((LocalActivity) getActivity()).reloadArtists();
						}
					});
			lv_local_albums
					.setOnItemLongClickListener(new OnItemLongClickListener() {// 长按监听

						@Override
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int position, long arg3) {
							((AlbumListviewAdapter) lv_local_albums
									.getAdapter()).showDialog(position);
							return false;
						}
					});
		} else {
			this.rl_local_albums.setVisibility(View.GONE);
			this.rl_noalbum.setVisibility(View.VISIBLE);
		}
	}

	public void fuzzyInitLocalAlbums(List<Album> albumWordsList) {
		this.lv_local_albums.setAdapter(new AlbumListviewAdapter(getActivity(),
				albumWordsList));
	};
	
	/**
	 * 返回本地的letterview供activity調用
	 * @return
	 */
	public LetterView getLetterView(){
		return this.album_letterview;
	}
}
