package com.lakala.platform.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.adapter.DialogListviewAdapter;
import com.lakala.ui.dialog.AlertDialog;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 电话本管理，获取联系人信息
 * Created by xyz on 14-2-19.
 */
public class PhoneBookManager {

    private FragmentActivity ctx;

    public PhoneBookManager(FragmentActivity context) {
        this.ctx = context;
    }

    /**
     * 通过手机号码，获取联系人名称
     * @param phoneNumber
     * @return 如果通讯录中没有此号码，则联系人名称返回“”空串
     */
    public static String getNameByPhoneNumber(Context context ,String phoneNumber){

        String name = "";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor ;

        String[] projection = new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER };

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        cursor = contentResolver.query(uri,projection, ContactsContract.PhoneLookup.NUMBER + "='"+phoneNumber+"'",
                null,null);
        if(cursor != null && cursor.getCount() != 0){
            while(cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();
            cursor = null;
        }
        return name;
    }


    /**
     * 手机号码选择接口
     */
    public interface OnPhoneNumberSelectedListener{
        /**
         * 联系人有手机号码，显示手机号码
         * @param phoneNumber
         */
        public void onPhoneNumberSelected(String name, String phoneNumber);
    }


    /**
     * 选择联系人
     * @param uri
     * @param phoneNumberSelectedListener 选择联系人电话后的回调
     * @return 返回联系人信息，姓名和电话号码
     */
    public static void getConcat(Context context ,boolean isFilter,Uri uri,final OnPhoneNumberSelectedListener phoneNumberSelectedListener){

        Cursor cursor   = context.getContentResolver().query(uri, null, null, null, null);

        //CursorIndexOutOfBoundsException加上判断，避免crash
        if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                String numberid = cursor.getString(cursor
                        .getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                Cursor phones = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + numberid, null, null);

                //过滤掉重复的号码
                HashSet<String> hashSet = new HashSet<String>();
                String name = "";
                boolean isToastPrompt = false;
                while(phones.moveToNext()){
                    String phoneNumber = phones.getString(0);
                    name = phones.getString(1);
                    if (isFilter){//要过滤非手机号的号码
                        if (StringUtil.isNotEmpty(phoneNumber) && PhoneNumberUtil.checkPhoneNumber(StringUtil.formatString(phoneNumber))){
                            hashSet.add(phoneNumber);
                        }else {
                            isToastPrompt = true;
                        }
                    }else {//不过滤号码，手机固话号码均可
                        hashSet.add(phoneNumber);
                    }
                }

            final ArrayList<String> phoneNumbers = new ArrayList<String>(hashSet);

            if(phoneNumbers.size()==0){
                if (isToastPrompt) ToastUtil.toast(context,"手机号码有误,格式不正确");
                phoneNumberSelectedListener.onPhoneNumberSelected(name,"");
            }if(phoneNumbers.size()==1){
                phoneNumberSelectedListener.onPhoneNumberSelected(name, phoneNumbers.get(0));
            }if(phoneNumbers.size()>1){
                final String[] arrayNumbers = phoneNumbers.toArray(new String[phoneNumbers.size()]);
                final String selectName = name;
//                Dialog dialog = new AlertDialog.Builder(context)
//                        .setTitle("请选择号码：")
//                        .setItems(arrayNumbers, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int which) {
//                                phoneNumberSelectedListener.onPhoneNumberSelected(selectName,phoneNumbers.get(which));
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//
//                            }
//                        })
//                        .create();
//                dialog.show();

                /******开启-----------------------修改为自带的dialog--------------------------****/
                final ArrayList<String> list = new ArrayList<String>();
                for(int i = 0; i < arrayNumbers.length; i++){
                    list.add(arrayNumbers[i]);
                }
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_listview,null);
                ListView lv_phone = (ListView) view.findViewById(R.id.lv_phone);
                DialogListviewAdapter dialogListviewAdapter = new DialogListviewAdapter(context,list);
                lv_phone.setAdapter(dialogListviewAdapter);
                lv_phone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        phoneNumberSelectedListener.onPhoneNumberSelected(selectName,phoneNumbers.get(position));
                    }
                });
                showPhoneNumDialog(context,view);
                /******开启-----------------------修改为自带的dialog--------------------------****/
            }
            cursor.close();
            cursor = null;
        }
    }

    public static void showPhoneNumDialog(Context context,View view){

        FragmentActivity fragmentActivity = (FragmentActivity)context;

        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle("请选择号码：");
        alertDialog.setMiddleView(view);
        alertDialog.setButtons(new String[]{context.getString(R.string.com_cancel)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                super.onButtonClick(dialog, view, index);
                dialog.dismiss();
            }
        });
        alertDialog.show(fragmentActivity.getSupportFragmentManager());
    }


}

