package com.gavin.demo_indicator;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabPageIndicatorAdapter extends FragmentPagerAdapter {
	private List<Category> categories;

	public TabPageIndicatorAdapter(FragmentManager fm) {
		super(fm);
	}

	public void setData(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = new MyFragment();
		Bundle args = new Bundle();
		args.putString(MyFragment.NAME, categories.get(position).getName());
		fragment.setArguments(args);
		return fragment;
	}

//	@Override
//	public CharSequence getPageTitle(int position) {
//		return categories.get(position % categories.size()).getName();
//	}

	@Override
	public int getCount() {
		return categories == null ? 0 : categories.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}
