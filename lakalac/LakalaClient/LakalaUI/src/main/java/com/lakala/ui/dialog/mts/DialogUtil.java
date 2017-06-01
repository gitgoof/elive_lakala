package com.lakala.ui.dialog.mts;

import android.support.v4.app.FragmentManager;
import android.view.View;

/**
 * Created by andy_lv on 14-1-3.
 *
 * Dialog创建工具类
 */
public class DialogUtil {

    /**
     * Dialog 类别
     */
    public enum DialogTypeEnum{
        BASE_DIALOG,ALERT_DIALOG,PROGRESS_DIALOG
    }

    /**
     * 通过 DialogTypeEnum 类型，创建相应的Dialog类
     * @param dialogTypeEnum  DialogTypeEnum dialogTypeEnum
     * @param fragmentManager FragementManager 对象
     * @return
     */
    public static BaseDialog createDialog(DialogTypeEnum dialogTypeEnum,FragmentManager fragmentManager){
         BaseDialog baseDialog = null;
         switch (dialogTypeEnum){
             case BASE_DIALOG:
                  baseDialog = new BaseDialog(fragmentManager);
                  break;
             case ALERT_DIALOG:
                  baseDialog = new AlertDialog(fragmentManager);
                  break;
             case PROGRESS_DIALOG:
                  baseDialog = new ProgressDialog(fragmentManager);
                  break;
             default:
                  baseDialog = new BaseDialog(fragmentManager);
         }
        return  baseDialog;
    }

    /**
     *  创建有提示文字的ProgressDialog类
     * @param fragmentManager FragementManager 对象
     * @param loadingMessage  加载框提示文字,若为空，提示默认文字
     * @return
     */
    public static ProgressDialog createProgressDialog(FragmentManager fragmentManager,String loadingMessage){
      ProgressDialog baseDialog = null;
      baseDialog = new ProgressDialog(fragmentManager);
      baseDialog.setMessageText(loadingMessage);
      return baseDialog;
    }

    /**
     *  创建有标题，正文内容的AlertDialog类
     * @param fragmentManager   FragementManager 对象
     * @param title             标题，若为空，标题标题栏不显示
     * @param message           正文内容
     * @return
     */
    public static AlertDialog createAlertDialog(FragmentManager fragmentManager,String title,String message){
        AlertDialog alertDialog = new AlertDialog(fragmentManager);
        AlertDialog.Builder builder =new AlertDialog.Builder(title,message);
        alertDialog.setBuilder(builder);
        return alertDialog;
    }

    /**
     * 创建有图标，标题，正文内容的AlertDialog
     * @param fragmentManager   FragementManager 对象
     * @param titleIconId       图标id，若id ==0，图标不显示
     * @param title             标题  ，若为空，表示栏不显示
     * @param message           正文内容
     * @return
     */
    public static AlertDialog createAlertDialog(FragmentManager fragmentManager,
                                                int titleIconId,String title,String message){
        AlertDialog alertDialog = new AlertDialog(fragmentManager);
        AlertDialog.Builder builder =new AlertDialog.Builder(title,message);
        builder.setTitleIconId(titleIconId);
        alertDialog.setBuilder(builder);
        return alertDialog;
    }

    /**
     * 创建有图标，标题，正文，左边button文字，右边button文字，中间button文字，及button点击回调接口
     * @param fragmentManager          FragementManager 对象
     * @param titleIconId              图标id，若id ==0，图标不显示
     * @param title                    标题  ，若为空，表示栏不显示
     * @param message                  正文
     * @param leftButtonText           左边button文字，若为空，不显示
     * @param rightButtonText          右边button文字，若为空，不显示
     * @param middleButtonText         中间button文字，若为空，不显示
     * @param alertDialogClickListener button点击回调接口
     * @return
     */
    public static AlertDialog createAlertDialog(
            FragmentManager fragmentManager,
            int titleIconId,
            String title,
            String message,
            String leftButtonText,
            String rightButtonText,
            String middleButtonText,
            AlertDialog.Builder.AlertDialogClickListener alertDialogClickListener){

      AlertDialog alertDialog = new AlertDialog(fragmentManager);
      AlertDialog.Builder builder =new AlertDialog.Builder(title,message);
      builder.setTitleIconId(titleIconId);
      builder.setLeftButtonText(leftButtonText);
      builder.setMiddleButtonText(middleButtonText);
      builder.setRightButtonText(rightButtonText);
      builder.setButtonClickListener(alertDialogClickListener);
      alertDialog.setBuilder(builder);
      alertDialog.setButtonVisiblity(AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON, View.VISIBLE);
      alertDialog.setButtonVisiblity(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, View.VISIBLE);
      alertDialog.setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.LEFT_BUTTON, true);
      alertDialog.setButtonEnable(AlertDialog.Builder.ButtonTypeEnum.RIGHT_BUTTON, true);
      return alertDialog;
    }
}
