package com.lakala.shoudan.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.QpbocSwitchActivity;
import com.lakala.shoudan.activity.collection.CalculatorArith;
import com.lakala.shoudan.component.MCCPopupwindow;
import com.lakala.ui.component.NavigationBar;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 金额输入
 *
 * @author More date2014-4-25
 */
public abstract class AmountInputActivity extends AppBaseActivity {

    protected TextView tvTips;
    private TextView calculateResult;//结果（第一个运算数）
    private int operatorId = 0;//运算符
    private String result = "";//旧结果
    private StringBuffer tempInputBuffer;//输入的用作运算的Stringbuffer
    private String opNum = "";
    private StringBuffer expression = new StringBuffer();
    private static final String ZERO_STYLE = "0";
    public LinearLayout linearLayout;//MCC行业选择
    public static boolean ifClean = false;
    private Activity act;
    private int maxLengthOfMaxSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_calculator);
        act = this;
        initUI();
    }

    protected void showForceQpbocSwitch() {
        navigationBar.setActionBtnText("挥卡设置");
    }

    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        switch (navBarItem) {
            case back:
                super.onNavItemClick(navBarItem);
                break;
            case action:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.SwipingSet, this);
                Intent intent = new Intent(context, QpbocSwitchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private float maxTextSize;

    protected void initUI() {
        tempInputBuffer = new StringBuffer();
        tvTips = (TextView) findViewById(R.id.tv_tips);
        navigationBar.setTitle(getString(R.string.receivables));
        linearLayout= (LinearLayout) findViewById(R.id.mcc);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MCCPopupwindow(context,v);
            }
        });
        calculateResult = (TextView) findViewById(R.id.shoudan_calculator_rslt);
        calculateResult.setText(ZERO_STYLE);
        maxTextSize = calculateResult.getPaint().getTextSize();
        maxLengthOfMaxSize = getMaxTextLength();
        addTextWatcher();
        findViewById(R.id.key_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (R.id.key_equals != operatorId) {
                    handleOperator(R.id.key_equals);
                }
                String amount = calculateResult.getText().toString();
                amount = amount.replace(",", "");
                if (amount.length() == 0) {
                    amount = "0";
                }
                BigDecimal bgAmount;
                try {
                    bgAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP);

                } catch (Exception e) {
                    calculateResult.setText(ZERO_STYLE);
                    ToastUtil.toast(act, "金额错误");
                    return;
                }
                //返回的i可能为-1、0、1，分别表示小于、等于、大于
                if (bgAmount.compareTo(new BigDecimal(0)) <= 0) {
                    calculateResult.setText(ZERO_STYLE);
                    ToastUtil.toast(act, "金额错误");
                    return;
                }
                if (bgAmount.compareTo(new BigDecimal("9999999")) > 0) {
                    ToastUtil.toast(act, "金额过大!");
                    return;
                }
                onInputComplete(bgAmount);
            }
        });

    }

    protected abstract void onInputComplete(BigDecimal amount);

    @Override
    protected void onResume() {
        super.onResume();
        if (ifClean) {
            allClean();
            ifClean = false;
        }
    }

    /**
     * 全清
     */
    private void allClean() {
        expression.delete(0, expression.length());
        calculateResult.setText(ZERO_STYLE);
        operatorId = 0;
        result = "";
        tempInputBuffer.delete(0, tempInputBuffer.length());
        tempInputBuffer.append("0");
        opNum = "";
    }

    /**
     * 计算器按键监听
     *
     * @param v
     */
    public void calculatorItemOnClick(View v) {
        int id = v.getId();
        handleCalculatorItemClick(id);
    }

    /**
     * 点击运算符
     */
    public void calculatorOperator(View v) {


        int id = v.getId();
        handleOperator(id);

    }

    private boolean handleOperator(int id) {
        if (id == R.id.key_equals && calculateResult.getText().length() == 0 && operatorId != 0)
            return false;

        if (operatorId == 0 || operatorId == R.id.key_del) {
            //缓存区没有操作符,将第一个运算数存入 result 中,同时清空 tempInputBuffer
            operatorId = id;
        }
        if (id == R.id.key_del) {
            if (tempInputBuffer.length() > 0) {
                tempInputBuffer.deleteCharAt(tempInputBuffer.length() - 1);
                if (expression.length() == 0 && tempInputBuffer.length() == 0) {
                    tempInputBuffer.append("0");
                    calculateResult.setText(ZERO_STYLE);
                } else {
                    calculateResult.setText(expression.toString() + addSeparator(tempInputBuffer.toString()));
                }
            } else {
                result = "";
                operatorId = 0;
                if (expression.length() == 0) {
                    allClean();
                    return false;
                }
                expression.delete(expression.length() - 1, expression.length());
                tempInputBuffer.append(expression.toString().replace(",", ""));
                expression.delete(0, expression.length());
                calculateResult.setText(addSeparator(tempInputBuffer.toString()));
            }
            return false;
        }

        if (result.length() == 0) {
            expression = new StringBuffer();
            expression.append(addSeparator(tempInputBuffer.toString()));
            appendOperator(id);
            calculateResult.setText(expression.toString());
            result = tempInputBuffer.toString();
            tempInputBuffer.delete(0, tempInputBuffer.length());
        } else {
            opNum = tempInputBuffer.toString();
            if (opNum.length() == 0) {
                operatorId = id;
                appendOperator(id);
                calculateResult.setText(expression);
                return false;
            }
            if (id == R.id.key_equals) {
                if (calculate(operatorId)) {
                    operatorId = id;
                    appendOperator(id);
                    return true;
                }
            } else {
                if (calculate(operatorId)) {

                    operatorId = id;
                    appendOperator(id);
                    calculateResult.setText(expression);
                    return true;
                }
            }
        }
        return false;
    }

    private void appendOperator(int id) {
        if (expression.length() == 0)
            return;
        String tempOperator = expression.substring(expression.length() - 1, expression.length());
        if ("-+÷x".contains(tempOperator)) {
            expression.delete(expression.length() - 1, expression.length());
            expression.append(getOperators(id));
        } else {
            expression.append(getOperators(id));
        }

    }


    private String getOperators(int operatorId) {
        String operators = "";
        switch (operatorId) {
            case R.id.key_min:
                operators = "-";
                break;
            case R.id.key_plus:
                operators = "+";
                break;
            case R.id.key_div:
                // 0.0的情况
                operators = "÷";
                break;
            case R.id.key_mul:
                operators = "x";
                break;
            default:
                break;
        }
        return operators;
    }

    private void handleCalculatorItemClick(int id) {
        if (R.id.key_equals == operatorId) {
            allClean();
        }
        if (tempInputBuffer.length() == 1 && tempInputBuffer.toString().equals("0")) {
            tempInputBuffer.delete(0, tempInputBuffer.length());
        }
        if (id != R.id.key_dot) {//增加输入位数限制
            if (tempInputBuffer.length() > 15)
                return;
            if (tempInputBuffer.indexOf(".") != -1) {
                if (tempInputBuffer.indexOf(".") > 8) {
                    return;
                }
            } else {
                if (tempInputBuffer.length() > 8) {
                    return;
                }

            }

        }
        switch (id) {
            case R.id.key_00:
                tempInputBuffer.append("00");
                break;
            case R.id.key_0:
                tempInputBuffer.append("0");
                break;
            case R.id.key_1:
                tempInputBuffer.append("1");
                break;
            case R.id.key_2:
                tempInputBuffer.append("2");
                break;
            case R.id.key_3:
                tempInputBuffer.append("3");
                break;
            case R.id.key_4:
                tempInputBuffer.append("4");
                break;
            case R.id.key_5:
                tempInputBuffer.append("5");
                break;
            case R.id.key_6:
                tempInputBuffer.append("6");
                break;
            case R.id.key_7:
                tempInputBuffer.append("7");
                break;
            case R.id.key_8:
                tempInputBuffer.append("8");
                break;
            case R.id.key_9:
                tempInputBuffer.append("9");
                break;
            case R.id.key_dot:
                if (tempInputBuffer.indexOf(".") == -1) {
                    if (tempInputBuffer.length() == 0) {
                        tempInputBuffer.append("0");
                    }
                    tempInputBuffer.append(".");
                }
                break;
//            case R.id.key_ac:
//            {
//                allClean();
//            }
//            break;
            default:
                break;
        }
        calculateResult.setText(expression.toString() + addSeparator(tempInputBuffer.toString()));//修改完 tempInputBuffer 同步 calculateResult

    }

    private String addSeparator(String amt) {

        int dot = amt.indexOf(".") == -1 ? 0 : amt.indexOf(".");
        StringBuffer decimal = new StringBuffer();
        if (dot > 0) {
            decimal.append(amt.substring(dot, amt.length()));
        }
        StringBuffer sb = new StringBuffer();
        char[] charArray;
        if (dot == 0) {
            charArray = amt.toCharArray();
        } else {
            charArray = amt.substring(0, dot).toCharArray();
        }
        int amtSeparator = 1;
        for (int i = charArray.length - 1; i >= 0; i--) {

            if (sb.length() == 0) {
                sb.append(charArray[i]);
            } else {
                sb.insert(0, charArray[i]);
            }
            if (amtSeparator % 3 == 0 && i != 0)
                sb.insert(0, ",");
            amtSeparator++;
        }
        sb.append(decimal);
        return sb.toString();
    }

    private int getMaxTextLength() {

        float tvWidth = (float) calculateResult.getWidth();
        StringBuffer sb = new StringBuffer("000000");
        do {
            sb.append("0");
        } while ((calculateResult.getPaint().measureText(sb.toString()) <= tvWidth));

        return sb.length() - 2;

    }

    private void addTextWatcher() {

        calculateResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                autoAdaptTextSize(editable.toString());

            }
        });

    }

    private void autoAdaptTextSize(String str) {
        final float tvWidth = (float) calculateResult.getWidth();
        if (str.length() < maxLengthOfMaxSize && calculateResult.getTextSize() < maxTextSize) {
            calculateResult.getPaint().setTextSize(maxTextSize);
        } else {

            final float originalSize = calculateResult.getPaint().getTextSize();
            float changeSize = originalSize;
            TextView textView = new TextView(AmountInputActivity.this);
            textView.getPaint().setTextSize(calculateResult.getPaint().getTextSize());
            while (textView.getPaint().measureText(str) > tvWidth && textView.getTextSize() > 30) {
                changeSize = changeSize - 2;
                textView.getPaint().setTextSize(changeSize);
            }

            calculateResult.getPaint().setTextSize(changeSize);


        }
    }

    private String deleteSeparator(String amt) {
        return amt.replace(",", "");
    }

    private String formatResult(String rslt) {
        DecimalFormat sf = new DecimalFormat("#.#####");
        if (rslt == null)
            return "";
        return sf.format(new BigDecimal(rslt));
    }

    /**
     * 依据 operator 调用计算
     */
    private boolean calculate(int id) {
        final String tempResult = result;
        if (result == null || result.length() == 0) {
            result = "0";
        }
        result = deleteSeparator(result);
        boolean allClean = false;
        switch (id) {
            case R.id.key_min:
                result = formatResult(CalculatorArith.sub(result, opNum));
                break;
            case R.id.key_plus:
                result = formatResult(CalculatorArith.add(result, opNum));
                break;
            case R.id.key_div:
                // 0.0的情况
                BigDecimal bdNum = null;

                try {

                    bdNum = new BigDecimal(opNum);

                } catch (Exception e) {
                    LogUtil.print(e.toString());
                }

                if (bdNum == null) {
                    ToastUtil.toast(act, "除数有误");
                } else {

                    if (new BigDecimal("0.0").compareTo(bdNum) != 0) {
                        result = formatResult(CalculatorArith.div(result, opNum));
                    } else {
                        ToastUtil.toast(act, "除数为零");
                        allClean = true;
                        allClean();
                    }

                }
                break;
            case R.id.key_mul:
                result = formatResult(CalculatorArith.mul(result, opNum));
                break;
            default:
                break;
        }
        if (result.length() != 0) {
            //完成运算后,显示结果,清空 tempInputBuffer以便输入新的数据
            int length = (result.indexOf(".") == -1 ? result.length() : result.indexOf("."));
            if (length > 10) {//超出计算长度
                //allClean = false;
                //allClean();
                result = tempResult;
                ToastUtil.toast(act, "超出运算长度");
                return false;
            }
        }
        expression = new StringBuffer();
        expression.append(addSeparator(result));
        calculateResult.setText(addSeparator(result));
        tempInputBuffer.delete(0, tempInputBuffer.length());
        if (allClean)
            allClean();
        return true;
    }

}
