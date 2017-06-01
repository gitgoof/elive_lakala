package com.lakala.shoudan.activity.payment.base;

import com.lakala.shoudan.component.VerticalListView;

/**
 * Created by More on 15/9/23.
 */
public class TransferInfoWithIconEntity extends TransferInfoEntity{


    private VerticalListView.IconType type;

    public TransferInfoWithIconEntity(String name, String value, VerticalListView.IconType type) {
        super(name, value);
        this.type = type;
    }

    public TransferInfoWithIconEntity(String name, String value, int colorId, boolean needdevider, VerticalListView.IconType type) {
        super(name, value, colorId, needdevider);
        this.type = type;
    }

    public VerticalListView.IconType getType() {
        return type;
    }

    public void setType(VerticalListView.IconType type) {
        this.type = type;
    }
}
