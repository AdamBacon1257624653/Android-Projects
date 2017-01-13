package edu.ecjtu.domain.fragments.playacitivy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import edu.ecjtu.activities.OnLineSearchActivity;
import edu.ecjtu.activities.PlayerActivity;
import edu.ecjtu.domain.Util.Utils;
import edu.ecjtu.domain.adapters.playactivity.GridViewAdapter;
import edu.ecjtu.domain.adapters.playactivity.ImageVIewPageAdapter;
import edu.ecjtu.domain.vo.objects.Advertisement;
import edu.ecjtu.domain.vo.objects.Gvitem;
import edu.ecjtu.musicplayer.R;

public class Musicdb extends Fragment implements OnClickListener {
	private ViewPager vp_image;
	private View view;
	private List<Advertisement> advertisements = new ArrayList<Advertisement>();
	private List<Gvitem> gvitems = new ArrayList<Gvitem>();
	private PlayerActivity playerActivity;
	private Handler handler;
	private TextView tv_intro, tv_musicdb_titlea, tv_musicdb_descriptionb;
	private LinearLayout ll_dot;
	private GridView gv_musicdb;
	private EditText et_onlinesearch_db;
	private int value;
	private int itemPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_musicdb, null);
		initViews();
		initData();
		inflateViews();
		registerListeners();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initViews() {
		vp_image = (ViewPager) view.findViewById(R.id.vp_image);
		ll_dot = (LinearLayout) view.findViewById(R.id.ll_dot);
		tv_intro = (TextView) view.findViewById(R.id.tv_intro);
		gv_musicdb = (GridView) view.findViewById(R.id.gv_musicdb);
		tv_musicdb_titlea = (TextView) view
				.findViewById(R.id.tv_musicdb_titlea);
		tv_musicdb_descriptionb = (TextView) view
				.findViewById(R.id.tv_musicdb_descriptionb);
		et_onlinesearch_db = (EditText) view
				.findViewById(R.id.et_onlinesearch_db);
	}

	private void initData() {
		initAdavertisements();
		initGvItems();
		playerActivity = (PlayerActivity) getActivity();
		handler = playerActivity.getHandler();
	}

	/**
	 * 为类似listview的textview填充数据
	 */
	private void inflateItemsTextView() {
		int dayInt = new Date().getDate();
		int monthInt = new Date().getMonth() + 1;
		tv_musicdb_titlea.setText(monthInt + "月" + dayInt + "日最新单曲");
		tv_musicdb_descriptionb.setText("非常原创电台" + monthInt + "." + dayInt);
	}

	private void initGvItems() {
		gvitems.add(new Gvitem(R.drawable.gv_sort, "分类"));
		gvitems.add(new Gvitem(R.drawable.gv_rank, "排行"));
		gvitems.add(new Gvitem(R.drawable.gv_radio, "电台"));
		gvitems.add(new Gvitem(R.drawable.gv_singer, "歌手"));
		gvitems.add(new Gvitem(R.drawable.gv_local, "本地"));
		gvitems.add(new Gvitem(R.drawable.gv_earphone, "耳机"));
	}

	private void initAdavertisements() {
		advertisements.add(new Advertisement(R.drawable.page_image01,
				"我和音乐有个约会"));
		advertisements.add(new Advertisement(R.drawable.page_image02,
				"因为旋律和梦,这里不再孤单"));
		advertisements.add(new Advertisement(R.drawable.page_image03,
				"风马音乐节,携手郑钧和许巍"));
		advertisements.add(new Advertisement(R.drawable.page_image04,
				"国际音乐节,Come on!"));
		advertisements.add(new Advertisement(R.drawable.page_image05,
				"中国艺术家,美国纽约林肯中心音乐会"));
	}

	private void inflateViews() {
		vp_image.setAdapter(new ImageVIewPageAdapter(playerActivity,
				advertisements));
		gv_musicdb.setAdapter(new GridViewAdapter(gvitems, playerActivity));
		inflateItemsTextView();
		value = Integer.MAX_VALUE / 2;
		value = value - value % advertisements.size();
		vp_image.setCurrentItem(value);
		itemPosition = vp_image.getCurrentItem() % advertisements.size();
		tv_intro.setText(advertisements.get(itemPosition).getDescription());
		for (int i = 0; i < advertisements.size(); i++) {
			View dotView = new View(playerActivity);
			LayoutParams params = new LayoutParams(15, 15);
			if (i != 0) {
				params.leftMargin = 20;
			}
			dotView.setLayoutParams(params);
			dotView.setBackgroundResource(R.drawable.ad_selector);
			dotView.setEnabled(i == itemPosition);
			ll_dot.addView(dotView);
		}
		handler.sendEmptyMessageDelayed(Utils.UPDATE_IMAGE, 3000);
	}

	/**
	 * 更新vp_image的图片
	 */
	public void updatePage() {
		vp_image.setCurrentItem(vp_image.getCurrentItem() + 1, true);
		updateIntroDot();
	}

	/**
	 * 更新文本和点
	 */
	private void updateIntroDot() {
		itemPosition = vp_image.getCurrentItem() % advertisements.size();
		tv_intro.setText(advertisements.get(itemPosition).getDescription());
		for (int i = 0; i < advertisements.size(); i++) {
			View dotView = ll_dot.getChildAt(i);
			dotView.setEnabled(i == itemPosition);
		}
	}

	private void registerListeners() {
		this.et_onlinesearch_db.setOnClickListener(this);
		this.vp_image.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				Musicdb.this.updateIntroDot();// 更新文本消息和圆点状态
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_onlinesearch_db:
			Intent onlineSearchIntent = new Intent(playerActivity,
					OnLineSearchActivity.class);
			startActivity(onlineSearchIntent);
			break;
		}
	}
}
