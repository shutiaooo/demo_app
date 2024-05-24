package com.broken.finalproject.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展下拉列表需要绑定的实体类
 */

public class RecordList {
    private String time;
    private List<PathRecord> records = new ArrayList<>();

    public RecordList() {}

    public RecordList(String time, List<PathRecord> records) {
        this.time = time;
        this.records = records;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<PathRecord> getRecords() {
        return records;
    }

    public void setRecords(List<PathRecord> records) {
        this.records = records;
    }
}
