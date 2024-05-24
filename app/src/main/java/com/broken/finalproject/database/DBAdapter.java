package com.broken.finalproject.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.broken.finalproject.DataUtils;
import com.broken.finalproject.po.PathRecord;


/**
 * 数据库相关操作，用于存取运动、轨迹记录
 *
 */
public class DBAdapter {
    private static final String KEY_ROWID = "id";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_SPEED = "averagespeed";
    private static final String KEY_MINSPEED = "minspeed";
    private static final String KEY_MAXSPEED = "maxspeed";
    private static final String KEY_LINE = "pathline";
    private static final String KEY_START = "startpoint";
    private static final String KEY_END = "endpoint";
    private static final String KEY_DATE = "date";
    private static final String KEY_CALORIE = "calorie";
    private static final String KEY_TYPE = "type";

    private final static String DATABASE_PATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/MyApplication";
    private static final String DATABASE_NAME = DATABASE_PATH + "/" + "record.db";
    private static final int DATABASE_VERSION = 1;
    private static final String RECORD_TABLE = "record";
    private static final String RECORD_CREATE = "create table if not exists record("
            + KEY_ROWID
            + " integer primary key autoincrement,"
            + "startpoint STRING,"
            + "endpoint STRING,"
            + "pathline STRING,"
            + "distance STRING,"
            + "duration STRING,"
            + "averagespeed STRING,"
            + "minspeed STRING,"
            + "maxspeed STRING,"
            + "date STRING,"
            + "calorie STRING,"
            + "type STRING" + ");";

    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(RECORD_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // constructor
    public DBAdapter(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getall() {
        return db.rawQuery("SELECT * FROM record", null);
    }

    // remove an entry
    public boolean delete(long rowId) {

        return db.delete(RECORD_TABLE, "id=" + rowId, null) > 0;
    }

    /**
     * 数据库存入一条轨迹
     */
    public void createRecord(String distance, String duration,
                             String averagespeed, String minspeed, String maxspeed, String pathline, String startpoint,
                             String endpoint, String date, String calorie, String type) {
        ContentValues args = new ContentValues();
        args.put("distance", distance);
        args.put("duration", duration);
        args.put("averagespeed", averagespeed);
        args.put("minspeed", minspeed);
        args.put("maxspeed", maxspeed);
        args.put("pathline", pathline);
        args.put("startpoint", startpoint);
        args.put("endpoint", endpoint);
        args.put("date", date);
        args.put("calorie", calorie);
        args.put("type", type);
        db.insert(RECORD_TABLE, null, args);
    }

    /**
     * 查询所有轨迹记录
     */
    public List<PathRecord> queryRecordAll() {
        List<PathRecord> allRecord = new ArrayList<PathRecord>();
        Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), null,
                null, null, null, null);
        while (allRecordCursor.moveToNext()) {
            PathRecord record = new PathRecord();
            record.setId(allRecordCursor.getInt(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_ROWID)));
            record.setDistance(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DISTANCE)));
            record.setDuration(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DURATION)));
            record.setDate(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DATE)));
            String lines = allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_LINE));
            record.setPathLinePoints(DataUtils.parseLocations(lines));
            record.setStartPoint(DataUtils.parseLocation(allRecordCursor
                    .getString(allRecordCursor
                            .getColumnIndex(DBAdapter.KEY_START))));
            record.setEndPoint(DataUtils.parseLocation(allRecordCursor
                    .getString(allRecordCursor
                            .getColumnIndex(DBAdapter.KEY_END))));
            record.setAverageSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_SPEED)));
            record.setMinSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_MINSPEED)));
            record.setMaxSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_MAXSPEED)));
            record.setCalorie(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_CALORIE)));
            record.setType(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_TYPE)));
            allRecord.add(record);
        }
        Collections.reverse(allRecord);
        allRecordCursor.close();
        return allRecord;
    }


    /**
     * 按照date查询
     */
    public PathRecord queryRecordByDate(String mRecordItemDate) {
        String where = KEY_DATE + "=?";
        String[] selectionArgs = new String[] { mRecordItemDate };
        Cursor cursor = db.query(RECORD_TABLE, getColumns(), where,
                selectionArgs, null, null, null);
        PathRecord record = new PathRecord();
        if (cursor.moveToNext()) {
            record.setId(cursor.getInt(cursor
                    .getColumnIndex(DBAdapter.KEY_ROWID)));
            record.setDistance(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_DISTANCE)));
            record.setDuration(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_DURATION)));
            record.setDate(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_DATE)));
            String lines = cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_LINE));
            record.setPathLinePoints(DataUtils.parseLocations(lines));
            record.setStartPoint(DataUtils.parseLocation(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_START))));
            record.setEndPoint(DataUtils.parseLocation(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_END))));
            record.setAverageSpeed(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_SPEED)));
            record.setMinSpeed(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_MINSPEED)));
            record.setMaxSpeed(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_MAXSPEED)));
            record.setCalorie(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_CALORIE)));
            record.setType(cursor.getString(cursor
                    .getColumnIndex(DBAdapter.KEY_TYPE)));
        }
        cursor.close();
        return record;
    }


    public List<PathRecord> queryRecordAllByDateAndType(String date, String type) {
        String where;
        String[] selectionArgs;
        List<PathRecord> allRecord = new ArrayList<>();
        if (type.equals("all")) {
            where = "substr(date(" + KEY_DATE + "),1,7) =?";
            selectionArgs = new String[] { date };
        } else {
            where = "substr(date(" + KEY_DATE + "),1,7) =? and " + KEY_TYPE + " =?";
            selectionArgs = new String[] { date, type };
        }
        Cursor allRecordCursor = db.query(RECORD_TABLE, getColumns(), where,
                selectionArgs, null, null, null);
        while (allRecordCursor.moveToNext()) {
            PathRecord record = new PathRecord();
            record.setId(allRecordCursor.getInt(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_ROWID)));
            record.setDistance(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DISTANCE)));
            record.setDuration(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DURATION)));
            record.setDate(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_DATE)));
            String lines = allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_LINE));
            record.setPathLinePoints(DataUtils.parseLocations(lines));
            record.setStartPoint(DataUtils.parseLocation(allRecordCursor
                    .getString(allRecordCursor
                            .getColumnIndex(DBAdapter.KEY_START))));
            record.setEndPoint(DataUtils.parseLocation(allRecordCursor
                    .getString(allRecordCursor
                            .getColumnIndex(DBAdapter.KEY_END))));
            record.setAverageSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_SPEED)));
            record.setMinSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_MINSPEED)));
            record.setMaxSpeed(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_MAXSPEED)));
            record.setCalorie(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_CALORIE)));
            record.setType(allRecordCursor.getString(allRecordCursor
                    .getColumnIndex(DBAdapter.KEY_TYPE)));
            allRecord.add(record);
        }
        Collections.reverse(allRecord);
        allRecordCursor.close();
        return allRecord;
    }

    private String[] getColumns() {
        return new String[] { KEY_ROWID, KEY_DISTANCE, KEY_DURATION, KEY_SPEED, KEY_MINSPEED,
                KEY_MAXSPEED, KEY_LINE, KEY_START, KEY_END, KEY_DATE, KEY_CALORIE, KEY_TYPE };
    }
}
