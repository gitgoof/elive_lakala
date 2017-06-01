package com.lakala.shoudan.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * Created by admin on 2017/2/24.
 */

public class IconTextView extends LinearLayout {
    public IconTextView(Context context) {
        super(context);
        initView(context,R.drawable.logo,"");
    }



    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.iconTextView);
        int  icon=ta.getResourceId(R.styleable.iconTextView_left_icon,R.drawable.logo);
        String  name=ta.getString(R.styleable.iconTextView_name);

        initView(context,icon,name);
    }
    private void initView(Context context,int icon, String name) {
        View v=LayoutInflater.from(context).inflate(R.layout.layout_icontextview,this,true);
        ImageView iv= (ImageView) v.findViewById(R.id.iv_icon);
        TextView tv= (TextView) v.findViewById(R.id.tv_title);
        iv.setImageResource(icon);
        tv.setText(name);


    }
}
