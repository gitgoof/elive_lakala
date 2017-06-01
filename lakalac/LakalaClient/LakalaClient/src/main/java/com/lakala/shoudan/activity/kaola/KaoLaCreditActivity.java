package com.lakala.shoudan.activity.kaola;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.common.PerfectClickListener;
import com.lakala.shoudan.component.KaoLaCreditView;
import com.lakala.shoudan.component.SharePopupWindow;
import com.lakala.ui.component.NavigationBar;

import java.util.ArrayList;

/**
 * Created by huwei on 2017/2/28.
 */

public class KaoLaCreditActivity extends AppBaseActivity {
    //    private ActivityKaolaCreditBinding mKaoLaBinding;
    private final String kaola_description = "考拉信用分是参照国际信用评估体系，根据您在拉卡拉的交易数据及在其他渠道提供的公开信息综合评估而得";
    /**
     * 信用等级
     */
    private final String[] levelMarks = new String[]{"C", "CC", "CCC", "B", "BB", "BBB", "A", "AA", "AAA"};
    /**
     * 信用分数
     */
    private final int[] levelPoints = new int[]{300, 399, 449, 549, 599, 649, 699, 749, 850};
    /**
     * 当前页标题
     */
    private final String mPageTitle = "考拉信用";
    /**
     * 考拉信用分享URL、
     */
    private final String mShareUrl = "http://www.antfortune.com";
    private SharePopupWindow mSharePopupWindow;


    private Button btnShare;
    private KaoLaCreditView kaolaView;
    private RelativeLayout rl_kaola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaola_credit);
        kaolaView = (KaoLaCreditView) findViewById(R.id.kaolaView);
        btnShare = (Button) findViewById(R.id.btnShare);
        rl_kaola = (RelativeLayout) findViewById(R.id.rl_kaola);

//        mKaoLaBinding = DataBindingUtil.setContentView(this, R.layout.activity_kaola_credit);
        initViews();
        setListener();
    }

    private void setListener() {
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });
        btnShare.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                showShareWindow();
            }
        });
    }

    private void showShareWindow() {
        if (mSharePopupWindow == null) {
            mSharePopupWindow = new SharePopupWindow(KaoLaCreditActivity.this, rl_kaola, mShareUrl, "你的考拉信用分是多少？快来收款宝测一测吧！",1);
        }
        mSharePopupWindow.showSharePopupWindow();
        setBackgroundAlpha(0.5f);
        mSharePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

    }

    private void initViews() {

        navigationBar.setTitle(mPageTitle);

        kaolaView.setAngle(270);
        kaolaView.setStartAngle(135);
        //信用分数集合
        ArrayList<Integer> mLevelPoints = new ArrayList<>();
        for (int point : levelPoints) {
            mLevelPoints.add(point);
        }
        kaolaView.setFigureDatas(mLevelPoints);
        //信用等级集合
        ArrayList<String> mLevelMarks = new ArrayList<>();
        for (String level : levelMarks) {
            mLevelMarks.add(level);
        }
        kaolaView.setLevelDatas(mLevelMarks);
        //几次出现一个长刻度
        kaolaView.setDialPer(5);
        //文字
        kaolaView.setLable("信用等级");
        //重绘数值
        kaolaView.refresh(750);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
