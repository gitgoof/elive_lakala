package com.lakala.elive.qcodeenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.elive.R;
import com.lakala.elive.common.utils.ScreenShot;
import com.lakala.elive.common.utils.Utils;
import com.lakala.elive.merapply.activity.BaseActivity;
import com.lakala.elive.qcodeenter.response.QCodeListResponse;
import com.mining.app.zxing.encoding.EncodingHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Q码详情
 */
public class QCodeDetailActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();

    private ImageView back;
    private Button mRightBtn;

    private ImageView mCodeImg;
    private TextView mCodeNumTxt;

    private Bitmap bitmap;
    private String qCodeNum;

    private QCodeListResponse.ContentBean.Qcodes qcodeBean;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_qcodepub_detail);
    }

    @Override
    protected void bindView() {
        back = findView(R.id.btn_iv_back);
        mRightBtn = findView(R.id.btn_qcodedetail_action);
        mCodeImg = findView(R.id.qcodebind_detail_codeimg);
        mCodeNumTxt = findView(R.id.qcodebind_detail_codetxt);
        back.setVisibility(View.VISIBLE);
        mRightBtn.setTextSize(17);
        mRightBtn.setText("保存图片");
        mRightBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void bindEvent() {
        back.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("Q码详情");
        Intent intent = getIntent();
        if (intent != null) {
            qcodeBean = (QCodeListResponse.ContentBean.Qcodes) intent.getSerializableExtra("QCODEBEAN");
            if (qcodeBean != null) {
                qCodeNum = qcodeBean.getQCode();
                mCodeNumTxt.setText("NO." + qCodeNum);
                bitmap = EncodingHandler.createQRCode(qcodeBean.getQcodeUrl(), 300);
                mCodeImg.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "qcodeBean为空");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
            case R.id.btn_qcodedetail_action://保存图片
             bitmap=   ScreenShot.takeScreenShot(QCodeDetailActivity.this,50f);

                showProgressDialog("保存中...");
                if (bitmap != null) {
                    saveImageToGallery(this, bitmap);
                } else {
                    bitmap = EncodingHandler.createQRCode(qCodeNum, 300);
                    mCodeImg.setImageBitmap(bitmap);
                    if (bitmap == null) {
                        Utils.showToast(this, "图片为空，不能保存");
                    }
                }
                closeProgressDialog();
                break;
        }
    }

    @Override
    public void onSuccess(int method, Object obj) {
    }

    @Override
    public void onError(int method, String statusCode) {
        closeProgressDialog();
        Utils.showToast(this, statusCode + "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    //保存图片到手机本地
    private static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File appDir = new File(dir, "elive");
//        File appDir = new File(Environment.getExternalStorageDirectory(), "ELIVEPIC");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        Toast.makeText(context, "保存成功,请到相册查看", Toast.LENGTH_LONG).show();
    }
}
