package com.map.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.map.R;
import com.map.common.Params;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SerachActivity extends AppCompatActivity {
    @BindView(R.id.edit_distance)
    EditText edit_distance;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        intent = getIntent();
    }

    @OnClick({R.id.btn_serach})
    void click(View view) {
        switch (view.getId()) {
            case R.id.btn_serach:
                int distance = Integer.valueOf(edit_distance.getText().toString());
                intent.putExtra(Params.DISTANCE, distance);
                setResult(100, intent);
                finish();
                break;
        }
    }
}
