package com.calvin.games.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.calvin.games.R;
import com.calvin.games.adapter.MainAdapter;
import com.calvin.games.bean.GameBean;
import com.calvin.games.common.Downloader;
import com.calvin.games.view.NumberProgressBar;

import com.crashlytics.android.Crashlytics;
import com.seleuco.mame4droid.MAME4droid;

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
            game.setUrl("http://172.30.66.19/1Q2W3E4R5T6Y7U8I9O0P1Z2X3C4V5B/fd1.yingyonghui.com/d762e34ee5f933f3cecc58bcd1083dff/53e3b0df/apk/2136048/com.roamingsoft.manager.1405848887678.apk");
            games.add(game);
        }
        adapter=new MainAdapter(this,games);
        lvMain.setAdapter(adapter);

        //lvMain.setOnItemClickListener();

    }

    public void start(View view){
        startActivity(new Intent(this, MAME4droid.class));
    }
}







































