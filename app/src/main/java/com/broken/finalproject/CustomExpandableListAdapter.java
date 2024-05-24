package com.broken.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.broken.finalproject.po.PathRecord;
import com.broken.finalproject.po.RecordList;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomExpandableListAdapter 重写自定义ExpandableListAdapter类的适配器
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private List<RecordList> recordList = new ArrayList<>();


    CustomExpandableListAdapter(Context context, List<RecordList> recordLists) {
        this.inflater = LayoutInflater.from(context);
        this.recordList = recordLists;
    }

    public void refreshData(List<RecordList> recordLists) {
        this.recordList = recordLists;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return recordList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return recordList.get(groupPosition).getRecords().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return recordList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return recordList.get(groupPosition).getRecords().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView date;
        ImageView image;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.statistic_item, parent, false);
            convertView.setTag(convertView);
        } else {
            convertView = (View)convertView.getTag();
        }
        date = convertView.findViewById(R.id.date);
        image = convertView.findViewById(R.id.arrowImage);
        String t = ((RecordList)getGroup(groupPosition)).getTime();
        date.setText(t);
        if (isExpanded) {
            image.setImageResource(R.drawable.arrow_up_foreground);
        } else {
            image.setImageResource(R.drawable.arrow_down_foreground);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView distance, duration, speed, date;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.statistic_childitem, parent, false);
            convertView.setTag(convertView);
        } else {
            convertView = (View)convertView.getTag();
        }
        distance = convertView.findViewById(R.id.distanceText);
        duration = convertView.findViewById(R.id.durationText);
        speed = convertView.findViewById(R.id.speedText);
        date = convertView.findViewById(R.id.dateText);

        PathRecord temp = ((RecordList)getGroup(groupPosition)).getRecords().get(childPosition);
        distance.setText(temp.getDistance());
        duration.setText(temp.getDuration());
        speed.setText(temp.getAverageSpeed());
        date.setText(temp.getDate().substring(5, 10));

        ImageView typeImage = convertView.findViewById(R.id.type);
        switch (temp.getType()) {
            case "run":
                typeImage.setImageResource(R.drawable.ic_btn_run_foreground);
                break;
            case "walk":
                typeImage.setImageResource(R.drawable.ic_btn_walk_foreground);
                break;
            case "bike":
                typeImage.setImageResource(R.drawable.ic_btn_bike_foreground);
                break;
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}