package com.gavin.demo_indicator;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity {
	private ViewPager viewPager;
	private ViewPagerIndicator indicator;
	private TabPageIndicatorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		initData();
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.pager);
		adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(5);
		indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);

		// 设置选中和非选中文字的大小
		indicator.setItemTextSize(16, 16);
		// 设置选中和非选中文字的颜色
		indicator.setItemTextColor(0xff515151, getResources().getColor(R.color.text_pressed));
		// 一屏显示5项  
		indicator.setItemCount(5);
		indicator.setCurrentItem(0);
	}

	private void initData() {
		List<Category> categories = new ArrayList<Category>();
		List<String> titleNames = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			titleNames.add("item " + i);

			Category category = new Category();
			category.setId(i);
			category.setName("item " + i);
			categories.add(category);
		}
		indicator.setTabItemTitles(titleNames);
		adapter.setData(categories);
		adapter.notifyDataSetChanged();
	}
}
