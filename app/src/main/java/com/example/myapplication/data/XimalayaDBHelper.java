package com.example.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;

public class XimalayaDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "XimalayaDBHelper";

    public  XimalayaDBHelper(Context context) {
        //name数据库名字 ,factory游标工厂,version版本

        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG,"XimalayaDBHelper onCreate");
        //创建数据库
        //订阅相关的字段
        String subTbSql="create table "+Constants.SUB_TB_NAME+"("+
                Constants.SUB_ID+" integer primary key autoincrement,"+
                Constants.SUB_COVER_URL + " varchar," +
                Constants.SUB_SMALL_COVER_URL + " varchar," +
                Constants.SUB_TITLE+" varchar," +
                Constants.SUB_DESCRIPTION+ " varchar," +
                Constants.SUB_PLAY_COUNT +" integer," +
                Constants.SUB_TRACKS_COUNT +" integer," +
                Constants.SUB_AUTHOR_NAME +" varchar," +
                Constants.SUB_ALBUM_ID +" integer" +
                ")";
        db.execSQL(subTbSql);


        String historyTbsql="create table "+Constants.HISTORY_TB_NAME+" ("+
                Constants.HISTORY_TB_ID+" integer primary key autoincrement,"+
                Constants.HISTORY_TRACK_ID+" integer,"+
                Constants.HISTORY_TB_TITLE+" varchar,"+
                Constants.HISTORY_TB_LARGE_COVER+" varchar,"+
                Constants.HISTORY_TB_MIDDLE_COVER +" varchar,"+
                Constants.HISTORY_TB_AUTHOR+" varchar,"+
                Constants.HISTORY_TB_PLAY_COUNT+" integer,"+
                Constants.HISTORY_TB_PLAY_DURATION+" integer,"+
                Constants.HISTORY_TB_UPDATE_TIME+" integer"+
                ")";
        db.execSQL(historyTbsql);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
