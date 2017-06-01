package com.lakala.shoudan.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertListDialog;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by HUASHO on 2015/1/27.
 */
public class ContactBookUtil {
    private ArrayList<String> phoneNumbers = new ArrayList<String>();
    private EditText phoneNumberEditText;
    private Context mContext;
    private Intent mIntent;
    public  String numberFromPhoneBook = "";
    private OnPhoneNumberSelectedListener mPhoneNumberListener;
    /**
     * 手机号码选择接口
     */
    public interface OnPhoneNumberSelectedListener{
        /**
         * 联系人有手机号码，显示手机号码
         * @param phoneNumber
         */
        public void onPhoneNumberSelected(String phoneNumber);

        /**
         * 联系人没有手机号，hint提示
         * @param hint
         */
        public void onNoNumber(String hint);
    }

    public void setOnPhoneNumberSelectedListener(final OnPhoneNumberSelectedListener
                                                         mPhoneNumberListener) {
        this.mPhoneNumberListener = mPhoneNumberListener;
        analysisIntent();
        if (phoneNumbers.size()==1){
            mPhoneNumberListener.onPhoneNumberSelected(handlePhoneNumber(phoneNumbers.get(0)));
        }else if (phoneNumbers.size() > 1){
//            selectNumberFromList();
            AlertListDialog listDialog = new AlertListDialog();
            listDialog.setItems(phoneNumbers,new AlertListDialog.ItemClickListener() {
                @Override
                public void onItemClick(AlertDialog dialog, int index) {
                    mPhoneNumberListener.onPhoneNumberSelected(handlePhoneNumber(phoneNumbers.get(index)));
                }
            });
            listDialog.setTitle("请选择手机号码");
            listDialog.setButtons(new String[]{mContext.getString(R.string.button_cancel)});
            listDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
                @Override
                public void onButtonClick(AlertDialog dialog, View view, int index) {
                    super.onButtonClick(dialog, view, index);
                    dialog.dismiss();
                }
            });
            listDialog.show(((AppBaseActivity)mContext).getSupportFragmentManager());
        }else {
            mPhoneNumberListener.onNoNumber(mContext.getString(R.string.fail_get_phone_number));
        }
    }

    public ContactBookUtil(Context context,Intent intent){
        mContext = context;
        mIntent = intent;
    }

    public ContactBookUtil(Context context,EditText editText,Intent intent){
        mContext = context;
        mIntent = intent;
        phoneNumberEditText = editText;
    }

    /**
     * 分析intent，解析里边的手机号码
     */
    private void analysisIntent() {
        Cursor cursor = null;
        try {
            if (mIntent != null) {
                if (mIntent.getData()!=null) {
                    cursor = mContext.getContentResolver().query(mIntent.getData(), null, null, null, null);
                }
            }
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // 获取到联系人的id号
                    String number_id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    // 根据id查询号码
                    Cursor phone = mContext.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + number_id, null, null);
                    //过滤掉重复的号码
                    HashSet<String> hashSet = new HashSet<String>();
                    for (int position = 0; position < phone.getCount(); position++) {
                        phone.moveToPosition(position);
                        String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        hashSet.add(strPhoneNumber);
                    }
                    phoneNumbers = new ArrayList<String>(hashSet);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给EditText填充手机号码
     */
    public void setPhoneNumberToEditText() {

        analysisIntent();
        if (phoneNumbers.size() == 1) {
            setPhoneNumber(0);
        }else if (phoneNumbers.size() > 1) {
//            selectNumberFromList();
        }else if (phoneNumbers.size() == 0) {
            phoneNumberEditText.setText(null);
            //先设置gravity后，hint才能显示出来
            phoneNumberEditText.setHint(R.string.fail_get_phone_number);
        }
    }

    public String getPhoneNumber() {
        return phoneNumberEditText.getText().toString();
    }

    /**
     * 手机号码大于11位时进行处理，比如+8613212345678
     * @param phoneNumber
     * @return
     */
    private String handlePhoneNumber(String phoneNumber){
        phoneNumber = StringUtil.formatString(phoneNumber);
        int length = phoneNumber.length();
        String result = (length > 11) ? phoneNumber.substring(length-11,length) : phoneNumber;
        return result;
    }

    /**
     * 设置手机号，并进行样式处理，右对齐且上下居中
     * @param position
     */
    private void setPhoneNumber(int position){
        numberFromPhoneBook = handlePhoneNumber(phoneNumbers.get(position));
        phoneNumberEditText.setText(numberFromPhoneBook);
//		phoneNumberEditText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }

    /**
     * 电话本图标点击处理，获取通讯录中电话号码
     */
    public static void onContactBookClick(Context context){
        try{

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(ContactsContract.Contacts.CONTENT_URI);
            ((Activity) context).startActivityForResult(intent, UniqueKey.PHONE_NUMBER_REQUEST_CODE);
        }catch (Exception e){
            ToastUtil.toast(context, context.getResources().getString(R.string.not_read_contact));
        }
    }
}
