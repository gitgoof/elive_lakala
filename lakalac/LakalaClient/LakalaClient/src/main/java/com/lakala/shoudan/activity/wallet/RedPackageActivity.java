package com.lakala.shoudan.activity.wallet;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.finance.trade.NoRecordsFragment;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;
import com.lakala.shoudan.activity.wallet.fragment.OutDateFragment;
import com.lakala.shoudan.activity.wallet.fragment.UnuseFragment;
import com.lakala.shoudan.activity.wallet.fragment.UsedFragment;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/11/20.
 */
public class RedPackageActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private RadioButton rbUnuse;
    private RadioButton rbUsing;
    private RadioButton rbOutdate;
    private RadioGroup group;
    private FrameLayout redContent;
    private UnuseFragment unuseFragment;
    private UsedFragment usedFragment;
    private OutDateFragment outDateFragment;
    private RedPackageInfo redPackageInfo = new RedPackageInfo();
    private NoRecordsFragment noRecordsFragment;
    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_package);
        jsonString = getIntent().getStringExtra("data");
        json2Info();
        initUI();
    }

    private void json2Info() {
        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            redPackageInfo.paseObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("红包");
        navigationBar.setActionBtnText("使用规则");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Check_Red_Envelope_Rule, RedPackageActivity.this);
                    ProtocalActivity.open(RedPackageActivity.this, ProtocalType.RED_PACKAGE_RULE);
                }
            }
        });
        assignViews();

        if (redPackageInfo.getGiftList().size() == 0) {
            replace(0);
        } else {
            replace(1);
        }
    }

    private void assignViews() {
        rbUnuse = (RadioButton) findViewById(R.id.rb_unuse);
        rbUsing = (RadioButton) findViewById(R.id.rb_using);
        rbOutdate = (RadioButton) findViewById(R.id.rb_outdate);
        group = (RadioGroup) findViewById(R.id.group);
        rbUnuse.setOnCheckedChangeListener(this);
        rbUsing.setOnCheckedChangeListener(this);
        rbOutdate.setOnCheckedChangeListener(this);

        redContent = (FrameLayout) findViewById(R.id.red_content);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_unuse:
                if (isChecked) {
                    replace(1);
                }
                break;
            case R.id.rb_using:
                if (isChecked) {
                    replace(2);
                }
                break;

            case R.id.rb_outdate:
                if (isChecked) {
                    replace(3);
                }
                break;
        }
    }

    //type:0、无记录，1、未使用，2、已使用，3、已过期
    public void replace(int type) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (type == 0) {
            if (noRecordsFragment == null) {
                noRecordsFragment = new NoRecordsFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRed", true);
                noRecordsFragment.setArguments(bundle);
            }
            transaction.replace(R.id.red_content, noRecordsFragment);
            transaction.commit();
        } else if (type == 1) {

            if (unuseFragment == null) {
                unuseFragment = new UnuseFragment();
                Bundle bundle = new Bundle();
                bundle.putString("jsonString", jsonString);
                unuseFragment.setArguments(bundle);
            }
            transaction.replace(R.id.red_content, unuseFragment);
            transaction.commit();
        } else if (type == 2) {

            if (usedFragment == null) {
                usedFragment = new UsedFragment();
            }
            transaction.replace(R.id.red_content, usedFragment);
            transaction.commit();
        } else if (type == 3) {

            if (outDateFragment == null) {
                outDateFragment = new OutDateFragment();
            }
            transaction.replace(R.id.red_content, outDateFragment);
            transaction.commit();
        }

    }
}
