package com.calvin.games.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.calvin.games.R;
import com.seleuco.mame4droid.MAME4droid;

/**
 * 主界面
 * Created by calvin on 2014/7/23.
 */
public class MainActivity extends Activity{
    private Button btnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
