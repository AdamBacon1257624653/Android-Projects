package edu.ecjtu.domain.adapters.localactivity;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LocalViewPagerAdapter extends FragmentPagerAdapter {

	List<Fragment> fragments = null;

	public LocalViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	/**
	 * 类似于listview中的getView
	 */
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
