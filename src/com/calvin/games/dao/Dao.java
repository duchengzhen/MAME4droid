package com.calvin.games.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.calvin.games.bean.DownloadInfoBean;
import com.calvin.games.helper.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类(单例模式)
 * Created by calvin on 2014/8/3.
 */
public class Dao {
    private static Dao dao=null;
    private Context context;
    private Dao(Context context){
        this.context=context;
    }

    /**获取dao的实例,没有考虑多线程安全问题*/
    public static Dao getInstance(Context context){
        if(dao==null){
            dao=new Dao(context);
        }
        return dao;
    }

    /**获取一个ReadableDatabase*/
    public SQLiteDatabase getReadableDb(){
        return new DBHelper(context).getReadableDatabase();
    }

    /**获取一个WritableDatabase*/
    public SQLiteDatabase getWritableDb(){
        return new DBHelper(context).getWritableDatabase();
    }

    /**向数据库中插入下载信息*/
    public synchronized void saveInfos(List<DownloadInfoBean> infos){
        SQLiteDatabase database = getWritableDb();
        String sql="insert into download_info(thread_id,start_pos, end_pos,compelete_size,url) values (?,?,?,?,?)";
        for(DownloadInfoBean info:infos){
            Object[] bindArgs={info.getThreadId(),info.getStartPos(),info.getEndPos(),info.getCompeteSize(),info.getUrl()};
            database.execSQL(sql,bindArgs);
        }
        database.close();
    }

    /**
     * 查询是否有数据
     * @param url 下载url
     * @return
     */
    public boolean isHasInfos(String url){
        SQLiteDatabase db = getReadableDb();
        int count=-1;
        String sql="select count(*)  from download_info where url=?";
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        if(cursor.moveToNext()){
            count=cursor.getInt(0);
        }
        close(db,cursor);
        //todo ???
        return count==0;
    }

    /**
     * 获取下载具体信息
     * @param url
     * @return
     */
    public synchronized List<DownloadInfoBean> getInfos(String url){
        List<DownloadInfoBean> list=new ArrayList<DownloadInfoBean>();
        SQLiteDatabase db = getReadableDb();
        String sql="select thread_id, start_pos, end_pos,compelete_size,url from download_info where url=?";
        Cursor cursor = db.rawQuery(sql, new String[]{url});
        while (cursor.moveToNext()){
            DownloadInfoBean info = new DownloadInfoBean(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4));
            list.add(info);
        }
        close(db,cursor);
        return list;
    }

    /**
     * 更新下载信息(那个线程url下载到哪个size)
     * @param threadId
     * @param completeSize
     * @param url
     */
    public synchronized void updateInfos(int threadId,int completeSize,String url){
        SQLiteDatabase db = getWritableDb();
        String sql="update download_info set compelete_size=? where thread_id=? and url=?";
        db.execSQL(sql,new Object[]{completeSize,threadId,url});
        close(db,null);
    }


    /**
     * 删除下载信息
     * @param url
     */
    public synchronized void delete(String url){
        SQLiteDatabase db = getWritableDb();
        db.delete("download_info","url=?",new String[]{url});
        close(db,null);
    }
    /**
     * 关闭db或cursor
     * @param db
     * @param cursor
     */
    public static void close(SQLiteDatabase db,Cursor cursor){
        if(db!=null) db.close();
        if(cursor!=null) cursor.close();
    }
}









































































