package com.broken.finalproject.po;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动记录实体类
 */

public class PathRecord {
    private AMapLocation startPoint;
    private AMapLocation endPoint;
    private List<AMapLocation> pathLinePoints = new ArrayList<>();
    private String distance;
    private String duration;
    private String averageSpeed;
    private String minSpeed;
    private String maxSpeed;
    private String date;
    private String calorie;
    private String type;
    private int id = 0;

    public PathRecord() {}

    public AMapLocation getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(AMapLocation startPoint) {
        this.startPoint = startPoint;
    }

    public AMapLocation getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(AMapLocation endPoint) {
        this.endPoint = endPoint;
    }

    public List<AMapLocation> getPathLinePoints() {
        return pathLinePoints;
    }

    public void setPathLinePoints(List<AMapLocation> pathLinePoints) {
        this.pathLinePoints = pathLinePoints;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(String minSpeed) {
        this.minSpeed = minSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
