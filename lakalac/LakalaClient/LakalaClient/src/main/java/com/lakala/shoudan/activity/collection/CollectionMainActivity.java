package com.lakala.shoudan.activity.collection;

import android.os.Bundle;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

public class CollectionMainActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_main);
        navigationBar.setTitle(R.string.title_activity_collection_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.collection_container,
                                                               new CollectionMainFragment())
                .commit();
    }
}
