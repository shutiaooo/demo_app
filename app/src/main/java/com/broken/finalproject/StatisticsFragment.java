package com.broken.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.broken.finalproject.database.DBAdapter;
import com.broken.finalproject.po.PathRecord;
import com.broken.finalproject.po.RecordList;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * StatisticsFragment 主界面数据分页
 */
public class StatisticsFragment extends Fragment {
    private List<PathRecord> records = new ArrayList<>();
    private List<RecordList> recordLists = new ArrayList<>();
    private String type;
    private CustomExpandableListAdapter customExpandableListAdapter;


    public StatisticsFragment() {
        type = "run";
    }

    static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistic_fragment, container, false);
        Spinner spinner = view.findViewById(R.id.mySpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = getActivity().getResources().getStringArray(R.array.sports_type)[position];
                switch (type) {
                    case "所有运动":
                        type = "all";
                        break;
                    case "跑步":
                        type = "run";
                        break;
                    case "骑行":
                        type = "bike";
                        break;
                    case "步行":
                        type = "walk";
                        break;
                }
                initData();
                customExpandableListAdapter.refreshData(recordLists);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomExpandableListView expandableListView = new CustomExpandableListView(getContext());
        expandableListView.setOnGroupClickListener(new CustomExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new CustomExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                PathRecord pathRecord = recordLists.get(groupPosition).getRecords().get(childPosition);
                Intent intent = new Intent(getContext(), InfoActivity.class);
                intent.putExtra("type", pathRecord.getType());
                intent.putExtra("time", pathRecord.getDuration());
                intent.putExtra("calorie", pathRecord.getCalorie());
                intent.putExtra("distance", pathRecord.getDistance());
                intent.putExtra("startTime", pathRecord.getDate());
                intent.putExtra("speed", pathRecord.getAverageSpeed());
                intent.putExtra("maxSpeed", pathRecord.getMaxSpeed());
                intent.putExtra("minSpeed", pathRecord.getMinSpeed());
                intent.putExtra("selectTime", pathRecord.getDate());
                startActivity(intent);

                return false;
            }
        });

        expandableListView.setGroupIndicator(null);

        initData();

        customExpandableListAdapter = new CustomExpandableListAdapter(getContext(), recordLists);
        expandableListView.setAdapter(customExpandableListAdapter);

        LinearLayout layout = view.findViewById(R.id.listContainer);
        layout.addView(expandableListView);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        recordLists.clear();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String year = simpleDateFormat.format(date);

        DBAdapter adapter = new DBAdapter(getContext());
        adapter.open();

        for (int i = 1; i < 3; i++) {
            for (int j = 12; j > 0; j--) {
                List<PathRecord> records;
                if (j <= 9) {
                    records = adapter.queryRecordAllByDateAndType(year + "-0" + j, type);
                } else {
                    records = adapter.queryRecordAllByDateAndType(year + "-" + j, type);
                }
                if (records.size() != 0) {
                    recordLists.add(new RecordList(year + "年" + j + "月", records));
                }
            }
            year = String.valueOf(Integer.parseInt(year) - i);
        }
        adapter.close();
    }
}
