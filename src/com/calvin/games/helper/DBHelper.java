package com.calvin.games.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * Created by calvin on 2014/8/3.
 */
public class DBHelper extends SQLiteOpenHelper{
    public DBHelper(Context context) {
        super(context, "download", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table download_info(_id integer PRIMARY KEY AUTOINCREMENT, thread_id integer, "+
                "start_pos integer, end_pos integer, compelete_size integer,url char)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本更新时调用
    }
}















































