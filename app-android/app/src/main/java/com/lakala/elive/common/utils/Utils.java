package com.lakala.elive.common.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.EliveApplication;
import com.lakala.elive.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Utils {


    /**
     * 在屏幕中央显示一个Toast
     * 单例toast
     * @param text
     */
    private static WeakReference<Toast> sToast;

    public static void showToast(Context context,CharSequence content) {
        if (sToast != null && sToast.get() != null) {
            sToast.get().setText(content);
            sToast.get().show();
        } else {
            Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            sToast = new WeakReference<>(toast);
            toast.show();
        }
    }

    /**
     * 取消toast
     */
    public static void cancel() {
        if (sToast != null && sToast.get() != null) {
            sToast.get().cancel();
        }
    }

    /**
     * Show toast information
     *
     * @param context application context
     * @param text    the information which you want to show
     * @return show toast dialog
     */
    public static void makeEventToast(Context context, String text, boolean isLongToast) {
        Toast toast = null;
        if (isLongToast) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
//        View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
//        TextView textView = (TextView) v.findViewById(R.id.text);
//        textView.setText(text);
//        toast.setView(v);
        toast.show();
    }

    /**
     * 安装apk应用
     *
     * @param context
     * @param filePath
     */
    public static void installApp(Context context, String filePath) {
        File _file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(_file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * Android 安装应用
     *
     * @param context Application Context
     */
    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
        } else {
            makeEventToast(context, context.getString(R.string.install_fail_file_not_exist), false);
        }
    }

    /**
     * 判断当前日期是星期几<br>
     * <br>
     *
     * @param pTime 修要判断的时间<br>
     * @return dayForWeek 判断结果<br>
     * @Exception 发生异常<br>
     */
    public static String dayForWeek(String pTime) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int dayForWeek = 0;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(pTime));
            if (1 == c.get(Calendar.DAY_OF_WEEK)+1-1)  {
                dayForWeek = 7;
            } else {
                dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weeks[dayForWeek];
    }

    /**
     * 判断密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    public static String passwordStrong(String passwordStr) {
        if (TextUtils.equals("", passwordStr)) {
            return "出现故障";
        }
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "弱";
        }
        if (passwordStr.matches(regexS)) {
            return "弱";
        }
        if (passwordStr.matches(regexT)) {
            return "弱";
        }
        if (passwordStr.matches(regexZT)) {
            return "中";
        }
        if (passwordStr.matches(regexST)) {
            return "中";
        }
        if (passwordStr.matches(regexZS)) {
            return "中";
        }
        if (passwordStr.matches(regexZST)) {
            return "强";
        }
        return passwordStr;
    }

    /**
     * 获取应用的申请的权限列表
     *
     * @param mContext
     * @return
     */
    public static String[] getPermissionStrings(Context mContext) {
        PackageManager pm = mContext.getPackageManager();
        String[] permissionStrings = new String[0];
        try {
            PackageInfo pack = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_PERMISSIONS);
            permissionStrings = pack.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissionStrings;
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


}
