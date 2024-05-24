package com.broken.finalproject;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {

    /**
     * 将AMapLocation List 转为LatLng list
     * @param list LocationList
     * @return LatLngList
     */
    public static List<LatLng> parseLatLngList(List<AMapLocation> list) {
        List<LatLng> traceList = new ArrayList<>();
        if (list == null) {
            return traceList;
        }
        for (int i = 0; i < list.size(); i++) {
            AMapLocation loc = list.get(i);
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            LatLng latlng = new LatLng(lat, lng);
            traceList.add(latlng);
        }
        return traceList;
    }

    public static AMapLocation parseLocation(String latLonStr) {
        if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
            return null;
        }
        String[] loc = latLonStr.split(",");
        AMapLocation location = null;
        if (loc.length == 6) {
            location = new AMapLocation(loc[2]);
            location.setProvider(loc[2]);
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
            location.setTime(Long.parseLong(loc[3]));
            location.setSpeed(Float.parseFloat(loc[4]));
            location.setBearing(Float.parseFloat(loc[5]));
        }else if(loc.length == 2){
            location = new AMapLocation("gps");
            location.setLatitude(Double.parseDouble(loc[0]));
            location.setLongitude(Double.parseDouble(loc[1]));
        }

        return location;
    }

    public static ArrayList<AMapLocation> parseLocations(String latLonStr) {
        ArrayList<AMapLocation> locations = new ArrayList<AMapLocation>();
        String[] latLonStrs = latLonStr.split(";");
        for (String lonStr : latLonStrs) {
            AMapLocation location = DataUtils.parseLocation(lonStr);
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }
}

