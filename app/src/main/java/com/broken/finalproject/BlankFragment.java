package com.broken.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * BlankFragment 主界面运动分页模式切换的fragment
 */
public class BlankFragment extends Fragment {
    private static final String ARG_PARAM1 = "";
    private String context;

    public BlankFragment() {}

    public static BlankFragment newInstance(String context) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            context = getArguments().getString(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment, container, false);
        ImageButton imageButton = rootView.findViewById(R.id.btn);
        if ("run".equals(context)) {
            imageButton.setBackgroundResource(R.drawable.ic_btn_run_foreground);
            imageButton.setContentDescription("run");
        } else if ("bike".equals(context)) {
            imageButton.setBackgroundResource(R.drawable.ic_btn_bike_foreground);
            imageButton.setContentDescription("bike");
        } else if ("walk".equals(context)) {
            imageButton.setBackgroundResource(R.drawable.ic_btn_walk_foreground);
            imageButton.setContentDescription("walk");
        } else {
            imageButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark)); // 错误图标表示异常
        }
        imageButton.setOnClickListener(new View.OnClickListener() { //监听跳转具体轨迹运动活动界面
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecordActivity.class);
                ImageButton button = (ImageButton)v;
                intent.putExtra("type", button.getContentDescription().toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
