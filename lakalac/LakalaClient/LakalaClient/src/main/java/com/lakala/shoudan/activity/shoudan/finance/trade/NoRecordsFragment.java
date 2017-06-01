package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * Created by Administrator on 2015/10/12.
 */
public class NoRecordsFragment extends Fragment {

    private ImageView imageView;
    private TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_records,null);
        imageView = (ImageView) view.findViewById(R.id.img_no_record);
        textView = (TextView) view.findViewById(R.id.tv_no_record);
        Bundle bundle = getArguments();
        if (bundle != null){
            boolean isRed = bundle.getBoolean("isRed",false);
            if (isRed){
                textView.setText("您现在没有红包");
                imageView.setImageResource(R.drawable.img_no_red);
            }
            boolean isTrade = bundle.getBoolean("isTrade",false);
            if (isTrade){
                textView.setText("无交易记录");
            }
            boolean isWallet = bundle.getBoolean("isWallet",false);
            if(isWallet){
                textView.setText("亲，还没有使用过钱包哦");
            }
        }
        return view;
    }
}
