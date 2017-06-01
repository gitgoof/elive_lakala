package com.lakala.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lakala.ui.R;

/**
 * Created by LMQ on 2015/7/28.
 */
public class AdvertTextView extends FrameLayout {
    private LayoutInflater mInflater;
    private TextView text1;
    private TextView text2;
    private TranslateAnimation outAnimation;
    private TranslateAnimation inAnimation;
    private TextView showText;
    private TextView animText;

    public AdvertTextView(Context context) {
        super(context);
        init(context);
    }

    public AdvertTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdvertTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.advert_view,this);
        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);

        outAnimation = new TranslateAnimation(
                Animation.ABSOLUTE,0,Animation.ABSOLUTE,0,Animation
                .RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-1);
        outAnimation.setDuration(1000);
        inAnimation = new TranslateAnimation(Animation.ABSOLUTE,0,Animation.ABSOLUTE,0,Animation
                .RELATIVE_TO_SELF,1,Animation.RELATIVE_TO_SELF,0);
        inAnimation.setDuration(1000);
        inAnimation.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        CharSequence text = animText.getText();
                        showText.setText(text);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );
    }
    public void setText(String text){
        setText(text,true);
    }
    public void setText(String text,boolean showAnim){
        if(showText == null){
            text1.setText(text);
            text2.setText(text);
            showText = text1;
            animText = text2;
        }else{
            animText.setText(text);
            if(showAnim){
                showText.startAnimation(outAnimation);
                animText.startAnimation(inAnimation);
            }else{
                showText.setText(text);
            }
        }
    }
}
