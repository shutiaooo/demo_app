package com.broken.finalproject;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/*
 * InfoActivity 详细数据页的activity
 */

public class InfoActivity extends AppCompatActivity {
    private String type, time, startTime, calorie, distance, averageSpeed, minSpeed, maxSpeed, selectTime;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if(Build.VERSION.SDK_INT >= 23) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(R.layout.actionbar);
                TextView view = actionBar.getCustomView().findViewById(R.id.title);
                view.setText("详细数据");
                actionBar.setElevation(0);
                actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorWhite)));
            }
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.colorWhite));
        }

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        time = intent.getStringExtra("time");
        startTime = intent.getStringExtra("startTime");
        calorie = intent.getStringExtra("calorie");
        distance = intent.getStringExtra("distance");
        averageSpeed = intent.getStringExtra("speed");
        maxSpeed = intent.getStringExtra("maxSpeed");
        minSpeed = intent.getStringExtra("minSpeed");
        selectTime = intent.getStringExtra("selectTime");

        RadioGroup radioGroup = findViewById(R.id.tabs);
        fragments = new ArrayList<>(2);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragments.add(InfoFragment.newInstance(type, time, startTime, calorie, distance, averageSpeed, minSpeed, maxSpeed));
        transaction.add(R.id.content, fragments.get(0));
        transaction.commit();

    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            hideFragments(transaction);
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    if (fragments.size() == 1) {
                        fragments.add(TraceFragment.newInstance(selectTime));
                        transaction.add(R.id.content, fragments.get(1));
                    }
                    transaction.show(fragments.get(i));
                    transaction.commit();
                    return ;
                }
            }
        }
    };

    private void hideFragments(FragmentTransaction transaction) {
        for (Fragment item: fragments) {
            if (item != null) {
                transaction.hide(item);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
