package com.lakala.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 *  火车票、飞机票相关票务信息
 *  +-----------------------------------+
 *  |        label1        label2       |
 *  |        label3 [icon] label4       |
 *  |  [icon]label5        [icon]label5 |
 *  +-----------------------------------+
 *  Created by TOM on 13-10-11.
 */
public class FromToInfoItemView extends IconItemView {
    private TextView leftFirstLineText;  //左边第一行文本
    private TextView rightFirstLineText;  //右边第一行文本
    private TextView leftSecondLineText; //左边第二行文本
    private TextView rightSecondLineText; //右边第二行文本
    private TextView leftThirdLineText;  //左边第三行文本
    private TextView rightThirdLineText;  //右边第三行文本
    private ImageView centerIcon;       //中部的图标

    public FromToInfoItemView(Context context) {
        super(context);
    }
    public FromToInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置左边第一行文本
     * @param text  文本
     */
    public void setLeftFirstLineText(CharSequence text){
        leftFirstLineText.setText(text);
    }

    /**
     * 设置左边第一行文本
     * @param resid 文本资源 id。
     */
    public void setLeftFirstLineText(int resid){
        leftFirstLineText.setText(resid);
    }

    /**
     * 设置左边第一行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setLeftFirstLineTextSize(int unit,float size){
        leftFirstLineText.setTextSize(unit,size);
    }

    /**
     * 设置左边第一行文本字体颜色
     * @param color  颜色值。
     */
    public void setLeftFirstLineTextColor(int color){
        leftFirstLineText.setTextColor(color);
    }

    /**
     * 设置左边第一行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setLeftFirstLineTextStyle(int style){
        TextPaint paint = leftFirstLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置左边第一行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setLeftFirstLineTextVisible(int isVisible){
        leftFirstLineText.setVisibility(isVisible);
    }

    /**
     * 设置左边第一行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setLeftFirstLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        leftFirstLineText.setLayoutParams(layoutParams);
    }

    /**
     * 设置右边第一行文本
     * @param text  文本
     */
    public void setRightFirstLineText(CharSequence text){
        rightFirstLineText.setText(text);
    }

    /**
     * 设置右边第一行文本
     * @param resid 文本资源 id。
     */
    public void setRightFirstLineText(int resid){
        rightFirstLineText.setText(resid);
    }

    /**
     * 设置右边第一行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setRightFirstLineTextSize(int unit,float size){
        rightFirstLineText.setTextSize(unit,size);
    }

    /**
     * 设置右边第一行文本字体颜色
     * @param color  颜色值。
     */
    public void setrightFirstLineTextColor(int color){
        rightFirstLineText.setTextColor(color);
    }

    /**
     * 设置右边第一行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setRightFirstLineTextStyle(int style){
        TextPaint paint = rightFirstLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置右边第一行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setrightFirstLineTextVisible(int isVisible){
        rightFirstLineText.setVisibility(isVisible);
    }

    /**
     * 设置右边第一行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setRightFirstLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        rightFirstLineText.setLayoutParams(layoutParams);
    }

    /****************************************************************/
    /**
     * 设置左边第二行文本
     * @param text  文本
     */
    public void setLeftSecondLineText(CharSequence text){
        leftSecondLineText.setText(text);
    }

    /**
     * 设置左边第二行文本
     * @param resid 文本资源 id。
     */
    public void setLeftSecondLineText(int resid){
        leftSecondLineText.setText(resid);
    }

    /**
     * 设置左边第二行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setLeftSecondLineTextSize(int unit,float size){
        leftSecondLineText.setTextSize(unit,size);
    }

    /**
     * 设置左边第二行文本字体颜色
     * @param color  颜色值。
     */
    public void setLeftSecondLineTextColor(int color){
        leftSecondLineText.setTextColor(color);
    }

    /**
     * 设置左边第二行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setLeftSecondLineTextStyle(int style){
        TextPaint paint = leftSecondLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置左边第二行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setLeftSecondLineTextVisible(int isVisible){
        leftSecondLineText.setVisibility(isVisible);
    }

    /**
     * 设置左边第二行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setLeftSecondLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        leftSecondLineText.setLayoutParams(layoutParams);
    }

    /**
     * 设置右边第二行文本
     * @param text  文本
     */
    public void setRightSecondLineText(CharSequence text){
        rightSecondLineText.setText(text);
    }

    /**
     * 设置右边第二行文本
     * @param resid 文本资源 id。
     */
    public void setRightSecondLineText(int resid){
        rightSecondLineText.setText(resid);
    }

    /**
     * 设置右边第二行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setRightSecondLineTextSize(int unit,float size){
        rightSecondLineText.setTextSize(unit,size);
    }

    /**
     * 设置右边第二行文本字体颜色
     * @param color  颜色值。
     */
    public void setRightSecondLineTextColor(int color){
        rightSecondLineText.setTextColor(color);
    }

    /**
     * 设置右边第二行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setRightSecondLineTextStyle(int style){
        TextPaint paint = rightSecondLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置右边第二行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setRightSecondLineTextVisible(int isVisible){
        rightSecondLineText.setVisibility(isVisible);
    }

    /**
     * 设置右边第二行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setRightSecondLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        rightSecondLineText.setLayoutParams(layoutParams);
    }
    /*****************************************************************************/
    /**
     * 设置左边第三行文本
     * @param text  文本
     */
    public void setLeftThirdLineText(CharSequence text){
        leftThirdLineText.setText(text);
    }

    /**
     * 设置左边第三行文本
     * @param resid 文本资源 id。
     */
    public void setLeftThirdLineText(int resid){
        leftThirdLineText.setText(resid);
    }

    /**
     * 设置左边第三行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setLeftThirdLineTextSize(int unit,float size){
        leftThirdLineText.setTextSize(unit,size);
    }

    /**
     * 设置左边第三行文本字体颜色
     * @param color  颜色值。
     */
    public void setLeftThirdLineTextColor(int color){
        leftThirdLineText.setTextColor(color);
    }

    /**
     * 设置左边第三行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setThirdLineTextStyle(int style){
        TextPaint paint = leftThirdLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置左边第三行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setLeftThirdLineTextVisible(int isVisible){
        leftThirdLineText.setVisibility(isVisible);
    }

    /**
     * 设置左边第三行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setLeftThirdLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        leftThirdLineText.setLayoutParams(layoutParams);
    }

    /**
     * 设置边第三行文左本的Padding位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setLeftThirdLinePadding(int left,int top,int right,int bottom){
        leftThirdLineText.setPadding(left,top,right,bottom);
    }

    /**
     * 设置右边第三行文本
     * @param text  文本
     */
    public void setRightThirdLineText(CharSequence text){
        rightThirdLineText.setText(text);
    }

    /**
     * 设置右边第三行文本
     * @param resid 文本资源 id。
     */
    public void setRightThirdLineText(int resid){
        rightThirdLineText.setText(resid);
    }

    /**
     * 设置右边第三行文本字体大小
     * @param unit  单位
     * @param size  字体大小。
     * @see android.widget.TextView#setTextSize(int, float)
     */
    public void setRightThirdLineTextSize(int unit,float size){
        rightThirdLineText.setTextSize(unit,size);
    }

    /**
     * 设置右边第三行文本字体颜色
     * @param color  颜色值。
     */
    public void setRightThirdLineTextColor(int color){
        rightThirdLineText.setTextColor(color);
    }

    /**
     * 设置右边第三行文本字体样式（正常、粗体、斜体 的组合值）
     * @param style  字体样式值。
     */
    public void setRightThirdLineTextStyle(int style){
        TextPaint paint = rightThirdLineText.getPaint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, style));
    }

    /**
     * 设置右边第三行文本是否显示
     * @param isVisible  是否显示。
     */
    public void setRightThirdLineTextVisible(int isVisible){
        rightThirdLineText.setVisibility(isVisible);
    }

    /**
     * 设置右边第三行文本的Margin位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setRightThirdLineMargin(int left,int top,int right,int bottom){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(left,top,right,bottom);
        rightThirdLineText.setLayoutParams(layoutParams);
    }

    /**
     * 设置右边第三行文本的Padding位置
     * @param left  距离左部dp值
     * @param top   距离顶部dp值
     * @param right 距离右部dp值
     * @param bottom 距离底部dp值
     * */
    public void setRightThirdLinePadding(int left,int top,int right,int bottom){
        rightThirdLineText.setPadding(left,top,right,bottom);
    }
    /*********************************************************************/

    /**
     * 设置中部图标
     * @param drawable   Drawable 对象
     */
    public void setCenterIconDrawable(Drawable drawable){
        centerIcon.setImageDrawable(drawable);
    }

    /**
     * 设置中部图标
     * @param bitmap   Bitmap 对象
     */
    public void setCenterIconBitmap(Bitmap bitmap){
        centerIcon.setImageBitmap(bitmap);
    }

    /**
     * 设置中部图标资源 ID
     * @param resid   资源id
     */
    public void setCenterIconResource(int resid){
        centerIcon.setImageResource(resid);
    }

    /**
     * 设置中部图标资源是否可见
     * @param isVisible   是否显示
     */
    public void setCenterIconVisible(int isVisible){
        centerIcon.setVisibility(isVisible);
    }
    /************************************************************/

    @Override
    protected ViewGroup loadLayout(ViewGroup container, AttributeSet attrs) {
        ViewGroup vg = super.loadLayout(container, attrs);
        if (vg == null) return null;

        //从布局文件 l_tick_info_item.xml
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.l_tick_info_item,vg);

        leftFirstLineText    = (TextView)  findViewById(R.id.left_first_line_text);
        leftSecondLineText   = (TextView)  findViewById(R.id.left_second_line_text);
        leftThirdLineText    = (TextView)  findViewById(R.id.left_third_line_text);

        centerIcon          = (ImageView) findViewById(R.id.center_imageview);

        rightFirstLineText    = (TextView)  findViewById(R.id.right_first_line_text);
        rightSecondLineText   = (TextView)  findViewById(R.id.right_second_line_text);
        rightThirdLineText    = (TextView)  findViewById(R.id.right_third_line_text);

        //继续返回
        return vg;
    }
}
