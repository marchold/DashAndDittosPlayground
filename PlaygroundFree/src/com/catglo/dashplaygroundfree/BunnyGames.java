package com.catglo.dashplaygroundfree;

//things to finish:

//- driving - remove th bunny from news paper game
//                  fractional display
//- buny cage - colors 
//                   -  fractional display
 
//                    
//*****Bus driving - I stopped after a few friends had been picked up and the next time I played the road started as a wide road, I think I magic hat restarted the game


//There still seems to be a synchronization issue in jump rope where the jumper can get stuck out there, surly a race condition.



//- verify - Golf - we get stuck in a weird mode when the ball goes down the hole-supress actions atays on!
//fixed this- needs verification- babyBunnyRect is where they are going, this causes a problem where a tap on nothing brings up baby menu
//- look for leaks in monkey long run - add code to check for leaks in lists of actions and animations

// after running monkey went back to bus driving and the bunny did not move (maaybe supressActions was on?)

//- maybe fixed - Jumprope = the jumper can be drawn 2 times again!! 



//BUG related to bigNote line 262 of busDriving - does this still happen - YES it did and I made 
//    bigNote part of the bitmapCache, if we get a crash due to road segments missing, then it means
//    that there is a problem with pause, and resume not getting called equally

//Cant-Repro- its possible for the hat to stay big
//we got a baby menu in a screen with no babies in bus driving  



//Observed bugs with no clear fix
//TODO: Cant-Repro- hopscotch - ball can get stuck on a number - this seemed to happen when zan somehow triggered some action while the bunny was hopscotching
//bug?: Cant-Repro-There seems to be a way to get start zoom in to start a 2nd time in bus driving
//TODO: Cant-Repro- bus driving can get in to a state where the show note is visible but not the streering wheel. Monkey found this.

//features that would be good
//Switch Screen With fling: process on got there handlers from gesture moves so we can swtich from screen to screen with fling the bunny
//make babyBunnies avoid mainBunny
//Improve Driving Controles: Make bus steering from point move twards the users finger.
//start button for feed the bunny with countdown timer, and score, and start button.
//The baby bunny cage door needs to open to let the baby bunny in
//The baby bunnies should clap and jump when you start to do well at a game
//The friend and babies should avoid the hopscotch board
//Enable gestures double tap, triple tap, tap and fling, for various bunny dance moves
//The friend and babies should start to minic dance moves after main bunny does them 2 times
//double  tap cancles main bunny animation, to restart hopscothch, golf, or tic-tac-toe without waiting
//Babies should follow closer: when you press follow the babies lag behind quite a bit
//bus driving smooth scrolling textured background in driving mode.


/*// CRASH: com.catglo.dashplaygroundfree (pid 3355)
// Short Msg: bitmap size exceeds VM budget
// Long Msg: java.lang.OutOfMemoryError: bitmap size exceeds VM budget
// Build Label: android:tmobile/kila/dream/trout:1.6/DMD64/21415:user/ota-rel-keys,release-keys
// Build Changelist: 21415
// Build Time: 1259893752
// ID: 
// Tag: AndroidRuntime
// java.lang.OutOfMemoryError: bitmap size exceeds VM budget
//   at android.graphics.Bitmap.nativeCopy(Bitmap.java:-2)
//   at android.graphics.Bitmap.copy(Bitmap.java:311)
//   at com.catglo.dashplaygroundfree.BusDrivingGame.startShowNote(BusDrivingGame.java:367)
//   at com.catglo.dashplaygroundfree.BusDrivingGame$2.run(BusDrivingGame.java:439)
//   at com.catglo.dashplaygroundfree.DrawingView.run(DrawingView.java:210)
//   at com.catglo.dashplaygroundfree.DrawingView$DrawingThread.run(DrawingView.java:407)

** Monkey aborted due to error.
Events injected: 6693
## Network stats: elapsed time=293655ms (0ms mobile, 293655ms wifi, 0ms not connected)
** System appears to have crashed at event 6693 of 10000000 using seed 0

*
*
*
*
*
*
* java.lang.ArrayIndexOutOfBoundsException: 
//   at com.catglo.dashplaygroundfree.BunnyGames$1.run(BunnyGames.java:301)
//   at android.os.Handler.handleCallback(Handler.java:587)
//   at android.os.Handler.dispatchMessage(Handler.java:92)
//   at android.os.Looper.loop(Looper.java:123)
//   at android.app.ActivityThread.main(ActivityThread.java:4203)
//   at java.lang.reflect.Method.invokeNative(Method.java:-2)
//   at java.lang.reflect.Method.invoke(Method.java:521)
//   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
//   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
//   at dalvik.system.NativeStart.main(NativeStart.java:-2)


// ID: 
// Tag: AndroidRuntime
// java.lang.NullPointerException: 
//   at com.catglo.dashplaygroundfree.BusDrivingGame.startShowNote(BusDrivingGame.java:313)
//   at com.catglo.dashplaygroundfree.BusDrivingGame$2.run(BusDrivingGame.java:439)
//   at com.catglo.dashplaygroundfree.DrawingView.run(DrawingView.java:210)
//   at com.catglo.dashplaygroundfree.DrawingView$DrawingThread.run(DrawingView.java:407)

** Monkey aborted due to error.
Events injected: 11583
## Network stats: elapsed time=435368ms (0ms mobile, 435368ms wifi, 0ms not connected)


*/


//TODO: fixed-not tested: the baby bunny cage was open but the babies were not out of their cage
//TODO: fixed-not-tested reset feed the bunny on exit. The moles get stuck coming out of there hole on exit and then when we go back they are 1/2 way out


//Not show stoppers
//TODO: make shake and/or double tap cancel current animation.
//TODO: Make friend and babies mimic bunny dance moves & fix global double & triple tap
//TODO: ball on 2,5,8 -  should be pick up from 3,6, & 7 respectivley 
//TODO: Make background scroll along with the road



import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.TableLayout.LayoutParams;


interface SwitchViews {
	final static int RIGHT  =1;
	final static int LEFT   =2;
	final static int TOP    =3;
	final static int BOTTOM =4;
	void whichView(int direction);
	void switchToView(int viewId, int whoAmI);
}



public class BunnyGames extends Activity implements SwitchViews {
	//Constants that control which mode the game is in
    public final static int BUS_PUZZLE  = 1;
    private static final int SETTINGS   = 3;
	protected static final int CLOTHING = 4;
	
	
	protected static final int FEED_THE_BUNNY = 5;
	protected static final int JUMP_ROPE = 6;
	protected static final int HOPSCOTCH = 7;
	protected static final int TIC_TAC_TOE = 8;
	protected static final int BABY_BUNNIES = 9;
	protected static final int TILE_2B = 10;
	protected static final int MINI_GOLF = 11;
	protected static final int TILE_2C = 12;
	protected static final int TILE_2D = 13;
	protected static final int BUS_DRIVING_GAME = 14;
	
	BitmapCache bitmapCache;
	
	private int gridPositionX=0;
	int gridPositionY=0;
	

    BuildASnowman puzzle; 
    JumpRope jumpRope;
    ViewFlipper horizantalRow1;
    int currentViewFlipperView;
    
    final Handler messageHandler = new Handler();    
	DrawingView currentView;
	private SharedPreferences sharedPreferences;
	private HopScotch hopscotch;
	
    private FeedTheBunny feedTheBunny;
	private TicTacToe ticTacToe;
	private BabysAndCarotPile babiesInCage;
	private MinitureGolf miniGolf;
	
	private BusDrivingGame busDriving;
	
	private int width;
	private int height;

	boolean cacheLoaded=false;
	public void onDestroy(){
		super.onDestroy();
		
		if (cacheLoaded)
			bitmapCache.freeCache();
		
		feedTheBunny.destrory();
		ticTacToe.destrory();
		busDriving.destrory();
		miniGolf.destrory();
		babiesInCage.destrory();
		jumpRope.destrory();
		hopscotch.destrory();
	}
	
	public void onStop(){
		super.onStop();
		
	}
	
	protected void onPause(){
    	super.onPause();
    	thread2.interrupt();
    	if (zzz_Version.freeVersion){
        	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	Editor editor = sharedPreferences.edit();
        	playTimeRemaining-=(System.currentTimeMillis()-playTimeStart);
        	editor.putLong("playTimeRemaining", playTimeRemaining);
        	editor.commit();
    	}
    	
    	if (basicInitDone==false)
    		return;
    	currentView.pause();
    }
    
	long playTimeRemaining;
	long playTimeStart;
	static final int DIALOG_BUYNOW=1;
	AlertDialog alert = null;
	boolean versionUnockable=false;
	Thread thread2;
    protected void onResume(){
    	super.onResume();
    	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final boolean unlocked = sharedPreferences.getBoolean("isUnlocked", false);
		
		
		Thread thread = new Thread(new Runnable(){public void run(){
			HttpDataFetcher get = new HttpDataFetcher(new DefaultHttpClient(),"http://www.catglo.com/key.html",new DoForEachLine(){public void parseAndStore(String line) {
				if (line.compareTo("brickRoad")==0){
					versionUnockable=true;
				}
			}});
			get.fetchAndParse(); 
		}});
		thread.start();
		
		
		thread2 = new Thread(new Runnable(){public void run(){
			
			synchronized(thread2){try {thread2.wait(4000);} catch (InterruptedException e) {}}
			
			
			if (zzz_Version.freeVersion && !unlocked){
				playTimeStart=System.currentTimeMillis();
	    		
	    		final Editor prefEditor = sharedPreferences.edit();
	    		playTimeRemaining=sharedPreferences.getLong("playTimeRemaining", 1000*60*60);
	    		  
	    		
	    		if (playTimeRemaining < (1000*60*60)-(60*1000*5) && versionUnockable){
	    			BunnyGames.this.runOnUiThread(new Runnable(){public void run(){
	    				/*Dialog dialog = new Dialog(this);
		    			//dialog.setTitle("Custom Dialog");
		    			dialog.setContentView(R.layout.review_me_dialog);
		    			dialog.show();*/
		    			AlertDialog.Builder builder = new AlertDialog.Builder(BunnyGames.this);
		    			
		    			Context mContext = getApplicationContext();
		    			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		    			View layout = inflater.inflate(R.layout.review_me_dialog, (ViewGroup) findViewById(R.id.review_me_dialog_root));
		
		    			//String s = getString(R.string.freeplayremining);
		    			//TextView titleText = (TextView)layout.findViewById(R.id.titleText);
		        		//titleText.setText(String.format(s, (playTimeRemaining/60000)));
		    			
		    			
		    			/*Button buy = (Button)layout.findViewById(R.id.BuyNowButton);
		    			buy.setOnClickListener(new OnClickListener(){public void onClick(View v) {
		    				final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=com.catglo.dashplayground")); 
							startActivity(marketIntent);
							finish();
							alert=null;
						}});
		    			*/
		    			
		    		    Button noThanks = (Button)layout.findViewById(R.id.noThanksButton);
		    		    if (playTimeRemaining>0){
							noThanks.setOnClickListener(new OnClickListener(){public void onClick(View v) {
								if (alert!=null)
									alert.cancel();
			    				alert=null;
							}});
		    		    } else {
		    		    	noThanks.setOnClickListener(new OnClickListener(){public void onClick(View v) {
			    				finish();
							}});
		    		    } 
		    			TextView getThemMessage = (TextView)layout.findViewById(R.id.getThemMessage);
		    		
		    			Button review = (Button)layout.findViewById(R.id.reviewTheApp);
		    			review.setOnClickListener(new OnClickListener(){public void onClick(View v) {
		    				final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.catglo.dashplaygroundfree")); 
							startActivity(marketIntent);
							prefEditor.putBoolean("isUnlocked", true);
							prefEditor.commit();
							if (alert!=null)
								alert.cancel();
							alert=null;
						}});
		    			
		    			builder.setView(layout);
		    			alert = builder.create();
		    			if (thread2.isInterrupted())
		    				return;
		    			alert.setCancelable(false);
		    			alert.show(); 
	    			}});
	    		} else if (playTimeRemaining < (1000*60*60)-(60*1000*15)) {
	    			BunnyGames.this.runOnUiThread(new Runnable(){public void run(){
	    				if (alert!=null){
		        			alert.cancel();
		        		}
		        		
			    		final AlertDialog.Builder builder = new AlertDialog.Builder(BunnyGames.this);
						builder.setTitle("Buy Now");
						String s = getString(R.string.freeplayremining);
		    			builder.setMessage(String.format(s, (playTimeRemaining/60000)));
						if (playTimeRemaining>0){
							builder.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener(){public void onClick(final DialogInterface dialog, final int whichButton) {
						    	alert=null;
							}});
						}
						builder.setPositiveButton("Buy Now.", new DialogInterface.OnClickListener(){public void onClick(final DialogInterface dialog, final int whichButton) {
							final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=com.catglo.dashplayground")); 
							startActivity(marketIntent);
							finish();
							alert=null;
						}});
					
						alert = builder.create();
						alert.setCancelable(false);
						if (thread2.isInterrupted())
		    				return;
		    			
						alert.show();
	    			}});
				}	
			}
		}});
		thread2.start();
    	if (basicInitDone==false)
    		return;
    	currentView.resume();
    }
	
  
	
	int[] gridViewDefinition = {
			FEED_THE_BUNNY,
			TIC_TAC_TOE, 
			BUS_DRIVING_GAME, 
			MINI_GOLF,
			BABY_BUNNIES,
			JUMP_ROPE,
			HOPSCOTCH
	};
	
	int row;
	int col;
	
	boolean basicInitDone=false;
	Thread startup;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
   // 	Debug.startMethodTracing();
        super.onCreate(savedInstanceState);
        final long startTime = System.currentTimeMillis();
    	

    	width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
    	height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
    	
        horizantalRow1 = new ViewFlipper(BunnyGames.this);
        horizantalRow1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
  	
		
		setContentView(R.layout.main);
		
		
		startup = new Thread(new Runnable(){ public void run(){
    		Looper.prepare();
    		
    		if (cacheLoaded==false){
    			bitmapCache = new BitmapCache(BunnyGames.this.getApplicationContext(),width,height);
    			cacheLoaded=true;
    		}
    		
    		feedTheBunny  = (FeedTheBunny)      new FeedTheBunny(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480));
		    feedTheBunny  = (FeedTheBunny)      addNewScreen(feedTheBunny);
	        ticTacToe     = (TicTacToe)         addNewScreen(new TicTacToe(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));
	      	busDriving    = (BusDrivingGame)    addNewScreen(new BusDrivingGame(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));
	        miniGolf      = (MinitureGolf)      addNewScreen(new MinitureGolf(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));
			babiesInCage  = (BabysAndCarotPile) addNewScreen(new BabysAndCarotPile(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));
			jumpRope      = (JumpRope)          addNewScreen(new JumpRope(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));
	        hopscotch     = (HopScotch)         addNewScreen(new HopScotch(BunnyGames.this,width,height,bitmapCache,new Point(col*320,row*480)));	
			
	       

			bitmapCache.loadGridPositionBackgrounds(2);
			bitmapCache.loadGridPositionBackgrounds(3);
			bitmapCache.loadGridPositionBackgrounds(4);
			
			currentView=miniGolf;
			
			busDriving.backgroundImage = bitmapCache.getBackground(2);
			miniGolf.backgroundImage = bitmapCache.getBackground(3);   
			babiesInCage.backgroundImage = bitmapCache.getBackground(4);
		
			
	        int delay = (int) (startTime+4000 - System.currentTimeMillis());
	       
	        if (delay > 0){
	    		synchronized (this){
		    		try {
						wait(delay);
					} catch (InterruptedException e) {
					}
	    		}
    		}
	        
	       
        
    		
    		messageHandler.post(new Runnable(){public void run(){
    		 
    	        //   horizantalRow1.setAnimationCacheEnabled(false);
    	        
    			
    			
    	        currentViewFlipperView=0;
    	        row=0;
    	        col=0;
    	        
    	    
    			
    		    horizantalRow1.setAnimateFirstView(true);
    		    
    		    horizantalRow1.setAnimationCacheEnabled(false);
    		    miniGolf.enter(160,300,false);
    			DrawingView.friendPoint.x=180 + 320*3;
    			DrawingView.friendPoint.y=333;
    			
    		
    			
    			horizantalRow1.showNext();
	    		horizantalRow1.showNext();
	    		horizantalRow1.showNext();
	    		gridPositionX=3;
	    		
	    		setContentView(horizantalRow1);
	    		
	    		miniGolf.resume();
	    		
	    		
	    		
    		}});
    		basicInitDone=true;
    		
    	}});
		startup.start();
    	 
    }
    
    private DrawingView addNewScreen(DrawingView screen) {
    	col++;
    	screen.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        screen.setSwitcher(this);
        horizantalRow1.addView(screen);
        //screen.pause();
		return screen;
	}

	
    
    synchronized public void switchToView(final int to, int from){
    	if (to==from)
    		return;
    	int startPage=-1;
    	int goToPage=-1;
    	for (int i=0; i<gridViewDefinition.length; i++){
    		if (to==gridViewDefinition[i]){
    			goToPage=i;
    		}
    		if (from==gridViewDefinition[i]){
    			startPage=i;
    		}
    	} 
    	if (startPage!=gridPositionX){
    		startPage=gridPositionX;
    		from=gridViewDefinition[gridPositionX];
    	}
    	
    	horizantalRow1.clearAnimation();//csetInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
		horizantalRow1.clearAnimation();//setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
	
    	if (goToPage==-1 || startPage==-1) throw(new IllegalStateException());
    	int distance = Math.abs(goToPage-startPage);
    	DrawingView oldView = getScreen(from);
    	DrawingView newView = getScreen(to);
    	if (goToPage-startPage<0){//left
    		for (int i = 0; i < distance;i++){
    			gridPositionX--;
    			horizantalRow1.showPrevious();	
    		}
    	
    		newView.enter(300,oldView.mainBunny.actualY,oldView.mainBunny.lastAnimationMirrored);
    	} else { //right
    		for (int i = 0; i < distance;i++){
    			gridPositionX++;
    			horizantalRow1.showNext();	
    		}
    		
    		newView.enter(20,oldView.mainBunny.actualY,oldView.mainBunny.lastAnimationMirrored);
		}
    	bitmapCache.loadGridPositionBackgrounds(goToPage);
    	bitmapCache.loadGridPositionBackgrounds(goToPage+1);
    	bitmapCache.loadGridPositionBackgrounds(goToPage-1);
		newView.backgroundImage = bitmapCache.getBackground(goToPage);
		newView.mainBunny.bunnyEnteringBorrowedAnimation(oldView.mainBunny.bunnyImage,oldView.mainBunny.getBunnyMatrix());
		oldView.pause();
		newView.resume();
		currentView = newView;
    }
    	
	synchronized public void whichView(final int direction){
		messageHandler.post(new Runnable(){
			public void run() {
				DrawingView newView=null;
				DrawingView oldView=null;
				
				//Clip to grid boundary
				if (gridPositionX==6 && direction==SwitchViews.RIGHT)
					return;
				if (gridPositionX==0 && direction==SwitchViews.LEFT)
					return;
				
				
				Thread t;
				oldView = getScreen(gridViewDefinition[gridPositionX]);
				switch (direction){
				case SwitchViews.LEFT:
					
					gridPositionX--;
					
					newView = getScreen(gridViewDefinition[gridPositionX]);
					int x = (int) (oldView.mainBunny.actualX+320);
					newView.enter(x,oldView.mainBunny.actualY,oldView.mainBunny.lastAnimationMirrored);
					newView.backgroundImage = bitmapCache.getBackground(gridPositionX);
					
					t = new Thread(new Runnable(){public void run() {
						bitmapCache.freeGridPositionBackground(gridPositionX+2);
						
						bitmapCache.loadGridPositionBackgrounds(gridPositionX-1);
						
						
					}});
					t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
					t.start();
					
					
					horizantalRow1.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_right_in));
					horizantalRow1.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_right_out));
				
					newView.mainBunny.bunnyEnteringBorrowedAnimation(oldView.mainBunny.bunnyImage,oldView.mainBunny.getBunnyMatrix());
					
					horizantalRow1.showPrevious();	
					break;
					
				case SwitchViews.RIGHT:
					
					
					gridPositionX++;
					
					newView = getScreen(gridViewDefinition[gridPositionX]);
					newView.enter(oldView.mainBunny.actualX-320,oldView.mainBunny.actualY,oldView.mainBunny.lastAnimationMirrored);
					
					newView.backgroundImage = bitmapCache.getBackground(gridPositionX);
					
					t= new Thread(new Runnable(){public void run() {
						bitmapCache.freeGridPositionBackground(gridPositionX-2);
						
						bitmapCache.loadGridPositionBackgrounds(gridPositionX+1);
						
						
					}});
					t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
					t.start();
					
					
					horizantalRow1.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left_in));
					horizantalRow1.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left_out));
				
					newView.mainBunny.bunnyEnteringBorrowedAnimation(oldView.mainBunny.bunnyImage,oldView.mainBunny.getBunnyMatrix());
					
					horizantalRow1.showNext();
					
					break;
					
				
				}
				
			//	newView.setBunnyBitmap(oldView.bunnyImage,oldView.getBunnyMatrix());
				//newView.bunnyImage = oldView.bunnyImage;
				
				oldView.pause();
				
				newView.resume();
				currentView = newView;
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709): java.lang.NullPointerException
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at com.catglo.dashplaygroundfree.BunnyGames$1.run(BunnyGames.java:350)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at android.os.Handler.handleCallback(Handler.java:587)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at android.os.Handler.dispatchMessage(Handler.java:92)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at android.os.Looper.loop(Looper.java:123)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at android.app.ActivityThread.main(ActivityThread.java:4203)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at java.lang.reflect.Method.invokeNative(Native Method)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at java.lang.reflect.Method.invoke(Method.java:521)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
				//06-25 15:01:21.715: ERROR/AndroidRuntime(9709):     at dalvik.system.NativeStart.main(Native Method)

			}

			
		});
	}
	
	

	private DrawingView getScreen(int viewID) {
		DrawingView view=null;
		switch (viewID){
		case HOPSCOTCH:
			view = hopscotch;
			break;
			
		case JUMP_ROPE:
			view=jumpRope;
			break;
	
		case FEED_THE_BUNNY:
			view=feedTheBunny;
			break;
			
		case TIC_TAC_TOE:
			view = ticTacToe;
			break;	
		case BABY_BUNNIES:
			view = babiesInCage;
			break;
		case MINI_GOLF:
			view =miniGolf;
			break;
		case BUS_DRIVING_GAME:
			view =busDriving;
			break;

		}
		return view;
	}
	static final int TOGGLE=100;
	static final int DEBUG=101;
	
	
/*	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);	    
	    menu.add(0, TOGGLE, 0, "Toggle Perspective").setIcon(android.R.drawable.ic_menu_camera);	    
	    menu.add(0, DEBUG, 0, "Toggle Debug").setIcon(android.R.drawable.ic_menu_compass);	    
		      return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case SETTINGS:{
		    	Intent myIntent = new Intent(getApplicationContext(),bunnySettings.class);
		    	startActivity(myIntent);
		    	return true;
		    }
		    case TOGGLE:
		    	if (BusDrivingGame.toggleOn)
		    		BusDrivingGame.toggleOn=false;
		    	else
		    		BusDrivingGame.toggleOn=true;
		    break;
		    case DEBUG:
		    	if (DrawingView.showDebug)
		    		DrawingView.showDebug=false;
		    	else
		    		DrawingView.showDebug=true;
		}
	    return false;
	}*/
	
	 
	/* Creates the menu items 
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, SETTINGS, 0, "E-Mail Feedback").setIcon(android.R.drawable.ic_menu_send);	 
	      return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case SETTINGS:{
		    	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
				emailIntent .setType("plain/text"); 
				emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Android.bunnygames@gmail.com"}); 
				emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "Bunny Games Feedback"); 
				emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, ""); 
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				return true;
		    }
		}
	    return false;
	}*/
	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			  return true;
		  }
		 // if (keyCode == KeyEvent.KEYCODE_BACK) {
		//	  keyCode = KeyEvent.KEYCODE_HOME;
		 // }
		  return super.onKeyDown(keyCode, event);
	}
	/*// Build Time: 1259893752
// ID: 
// Tag: AndroidRuntime
// java.lang.ArrayIndexOutOfBoundsException: 
//   at com.catglo.dashplaygroundfree.BunnyGames$1.run(BunnyGames.java:362)
//   at android.os.Handler.handleCallback(Handler.java:587)
//   at android.os.Handler.dispatchMessage(Handler.java:92)
//   at android.os.Looper.loop(Looper.java:123)
//   at android.app.ActivityThread.main(ActivityThread.java:4203)
//   at java.lang.reflect.Method.invokeNative(Method.java:-2)
//   at java.lang.reflect.Method.invoke(Method.java:521)
//   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
//   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
//   at dalvik.system.NativeStart.main(NativeStart.java:-2)




// Tag: AndroidRuntime
// java.lang.ArrayIndexOutOfBoundsException: 
//   at com.catglo.dashplaygroundfree.BunnyGames$1.run(BunnyGames.java:362)
//   at android.os.Handler.handleCallback(Handler.java:587)
//   at android.os.Handler.dispatchMessage(Handler.java:92)
//   at android.os.Looper.loop(Looper.java:123)
//   at android.app.ActivityThread.main(ActivityThread.java:4203)
//   at java.lang.reflect.Method.invokeNative(Method.java:-2)
//   at java.lang.reflect.Method.invoke(Method.java:521)
//   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
//   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
//   at dalvik.system.NativeStart.main(NativeStart.java:-2)

** Monkey abort
*
*
*
*
* Tag: AndroidRuntime
// java.lang.ArrayIndexOutOfBoundsException: 
//   at com.catglo.dashplaygroundfree.BunnyGames$1.run(BunnyGames.java:366)
//   at android.os.Handler.handleCallback(Handler.java:587)
//   at android.os.Handler.dispatchMessage(Handler.java:92)
//   at android.os.Looper.loop(Looper.java:123)
//   at android.app.ActivityThread.main(ActivityThread.java:4203)
//   at java.lang.reflect.Method.invokeNative(Method.java:-2)
//   at java.lang.reflect.Method.invoke(Method.java:521)
//   at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
//   at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
//   at dalvik.system.NativeStart.main(NativeStart.java:-2)

07-04 19:30:02.149: INFO/BUNNY(13481): Grid goToPage=5   srartPage=2
07-04 19:30:02.149: INFO/BUNNY(13481): 2,Grid = 3
07-04 19:30:02.149: INFO/BUNNY(13481): 2,Grid = 4
07-04 19:30:02.159: INFO/BUNNY(13481): 2,Grid = 5
07-04 19:30:58.599: INFO/BUNNY(13481): 2,Grid = 4
07-04 19:32:03.329: INFO/BUNNY(13481): Grid goToPage=1   srartPage=4
07-04 19:32:03.339: INFO/BUNNY(13481): 1,Grid = 3
07-04 19:32:03.339: INFO/BUNNY(13481): 1,Grid = 2
07-04 19:32:03.339: INFO/BUNNY(13481): 1,Grid = 1
07-04 19:32:03.359: INFO/BUNNY(13481): Grid goToPage=1   srartPage=3
07-04 19:32:03.369: INFO/BUNNY(13481): 1,Grid = 0
07-04 19:32:03.369: INFO/BUNNY(13481): 1,Grid = -1
07-04 19:32:20.759: INFO/BUNNY(13481): Grid goToPage=3   srartPage=6
07-04 19:32:20.769: INFO/BUNNY(13481): 1,Grid = -2
07-04 19:32:20.769: INFO/BUNNY(13481): 1,Grid = -3
07-04 19:32:20.769: INFO/BUNNY(13481): 1,Grid = -4
07-04 19:32:22.299: INFO/BUNNY(13481): Grid goToPage=2   srartPage=3
07-04 19:32:22.299: INFO/BUNNY(13481): 1,Grid = -5


**/
	
	
}