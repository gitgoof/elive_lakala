package com.lakala.ui.dialog;

import android.view.LayoutInflater;
import android.view.View;

import com.lakala.ui.R;

import net.simonvt.numberpicker.NumberPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LMQ on 2015/3/29.
 */
public class AlertDateSelectorDialog extends AlertDialog {
    private NumberPicker pickerYear;
    private NumberPicker pickerMonth;
    private NumberPicker pickerDay;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private Calendar mValueDate;
    private final Calendar defaultMaxDate;
    private final Calendar defaultMinDate;
    private SimpleDateFormat stringDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public AlertDateSelectorDialog() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.YEAR, -50);
        date.set(Calendar.MONTH, Calendar.JANUARY);
        date.set(Calendar.DAY_OF_MONTH, 1);
        defaultMinDate = date;

        date = Calendar.getInstance();
        date.add(Calendar.YEAR, 50);
        date.set(Calendar.MONTH, Calendar.DECEMBER);
        date.set(Calendar.DAY_OF_MONTH, 31);
        defaultMaxDate = date;
    }

    @Override
    View middleView() {
        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.dialog_date_selector_middle, null);

        pickerYear = (NumberPicker) middle_root.findViewById(R.id.pickerYear);
        pickerYear.setFocusable(true);
        pickerYear.setFocusableInTouchMode(true);
        pickerYear.setOnValueChangedListener(onValueChangedListener);

        pickerMonth = (NumberPicker) middle_root.findViewById(R.id.pickerMonth);
        pickerMonth.setFocusable(true);
        pickerMonth.setFocusableInTouchMode(true);
        pickerMonth.setOnValueChangedListener(onValueChangedListener);

        pickerDay = (NumberPicker) middle_root.findViewById(R.id.pickerDay);
        pickerDay.setFocusable(true);
        pickerDay.setFocusableInTouchMode(true);
        if (mMinDate == null) {
            mMinDate = defaultMinDate;
        }
        if (mMaxDate == null) {
            mMaxDate = defaultMaxDate;
        }
        //设置最小时间
        pickerYear.setMinValue(mMinDate.get(Calendar.YEAR));
        pickerMonth.setMinValue(mMinDate.get(Calendar.MONTH) + 1);
        pickerDay.setMinValue(mMinDate.get(Calendar.DAY_OF_MONTH));
        //设置最大时间
        pickerYear.setMaxValue(mMaxDate.get(Calendar.YEAR));
        pickerMonth.setMaxValue(mMaxDate.get(Calendar.MONTH) + 1);
        pickerDay.setMaxValue(mMaxDate.get(Calendar.DAY_OF_MONTH));
        //设置当前显示时间
        if(mValueDate != null){
            pickerYear.setValue(mValueDate.get(Calendar.YEAR));
            pickerMonth.setValue(mValueDate.get(Calendar.MONTH)+1);
            pickerDay.setValue(mValueDate.get(Calendar.DAY_OF_MONTH));
        }
        return middle_root;
    }

    public NumberPicker getPickerYear() {
        return pickerYear;
    }

    public NumberPicker getPickerMonth() {
        return pickerMonth;
    }

    public NumberPicker getPickerDay() {
        return pickerDay;
    }

    public void setMaxDate(long maxDate) {
        mMaxDate = Calendar.getInstance(Locale.CHINA);
        mMaxDate.setTimeInMillis(maxDate);
    }
    public void setMinDate(long minDate){
        mMinDate = Calendar.getInstance(Locale.CHINA);
        mMinDate.setTimeInMillis(minDate);
    }
    public void setValueDate(long valueDate){
        mValueDate = Calendar.getInstance(Locale.CHINA);
        mValueDate.setTimeInMillis(valueDate);
    }

    /**
     *
     * @param formatDate 格式化的日期"yyyy-MM-dd"
     * @throws java.text.ParseException
     */
    public void setMaxDate(String formatDate) throws ParseException {
        Date date = stringDateFormat.parse(formatDate);
        mMaxDate = Calendar.getInstance(Locale.CHINA);
        mMaxDate.setTime(date);
    }
    public void setMinDate(String formatDate) throws ParseException {
        Date date = stringDateFormat.parse(formatDate);
        mMinDate = Calendar.getInstance(Locale.CHINA);
        mMinDate.setTime(date);
    }
    public void setValueDate(String formatDate) throws ParseException {
        Date date = stringDateFormat.parse(formatDate);
        mValueDate = Calendar.getInstance(Locale.CHINA);
        mValueDate.setTime(date);
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    private int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
    private void updatePickerViews(NumberPicker picker){
        int id = picker.getId();
        if(id == R.id.pickerYear){
            int minMonth = 1;
            int maxMonth = 12;
            if(pickerYear.getValue() == mMinDate.get(Calendar.YEAR)){
                minMonth = mMinDate.get(Calendar.MONTH)+1;
                maxMonth = 12;
            }else if(pickerYear.getValue() == mMaxDate.get(Calendar.YEAR)){
                minMonth = 1;
                maxMonth = mMaxDate.get(Calendar.MONTH)+1;
            }else{
                minMonth = 1;
                maxMonth = Calendar.DECEMBER+1;
            }
            pickerMonth.setMinValue(minMonth);
            pickerMonth.setMaxValue(maxMonth);
        }else if(id == R.id.pickerMonth){
            int valueYear = pickerYear.getValue();
            int valueMonth = pickerMonth.getValue();
            int maxDay = getDaysByYearMonth(valueYear,valueMonth);
            pickerDay.setMinValue(1);
            pickerDay.setMaxValue(maxDay);
        }
    }
    private NumberPicker.OnValueChangeListener onValueChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            updatePickerViews(picker);
        }
    };
}
