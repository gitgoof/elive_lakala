package com.lakala.elive.merapply.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.lakala.elive.R;

/**
 * 进件完成
 * Created by wenhaogu on 2017/1/16.
 */

public class MerApplyCompleteActivity extends BaseActivity {

    private Button btnComplete;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_mer_apply_complete);
    }

    @Override
    protected void bindView() {
        btnComplete = findView(R.id.btn_complete);
        WaitInputActivity.needRefresh = true;
    }

    @Override
    protected void bindEvent() {
        btnComplete.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("进件完成");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                startActivity(new Intent(this, EnterPieceActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, EnterPieceActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return false;
    }

}
