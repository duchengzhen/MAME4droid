package com.calvin.games.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.calvin.games.R;
import com.calvin.games.bean.GameBean;
import com.calvin.games.bean.LoadInfoBean;
import com.calvin.games.common.Downloader;
import com.calvin.games.view.NumberProgressBar;
import com.calvin.games.view.RoundAngleImageView;

import java.io.File;
import java.util.*;

/**
 * 主页adpater
 * Created by calvin on 2014/8/3.
 */
public class MainAdapter extends BaseAdapter {

    private Context mContext;
    private List<GameBean> games;
    //存放各个下载器
    private Map<String,Downloader> downloaders=new HashMap<>();
    //存放于下载器对应的进度条
    private Map<String,NumberProgressBar> progressBars=new HashMap<>();

    private HoldView hold;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                String url= (String) msg.obj;
                int length=msg.arg1;
                NumberProgressBar bar = progressBars.get(url);
                if(bar!=null){
                    //进度条读取的length长度更新
                    bar.incrementProgressBy(length);
                    System.out.println(length+"已下载");
                    if(bar.getProgress()==bar.getMax()){
                        //下载完成后清除进度条并将map中数据清空
                        progressBars.remove(url);
                        downloaders.get(url).delete(url);
                        downloaders.get(url).reset();
                        downloaders.remove(url);
                    }
                }
            }
        }
    };

    public MainAdapter(Context mContext,List<GameBean> games) {
        this.mContext = mContext;
        this.games=games;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public Object getItem(int position) {
        return games.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameBean game=games.get(position);
        String url=game.getUrl();
        //View convertView;
        if(convertView==null){
            hold =new HoldView();
            convertView = View.inflate(mContext, R.layout.main_item, null);
            hold.ivCover= (RoundAngleImageView) convertView.findViewById(R.id.raiv_cover);
            hold.tvName= (TextView) convertView.findViewById(R.id.tv_name);
            hold.tvCount= (TextView) convertView.findViewById(R.id.tv_count);
            hold.tvDesc= (TextView) convertView.findViewById(R.id.tv_desc);
            hold.tvSize= (TextView) convertView.findViewById(R.id.tv_size);
            hold.npbDownload= (NumberProgressBar) convertView.findViewById(R.id.npb_download);
            hold.btnInstall= (Button) convertView.findViewById(R.id.btn_install);
            convertView.setTag(hold);
        }else{
            hold = (HoldView) convertView.getTag();
        }
        hold.ivCover.setImageResource(R.drawable.cover_demo);
        hold.tvName.setText(game.getName());
        hold.tvSize.setText(game.getSize());
        hold.tvCount.setText(game.getCount());
        hold.tvDesc.setText(game.getDescription());

        hold.btnInstall.setOnClickListener(new DlListener(url));

        return convertView;
    }

    private class MnDownload extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            hold.npbDownload.setMax(100);
            int i=0;
            while(i<=100){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                publishProgress(i);
            }
            return params[0];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            hold.npbDownload.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
        }
    }

    private class DlListener implements View.OnClickListener{
        //int postion;
        String url;

        private DlListener(String url) {
            this.url=url;
        }

        @Override
        public void onClick(View v) {
            //String url=games.get(postion).getUrl();
            //线程数
            /*String threadCount="3";
            //下载到路径
            String localFile= Environment.getExternalStorageDirectory()+ File.separator+"dl.apk";
            DownloadTask downloadTask = new DownloadTask(v,url);
            downloadTask.execute(localFile,threadCount);*/
            MnDownload task = new MnDownload();
            task.execute("下载完毕");

        }
    }

    class DownloadTask extends AsyncTask<String,Integer,LoadInfoBean>{

        Downloader downloader=null;
        View v=null;
        String url=null;
        //int position;
        public DownloadTask(final View v,String url){
            this.v=v;
            //this.position=position;
            this.url=url;
        }

        @Override//执行前调用,UI线程中
        protected void onPreExecute() {
            Button btnDownloading= (Button) ((View)v.getParent()).findViewById(R.id.btn_downloading);
            v.setVisibility(View.GONE);
            btnDownloading.setVisibility(View.VISIBLE);
        }

        @Override
        protected LoadInfoBean doInBackground(String... params) {
            String localFile=params[0];
            int threadCount= Integer.parseInt(params[1]);
            //初始化downloader下载器
            downloader=downloaders.get(url);
            if(downloader==null){
                downloader=new Downloader(url,localFile,threadCount,mHandler,mContext);
                downloaders.put(url,downloader);
            }
            if(downloader.isDownloading())
                return null;
            //得到下载类信息个数组成的集合
            return downloader.getDownloadInfos();
        }

        @Override
        protected void onPostExecute(LoadInfoBean loadInfoBean) {
            if(loadInfoBean!=null){
                //显示进度
                showProcess(loadInfoBean,url,v);
                //开始下载
                downloader.download();
            }
        }
    }

    private void showProcess(LoadInfoBean loadInfoBean, String url, View v) {
        NumberProgressBar bar=progressBars.get(url);
        if(bar==null){
            bar=hold.npbDownload;
            bar.setMax(loadInfoBean.getFileSize());
            bar.setProgress(loadInfoBean.getComplete());
            progressBars.put(url,bar);
        }
    }

    public void pauseDownload(View v,int postion){
        downloaders.get(games.get(postion)).pause();

    }


    private class HoldView{
        RoundAngleImageView ivCover;
        TextView tvName;
        TextView tvCount;
        TextView tvSize;
        TextView tvDesc;
        NumberProgressBar npbDownload;
        //下载按钮
        Button btnInstall;
    }
}



















































