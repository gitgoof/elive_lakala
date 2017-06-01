package com.lakala.shoudan.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.R;

/**
 * 引导页面
 * Created by andy_lv on 2014/6/18.
 */
public class GuideActivity extends AppBaseActivity {
    /**
     * 存放下一级页面,不要在这里写死啊!!!!!
     */
    public static final String KEY_NEXT_PAGE = "KEY_NEXT_PAGE";

    /**
     * 引导图片id
     */
    private static final int[] IMAGE_IDS = {R.drawable.guide_1};

    /**
     * 下一个页面,要求调用本类的页面传递过来
     */
    private String next;

    private Activity self;
    private ViewPager viewPager;
    private GuideAdapter guideAdapter;

    private int displayWidth = 0,displayHeight = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_guide);
        hideNavigationBar();
        viewPager = (ViewPager) findViewById(R.id.activity_guide_viewpager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null == guideAdapter) {
            initView();
        }

    }

    /**
     * 初始化UI
     */
    private void initView(){
        Rect outRect = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        displayHeight = outRect.height();
        displayWidth = outRect.width();

        guideAdapter = new GuideAdapter();
        viewPager.setAdapter(guideAdapter);
    }


    /**
     * 适配器
     */
    private class GuideAdapter extends PagerAdapter implements View.OnClickListener {

        private View getItemView(int position){
            View itemView = getLayoutInflater().inflate(R.layout.activity_guide_item,null);

            ImageView image = (ImageView) itemView.findViewById(R.id.activity_guide_imageview);

            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setImageDrawable(getScaleDrawable(IMAGE_IDS[position]));

            View btnOpen  = itemView.findViewById(R.id.activity_guide_open_app_button);
            btnOpen.setVisibility(position == IMAGE_IDS.length - 1 ? View.VISIBLE : View.INVISIBLE);
            btnOpen.setOnClickListener(this);

            return itemView;
        }

        @Override
        public void onClick(View view) {
            BusinessLauncher.getInstance().start("login_page");
        }

        @Override
        public int getCount() {
            return IMAGE_IDS.length;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (object != null) {
                ((ViewGroup) container).removeView((View) object);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = getItemView(position);
            container.addView(itemView);
            return itemView;
        }

        /**
         * 图片等比例缩放
         * @param drawbale
         * @return
         */
        private Drawable getScaleDrawable(int drawbale){
            Drawable returnDrawable  = null;
            Bitmap originalBitmap = BitmapFactory.decodeResource(self.getResources(),drawbale);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,displayWidth,displayHeight,false);

            returnDrawable = new BitmapDrawable(scaledBitmap);
            return returnDrawable;
        }

    }

}
