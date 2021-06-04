package com.example.myapplication.utils;

public class Constants {


    //获取专辑数量
    public static int RECOMMEND_COUNT=50;

    //默认获取专辑列表的数量
    public static int COUNT_DEFAULT=50;

    //热词长度
    public static int COUNT_HOT_WORED=20;

    public static String ERROR_TYPE_HOT_WORD="hotWord";

    public static String ERROR_TYPE_SEARCH="toSearch";


    //数据库相关的常量
    public static final String DB_NAME="ximalaya.db";

    //版本号
    public static  final int DB_VERSION_CODE=1;

    //订阅表
    public static final String SUB_TB_NAME="tb_subscription";
    public static final String SUB_ID="_id";
    public static final String SUB_COVER_URL ="coverUrl";
    public static final String SUB_TITLE="title";
    public static final String SUB_DESCRIPTION="description";
    public static final String SUB_PLAY_COUNT ="playCount";
    public static final String SUB_TRACKS_COUNT ="tracksCount";
    public static final String SUB_AUTHOR_NAME ="authorName";
    public static final String SUB_ALBUM_ID ="albumId";
    public static final String SUB_SMALL_COVER_URL ="smallCoverUrl";
    //订阅最多个数
    public static final int SUB_MAX_COUNT =5;
    //历史最大数量
    public static final int HIS_MAX_COUNT =100;

    //历史表
    public static final String HISTORY_TB_NAME="tb_history";
    public static final String HISTORY_TB_ID="_id";
    public static final String HISTORY_TRACK_ID="history_track_id";
    public static final String HISTORY_TB_TITLE="history_title";
    public static final String HISTORY_TB_PLAY_COUNT="history_play_count";
    public static final String HISTORY_TB_PLAY_DURATION="history_play_duration";
    public static final String HISTORY_TB_UPDATE_TIME="history_update_time";
    public static final String HISTORY_TB_LARGE_COVER="history_large_cover";
    public static final String HISTORY_TB_MIDDLE_COVER ="history_Middle_cover";
    public static final String HISTORY_TB_AUTHOR="history_author";






}
