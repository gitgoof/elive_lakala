package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lakala.ui.R;

import java.util.ArrayList;

/**
 * Created by Blues on 13-10-12.
 */
public class IndexLayout extends View {

    OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    String[] indexStrings = {"当前","历史","热门","A","B","C","D","E","F","G","H","I","J","K","L"
            ,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","#"};

    String[] indexLowerCaseStrings = {"当前","历史","热门","a","b","c","d","e","f","g","h","i","j","k","l"
            ,"m","n","o","p","q","r","s","t","u","v","w","x","y","z","#"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    
    private int     color  = 0;
    private int     chooseColor  = 0;
    private float   size   = 0.0f;
    private ArrayList<String> indexList;
    private boolean isUppercase = true;

    public IndexLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * 设置Index是否大写
     * @param isUppercase 是否大写 true  大写   false 小写
     */
    public void setIndexUppercase(boolean isUppercase){
        this.isUppercase = isUppercase;
    }

    /**
     * 获取Index大小写
     * @return 是否大写 true  大写   false 小写
     */
    public boolean getIndexUppercase(){
        return isUppercase;
    }

    /**
     * 初始化索引内容
     */
    private void initIndexList(){
        indexList = new ArrayList<String>();
        for (int i=0;i<indexStrings.length;i++){
            if (isUppercase){
                indexList.add(indexStrings[i]);
            }else {
                indexList.add(indexLowerCaseStrings[i]);
            }
        }
    }

    /**
     * 更新索引条内容
     * @param list
     */
    public void updateIndexList(ArrayList<String> list){
        if (list==null && list.size()<=0) {
            initIndexList();
        }else {
            indexList = list;
        }
        invalidate();
    }

    /**
     * 移除定位，历史，或者热门索引
     * @param isHaveLoc
     * @param isHaveHis
     * @param isHaveHot
     */
    public void removeLocaAndHot(boolean isHaveLoc,boolean isHaveHis,boolean isHaveHot){
        initIndexList();
        for (int i = 0; i < indexList.size(); i++) {
            if (!isHaveLoc && indexList.get(i).equals("定位")) {
                indexList.remove(i);
            }
            if (!isHaveHis && indexList.get(i).equals("历史")) {
                indexList.remove(i);
            }
            if (!isHaveHot && indexList.get(i).equals("热门")) {
                indexList.remove(i);
                break;
            }
        }
        invalidate();
    }

    /**
     * 设置索引文字颜色
     * @param color
     */
    public void setColor(int color) {
      this.color = color;
      invalidate();
    }

    /**
     * 设置索引文字选中时的文字
     * @param chooseColor
     */
    public void setChooseColor(int chooseColor) {
      this.chooseColor = chooseColor;
      invalidate();
    }

    /**
     * 设置索引文字大小
     * @param size
     */
    public void setSize(float size) {
      this.size = size;
      invalidate();
    }

    public IndexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IndexLayout(Context context) {
        super(context);
    }
    
    private void init(AttributeSet attrs) {
      
      TypedArray array  = getContext().obtainStyledAttributes(attrs,R.styleable.IndexLayout);
      size              = array.getDimension(R.styleable.IndexLayout_indexTextSize, 0);
      color             = array.getColor(R.styleable.IndexLayout_indexTextColor, 0);
      chooseColor       = array.getColor(R.styleable.IndexLayout_indexChooseTextColor, 0);
      
      array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showBkg){
            canvas.drawColor(Color.parseColor("#40000000"));
        }
        int height = getHeight();
        int width = getWidth();
        if (indexList==null) initIndexList();
        int singleHeight = height / indexList.size();
        for(int i=0;i< indexList.size();i++){
            if (color ==0) {
              paint.setColor(Color.WHITE);
            }else {
              paint.setColor(color);
            }
            if (size!=0) {
              paint.setTextSize(size);
            }
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);//去掉锯齿
            if(i == choose){
                if (chooseColor ==0) {
                  paint.setColor(Color.parseColor("#3399ff"));
                }else {
                  paint.setColor(chooseColor);
                }
                paint.setFakeBoldText(true);
            }
            float xPos = width/2  - paint.measureText(indexList.get(i))/2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(indexList.get(i), xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y/getHeight()* indexList.size());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                showBkg = true;
                if(oldChoose != c && listener != null){
                    if(c >= 0 && c< indexList.size()){
                        listener.onTouchingLetterChanged(indexList.get(c));
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(oldChoose != c && listener != null){
                    if(c > 0 && c< indexList.size()){
                        listener.onTouchingLetterChanged(indexList.get(c));
                        System.out.println(c+"c"+ indexList.get(c));
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 索引布局文字文字触摸状态变化监听器(选中&正常)
     * @author Blues
     */
    public interface OnTouchingLetterChangedListener{
        public void onTouchingLetterChanged(String s);
    }

}
