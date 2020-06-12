package com.example.tuantu.week7_findplacenearby;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by TUAN TU on 4/24/2016.
 */
public class testFragment extends Fragment {
    LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_fragment, container, false);
        //linearLayout = (LinearLayout) rootView.findViewById(R.id.linearlayout);
        return rootView;
    }
}
