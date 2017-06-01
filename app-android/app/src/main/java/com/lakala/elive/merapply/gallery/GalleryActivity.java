package com.lakala.elive.merapply.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.R;
import com.lakala.elive.merapply.gallery.adapter.ImagerAdapter;
import com.lakala.elive.merapply.gallery.bean.FolderBean;
import com.lakala.elive.merapply.gallery.util.PermissionsChecker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 相册选择
 * Created by wenhaogu on 16/7/21.
 */

public class GalleryActivity extends Activity {


    private GridView mGridView;
    private List<String> mImages;
    private ImagerAdapter mImgAdapter;

    private RelativeLayout mBottomLy;
    private TextView mDirName;
    private TextView mDirCount;

    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> mFolders;
    private ListImageDirPopuWindow mDirPopuWindow;

    private ProgressDialog mProgressDialog;
    private static final String[] PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};//权限
    private PermissionsChecker mPermissionsChecker;//检查权限
    private static final int PERMISSION_REQUEST_CODE = 0;        // 系统权限返回码
    private static final int REQUEST_CODE = 0; // 请求码
    private static final String PACKAGE_URL_SCHEME = "package:";
    //数据加载完成
    private static final int DATA_LOADED = 0x110;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DATA_LOADED) {
                mProgressDialog.dismiss();
                //把数据设置到view中
                data2View();
                initDirPopuWindow();
            }
        }
    };

    private void initDirPopuWindow() {
        if (mCurrentDir == null) {
            mCurrentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/camera/");
        }
        mDirPopuWindow = new ListImageDirPopuWindow(this, mFolders, mCurrentDir.getName());

        mDirPopuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //lighrOn();
            }
        });
        mDirPopuWindow.setOnDirSelectedListener(new ListImageDirPopuWindow.OnDirSelecteListener() {
            @Override
            public void onSeleted(FolderBean folderBean) {
                mCurrentDir = new File(folderBean.getDir());
                mImages = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png")) {
                            return true;
                        }
                        return false;
                    }
                }));
                //mImgAdapter = new ImagerAdapter(GalleryActivity.this, mImages, mCurrentDir.getAbsolutePath());
//                mGridView.setAdapter(mImgAdapter);
                mImgAdapter.setNewData(mImages, mCurrentDir.getAbsolutePath());
                mDirCount.setText(mImages.size() + "张");
                mDirName.setText(folderBean.getName());
                mDirPopuWindow.dismiss();
            }
        });
    }

    /**
     * 内容区变亮
     */
    private void lighrOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    /**
     * 内容去变暗
     */
    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mPermissionsChecker = new PermissionsChecker(this);
        initView();
        initEvent();
        if (Build.VERSION.SDK_INT >= 23) {
            if (mPermissionsChecker.judgePermissions(PERMISSION)) {
                ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_REQUEST_CODE);
            } else {
                initDatas();
            }
        } else {
            initDatas();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            initDatas();
        } else {
            showPermissionDialog();
        }
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void showPermissionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("帮助");
        dialog.setMessage("当前应用缺少必要权限。请点击\"设置\"-打开所需权限。");
        dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSetting();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 跳设置
     */
    private void startAppSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
        finish();
    }

    private void initEvent() {
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDirPopuWindow.setAnimationStyle(R.style.dir_popuwindow_anim);
                mDirPopuWindow.showAsDropDown(mBottomLy, 0, 0);
                //lightOff();
            }
        });
    }
    /**
     * 利用contentProvider扫描手机所以图片
     */
    private void initDatas() {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread() {
            @Override
            public void run() {
                Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = GalleryActivity.this.getContentResolver();
                Cursor cursor = cr.query(mImgUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/jpg"}, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<String>();
                mFolders = new ArrayList<FolderBean>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parenFile = new File(path).getParentFile();

                    if (parenFile == null)
                        continue;

                    String dirPath = parenFile.getAbsolutePath();

                    FolderBean folderBean = null;

                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        folderBean = new FolderBean();
                        folderBean.setDir(dirPath);
                        folderBean.setFirstImgPath(path);
                    }

                    if (parenFile.list() == null)
                        continue;

                    int picSize = parenFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png"))
                                return true;
                            return false;
                        }
                    }).length;
                    folderBean.setCount(picSize);
                    mFolders.add(folderBean);

                    //获取最多图片的文件夹
                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parenFile;
                    }
                }
                cursor.close();

                mHandler.sendEmptyMessage(0x110);
            }
        }.start();
    }

    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImages = Arrays.asList(mCurrentDir.list());
        mImgAdapter = new ImagerAdapter(this, mImages, mCurrentDir.getAbsolutePath());
        mGridView.setAdapter(mImgAdapter);
        mDirCount.setText(mMaxCount + "张");
        mDirName.setText(mCurrentDir.getName());

        mImgAdapter.setClickListener(new ImagerAdapter.ImagerAdapterClickListener() {
            @Override
            public void itemClickListener(String filePath) {
                setResult(RESULT_OK, new Intent().putExtra("GalleryActivity", filePath));
                finish();
            }
        });

    }

    private void initView() {

        mGridView = (GridView) findViewById(R.id.id_gridView);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mDirName = (TextView) findViewById(R.id.id_dir_name);
        mDirCount = (TextView) findViewById(R.id.id_dir_count);
    }


}
