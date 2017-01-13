package edu.ecjtu.domain.adapters.playactivity;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import edu.ecjtu.domain.vo.objects.Advertisement;
import edu.ecjtu.musicplayer.R;

public class ImageVIewPageAdapter extends PagerAdapter {

	private Context context;
	private List<Advertisement> advertisements;

	public ImageVIewPageAdapter(Context context,
			List<Advertisement> advertisements) {
		super();
		this.context = context;
		this.advertisements = advertisements;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = View.inflate(context, R.layout.ad_layout, null);
		ImageView iv_ad = (ImageView) view.findViewById(R.id.iv_ad);
		iv_ad.setImageResource(advertisements.get(
				position % advertisements.size()).getImageResId());
		container.addView(view);
		return view;
	}
}
