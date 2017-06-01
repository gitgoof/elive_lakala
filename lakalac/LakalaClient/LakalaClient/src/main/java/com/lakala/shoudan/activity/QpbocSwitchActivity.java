package com.lakala.shoudan.activity;

import android.os.Bundle;

import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.shoudan.R;
import com.lakala.ui.component.LabelSwitch;

/**
 * Created by linmq on 2016/6/7.
 */
public class QpbocSwitchActivity extends AppBaseActivity implements LabelSwitch.OnSwitchListener {
    private LabelSwitch lsQpbocSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpboc_switch);
        navigationBar.setTitle("挥卡设置");
        lsQpbocSwitch = (LabelSwitch)findViewById(R.id.ls_qpboc_switch);
        boolean forceQpboc = LklPreferences.getInstance()
                                           .getBoolean(LKlPreferencesKey.FORCE_QPBOC, false);
        lsQpbocSwitch.setSwitchStatus(forceQpboc? LabelSwitch.ESwitchStatus.ON:
                                      LabelSwitch.ESwitchStatus.OFF);
        lsQpbocSwitch.setOnSwitchListener(this);
    }

    @Override
    public void onSwitch(LabelSwitch.ESwitchStatus status) {
        boolean isForce = false;
        switch (status){
            case ON:
                isForce = true;
                break;
            case OFF:
                isForce = false;
                break;
        }
        LklPreferences.getInstance()
                      .putBoolean(LKlPreferencesKey.FORCE_QPBOC,isForce);
    }
}
