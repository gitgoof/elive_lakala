package com.lakala.shoudan.component;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;
import com.lakala.shoudan.adapter.PayChooseTypeAdapter;
import com.lakala.shoudan.adapter.PayTypeItemAdapter;
import com.lakala.shoudan.adapter.RedPackageDialogListAdapter;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.AlertListDialog;
import com.lakala.ui.dialog.BaseDialog;
import com.lakala.ui.dialog.ProgressDialog;

import java.util.Arrays;
import java.util.List;

/**
 * Created by LMQ on 2015/11/20.
 */
public class DialogCreator {
    public static ProgressDialog createProgressDialog(FragmentActivity context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        return progressDialog;
    }

    public static ProgressDialog createDialogWithNoMessage(FragmentActivity context) {
        return createProgressDialog(context, "");
    }

    public static BaseDialog createQuitDialog(FragmentActivity context,
                                              OnClickListener positiveListener) {
        OnClickListener negativeListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        if (positiveListener == null) {
            return createQuitDialog(context);
        }
        return createFullContentDialog(context, context.getString(R.string.ok),
                context.getString(R.string.cancel), "确认退出手机收款宝？", positiveListener,
                negativeListener);
    }

    public static BaseDialog createQuitDialog(FragmentActivity context) {
        OnClickListener negativeListener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        OnClickListener positiveListener = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                ApplicationEx.getInstance().exit();
            }
        };
        return createFullContentDialog(context, context.getString(R.string.ok),
                context.getString(R.string.cancel), "确认退出手机收款宝？", positiveListener,
                negativeListener);
    }

    public static AlertListDialog createCancelableListDialog(FragmentActivity context, String title,
                                                             String[] items,
                                                             final DialogInterface.OnClickListener listener) {
        AlertListDialog dialog = new AlertListDialog();
        dialog.setItems(
                Arrays.asList(items), new AlertListDialog.ItemClickListener() {
                    @Override
                    public void onItemClick(AlertDialog dialog, int index) {
                        listener.onClick(dialog.getDialog(), index);
                    }
                }
        );
        dialog.setButtons(new String[]{"取消"});
        dialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
            }
        });
        dialog.setTitle(title);
        dialog.setContext(context);
        if (TextUtils.isEmpty(title)) {
            dialog.setTopVisibility(View.GONE);
        }
        return dialog;
    }

    public static AlertListDialog createListDialog(FragmentActivity context, String title, String[]
            items, final DialogInterface.OnClickListener listener) {
        AlertListDialog dialog = new AlertListDialog();
        dialog.setItems(
                Arrays.asList(items), new AlertListDialog.ItemClickListener() {
                    @Override
                    public void onItemClick(AlertDialog dialog, int index) {
                        listener.onClick(dialog.getDialog(), index);
                    }
                }
        );
        dialog.setTitle(title);
        if (title.equals("") || title == null) {
            dialog.setTopVisibility(View.GONE);
        }
        dialog.setContext(context);
        return dialog;
    }

    public static ProgressDialog createDialogWithMessage(FragmentActivity context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        return dialog;
    }

    public static void showPayTypeChooseDialog(final FragmentActivity context,
                                               final DialogInterface.OnClickListener listener,
                                               List<PayType> data) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_paytype_layout, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ListView lvTypes = (ListView) view.findViewById(R.id.lv_types);
        lvTypes.setAdapter(new PayTypeItemAdapter(data));
        lvTypes.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        if (listener != null) {
                            listener.onClick(dialog, position);
                        }
                    }
                }
        );
    }

    /**
     *
     * 2016.12.28 调用TreasureBuyActivity
     * 单独添加主要是一块夺宝需要根据用户等级来展示提示信息
     */
    public static void showPayChooseTypeDialog(final Context context,
                                               final DialogInterface.OnClickListener listener,
                                               List<PayType> data) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_paytype_layout, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ListView lvTypes = (ListView) view.findViewById(R.id.lv_types);
        lvTypes.setAdapter(new PayChooseTypeAdapter(context,data));
        lvTypes.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        if (listener != null) {
                            listener.onClick(dialog, position);
                        }
                    }
                }
        );
    }

    public static void showRedPkgChooseDialog(final FragmentActivity context,
                                              final DialogInterface.OnClickListener listener,
                                              List<RedPackageInfo.GiftListEntity> data) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_redpkg_layout, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ListView lvTypes = (ListView) view.findViewById(R.id.lv_types);
        final RedPackageDialogListAdapter adapter = new RedPackageDialogListAdapter(data);
        lvTypes.setAdapter(adapter);
        lvTypes.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        adapter.setSelectedPosition(position);
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dialog, adapter.getSelectedPosition());
                }
            }
        });
    }

    /**
     * @param context 尚未开通拉卡拉收款宝服务提示框
     */
    public static void showMerchantNotOpenDialog(final FragmentActivity context) {
        OnClickListener negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ProtocalActivity.open(context, ProtocalType.GPS_PERMISSION);
            }
        };
        /**
         * 1
         */
        createFullContentDialog(
                context, "提示", "取消", "确定", "您要先开通拉卡拉收款服务才能使用该功能哦，快去开通吧！", negativeListener, positiveListener
        ).show();
    }

    /**
     * @param context 尚未开通拉卡拉极速到账服务提示框
     */
    public static void showMerchantNotOpenDialog2(final FragmentActivity context) {
        OnClickListener negativeListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ProtocalActivity.open(context, ProtocalType.GPS_PERMISSION);
            }
        };
        /**
         * 1您要先开通拉卡拉收款服务才能使用立即提款功能，快去开通吧！
         */
        createFullContentDialog(
                context, "提示", "取消", "确定", "您要先开通拉卡拉收款服务才能使用该功能哦，快去开通吧！", negativeListener, positiveListener
        ).show();
    }

    public static BaseDialog createFullContentDialog(FragmentActivity context, String okString,
                                                     String cancelString, String content,
                                                     final OnClickListener positiveListener,
                                                     final OnClickListener negativeListener) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0: {
                        if (positiveListener != null) {
                            positiveListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                    case 1: {
                        //立即开通
                        if (negativeListener != null) {
                            negativeListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                }
            }
        };
        BaseDialog dialog = createAlertDialog(
                context, "", content, listener, okString, cancelString
        );
        return dialog;
    }

    public static BaseDialog createFullContentDialog2(FragmentActivity context, String okString,
                                                      String cancelString, String content,
                                                      final OnClickListener positiveListener,
                                                      final OnClickListener negativeListener) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0: {
                        if (positiveListener != null) {
                            positiveListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                    case 1: {
                        //立即开通
                        if (negativeListener != null) {
                            negativeListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                }
            }
        };
        BaseDialog dialog = createAlertDialog2(
                context, "", content, listener, okString, cancelString
        );
        return dialog;
    }

    public static BaseDialog createFullContentDialog(FragmentActivity context, String title, String okString,
                                                     String cancelString, String content,
                                                     final OnClickListener positiveListener,
                                                     final OnClickListener negativeListener) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0: {
                        if (positiveListener != null) {
                            positiveListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                    case 1: {
                        if (negativeListener != null) {
                            negativeListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                }
            }
        };
        BaseDialog dialog = createAlertDialog(
                context, title, content, listener, okString, cancelString
        );
        return dialog;
    }

    public static BaseDialog createFullShueDialog(FragmentActivity context, String title, String okString,
                                                  String content,
                                                  final OnClickListener positiveListener,
                                                  final OnClickListener negativeListener) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index) {
                    case 0: {
                        if (positiveListener != null) {
                            positiveListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                    case 1: {
                        if (negativeListener != null) {
                            negativeListener.onClick(
                                    dialog.getDialog(), index
                            );
                        }
                        break;
                    }
                }
            }
        };
        BaseDialog dialog = createAlertDialog(
                context, title, content, listener, okString
        );
        return dialog;
    }

    /**
     * 信息提示（只含有提示信息内容和确定按钮）
     *
     * @param context
     * @param okString 确认按钮文字
     * @return
     */
    public static BaseDialog createConfirmDialog(FragmentActivity context, String okString,
                                                 String content) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        BaseDialog dialog = createOneConfirmButtonDialog(context, okString, content, listener);
        return dialog;
    }

    /**
     * 信息提示（只含有提示信息内容和确定按钮）
     *
     * @param context
     * @param okString         确认按钮文字
     * @param positiveListener 点击确认按钮监听事件
     * @return
     */
    public static BaseDialog createOneConfirmButtonDialog(FragmentActivity context, String okString,
                                                          String content,
                                                          final OnClickListener positiveListener) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                if (positiveListener != null) {
                    positiveListener.onClick(dialog.getDialog(), index);
                }
            }
        };
        BaseDialog dialog = createAlertDialog(context, "", content, listener, okString);
        return dialog;
    }

    public static BaseDialog createAlertDialog(FragmentActivity context, String title,
                                               String message,
                                               AlertDialog.AlertDialogDelegate listener,
                                               String... btns) {
        AlertDialog dialog = new AlertDialog();
        dialog.setContext(context);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            dialog.setMessage(message);
        }
        if (listener != null) {
            dialog.setDialogDelegate(listener);
        }
        if (btns != null) {
            dialog.setButtons(btns);
        }
        return dialog;
    }

    public static BaseDialog createAlertDialog2(FragmentActivity context, String title,
                                                String message,
                                                AlertDialog.AlertDialogDelegate listener,
                                                String... btns) {
        AlertDialog dialog = new AlertDialog();
        dialog.setContext(context);
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(message)) {
            dialog.setMessageViewSize();
            dialog.setMessage(message);
        }
        if (listener != null) {
            dialog.setDialogDelegate(listener);
        }
        if (btns != null) {
            dialog.setButtons(btns);
        }
        return dialog;
    }


}
