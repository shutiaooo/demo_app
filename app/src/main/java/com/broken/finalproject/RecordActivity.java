package com.broken.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.broken.finalproject.database.DBAdapter;
import com.broken.finalproject.po.PathRecord;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * RecordActivity 运动录制
 */
public class RecordActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private Chronometer chronometer;
    private RadioButton pause, stop, resume;
    private TextView distanceText, speedText, calorieText;

    private String type, currentTime;

    private OnLocationChangedListener onLocationChangedListener;
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private PolylineOptions mPolyoptions;
    private Polyline mPolyline;
    private PathRecord record;
    private List<AMapLocation> recordList = new ArrayList<>();

    private long tempTime;
    private float distance, speed;
    private List<Float> speedLists = new ArrayList<>();
    private LatLng lastLocation;
    private Date date;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= 21) {  //21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {  //19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_record);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        init(); //初始化地图
        initpolyline();

        type = getIntent().getStringExtra("type");
        distance = 0;
        speed = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        date = new Date(System.currentTimeMillis());
        currentTime = simpleDateFormat.format(date);

        distanceText = findViewById(R.id.myDistance);
        speedText = findViewById(R.id.mySpeed);
        calorieText = findViewById(R.id.myCalorie);

        chronometer = findViewById(R.id.test);
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                int time = (int)(SystemClock.elapsedRealtime() - chronometer.getBase());
                chronometer.setText(FormatMiss(time/1000));
                distanceText.setText(new DecimalFormat("0.00").format(distance/1000));
                speedText.setText(String.valueOf(speed));
                calorieText.setText(new DecimalFormat("0.00").format(getCalorie(distance)));
                speedLists.add(speed);
            }
        });
        //设置开始计时时间
        chronometer.setBase(SystemClock.elapsedRealtime());
        //启动计时器
        chronometer.start();

        pause = findViewById(R.id.btn_pause);
        stop = findViewById(R.id.btn_stop);
        resume = findViewById(R.id.btn_resume);
        pause.setOnClickListener(onClickListener);
        stop.setOnClickListener(onClickListener);
        resume.setOnClickListener(onClickListener);
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setupMap(); //设置地图基本属性
        }
    }

    private void setupMap() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        aMap.setMinZoomLevel(15);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMapLocationClient = new AMapLocationClient(getApplicationContext());
        aMapLocationClientOption = new AMapLocationClientOption();
        aMapLocationClientOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        if(null != aMapLocationClient){
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            aMapLocationClient.stopLocation();
            aMapLocationClient.startLocation();
        }
    }

    private RadioButton.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pause:
                    pause.setVisibility(View.GONE);
                    stop.setVisibility(View.VISIBLE);
                    resume.setVisibility(View.VISIBLE);
                    resume.setChecked(false);
                    chronometer.stop();
                    aMapLocationClient.stopLocation();
                    tempTime = SystemClock.elapsedRealtime();
                    break;
                case R.id.btn_stop:
                    String duration = chronometer.getText().toString();
                    String distance = distanceText.getText().toString();
                    String calorie = calorieText.getText().toString();
                    Intent intent = new Intent(RecordActivity.this, InfoActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("time", duration);
                    intent.putExtra("calorie", calorie);
                    intent.putExtra("distance", distance);
                    intent.putExtra("startTime", currentTime);

                    String[] speed = getSpeed();
                    intent.putExtra("speed", speed[0]);
                    intent.putExtra("maxSpeed", speed[1]);
                    intent.putExtra("minSpeed", speed[2]);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    saveRecord(duration, distance, speed[0], speed[2], speed[1], simpleDateFormat.format(date), calorie);
                    intent.putExtra("selectTime", simpleDateFormat.format(date));
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_resume:
                    pause.setVisibility(View.VISIBLE);
                    stop.setVisibility(View.GONE);
                    resume.setVisibility(View.GONE);
                    resume.setChecked(true);
                    if (tempTime != 0) {
                        chronometer.setBase(chronometer.getBase() + (SystemClock.elapsedRealtime() - tempTime));
                    }
                    chronometer.start();
                    aMapLocationClient.startLocation();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        chronometer.setOnChronometerTickListener(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
        startLocation();
    }

    @Override
    public void deactivate() {
        onLocationChangedListener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }

    private void startLocation() {
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(this);
            aMapLocationClientOption = new AMapLocationClientOption();
            // 设置定位监听
            aMapLocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            aMapLocationClientOption.setSensorEnable(true);
            aMapLocationClientOption.setInterval(1000);

            // 设置定位参数
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            aMapLocationClient.startLocation();

        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (onLocationChangedListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                onLocationChangedListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng myLocation = new LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(myLocation));
                if (resume.isChecked()) {
                    recordList.add(amapLocation);
                    if (lastLocation != null) {
                        Log.i("location", lastLocation.toString());
                        distance += AMapUtils.calculateLineDistance(lastLocation, myLocation);
                    }
                    lastLocation = myLocation;
                    speed = amapLocation.getSpeed();
                    speedLists.add(speed);
                    mPolyoptions.add(myLocation);
                    redrawline();
                    Log.i("location", myLocation.toString() + amapLocation.getSpeed());
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ", "
                        + amapLocation.getErrorInfo();
                Log.e("AMapErr", errText);
            }
        }
    }

    private void initpolyline() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.RED);
    }

    // 实时轨迹画线
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mPolyline != null) {
                mPolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mPolyline = aMap.addPolyline(mPolyoptions);
            }
        }
    }

    /**
     * 返回所有速度类型
     * @return String[0] 平均速度 String[1] 最高速度 String[2] 最低速度
     */

    protected String[] getSpeed() {
        String[] speed = new String[3];
        float averageSpeed = 0;
        while(speedLists.contains(Float.parseFloat("0"))) {
            speedLists.remove(Float.parseFloat("0"));
        }
        if (speedLists.size() == 0) {
            return new String[] {"--", "--", "--"};
        }
        for (int i = 0; i < speedLists.size(); i++) {
            averageSpeed += speedLists.get(i);
        }
        averageSpeed /= speedLists.size();
        float max = Collections.max(speedLists);
        float min = Collections.min(speedLists);
        speed[0] = new DecimalFormat("0.00").format(averageSpeed);
        speed[1] = new DecimalFormat("0.00").format(max);
        speed[2] = new DecimalFormat("0.00").format(min);
        return speed;
    }

    public static String FormatMiss(int time) {
        String hh = time/3600 > 9 ?time/3600+"" :"0"+time/3600;
        String mm = (time%3600)/60 > 9 ?(time% 3600)/60+"" : "0"+(time%3600)/60;
        String ss = (time%3600)%60 > 9 ?(time% 3600) % 60+"" : "0"+(time%3600)%60;
        return hh + ":" + mm + ":" + ss;
    }

    private float getCalorie(float distance) {
        float calorie = 0;
        switch (type) {
            case "run":
                calorie = distance / 1000 * 90;
                break;
            case "walk":
                calorie = distance / 1000 * 50;
                break;
            case "bike":
                calorie = distance / 1000 * 120;
                break;
        }
        return calorie;
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder pathline = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        return location.getLatitude() + "," +
                location.getLongitude() + "," +
                location.getProvider() + "," +
                location.getTime() + "," +
                location.getSpeed() + "," +
                location.getBearing();
    }

    protected void saveRecord(String duration, String distance, String average, String min, String max, String time, String calorie) {
        if (recordList != null && recordList.size() > 0) {
            DBAdapter DBHelper = new DBAdapter(this);
            DBHelper.open();
            String pathLineString = getPathLineString(recordList);
            AMapLocation firstLocation = recordList.get(0);
            AMapLocation lastLocation = recordList.get(recordList.size() - 1);
            String startPoint = amapLocationToString(firstLocation);
            String endPoint = amapLocationToString(lastLocation);
            DBHelper.createRecord(distance, duration, average, min, max,
                    pathLineString, startPoint, endPoint, time, calorie, type);
            DBHelper.close();
        } else {
            Toast.makeText(RecordActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
