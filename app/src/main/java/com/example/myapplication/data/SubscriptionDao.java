package com.example.myapplication.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubscriptionDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private static final String TAG = "SubscriptionDao";
    private final XimalayaDBHelper mXimalayaDBHelper;
    private ISubscritionDaoCallback mcallback=null;

    public static SubscriptionDao getInstance() {

        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());


    }

    @Override
    public void setCallback(ISubscritionDaoCallback callback) {
        mcallback=callback;
    }

    @Override
    public synchronized  void addAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isSuccess=false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues=new ContentValues();
            contentValues.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_SMALL_COVER_URL,album.getCoverUrlSmall());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            //插入数据
            long insert = db.insert(Constants.SUB_TB_NAME, null, contentValues);
            LogUtil.d(TAG,"success-->"+insert);
            db.setTransactionSuccessful();
            db.endTransaction();
            isSuccess=true;

        }catch (Exception e) {
               e.printStackTrace();
               isSuccess=false;
        }finally {
            if (db != null) {
                db.close();
            }
            if (mcallback != null) {
                mcallback.OnaddResult(isSuccess);
            }
        }

    }

    @Override
    public synchronized void delAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isSuccess=false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            //插入数据
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.d(TAG,"删除数据"+delete+"条");
            db.setTransactionSuccessful();
            db.endTransaction();
            isSuccess=true;
        }catch (Exception e) {
            e.printStackTrace();
            isSuccess=false;
        }finally {
            if (db != null) {
                db.close();
            }
            if (mcallback != null) {
                mcallback.OndelResult(isSuccess);
            }
        }

    }

    @Override
    public synchronized void listAlbum() {
        SQLiteDatabase db=null;
        List<Album> result=new ArrayList<>();
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null, null,null);

            while (query.moveToNext()) {
                Album album=new Album();
                String coverURL = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverURL);
                String smallCoverURL = query.getString(query.getColumnIndex(Constants.SUB_SMALL_COVER_URL));
                album.setCoverUrlSmall(smallCoverURL);
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);
                int tracks = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracks);
                String authorNmae = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer=new Announcer();
                announcer.setNickname(authorNmae);
                album.setAnnouncer(announcer);
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(albumId);
                result.add(album);

            }
            db.setTransactionSuccessful();
            db.endTransaction();
           query.close();
            db.endTransaction();

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.close();
            }
            if (mcallback != null) {
                mcallback.OnSubListLoaded(result);
            }
        }
    }
}
