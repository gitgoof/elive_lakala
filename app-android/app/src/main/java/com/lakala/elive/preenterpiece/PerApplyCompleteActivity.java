package com.lakala.elive.preenterpiece;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.common.utils.XAtyTask;
import com.lakala.elive.merapply.activity.BaseActivity;

/**
 * 合作商预进件完成
 */
public class PerApplyCompleteActivity extends BaseActivity {

    private Button btnComplete;
    private TextView mContent;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_mer_apply_complete);
    }

    @Override
    protected void bindView() {
        mContent = findView(R.id.txt_content);
        btnComplete = findView(R.id.btn_complete);
    }

    @Override
    protected void bindEvent() {
        btnComplete.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        tvTitleName.setText("进件");
        mContent.setText("预进件信息提交成功");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                completeOpt();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            completeOpt();
        }
        return false;
    }

    //预进件完成后的操作处理
    public void completeOpt() {
        XAtyTask.getInstance().killAllAty();
        startActivity(new Intent(this, PreEnterPieceListActivity.class));
        finish();
    }
}
