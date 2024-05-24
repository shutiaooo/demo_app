package com.broken.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/*
 * InfoFragment 详细数据页的两个分页
 */

public class InfoFragment extends Fragment {
    private String type, time, startTime, calorie, distance, averageSpeed, minSpeed, maxSpeed;

    public InfoFragment(String type, String time, String startTime, String calorie, String distance, String averageSpeed, String minSpeed, String maxSpeed) {
        this.type = type;
        this.time = time;
        this.startTime = startTime;
        this.calorie = calorie;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }

    public static InfoFragment newInstance(String type, String time, String startTime, String calorie, String distance, String averageSpeed, String minSpeed, String maxSpeed) {
        return new InfoFragment(type, time, startTime, calorie, distance, averageSpeed, minSpeed, maxSpeed);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.info_fragment, container, false);
        TextView timeView = view.findViewById(R.id.time);
        TextView distanceView = view.findViewById(R.id.myDistance);
        TextView totalTimeView = view.findViewById(R.id.myTotalTime);
        TextView calorieView = view.findViewById(R.id.myCalorie);
        TextView averageView = view.findViewById(R.id.myAverageSpeed);
        TextView minView = view.findViewById(R.id.minSpeed);
        TextView maxView = view.findViewById(R.id.maxSpeed);

        timeView.setText(startTime);
        distanceView.setText(distance);
        totalTimeView.setText(time);
        calorieView.setText(calorie);
        if (averageSpeed != null) {
            averageView.setText(averageSpeed);
        }
        if (minSpeed != null) {
            minView.setText(minSpeed);
        }
        if (maxSpeed != null) {
            maxView.setText(maxSpeed);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
