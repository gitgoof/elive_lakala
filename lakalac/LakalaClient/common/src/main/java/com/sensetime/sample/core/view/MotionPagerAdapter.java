package com.sensetime.sample.core.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensetime.library.finance.liveness.NativeMotion;
import com.sensetime.sample.common.R;

import java.util.Arrays;

public class MotionPagerAdapter extends PagerAdapter{
	private static final String TAG = "MotionPagerAdapter";

	private int[] mMotions;

	public MotionPagerAdapter(int[] motions){
		mMotions = Arrays.copyOf(motions, motions.length);
	}	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Context context = container.getContext();
		View view = View.inflate(context, R.layout.common_view_motion, null);
		int motion = mMotions[position];
		try {
			switch(motion) {
				case NativeMotion.CV_LIVENESS_BLINK:
					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_blink));
//					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_blink);
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_blink_1);
					break;
				case NativeMotion.CV_LIVENESS_MOUTH:
					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_mouth));
//					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_mouth);
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_blink_1);
					break;
				case NativeMotion.CV_LIVENESS_HEADNOD:
					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_nod));
//					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_nod);
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_blink_1);
					break;
				case NativeMotion.CV_LIVENESS_HEADYAW:
					((TextView)view.findViewById(R.id.txt_title)).setText(context.getString(R.string.common_yaw));
//					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_yaw);
					((ImageView)view.findViewById(R.id.img_image)).setImageResource(R.drawable.common_img_blink_1);
					break;
			}
		} catch(OutOfMemoryError e) {
			e.printStackTrace();
		}
//		AnimationDrawable aDrawable = (AnimationDrawable)
//						((((ImageView)view.findViewById(R.id.img_image))).getDrawable());
//		aDrawable.start();

		container.addView(view);
		return view;
	}

	@Override
	public int getCount() {
		return mMotions == null ? 0 : mMotions.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}
}