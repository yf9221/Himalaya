package com.example.myapplication.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHisttoryDao{


    private final XimalayaDBHelper mXimalayaDBHelper;
    private IHistoryDaoCallbak mCallback=null;


    public HistoryDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallbak(IHistoryDaoCallbak iHistoryDaoCallbak) {
        this.mCallback=iHistoryDaoCallbak;

    }

    @Override
    public void addHistory(Track track) {
        SQLiteDatabase db;
        db = mXimalayaDBHelper.getWritableDatabase();
        boolean isSuccess=false;
        try {
            db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});
            db.beginTransaction();
            ContentValues values=new ContentValues();
            values.put(Constants.HISTORY_TRACK_ID,track.getDataId());
            values.put(Constants.HISTORY_TB_TITLE,track.getTrackTitle());
            values.put(Constants.HISTORY_TB_PLAY_COUNT,track.getPlayCount());
            values.put(Constants.HISTORY_TB_PLAY_DURATION,track.getDuration());
            values.put(Constants.HISTORY_TB_UPDATE_TIME,track.getUpdatedAt());
            values.put(Constants.HISTORY_TB_LARGE_COVER,track.getCoverUrlLarge());
            values.put(Constants.HISTORY_TB_MIDDLE_COVER,track.getCoverUrlMiddle());
            values.put(Constants.HISTORY_TB_AUTHOR,track.getAnnouncer().getNickname());
            db.insert(Constants.HISTORY_TB_NAME,null,values);
            db.setTransactionSuccessful();
            db.endTransaction();
            isSuccess=true;

        }catch (Exception e)

        {
            e.printStackTrace();
            isSuccess=false;
        }
        finally {
            if (db != null) {
                db.close();

            }
            if (mCallback != null) {
                mCallback.onHistoryadd(isSuccess);
            }
        }

    }

    @Override
    public void delHistory(Track tarck) {
        SQLiteDatabase db;
        db = mXimalayaDBHelper.getWritableDatabase();
        boolean isSuccess=false;
        try {
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{tarck.getDataId()+""});
            db.setTransactionSuccessful();
            db.endTransaction();
            isSuccess=true;

        }catch (Exception e)

        {
            e.printStackTrace();
            isSuccess=false;
        }
        finally {
            if (db != null) {
                db.close();

            }
            if (mCallback != null) {
                mCallback.onHistorydel(isSuccess);
            }
        }
    }



    @Override
    public void clearnHistory() {
        SQLiteDatabase db;
        db = mXimalayaDBHelper.getWritableDatabase();
        boolean isSuccess=false;
        try {
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME,null,null);
            db.setTransactionSuccessful();
            db.endTransaction();
            isSuccess=true;

        }catch (Exception e)

        {
            e.printStackTrace();
            isSuccess=false;
        }
        finally {
            if (db != null) {
                db.close();

            }
            if (mCallback != null) {
                mCallback.onHistorydel(isSuccess);
            }
        }
    }

    @Override
    public void listHistories() {
        SQLiteDatabase db;
        db = mXimalayaDBHelper.getWritableDatabase();
        List<Track> tracks=new ArrayList<>();
        try {
            db.beginTransaction();
            Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
            while (cursor.moveToNext()){
                Track track=new Track();
                int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                track.setDataId(trackId);
                String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TB_TITLE));
                track.setTrackTitle(title);
                int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TB_PLAY_COUNT));
                track.setPlayCount(playCount);
                int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TB_PLAY_DURATION));
                track.setDuration(duration);
                long updateTime = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TB_UPDATE_TIME));
                track.setUpdatedAt(updateTime);
                String smallCover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TB_LARGE_COVER));
                track.setCoverUrlLarge(smallCover);
                String middleCover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TB_MIDDLE_COVER));
                track.setCoverUrlMiddle(middleCover);
                String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TB_AUTHOR));
                Announcer announcer=new Announcer();
                announcer.setNickname(author);
                track.setAnnouncer(announcer);

                tracks.add(track);

            }
            db.setTransactionSuccessful();
            db.endTransaction();

        }catch (Exception e)

        {
            e.printStackTrace();
         }
        finally {
            if (db != null) {
                db.close();

            }
            if (mCallback != null) {
                mCallback.onHistoryListLoaded(tracks);
            }
        }
    }


}
