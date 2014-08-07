package com.calvin.games.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.calvin.games.bean.DownloadInfoBean;
import com.calvin.games.bean.LoadInfoBean;
import com.calvin.games.dao.Dao;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载核心类
 * Created by calvin on 2014/8/4.
 */
public class Downloader {
    /**下载地址*/
    private String url;
    /**保存路径*/
    private String localfile;
    /**线程数*/
    private int threadcount;
    private Handler mHandler;
    /**所下载文件大小*/
    private int fileSize;
    private Context context;
    private List<DownloadInfoBean> infos;
    /*定义三种状态*/
    private static final int INIT=1;
    private static final int DOWNLOADING=2;
    private static final int PAUSE=3;
    /**记录下载器状态*/
    private int state=INIT;

    public Downloader(String url, String localfile, int threadcount, Handler mHandler,Context context) {
        this.url = url;
        this.localfile = localfile;
        this.threadcount = threadcount;
        this.mHandler = mHandler;
        this.context = context;
    }

    /**
     * 判断是否在下载
     * @return
     */
    public boolean isDownloading(){
        return state==DOWNLOADING;
    }

    public LoadInfoBean getDownloadInfos(){
        if(isFirst(url)){
            //是则初始化下载器
            init();
            /*计算每个线程下载的大小*/
            int range = fileSize / threadcount;
            infos=new ArrayList<DownloadInfoBean>();
            for(int i=0;i<threadcount-1;i++){
                DownloadInfoBean info = new DownloadInfoBean(i, i * range, (i + 1) * range - 1, 0, url);
                infos.add(info);
            }
            //最后一个线程
            DownloadInfoBean info = new DownloadInfoBean(threadcount - 1, (threadcount - 1) * range, fileSize - 1, 0, url);
            infos.add(info);
            //保存infos到数据库中
            Dao.getInstance(context).saveInfos(infos);
            //创建一个LoadInfoBean对象记载下载器的具体信息
            LoadInfoBean loadInfo = new LoadInfoBean(fileSize, 0, url);
            return loadInfo;
        }else{
            //从数据库中得到url下载器的具体信息
            infos=Dao.getInstance(context).getInfos(url);
            int size=0;
            int competeSize=0;
            for(DownloadInfoBean info:infos){
                competeSize+=info.getCompeteSize();
                size+=info.getEndPos()-info.getStartPos()+1;
            }
            return new LoadInfoBean(size,competeSize,url);
        }
    }

    /**
     * 利用线程开始下载
     */
    public void download(){
        if(infos!=null){
            if(state==DOWNLOADING) return;
            state=DOWNLOADING;
            for(DownloadInfoBean info:infos){
                new MyThread(info.getThreadId(),info.getStartPos(),info.getEndPos(),
                        info.getCompeteSize(),info.getUrl()).start();
            }
        }
    }

    public class MyThread extends Thread{
        private int threadId;
        private int startPos;
        private int endPos;
        private int completeSize;
        private String url;

        public MyThread(int threadId, int startPos, int endPos, int completeSize, String url) {
            this.threadId = threadId;
            this.startPos = startPos;
            this.endPos = endPos;
            this.completeSize = completeSize;
            this.url = url;
        }

        @Override
        public void run() {
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            InputStream is=null;
            try {
                URL url1 = new URL(url);
                conn= (HttpURLConnection) url1.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                //设置范围
                conn.setRequestProperty("Range","bytes="+(startPos+completeSize)+"-"+endPos);

                raf=new RandomAccessFile(localfile,"rwd");
                raf.seek(startPos+completeSize);
                //保存下载文件
                is = conn.getInputStream();
                int length=-1;
                byte[] buffer = new byte[4096];
                while((length = is.read(buffer)) != -1){
                    raf.write(buffer,0,length);
                    completeSize+=length;
                    //更新数据库中的下载信息
                    Dao.getInstance(context).updateInfos(threadId,completeSize,url);
                    //用消息将下载进度传给进度条,更新进度
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=url;
                    msg.arg1=length;
                    mHandler.sendMessage(msg);
                    if(state==PAUSE){
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**初始化下载器*/
    private void init() {
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            fileSize=connection.getContentLength();

            File file = new File(localfile);
            if(!file.exists()){
                file.createNewFile();
            }
            //本地文件访问
            RandomAccessFile rwd = new RandomAccessFile(file, "rwd");
            rwd.setLength(fileSize);
            rwd.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是第一次下载
     * @param url
     * @return
     */
    private boolean isFirst(String url){
        return Dao.getInstance(context).isHasInfos(url);
    }

    /**
     * 删除数据库中的记录
     * @param url
     */
    public void delete(String url){
        Dao.getInstance(context).delete(url);
    }

    /**设置暂停*/
    public void pause(){
        state=PAUSE;
    }

    /**重置下载状态*/
     public void reset(){
         state=INIT;
     }
}
