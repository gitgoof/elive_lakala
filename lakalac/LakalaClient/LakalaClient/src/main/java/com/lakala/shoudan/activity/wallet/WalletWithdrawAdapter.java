package com.lakala.shoudan.activity.wallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.ImageUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.wallet.bean.SimpleWithdrawCardInfo;

import java.util.List;

/**
 * Created by fengxuan on 2015/12/25.
 */
public class WalletWithdrawAdapter extends BaseAdapter{

    private Context context;
    private List<SimpleWithdrawCardInfo> data;

    public WalletWithdrawAdapter(Context context, List<SimpleWithdrawCardInfo> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_transfer_bind_card, null);
            holder = new Holder();
            holder.bankName = (TextView) view.findViewById(R.id.tv_bank_name);
            holder.bankType = (TextView) view.findViewById(R.id.tv_type);
            holder.bankCard = (TextView) view.findViewById(R.id.tv_bank_number);
            holder.bankIcon = (ImageView) view.findViewById(R.id.iv_bank_icon);

            view.setTag(holder);

        } else {
            holder = (Holder) view.getTag();
        }
        if (data.size()>0){
            holder.bankName.setText(data.get(i).getBankName());
            holder.bankType.setText(data.get(i).getBankType());
            String card = data.get(i).getBankCard();
            holder.bankCard.setText(CardUtil.formatCardNumberWithStar(card));
            String bankCode = data.get(i).getBankCode();
            Bitmap bitmap = ImageUtil.getBitmapInAssets(context, "bank_icon/" + bankCode + "" + ".png");
            if (bitmap != null) {
                holder.bankIcon.setImageBitmap(bitmap);
            }
        }

        return view;
    }

    class Holder{
        TextView bankName;
        TextView bankType;
        TextView bankCard;
        ImageView bankIcon;
    }
}

