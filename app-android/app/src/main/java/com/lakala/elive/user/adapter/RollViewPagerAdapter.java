package com.lakala.elive.user.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.lakala.elive.R;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/9/28 15:47
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class RollViewPagerAdapter extends StaticPagerAdapter {

    private int[] imgs = {
            R.drawable.gg_01,
            R.drawable.gg_02,
            R.drawable.gg_03
    };


    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setImageResource(imgs[position]);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return view;
    }


    @Override
    public int getCount() {
        return imgs.length;
    }

}
