package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * 自定义tabveiw
 * 默认显示4个tab
 * Created by xyz on 13-7-23.
 */
public class TabView extends LinearLayout implements View.OnClickListener{

    private int background;         //背景
    private int selectedTabBg;      //当前选中tab背景
    private int splitImage;         //tab分割线
    private int tipsBackground;     //小贴士背景
    private int tipsTextColor;      //小贴士文字颜色
    private float tabTextSize;      //tab 文字大小
    private int tabTextColor;       //tab 文字颜色
    private int tabSelectedTextColor;       //tab 选中后文字颜色

    private int[] tabSrc = new int[4];			//tab图标
    private int[] tabSelectedSrc = new int[4];	//tab选中后的图标
    private String[] tabTexts = new String[4];  //tab文本内容

    private int currentSelectedTab = -1; //当前选中的tab
    private Tab[] tabs = new Tab[4];

    private int tabCount = 4;

    //tab选中监听器
    private OnTabSelectedListener onTabSelectedListener;

    private class Tab{
        public FrameLayout frameLayout;
        public TextView textTab;
        public TextView textView;
    }

    private int[] tabIds = {
            R.id.tab1,
            R.id.tab2,
            R.id.tab3,
            R.id.tab4
    };

    private int[] imageIds = {
            R.id.tab1image,
            R.id.tab2image,
            R.id.tab3image,
            R.id.tab4image
    };

    private int[] textViewIds = {
            R.id.tab1tips,
            R.id.tab2tips,
            R.id.tab3tips,
            R.id.tab4tips
    };

    /**
     * tab被�?中监听器,通过position指定当前选中的tab位置
     */
    public interface OnTabSelectedListener {
        public void onTabSelectedListener(View parentView, int position);
    }

    @Override
    public void onClick(View view) {
        int position = -1;
        int id = view.getId();
		if (id == R.id.tab1image) {
			position = 0;
		} else if (id == R.id.tab2image) {
			position = 1;
		} else if (id == R.id.tab3image) {
			position = 2;
		} else if (id == R.id.tab4image) {
			position = 3;
		}
        currentSelectedTab = position;
        setCurrentSelectedTabBg();
        onTabSelectedListener.onTabSelectedListener(this,position);
    }

    public TabView(Context context){
        this(context,null);
    }

    public TabView(Context context, AttributeSet attrs){
        super(context, attrs);

        if (isInEditMode()){
            return;
        }

        initView(context,attrs);
    }

    /**
     * 设置当前选中的tab位置
     * @param currentSelectedTab tab索引
     */
    public void setCurrentSelectedTab(int currentSelectedTab) {
        if (!isIndexValid(currentSelectedTab))
            return;
        this.currentSelectedTab = currentSelectedTab;
        setCurrentSelectedTabBg();
    }

    /**
     * 设置tab选择的监听器
     * @param onTabSelectedListener tab监听器
     */
    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener){
        this.onTabSelectedListener = onTabSelectedListener;
    }

    /**
     * 取消tab图片的选中效果
     */
    public void cancelTabSelectedBg(){
        currentSelectedTab = -1;
        setCurrentSelectedTabBg();
    }

    /**
     * 设置tab左上角的tips内容
     * @param tabIndex  需要显示tips的tab索引
     * @param tips      显示的内容如果tips=-1，则此tab不显示tips;<br/>
     *                  如果tips>99,则显示字符n
     */
    public void setTabTips(int tabIndex,int tips){
        if (!isIndexValid(tabIndex))
            return;
        if (tips == -1){
            tabs[tabIndex].textView.setVisibility(View.GONE);
        }else{
            String text = String.valueOf(tips);
            if (tips > 99)
                text = "n";
            tabs[tabIndex].textView.setVisibility(View.VISIBLE);
            tabs[tabIndex].textView.setText(text);
        }
    }

    /**
     * 设置tab文本内容，给索引为index的tab设置文本内容text
     * @param index
     * @param text
     */
    public void setTabText(int index,String text){
        if (!isIndexValid(index)){
            return;
        }
        tabs[index].textTab.setText(text);
    }

    /**
     * 初始化tabview
     * @param context   上下文
     * @param attrs     属性
     */
    private void initView(Context context,AttributeSet attrs){
        //获取xml中配置的属性资源
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TabView);

        float tipsTextSize = 0.0f;
        try{
            background      = typedArray.getResourceId(R.styleable.TabView_backgroundSrc,0);
            selectedTabBg   = typedArray.getResourceId(R.styleable.TabView_selectedTabBg,0);
            splitImage      = typedArray.getResourceId(R.styleable.TabView_splitImage,0);
            tipsBackground  = typedArray.getResourceId(R.styleable.TabView_tipsBackground, 0);
            tipsTextColor   = typedArray.getColor(R.styleable.TabView_tipsTextColor, 0xFFFFFFFF);
            tabCount        = typedArray.getInt(R.styleable.TabView_tabCount, 4);
            tabTextSize     = typedArray.getDimension(R.styleable.TabView_tabTextSize, 0.0f);
            tabTextColor    = typedArray.getColor(R.styleable.TabView_tabTextColor, Color.BLACK);
            tabSelectedTextColor    = typedArray.getColor(R.styleable.TabView_tabSelectedTextColor, Color.BLACK);

            tabSrc[0] = typedArray.getResourceId(R.styleable.TabView_tab1Src,0);
            tabSrc[1] = typedArray.getResourceId(R.styleable.TabView_tab2Src,0);
            tabSrc[2] = typedArray.getResourceId(R.styleable.TabView_tab3Src,0);
            tabSrc[3] = typedArray.getResourceId(R.styleable.TabView_tab4Src,0);
            
            tabSelectedSrc[0] = typedArray.getResourceId(R.styleable.TabView_tab1SelectedSrc,0);
            tabSelectedSrc[1] = typedArray.getResourceId(R.styleable.TabView_tab2SelectedSrc,0);
            tabSelectedSrc[2] = typedArray.getResourceId(R.styleable.TabView_tab3SelectedSrc,0);
            tabSelectedSrc[3] = typedArray.getResourceId(R.styleable.TabView_tab4SelectedSrc,0);

            tabTexts[0] = typedArray.getString(R.styleable.TabView_tab1Text);
            tabTexts[1] = typedArray.getString(R.styleable.TabView_tab2Text);
            tabTexts[2] = typedArray.getString(R.styleable.TabView_tab3Text);
            tabTexts[3] = typedArray.getString(R.styleable.TabView_tab4Text);

            tipsTextSize = typedArray.getDimension(R.styleable.TabView_tipsTextSize,0.0f);

        }finally {
            typedArray.recycle();
        }

        //初始化tab元素
        LayoutInflater.from(context).inflate(R.layout.l_tabview, this, true);

        //设置tabview背景
//        setBackgroundResource(background);

        //设置tab项内容
        for (int index = 0; index < tabCount; index++){
        	tabs[index] = new Tab();
            tabs[index].frameLayout = (FrameLayout)findViewById(tabIds[index]);
            //这里设置frameLayout的背景，使其有宽高值，因为里边的TextView需要内容居中
            tabs[index].frameLayout.setBackgroundResource(background);
            tabs[index].frameLayout.setVisibility(VISIBLE);
            tabs[index].textTab = (TextView)findViewById(imageIds[index]);
            if (Float.compare(tabTextSize, 0.0f) > 0){
                tabs[index].textTab.setTextSize(TypedValue.COMPLEX_UNIT_PX,tabTextSize);
            }
            tabs[index].textTab.setTextColor(tabTextColor);
            tabs[index].textTab.setOnClickListener(this);
            tabs[index].textTab.setBackgroundResource(tabSrc[index]);

            //设置分割线
            if (splitImage != 0){
                Drawable drawable = context.getResources().getDrawable(splitImage);
                if (drawable != null)
                    tabs[index].textTab.setCompoundDrawables(null, null, drawable, null);
            }


            if (!"".equals(tabTexts[index]))
                tabs[index].textTab.setText(tabTexts[index]);

            tabs[index].textView    = (TextView)findViewById(textViewIds[index]);
            //设置tips字体颜色,及背景
            tabs[index].textView.setTextColor(tipsTextColor);
            tabs[index].textView.setBackgroundResource(tipsBackground);

            if (Float.compare(tipsTextSize, 0.0f) > 0){
                tabs[index].textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,tipsTextSize);
            }

            //设置tab分割线
            if (index < tabCount-1){
                ImageView splitImageView = new ImageView(context);
                splitImageView.setBackgroundResource(splitImage);
                //noinspection deprecation
                splitImageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.FILL_PARENT, Gravity.RIGHT));


                tabs[index].frameLayout.addView(splitImageView, 0);

            }

        }

    }

    /**
     * 设置当前选中tab的背景
     */
    private void setCurrentSelectedTabBg(){
        for (int index = 0;index < tabCount;index++){
            if (currentSelectedTab != index){	//非选中状态背景及tab图标
            	tabs[index].frameLayout.setBackgroundResource(background);
            	tabs[index].textTab.setBackgroundResource(tabSrc[index]);
                tabs[index].textTab.setTextColor(tabTextColor);
            }else{	//选中状态背景及tab图标，没有设置tab选中图标的话，则不设置

                tabs[index].frameLayout.setBackgroundResource(selectedTabBg);

            	if (tabSelectedSrc[index] != 0) {
		        	tabs[index].textTab.setBackgroundResource(tabSelectedSrc[index]);
				} 
                tabs[index].textTab.setTextColor(tabSelectedTextColor);

            }
        }
    }


    /**
     * 判断传入的index是否合法
     * @param index index索引
     * @return  如果index < 0 或index >= tabCount,返回false
     */
    private boolean isIndexValid(int index){
        return index >= 0 && index < tabCount;
    }
}
