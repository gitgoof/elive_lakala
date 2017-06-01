package com.lakala.elive.user.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ContactFragment extends Fragment {
	
	private final String TAG = ContactFragment.class.getSimpleName();

    public ContactFragment() {
    	
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.report_content_fragment, container, false);
        View view = null;
        return view;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState); 
    } 
	
}
