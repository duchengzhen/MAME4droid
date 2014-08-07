package com.calvin.games.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.calvin.games.R;
import com.calvin.games.adapter.MainAdapter;
import com.calvin.games.bean.GameBean;
import com.calvin.games.common.Downloader;
import com.calvin.games.view.NumberProgressBar;

import com.crashlytics.android.Crashlytics;

import java.util.*;

/**
 * 主界面
 * Created by calvin on 2014/7/23.
 */
public class MainActivity extends Activity{
    //适配器
    private MainAdapter adapter;
    private List<GameBean> games;

    private ListView lvMain;    //主listview
    //下载地址
    public static final String URL="http://";
    //下载路径
    public static final String PATH="";
    //存放下载器
    private Map<String,Downloader> downloaders=new HashMap<>();
    //存放与下载器对应的进度条
    private Map<String,NumberProgressBar> progressbars=new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        lvMain= (ListView) findViewById(R.id.lv_main);

        games=new ArrayList<GameBean>();
        for(int i=0;i<20;i++){
            GameBean game=new GameBean();
            game.setCount(getString(R.string.count_demo));
            game.setName(getString(R.string.name_demo));
            game.setDescription(getString(R.string.game_desc));
            game.setSize(getString(R.string.size_demo));
            //game.setUrl("http://gdown.baidu.com/data/wisegame/d4cdacae4dbc737c/buyudaren3_100.apk");
            game.setUrl("http://www.appchina.com/market/d/2136048/cop.baidu_0/com.roamingsoft.manager.apk");
            games.add(game);
        }
        adapter=new MainAdapter(this,games);
        lvMain.setAdapter(adapter);

        //lvMain.setOnItemClickListener();

    }

}







































