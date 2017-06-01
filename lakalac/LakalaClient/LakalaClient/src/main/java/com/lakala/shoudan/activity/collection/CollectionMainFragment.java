package com.lakala.shoudan.activity.collection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.R;
import com.lakala.ui.component.MenuItemLayout;
import com.lakala.ui.dialog.AlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionMainFragment extends Fragment implements View.OnClickListener {


    public CollectionMainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    protected void initUI(View view) {
        MenuItemLayout to_collection = (MenuItemLayout) view.findViewById(R.id.to_collection);
        View divide = to_collection.getBottomDivide();
        MenuItemLayout to_cancel_trans = (MenuItemLayout) view.findViewById(R.id.to_cancel_trans);
        to_cancel_trans.addControlDivider(divide);

        view.findViewById(R.id.to_collection).setOnClickListener(this);
        view.findViewById(R.id.to_cancel_trans).setOnClickListener(this);
    }


    private boolean checkMerchantStatus(){
        if(ApplicationEx.getInstance().getUser().getMerchantInfo().getMerchantStatus() == MerchantStatus.NONE){
            AlertDialog alertDialog = new AlertDialog();
            alertDialog.setMessage(getString(R.string.merchant_open_tips));
            alertDialog.setButtons(new String[]{getString(R.string.cancel),getString( R.string.button_ok)});
            alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
                @Override
                public void onButtonClick(AlertDialog dialog, View view, int index) {
                    dialog.dismiss();
                    switch (index){
                        case 0:
                            break;
                        case 1:
                            BusinessLauncher.getInstance().start("mpos_merchant_apply");
                            break;
                    }

                }
            });
            alertDialog.show(getFragmentManager());
            return false;
        }else{
            return true;
        }

    }

    @Override
    public void onClick(View v) {

        if(checkMerchantStatus()) {

            switch (v.getId()) {
                case R.id.to_collection: {

//                    StatisticManager.getInstance().onEvent(StatisticType.Collect_1, getActivity());

                    BusinessLauncher.getInstance().start("collection_transaction");
                    break;
                }
                case R.id.to_cancel_trans: {
//                    StatisticManager.getInstance().onEvent(StatisticType.Undo_1, getActivity());

                    BusinessLauncher.getInstance().start("revocation");
                    break;
                }
            }
        }
    }
}
