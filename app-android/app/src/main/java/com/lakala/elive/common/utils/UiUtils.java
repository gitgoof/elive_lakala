package com.lakala.elive.common.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.lakala.elive.Constants;
import com.lakala.elive.user.activity.UserCheckActivity;
import com.lakala.elive.user.activity.UserPwdSetActivity;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

/**
 * UI工具类
 *
 * @version 1.0
 */
public class UiUtils {

    /**
     * 判断用户是否设置保存密码
     *
     * @return
     */
    public static boolean isSetupPwd(Context context, SharedPreferences sp) {
        sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
        String spSavePassword = sp.getString(Constants.KEY_SP_USER_PWD, null);
        if (TextUtils.isEmpty(spSavePassword)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置view的显示状态
     */
    public static void setVisibilitySafe(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 设置view的显示状态
     */
    public static void setVisibilitySafe(View parent, int id, int visibility) {
        if (parent != null) {
            setVisibilitySafe(parent.findViewById(id), visibility);
        }
    }

    public static void setPressedSafe(View view, boolean pressed) {
        if (view != null && view.isPressed() != pressed) {
            view.setPressed(pressed);
        }
    }

    public static void setEnabledSafe(View parent, int id, boolean enabled) {
        if (parent != null) {
            View view = parent.findViewById(id);
            if (view != null) {
                view.setEnabled(enabled);
            }
        }
    }

    public static void setOnClickListenerSafe(View parent, int id, OnClickListener l) {
        if (parent != null) {
            View view = parent.findViewById(id);
            if (view != null) {
                view.setOnClickListener(l);
            }
        }
    }

    public static void requestFocus(View view) {
        if (view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    public static boolean isEditTextEmpty(EditText edit) {
        return edit.getText() == null || edit.getText().toString().trim().length() <= 0;
    }

    public static boolean hideInputMethod(Activity activity) {
        return hideInputMethod(activity, activity.getWindow().getDecorView().getWindowToken());
    }

    public static boolean hideInputMethod(Dialog dialog) {
        return hideInputMethod(dialog.getContext(), dialog.getWindow().getDecorView().getWindowToken());
    }

    public static boolean hideInputMethod(Context context, IBinder iBinder) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return im.hideSoftInputFromWindow(iBinder, 0);
    }

    public static void checkBackgroudThread() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("It must run in backgroud thread.");
        }
    }

    public static void cancelAsyncTask(AsyncTask<?, ?, ?> task) {
        if (task != null) {
            task.cancel(true);
        }
    }

    public static void clearBitmapInImageView(ImageView v) {
        if (v != null) {
            clearBitmapInDrawable(v.getDrawable());
        }
    }

    public static void clearBackgroundBitmapInView(View v) {
        if (v != null) {
            clearBitmapInDrawable(v.getBackground());
        }
    }

    public static void clearBitmapInDrawable(Drawable d) {
        if (d != null && d instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    public static Bitmap decodeResourceBitmap(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 传递参数页面跳转
     *
     * @param mContext
     * @param toUI
     * @param exKey
     * @param exObj
     */
    public static void startActivityWithExObj(Context mContext, Class toUI, String exKey, Serializable exObj) {
        Intent intent = new Intent(mContext, toUI);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(exKey, exObj);
        intent.putExtras(mBundle);
        mContext.startActivity(intent);
    }

    /**
     * 传递参数页面跳转
     *
     * @param mContext
     * @param toUI
     */
    public static void startActivity(Context mContext, Class toUI) {
        Intent intent = new Intent(mContext, toUI);
        mContext.startActivity(intent);
    }

    public static void startActivity(Context mContext, Class toUI, String login_name, String userName) {
        Intent intent = new Intent(mContext, toUI).putExtra(login_name,userName);
        mContext.startActivity(intent);
    }

    /**
     * 抓拍图片直接保存到指定路径
     */
    public static void startTakePhotoIntent(Context mContext, int actionId) {
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //指定照片保存路径(SD卡),为一个临时文件,每次拍照后这个图片都会被替换
            if (!FileUtils.fileIsExists(Constants.MER_IMG_SAVE_PATH)) {
                ImageTools.createSDDir(Constants.MER_IMG_SAVE_PATH);//创建保存路径
            }
            Uri imageUri = Uri.fromFile(new File(Constants.MER_IMG_TMP_FILE));
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            ((Activity) mContext).startActivityForResult(openCameraIntent, actionId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
