package com.lakala.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.lakala.ui.R;

/**
 * Created by More on 15/3/11.
 */
public class AlertMyDialog extends AlertDialog {


    @Override
    View middleView() {

        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.pop_name_auth, null);

        return middle_root;

    }

}
