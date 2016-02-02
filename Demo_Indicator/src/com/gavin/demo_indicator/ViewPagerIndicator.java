package com.gavin.demo_indicator;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * http://blog.csdn.net/lmj623565791/article/details/42160391
 */
public class ViewPagerIndicator extends HorizontalScrollView {
	/**
	 * 绘制矩形的画笔
	 */
	private Paint mPaint;
	/**
	 * path构成一个矩形
	 */
	private Rect mRect;

	/**
	 * 手指滑动时的偏移量
	 */
	private float mTranslationX;

	/**
	 * 默认的Tab数量
	 */
	private static final int COUNT_DEFAULT_TAB = 5;
	/**
	 * tab数量
	 */
	private int mTabVisibleCount = COUNT_DEFAULT_TAB;

	/**
	 * tab上的内容
	 */
	private List<String> mTabTitles;
	/**
	 * 与之绑定的ViewPager
	 */
	public ViewPager mViewPager;

	/**
	 * 标题正常时的颜色
	 */
	private int colorTextNormal = 0xff898989;
	/**
	 * 标题选中时的颜色
	 */
	private int colorTextSelected = 0xffb4161d;
	/**
	 * 标题正常时的大小
	 */
	private int sizeTextNormal = 18;
	/**
	 * 标题选中时的大小
	 */
	private int sizeTextSelected = 22;

	/**
	 * 
	 * 每一项的宽度
	 */
	private int itemWidth;
	/**
	 * 每一项的宽度
	 */
	private int itemHeight;

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTabVisibleCount = COUNT_DEFAULT_TAB;

		mPaint = new Paint();
		// 消除锯齿
		mPaint.setAntiAlias(true);
		// 设置画笔的颜色
		mPaint.setColor(colorTextSelected);

		linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		HorizontalScrollView.LayoutParams params = new HorizontalScrollView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		addView(linearLayout, params);

		setHorizontalScrollBarEnabled(false);
	}

	/**
	 * 绘制指示器
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(mTranslationX, getHeight() - itemHeight);

		// 设置paint的外框宽度
		// Log.i("mytag", "itemHeight:" + itemHeight);
		canvas.drawRect(mRect, mPaint);
		canvas.restore();

		super.dispatchDraw(canvas);
	}

	/**
	 * 初始化三角形的宽度
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		initRectancle();
	}

	/**
	 * 设置可见的tab的数量
	 * 
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		this.mTabVisibleCount = count;
	}

	/**
	 * 设置tab的标题内容 可选，可以自己在布局文件中写死
	 */
	public void setTabItemTitles(List<String> datas) {
		// 如果传入的list有值，则移除布局文件中设置的view
		if (datas != null && datas.size() > 0) {
			linearLayout.removeAllViews();
			this.mTabTitles = datas;
			for (String title : mTabTitles) {
				// 添加view
				TextView textView = generateTextView(title);
				linearLayout.addView(textView);
			}
			// 设置item的click事件
			setItemClickEvent();
		}

	}

	/**
	 * 对外的ViewPager的回调接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface PageChangeListener {
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	// 对外的ViewPager的回调接口
	private PageChangeListener onPageChangeListener;
	private LinearLayout linearLayout;
	private int itemCount;
	private int downX;

	// 对外的ViewPager的回调接口的设置
	public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
		this.onPageChangeListener = pageChangeListener;
	}

	// 设置关联的ViewPager
	public void setViewPager(ViewPager mViewPager) {
		this.mViewPager = mViewPager;

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 设置字体颜色高亮
				resetTextView(position);

				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// 滚动
				scroll(position, positionOffset);

				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrolled(position,
							positionOffset, positionOffsetPixels);
				}

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrollStateChanged(state);
				}

			}
		});
	}

	public void setCurrentItem(int position) {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(position);
		}
		resetTextView(position);
	}

	/**
	 * 重置文本
	 */
	private void resetTextView(int position) {
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			TextView view = (TextView) linearLayout.getChildAt(i);
			view.setTextColor(i == position ? colorTextSelected
					: colorTextNormal);
			view.setTextSize(i == position ? sizeTextSelected : sizeTextNormal);
		}
	}

	/**
	 * 设置点击事件
	 */
	public void setItemClickEvent() {
		int cCount = linearLayout.getChildCount();
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = linearLayout.getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}

	/**
	 * 根据标题生成我们的TextView
	 * 
	 * @param text
	 * @return
	 */
	private TextView generateTextView(String text) {
		TextView tv = new TextView(getContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = getScreenWidth() / mTabVisibleCount;
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(colorTextNormal);
		tv.setText(text);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setLayoutParams(lp);
		return tv;
	}

	/**
	 * 初始化矩形指示器
	 */
	private void initRectancle() {
		itemWidth = getWidth() / mTabVisibleCount;
		itemHeight = (int) (getResources().getDisplayMetrics().density * 2);
		mRect = new Rect(0, 0, itemWidth, itemHeight);
	}

	/**
	 * 指示器跟随手指滚动，以及容器滚动
	 * 
	 * @param position
	 * @param offset
	 */
	public void scroll(int position, float offset) {
		// 不断改变偏移量，invalidate
		mTranslationX = getWidth() / mTabVisibleCount * (position + offset);

		int tabWidth = getScreenWidth() / mTabVisibleCount;

		scrollTo((int) ((position - 1 + offset) * tabWidth), 0);

		invalidate();
	}

	/**
	 * 设置布局中view的一些必要属性；如果设置了setTabTitles，布局中view则无效
	 */
	@Override
	protected void onFinishInflate() {
//		Log.e("mytag", "onFinishInflate");
		super.onFinishInflate();

		int cCount = linearLayout.getChildCount();

		if (cCount == 0)
			return;

		for (int i = 0; i < cCount; i++) {
			View view = linearLayout.getChildAt(i);
			LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) view
					.getLayoutParams();
			lp.weight = 0;
			lp.width = getScreenWidth() / mTabVisibleCount;
			view.setLayoutParams(lp);
		}
		// 设置点击事件
		setItemClickEvent();

	}

	/**
	 * 获得屏幕的宽度
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/** 设置每一项默认和点击后的颜色 **/
	public void setItemTextColor(int nomalColor, int selectedColor) {
		colorTextNormal = nomalColor;
		colorTextSelected = selectedColor;
		// 设置底部下划线的颜色
		mPaint.setColor(colorTextSelected);
	}

	/** 设置每一项默认和点击后的字体大小 **/
	public void setItemTextSize(int nomalSize, int selectedSize) {
		sizeTextNormal = nomalSize;
		sizeTextSelected = selectedSize;
	}

	/** 设置tab的数量 **/
	public void setItemCount(int count) {
		mTabVisibleCount = count;
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent event) {
//		return true;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) event.getX();
		}
		int currentX = (int) event.getX();
		int scrollX = getScrollX();
		boolean disallowIntercept = true;
		if (scrollX == 0 && currentX > downX) {
			disallowIntercept = false;
		}
		getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
		return super.onTouchEvent(event);
	}

}
