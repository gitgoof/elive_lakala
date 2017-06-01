package com.lakala.elive.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lakala.elive.R;


/**
 * 自定义组件:加减数量控件
 * Created by wenhaogu on 2017/1/9.
 */

public class AmountView extends LinearLayout implements View.OnClickListener {

    private Button add;
    private Button minus;
    private EditText editCount;
    private int symbolColor;
    private String proportion;
    private int fontColor;
    private int overallSize;
    private int intValue;
    private float floatValue;
    private float v;
    private Paint paint;
    private String textNumber;
    private int inputType;
    private boolean enabled;
    private float maxCount;
    private String maxRate;

    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public AmountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        View view = LayoutInflater.from(context).inflate(R.layout.view_amount, this);
        add = (Button) view.findViewById(R.id.add);
        minus = (Button) view.findViewById(R.id.minus);
        editCount = (EditText) view.findViewById(R.id.count);
        setSelecror(editCount);

        getAttribute(context, attrs);
    }

    private void GetInitialValue() {
        if (isIntegerNumber(proportion)) {
            String tempIntValue = editCount.getText().toString().trim();
            if (isIntegerNumber(tempIntValue)) {
                intValue = Integer.parseInt(editCount.getText().toString().trim());
            } else {
                intValue = Integer.parseInt(tempIntValue.substring(0, Integer.parseInt(String.valueOf(tempIntValue.indexOf(".")))));
            }
        } else {
            floatValue = Float.parseFloat(editCount.getText().toString().trim());
        }
    }

    private void getAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AmountView);
        proportion = TextUtils.isEmpty(typedArray.getString(R.styleable.AmountView_proportion)) ? 0.01 + "" : typedArray.getString(R.styleable.AmountView_proportion);
        symbolColor = typedArray.getColor(R.styleable.AmountView_symbolColor, Color.BLACK);
        fontColor = typedArray.getColor(R.styleable.AmountView_fontColor, Color.BLACK);
        overallSize = (int) typedArray.getDimension(R.styleable.AmountView_overallSize, 0);
        textNumber = typedArray.getString(R.styleable.AmountView_setText);
        inputType = typedArray.getInt(R.styleable.AmountView_setInputType, 1);
        enabled = typedArray.getBoolean(R.styleable.AmountView_enabled, true);
        maxCount = typedArray.getFloat(R.styleable.AmountView_maxCount, (float) -1);
        maxRate = typedArray.getString(R.styleable.AmountView_maxRate);//最高费率

        typedArray.recycle();
        setSizeAndColor();
        setListener();

    }

    private void setSizeAndColor() {
        //editCount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (inputType == 1) {
            editCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            editCount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        editCount.setEnabled(enabled);
        /*add.setEnabled(enabled);
        minus.setEnabled(enabled);*/
        editCount.setText(textNumber);
        setSelecror(editCount);
        add.setTextColor(symbolColor);
        minus.setTextColor(symbolColor);
        editCount.setTextColor(fontColor);
        if (overallSize != 0) {
            add.setTextSize(overallSize / 2);
            minus.setTextSize(overallSize / 2);
            editCount.setTextSize(overallSize / 2);
        }

    }


    private void setListener() {
        add.setOnClickListener(this);
        minus.setOnClickListener(this);
    }

    private float max=100;
    public   void setMax(float max){
        this.max=max;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                GetInitialValue();
                if(floatValue<max){
                if (maxCount != -1) {
                    float v = Float.parseFloat(getText());
                    if (v >= maxCount) {
                        v = maxCount;
                    } else {
                        editCount.setText(isIntegerNumber(proportion) ? (intValue + Integer.parseInt(proportion)) + "" : (convert(floatValue + Float.parseFloat(proportion))) + "");
                    }
                } else {
                    editCount.setText(isIntegerNumber(proportion) ? (intValue + Integer.parseInt(proportion)) + "" : (convert(floatValue + Float.parseFloat(proportion))) + "");
                }
                }
                break;
            case R.id.minus:
                GetInitialValue();
                if (isLess()) {
                    editCount.setText(isIntegerNumber(proportion) ? (intValue - Integer.parseInt(proportion)) + "" : (convert(floatValue - Float.parseFloat(proportion))) + "");
                } else {
                    editCount.setText(isIntegerNumber(proportion) ? "1" : TextUtils.isEmpty(maxRate) ? "" : maxRate);
                }
                break;
        }
        setSelecror(editCount);
    }

    private int minReat=0;
    public   void setMinReat(int minreat){
        this.minReat=minreat;
    }

    private boolean isLess() {
        if (isIntegerNumber(proportion)) {
            if (intValue - Integer.parseInt(proportion) > minReat) {
                return true;
            } else {
                return false;
            }
        } else {
            if (convert(floatValue - Float.parseFloat(proportion)) > Float.parseFloat(maxRate)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void setText(String proportion) {
        digitalJudgment(proportion);
    }

    private void digitalJudgment(String proportion) {
        if (proportion.matches("^\\d+$")) {
            editCount.setText(proportion);
        }
    }

    public String getText() {
        return editCount.getText().toString().trim();
    }


    public boolean isIntegerNumber(String number) {
        int isNumber = number.indexOf(".");
        if (isNumber == -1) {
            return true;
        } else
            return false;
    }


    private float convert(float value) {
        float l1 = Math.round(value * 100);   //四舍五入
        float ret = (float) (l1 / 100.0);               //注意：使用   100.0   而不是   100
        return ret;
    }

    private void setSelecror(EditText edt) {
        int length = edt.getText().toString().trim().length();
        edt.setSelection(length);
    }


}
