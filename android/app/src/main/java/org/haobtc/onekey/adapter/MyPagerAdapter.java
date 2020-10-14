package org.haobtc.onekey.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

@Deprecated
public class MyPagerAdapter extends PagerAdapter {

	private List<View> viewList;

	public MyPagerAdapter(List<View> viewList){
		this.viewList = viewList;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@NonNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);
	}
/*	@Override
	public float getPageWidth(int position) {
		return 1f;
	}*/

	@Override
	public boolean isViewFromObject(View view, Object object) {

		return view == object;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}
