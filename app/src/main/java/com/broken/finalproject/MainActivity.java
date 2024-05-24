package com.broken.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity主界面
 */

public class MainActivity extends AppCompatActivity {
    private List<Fragment> fragments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 23) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(R.layout.actionbar);
                actionBar.setElevation(0);
                actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorWhite)));
            }
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.colorWhite));
        }

        RadioGroup radioGroup = findViewById(R.id.tabs);
        fragments = new ArrayList<>(2);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragments.add(new SportsFragment());
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
                        fragments.add(StatisticsFragment.newInstance());
                        transaction.add(R.id.content, fragments.get(1));
                    }
                    transaction.show(fragments.get(i));
                    transaction.commit();
                    return;
                }
            }
        }
    };

    private void hideFragments(FragmentTransaction transaction) {
        for (Fragment item : fragments) {
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