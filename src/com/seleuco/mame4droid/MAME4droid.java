package com.seleuco.mame4droid;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.calvin.games.R;
import com.seleuco.mame4droid.helpers.DialogHelper;
import com.seleuco.mame4droid.helpers.MainHelper;
import com.seleuco.mame4droid.helpers.MenuHelper;
import com.seleuco.mame4droid.helpers.PrefsHelper;
import com.seleuco.mame4droid.input.ControlCustomizer;
import com.seleuco.mame4droid.input.InputHandler;
import com.seleuco.mame4droid.input.InputHandlerExt;
import com.seleuco.mame4droid.input.InputHandlerFactory;
import com.seleuco.mame4droid.views.FilterView;
import com.seleuco.mame4droid.views.IEmuView;
import com.seleuco.mame4droid.views.InputView;

import android.app.*;
import android.content.*;

final class NotificationHelper
{
        private static NotificationManager notificationManager = null;

		public static void addNotification(Context ctx, String onShow, String title, String message)
        {
                if(notificationManager == null)
                        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                int icon = R.drawable.icon_sb; // TODO: don't hard-code
                long when = System.currentTimeMillis();
                Notification notification = new Notification(icon, /*onShow*/null, when);
                notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
                CharSequence contentTitle = title;
                CharSequence contentText = message;
                Intent notificationIntent = new Intent(ctx, MAME4droid.class);
                PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);

                notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
                notificationManager.notify(1, notification);
        }
       
        public static void removeNotification()
        {
                if(notificationManager != null)
                        notificationManager.cancel(1);
        }
}

public class MAME4droid extends Activity {

	protected View emuView = null;

	protected InputView inputView = null;
	
	protected FilterView filterView = null;
	
	protected MainHelper mainHelper = null;
	protected MenuHelper menuHelper = null;
	protected PrefsHelper prefsHelper = null;
	protected DialogHelper dialogHelper = null;
	
	protected InputHandler inputHandler = null;
	
	protected FileExplorer fileExplore = null;
	
	protected NetPlay netPlay = null;
	
	public NetPlay getNetPlay() {
		return netPlay;
	}

	public FileExplorer getFileExplore() {
		return fileExplore;
	}

	public MenuHelper getMenuHelper() {
		return menuHelper;
	}
    	
    public PrefsHelper getPrefsHelper() {
		return prefsHelper;
	}
    
    public MainHelper getMainHelper() {
		return mainHelper;
	}
    
    public DialogHelper getDialogHelper() {
		return dialogHelper;
	}
    
	public View getEmuView() {
		return emuView;
	}
	
	public InputView getInputView() {
		return inputView;
	}

	public FilterView getFilterView() {
		return filterView;
	}
	
    public InputHandler getInputHandler() {
		return inputHandler;
	}
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Log.d("EMULATOR", "onCreate "+this);
		
		overridePendingTransition(0, 0);
		getWindow().setWindowAnimations(0);
		
		prefsHelper = new PrefsHelper(this);

        dialogHelper  = new DialogHelper(this);
        
        mainHelper = new MainHelper(this);
                             
        fileExplore = new FileExplorer(this);
        
        netPlay = new NetPlay(this);
                
        menuHelper = new MenuHelper(this);
                
        inputHandler = InputHandlerFactory.createInputHandler(this);
        
        mainHelper.detectDevice();
        
        Emulator.setPortraitFull(getPrefsHelper().isPortraitFullscreen());
        boolean full = false;
		if(prefsHelper.isPortraitFullscreen() && mainHelper.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT)
		{
			setContentView(R.layout.main_fullscreen);
			full = true;
		}
		else 
		{
            setContentView(R.layout.main);
		}        
                
        FrameLayout fl = (FrameLayout)this.findViewById(R.id.EmulatorFrame);
        
        
        //Coment to avoid BUG on 2.3.4 (reload instead)
        Emulator.setVideoRenderMode(getPrefsHelper().getVideoRenderMode());
        
        this.getLayoutInflater().inflate(R.layout.netplayview, fl);
        View v = this.findViewById(R.id.netplay_view);
        if(v!=null)
        	v.setVisibility(View.GONE);
        
        if(prefsHelper.getVideoRenderMode()==PrefsHelper.PREF_RENDER_SW)
        {
        	this.getLayoutInflater().inflate(R.layout.emuview_sw, fl);
        	emuView = this.findViewById(R.id.EmulatorViewSW);        
        }
        else 
        { 
        	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN  && prefsHelper.getNavBarMode()!=PrefsHelper.PREF_NAVBAR_VISIBLE)
        	    this.getLayoutInflater().inflate(R.layout.emuview_gl_ext, fl);
        	else
        		this.getLayoutInflater().inflate(R.layout.emuview_gl, fl);
    		
        	emuView = this.findViewById(R.id.EmulatorViewGL);
        	
        }
        
        if(full && prefsHelper.isPortraitTouchController())
        {
        	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams )emuView.getLayoutParams();
        	lp.gravity =  Gravity.TOP | Gravity.CENTER;
        }
                       
        inputView = (InputView) this.findViewById(R.id.InputView);
                
        ((IEmuView)emuView).setMAME4droid(this);

        inputView.setMAME4droid(this);
        
        Emulator.setMAME4droid(this);        
            
        View frame = this.findViewById(R.id.EmulatorFrame);
	    frame.setOnTouchListener(inputHandler);        	
        	    
        if((prefsHelper.getPortraitOverlayFilterValue()!=PrefsHelper.PREF_OVERLAY_NONE && mainHelper.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT)
        		||
           (prefsHelper.getLandscapeOverlayFilterValue()!=PrefsHelper.PREF_OVERLAY_NONE && mainHelper.getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE))
        {	
        	String value;
            
            if(mainHelper.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT)
            	value = prefsHelper.getPortraitOverlayFilterValue();
            else
            	value = prefsHelper.getLandscapeOverlayFilterValue();
           
            if(!value.equals(PrefsHelper.PREF_OVERLAY_NONE))
            {
	        	getLayoutInflater().inflate(R.layout.filterview, fl);
	            filterView = (FilterView)this.findViewById(R.id.FilterView);
	            
	            String fileName = getPrefsHelper().getROMsDIR()+File.separator+"overlays"+File.separator+value;
	            
	            Bitmap bmp = BitmapFactory.decodeFile(fileName);
	            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
	            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
	            
	            int alpha = 0;	            
		   		switch(getPrefsHelper().getEffectOverlayIntensity())
			    {
			       case 1: alpha = 25;break;
			       case 2: alpha = 50;break;
			       case 3: alpha = 55;break;
			       case 4: alpha = 60;break;
			       case 5: alpha = 65;break;
			       case 6: alpha = 70;break;
			       case 7: alpha = 75;break;
			       case 8: alpha = 80;break;
			       case 9: alpha = 100;break;
			       case 10: alpha = 125;break;			       
                }
            
	            bitmapDrawable.setAlpha(alpha);
	            filterView.setBackgroundDrawable(bitmapDrawable);
	            
	            //this.getEmuView().setAlpha(250);
		            
	            if(full && prefsHelper.isPortraitTouchController())
	            {
	            	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams )filterView.getLayoutParams();
	            	lp.gravity =  Gravity.TOP | Gravity.CENTER;
	            }
	            
	            filterView.setMAME4droid(this);
            }
      
        }
                
        inputHandler.setInputListeners();
        
        mainHelper.updateMAME4droid();
               
        if(!Emulator.isEmulating())
        {
			if(prefsHelper.getROMsDIR()==null)
			{	            
				if(DialogHelper.savedDialog==DialogHelper.DIALOG_NONE)
				   showDialog(DialogHelper.DIALOG_ROMs_DIR);                      
			}
			else
			{
				getMainHelper().ensureROMsDir(prefsHelper.getROMsDIR());
				runMAME4droid();	
			}
        }
    }
        
    public void runMAME4droid(){  	
	    getMainHelper().copyFiles();
	    getMainHelper().removeFiles();
    	Emulator.emulate(mainHelper.getLibDir(),prefsHelper.getROMsDIR());	
    }
     
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		overridePendingTransition(0 , 0);
		this.getMainHelper().updateMAME4droid();
		//this.getMainHelper().reload();
		overridePendingTransition(0 , 0);
	}

	//MENU STUFF
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		
		if(menuHelper!=null)
		{
		   if(menuHelper.createOptionsMenu(menu))return true;
		}  
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(menuHelper!=null)
		{	
		   if(menuHelper.prepareOptionsMenu(menu)) return true;
		}   
		return super.onPrepareOptionsMenu(menu); 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(menuHelper!=null)
		{
		   if(menuHelper.optionsItemSelected(item))
			   return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//ACTIVITY
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(mainHelper!=null)
		   mainHelper.activityResult(requestCode, resultCode, data);
	}
	
	//LIVE CYCLE
	@Override
	protected void onResume() {
		Log.d("EMULATOR", "onResume");				
		super.onResume();
		if(prefsHelper!=null)
		   prefsHelper.resume();
				
		if(DialogHelper.savedDialog!=-1)
			showDialog(DialogHelper.savedDialog);
		else if(!ControlCustomizer.isEnabled())
		  Emulator.resume();
		
		if(inputHandler!= null)
		{
			if(inputHandler.getTiltSensor()!=null)
			   inputHandler.getTiltSensor().enable();
		}
		
		NotificationHelper.removeNotification();
		//System.out.println("OnResume");
	}
	
	@Override
	protected void onPause() {
		Log.d("EMULATOR", "onPause");
		super.onPause();
		if(prefsHelper!=null)
		   prefsHelper.pause();
		if(!ControlCustomizer.isEnabled())		
		   Emulator.pause();
		if(inputHandler!= null)
		{
			if(inputHandler.getTiltSensor()!=null)
			   inputHandler.getTiltSensor().disable();
		}	
		
		if(dialogHelper!=null)
		{
			dialogHelper.removeDialogs();
		}
		
		if(prefsHelper.isNotifyWhenSuspend()) 
		  NotificationHelper.addNotification(getApplicationContext(), "MAME4droid was suspended!", "MAME4droid was suspended", "Press to return to MAME4droid");
		
		//System.out.println("OnPause");
	}
	
	@Override
	protected void onStart() {
		Log.d("EMULATOR", "onStart");		
		super.onStart();
		try{InputHandlerExt.resetAutodetected();}catch(Error e){};		
		//System.out.println("OnStart");
	}

	@Override
	protected void onStop() {
		Log.d("EMULATOR", "onStop");
		super.onStop();
		//System.out.println("OnStop");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("EMULATOR", "onNewIntent");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("EMULATOR", "onDestroy "+this);
				
        View frame = this.findViewById(R.id.EmulatorFrame);
	    if(frame!=null)
           frame.setOnTouchListener(null); 
	    
		if(inputHandler!= null)
		{
		   inputHandler.unsetInputListeners();
		   
			if(inputHandler.getTiltSensor()!=null)
				   inputHandler.getTiltSensor().disable();
		}
			
        if(emuView!=null)
		   ((IEmuView)emuView).setMAME4droid(null);

        /*
        if(inputView!=null)
           inputView.setMAME4droid(null);
        
        if(filterView!=null)
           filterView.setMAME4droid(null);
                       
        prefsHelper = null;
        
        dialogHelper = null;
        
        mainHelper = null;
        
        fileExplore = null;
        
        menuHelper = null;
        
        inputHandler = null;
        
        inputView = null;
        
        emuView = null;
        
        filterView = null; */     	    
	}	
		

	//Dialog Stuff
	@Override
	protected Dialog onCreateDialog(int id) {

		if(dialogHelper!=null)
		{	
			Dialog d = dialogHelper.createDialog(id);
			if(d!=null)return d;
		}
		return super.onCreateDialog(id);		
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(dialogHelper!=null)
		   dialogHelper.prepareDialog(id, dialog);
	}
        
}