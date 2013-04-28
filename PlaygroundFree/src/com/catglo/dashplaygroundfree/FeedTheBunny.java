package com.catglo.dashplaygroundfree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FeedTheBunny extends DrawingView  {
	private MediaPlayer mp;

	int whoAmI(){
		return BunnyGames.FEED_THE_BUNNY;
	}
	int whereAmI(){
		return 0;
	}
	void helpSystem(){
		super.helpSystem();
		if (mp!=null){
			mp.seekTo(0);
			mp.start(); 
		}
	}
	
	final static int collums=3;
	final static int rows=3;
	final static int FEED_SHOW_INTERVAL=900;
	final static int FEED_SHOW_SPEEDUP=1;
	int feedShowInterval = FEED_SHOW_INTERVAL;
	final static int MOLE_SHOW_INTERVAL=100;
	final static int TIME_TO_RETURN_TO_BASE=1000;
	private static final int MARGIN_LEFT = 30;
	private static final int HORIZANTAL_SPACING = 100;
	private static final int MARGIN_TOP = 80;
	private static final int VERTICAL_SPACING=100;
	final int CARROT_RADIUS=60;
	final int CARROT_OFFSETX=30;
	final int CARROT_OFFSETY=30;
	
	private static final int CAROT_GONE=1;
	private static final int CAROT_SEEDLING=2;
	private static final int CAROT_READY_TO_PICK=3;
	private static final int CAROT_VERY_READY_TO_PICK=4;
	private static final int CAROT_SELECTED_FOR_PICKING=5;
	
	private static final int MOLE_NO_MOLE=4;
	private static final int MOLE_1=5;
	private static final int MOLE_2=6;
	private static final int MOLE_3=7;
	private static final int MOLE_4=8;
	private static final int MOLE_5=9;
	private static final int MOLE_6=10;
	
	
	int[][] isACarrot = new int[collums][rows];
	int[][] moleStage = new int[collums][rows];
	
	boolean[][] holdThatCarrot = new boolean[collums][rows];
	private long timeIntervalStart;
	int mozart;
	private MediaPlayer player;
	private int slotsound;
	
	Bitmap[] wheelbarrelFrames = new Bitmap[11];
	
	protected void pause(){
		super.pause();
		if (player!=null && player.isPlaying()==true)
			player.pause();
		resetGame();
		//if (watteringCanAnimation!=null)
		//	currentAnimation.remove(watteringCanAnimation);
	}
	
	void resetGame(){
		carrotInMouth=0;
		mode=MODE_WATTERING;
		removeAction(carrotsPopingOut);
		removeAction(gameTimer);
		stopWatteringCanAnimation();
		
		//This addresses a bug where there carrot/mole would be left partially out 
		//on a screen switch.
		if (isACarrot!=null && moleStage!=null){
			for (int i = 0; i < collums; i++){
				for (int j = 0; j < rows;j++){
					isACarrot[i][j]=CAROT_GONE;
					moleStage[i][j]=MOLE_NO_MOLE;
				}
			}
		}
		if (friend!=null) {
			friend.supressAI=false;
		}
		feedShowInterval = FEED_SHOW_INTERVAL;
	}
	 
	protected void resume(){
		super.resume();
		if (player != null)
			player.start();
		timeIntervalStart=System.currentTimeMillis();
		bitmapCache.loadFeedTheBunny();
		mode=MODE_WATTERING;
		totalPossibleCarrots=0;
		carrotPickCount=0;
		startWatteringCanAnimation();
		mainBunny.animationInterval=250;
		mainBunny.distanceOpen=80;
		
		
	}
	
	void onMenuExit(){
		super.onMenuExit();
		resetGame();
		
	}
	static final int GAME_PLAY_TIME  = 70000;
	static final int GAME_ABOUT_TO_END= 6000;
	int wheelbarrelExitStage;
	final static int GAME_TIMER_INTERVAL=100;
	final AnimationAction gameTimer = new AnimationAction(GAME_TIMER_INTERVAL,new Runnable(){public void run() {			
		synchronized (FeedTheBunny.this) {
			if (System.currentTimeMillis() > pickStartTime+GAME_PLAY_TIME){
				resetGame();
				removeAction(gameTimer);
				startWatteringCanAnimation();
				friend.numberedDance(mainBunny.randomDance());
				//Log.i("BUNNY","total ="+totalPossibleCarrots);
				//Log.i("BUNNY","picked = "+carrotPickCount);
				float ratioPicked=(float)totalPossibleCarrots/(float)carrotPickCount;
				if (ratioPicked > 0.65f){
					 bitmapCache.femaleCheer();
				}
				if (ratioPicked > 0.35f){
					bitmapCache.familyClap();
				}
				if (ratioPicked > 0.10f) {
					bitmapCache.shortClap();
				}
				int confetti=(int) (20*ratioPicked);
				launchConfetti(confetti);
			} else {
				wheelbarrow1X+=5;
				wheelbarrow2X+=5;
				if (System.currentTimeMillis() <= pickStartTime+GAME_PLAY_TIME-GAME_ABOUT_TO_END){
					if (wheelbarrow1X>300){
						//swap wheelbarrels
						wheelbarrow1X=wheelbarrow2X-WHEELBARREL_SPACING;
						wheelbarrel1Frame=0;
						whichWheelBarrel=1;
						wheelbarrel1Frame=0;
					}
					if (wheelbarrow2X>300){
						//swap wheelbarrels
						wheelbarrow2X=wheelbarrow1X-WHEELBARREL_SPACING;
						wheelbarrel2Frame=0;
						whichWheelBarrel=0;
						wheelbarrel2Frame=0;
					}
				}
			}
			
			postInvalidate();
			gameTimer.actionTime=GAME_TIMER_INTERVAL+System.currentTimeMillis();
		}
	}});
	synchronized private void wheelbarrelExit() {
	//	wheelbarrow1X+=20;
	//	postInvalidate();
	} 

	final AnimationAction molePopingOut = new AnimationAction(MOLE_SHOW_INTERVAL,new Runnable(){public void run() {			
		synchronized (FeedTheBunny.this) {
			for (int i = 0; i < collums; i++){
				for (int j = 0; j < rows;j++){
					if (moleStage[i][j] > MOLE_NO_MOLE){
						moleStage[i][j]++;
						if (holdThatCarrot[i][j]){
							moleStage[i][j] = MOLE_1;
						} else {
							if (moleStage[i][j] == MOLE_5){
								isACarrot[i][j]=CAROT_GONE;
							}
							if (moleStage[i][j] > MOLE_6) {
								moleStage[i][j]=MOLE_NO_MOLE;
								removeAction(molePopingOut);		
							}
						}
					}
				}
			}
			postInvalidate();
			molePopingOut.actionTime=MOLE_SHOW_INTERVAL+System.currentTimeMillis();
		}
	}});

	final Random r = new Random();	
	final AnimationAction carrotsPopingOut = new AnimationAction(feedShowInterval,new Runnable(){public void run() {			
		synchronized (FeedTheBunny.this) {
			//Log.i("BUNNY","Carrot Animation Interval");
			int X = r.nextInt(collums);
			int Y = r.nextInt(rows);
			totalPossibleCarrots++;
			for (int i = 0; i < collums; i++){
				for (int j = 0; j < rows;j++){
					//if (isACarrot[i][j]==CAROT_GONE)
					//	isACarrot[i][j]=CAROT_SEEDLING;
					 
					if (isACarrot[i][j]==CAROT_SEEDLING)
						isACarrot[i][j]=CAROT_READY_TO_PICK;
				
					
					else if (isACarrot[i][j]==CAROT_READY_TO_PICK) {
						isACarrot[i][j]=CAROT_VERY_READY_TO_PICK;
						molePopingOut.actionTime=MOLE_SHOW_INTERVAL+System.currentTimeMillis();
						moleStage[i][j]=MOLE_1;
						addAction(molePopingOut);
					} 
					else if (isACarrot[i][j]==CAROT_SELECTED_FOR_PICKING) {
						isACarrot[i][j]=CAROT_GONE;
					}
				}
			}
			isACarrot[X][Y]=CAROT_SEEDLING;
			postInvalidate();
			carrotsPopingOut.actionTime=feedShowInterval+System.currentTimeMillis();
		
			long time = System.currentTimeMillis();
			/*if (FeedTheBunny.this.timeStoppedMoving>0 
			    && time-FeedTheBunny.this.timeStoppedMoving > TIME_TO_RETURN_TO_BASE 
				&& mainBunny.targetX != 160 && mainBunny.targetY != 380){
				mainBunny.sendBunnyHome(160,380);
			} */
				
		}
	}});
	
	private int carrotInMouth=0;
	private int carrotPickCount=0;
	private float carrotX;
	private float carrotY;
	
	static final int WHEELBARREL_START_X=-80;
	static final int WHEELBARREL_SPACING=240;
	int whichWheelBarrel=0;
	float wheelbarrow1X=WHEELBARREL_START_X;
	float wheelbarrow1Y=380;
	float wheelbarrow2X=WHEELBARREL_START_X-WHEELBARREL_SPACING;
	float wheelbarrow2Y=380;
	
	
	private Matrix numberHintMatrix;
	
	int mode=MODE_WATTERING;
	static final int MODE_WATTERING=1;
	static final int MODE_PICKING=2;
	long pickStartTime;
	int totalPossibleCarrots=0;
	private int shortClap;
	private int femaleCheer;
	private int familyClap;
	
	public FeedTheBunny(Context context,int width, int height, BitmapCache bitmapCache,Point origin) {
		
		super(context,width,height, bitmapCache,origin);
		
		mp = MediaPlayer.create(getContext(), R.raw.feed_help);
		   
		
		startWatteringCanAnimation();	
	
		//mozart = sound.load(getContext(), R.raw.mozart_andante, 1);
		try { 
	      //   player=MediaPlayer.create(getContext(),  R.raw.circus_1);
	      //   player.setVolume(0.5f, 0.5f);
	      //   player.setLooping(true);
	        // player.start(); 
	        } 
	     catch (Exception e)  { }// handle errors ..   } 
	    
			
		numberHintMatrix=new Matrix();
		numberHintMatrix.postScale(.15f, .15f);
		numberHintMatrix.postTranslate(10, 10);
		
        
		
		for (int i = 0; i < collums; i++){
			for (int j = 0; j < rows;j++){
				isACarrot[i][j]=CAROT_GONE;
				moleStage[i][j]=MOLE_NO_MOLE;
				final int top = j*VERTICAL_SPACING+MARGIN_TOP-CARROT_RADIUS+CARROT_OFFSETY;
				final int left = i*HORIZANTAL_SPACING+MARGIN_LEFT-CARROT_RADIUS+CARROT_OFFSETX;		
				final int right = i*HORIZANTAL_SPACING+MARGIN_LEFT+CARROT_RADIUS+CARROT_OFFSETX;
				final int bottom = j*VERTICAL_SPACING+MARGIN_TOP+CARROT_RADIUS+CARROT_OFFSETY;
				final int x = i;
				final int y = j;
				addEventRect(new Rect(left,top,right,bottom),new MenuExecutor(){
					public void execute() {
						if (mode==MODE_PICKING){
							if (isACarrot[x][y]==CAROT_SEEDLING || 
								isACarrot[x][y]==CAROT_READY_TO_PICK || 
								isACarrot[x][y]==CAROT_SELECTED_FOR_PICKING || 
							    isACarrot[x][y]==CAROT_VERY_READY_TO_PICK ||
							    (moleStage[x][y] >= MOLE_1 && moleStage[x][y] <= MOLE_6))
							{
								mainBunny.sendBunnyTo(160,380);
								holdThatCarrot[x][y]=false;
								//carrotInMouth++;
								
								postInvalidate();
							}
						} 
						if (mode==MODE_WATTERING){
							startCountDown();
							//TODO: play sound "you have 1 minute to pick as many carots as you can
						}
					}
				}); 
			}
		}
		
		timeIntervalStart=System.currentTimeMillis();
				
		carrotsPopingOut.name="CARROT POPING OUT ANIMATION";
		carrotsPopingOut.actionTime=feedShowInterval+System.currentTimeMillis();

	
	}
	
	protected void setupExitAreas(){
		addEventRect(new Rect(260,380,320,480), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
	}

	
	Matrix carrotMatrix;
	private int wheelbarrel1Frame=0;
	private int wheelbarrel2Frame=0;
	/*@Override
	synchronized protected void onBunnyMoved(){
		if (carrotInMouth>0){
			carrotX = mainBunny.actualX;
			carrotY = mainBunny.actualY;
			carrotMatrix=new Matrix();
			carrotMatrix.postScale(.4f, .4f);
			float carrotXOffset;
			if (mainBunny.lastAnimationMirrored){
				carrotXOffset=10;
			//	carrotMatrix.postRotate(90);
			}else{
				carrotXOffset=-10;
		//		carrotMatrix.postRotate(-90);
			}
			carrotMatrix.postTranslate(carrotX+(carrotXOffset), carrotY-(18));
			//carrotMatrix.postRotate(90);	
		
			if (mainBunny.actualX<200 && mainBunny.actualY > 320){
				for (int i = 0; i < collums; i++){
					for (int j = 0; j < rows;j++){
						holdThatCarrot[i][j]=false;
					}
				}
				
				mainBunny.animationInterval=250;
				mainBunny.distanceOpen=80;
				if (whichWheelBarrel==0){
					wheelbarrel1Frame++;
					carrotInMouth=0;
					if (wheelbarrel1Frame>=10){
						if (System.currentTimeMillis() <= pickStartTime+GAME_PLAY_TIME-GAME_ABOUT_TO_END){
							whichWheelBarrel=1;
						}
					}
				} else {
					wheelbarrel2Frame++;
					carrotInMouth=0;
					if (wheelbarrel2Frame>=10){
						if (System.currentTimeMillis() <= pickStartTime+GAME_PLAY_TIME-GAME_ABOUT_TO_END){
							whichWheelBarrel=0;
						}
					}
				}
				
				wheelbarrelExit();
			}
		}		
	}
*/

	
	protected void drawSurface(Canvas canvas){
		if (mode==MODE_PICKING){
			canvas.drawBitmap(bitmapCache.waterCan[0], 125,10,null);
		}
		
		for (int x = 0; x < collums; x++){
			for (int y = 0; y < rows; y++){
				float X = (x*HORIZANTAL_SPACING+MARGIN_LEFT-40);
				float Y = (y*VERTICAL_SPACING+MARGIN_TOP-30);
			    
				final Bitmap[] molePose = new Bitmap[6];
				molePose[0] = bitmapCache.mole1;
				molePose[1] = bitmapCache.mole2;
				molePose[2] = bitmapCache.mole3;
				molePose[3] = bitmapCache.mole4;
				molePose[4] = bitmapCache.mole5;
				molePose[5] = bitmapCache.mole1;
				
				
				if (moleStage[x][y] > MOLE_NO_MOLE){
					canvas.drawBitmap(molePose[moleStage[x][y]-MOLE_1],X , Y, null); 
			    }
			}
		}
		if (bitmapCache.feedTheBunnyLoaded){
			for (int x = 0; x < collums; x++){
				for (int y = 0; y < rows; y++){
					float X = (x*HORIZANTAL_SPACING+MARGIN_LEFT);
					float Y = (y*VERTICAL_SPACING+MARGIN_TOP);
				    switch (isACarrot[x][y]){
				    case CAROT_GONE:
				    	canvas.drawBitmap(bitmapCache.noCarrot,X , Y, null); 
				    	break;
				    case CAROT_SEEDLING:
				    	canvas.drawBitmap(bitmapCache.seedling,X , Y, null); 
				    	break;
				    case CAROT_READY_TO_PICK:
				    	canvas.drawBitmap(bitmapCache.bush, X,Y,null);
				    	break;
				    case CAROT_VERY_READY_TO_PICK:
				    case CAROT_SELECTED_FOR_PICKING:
					    	canvas.drawBitmap(bitmapCache.bigBush,X,Y,null);
				    	break;
				    }
				}
			}
		}
		
		if (bitmapCache.initDone && mode==MODE_PICKING) {
			boolean drawWheelBarrel1=true;
			boolean drawWheelBarrel2=true;
			
			if (System.currentTimeMillis() > pickStartTime+GAME_PLAY_TIME-GAME_ABOUT_TO_END){
				if (wheelbarrow1X<wheelbarrow2X) {
					drawWheelBarrel2=false;
				} else {
					drawWheelBarrel1=false;
				}
				
			}
			
			if (drawWheelBarrel1){
				canvas.drawBitmap(bitmapCache.wheelbarrow[0], wheelbarrow1X, wheelbarrow1Y, null);
				if (wheelbarrel1Frame>0 && wheelbarrel1Frame < bitmapCache.wheelbarrow.length){
					canvas.drawBitmap(bitmapCache.wheelbarrow[wheelbarrel1Frame], wheelbarrow1X, wheelbarrow1Y, null);	
					/*java.lang.ArrayIndexOutOfBoundsException: 
					  at com.catglo.dashplayground.FeedTheBunny.drawSurface(FeedTheBunny.java:354)
					  I was getting this exception from joyc's device. I added the check for overflow and 
					  synchronized all the functions that touch wheelbarrel1Frame assuming a thread sync/race condition
					  caused the overflow
					  */
				}
			} 
			if (drawWheelBarrel2){
				canvas.drawBitmap(bitmapCache.wheelbarrow[0], wheelbarrow2X, wheelbarrow2Y, null);
				if (wheelbarrel2Frame>0 && wheelbarrel2Frame < bitmapCache.wheelbarrow.length){
					canvas.drawBitmap(bitmapCache.wheelbarrow[wheelbarrel2Frame], wheelbarrow2X, wheelbarrow2Y, null);	
				}
			}
		}
		
		super.drawSurface(canvas);
		
		if (carrotInMouth>0 && carrotMatrix!=null){
			canvas.drawBitmap(bitmapCache.carrots, carrotMatrix, null);
		}
		
		if (carrotPickCount>0){
			Matrix matrix=new Matrix();
			matrix.postScale(.15f, .15f);
			matrix.postTranslate(10, 10);
			if (carrotPickCount<10){
				if (bitmapCache.bigNumber[carrotPickCount]!=null){
					canvas.drawBitmap(bitmapCache.bigNumber[carrotPickCount], matrix, null);
				}
			} else {
				String s = new String(""+carrotPickCount);
				for (int i = 0; i < s.length(); i++){
					Integer n =  new Integer(""+s.charAt(i));
					if (bitmapCache.bigNumber[n]!=null){
						canvas.drawBitmap(bitmapCache.bigNumber[n], matrix, null);
						matrix.postTranslate((bitmapCache.bigNumber[n].getWidth()*0.15f)+15, 0);
					}					
				}
			}
		}
		
		if (countdownCurrent>0 && bitmapCache.initDone){
			int extraX=0;
			if (countdownCurrent==1)extraX=50;
			canvas.drawBitmap(bitmapCache.bigNumber[countdownCurrent], 40+extraX, 10,null);
		}
		
	}
	
	static final int COUNTDOWN_INTERVAL=800;
	int countdownCurrent=0;
	final AnimationAction countdown = new AnimationAction(COUNTDOWN_INTERVAL,new Runnable(){public void run() {
		synchronized (FeedTheBunny.this){
			if (countdownCurrent>0){
				countdownCurrent--;
				postInvalidate();
				countdown.actionTime = System.currentTimeMillis()+COUNTDOWN_INTERVAL;	
			} else {
				mode=MODE_PICKING;
				pickStartTime=System.currentTimeMillis();
				stopWatteringCanAnimation();
				addAction(carrotsPopingOut);
				addAction(gameTimer);
				friend.sendBunnyTo(195, 20);
				totalPossibleCarrots=0;
				carrotPickCount=0;
				friend.supressAI=true;
				friend.onDoneMovingHandler.add(new Runnable(){public void run(){
					friend.startBunnyAtAttention(60000);
					friend.supressAI=true;
				}});
				
				removeAction(countdown);
			}
		}
	}});
	synchronized protected void startCountDown() {
		countdownCurrent=3;
		countdown.actionTime = System.currentTimeMillis()+COUNTDOWN_INTERVAL;	
		addAction(countdown);
	}
	
	
	synchronized public boolean onTouchEvent (MotionEvent event){
    	super.onTouchEvent(event);
    
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {	
    		if (mode==MODE_PICKING){
    			final int NO_SOUND=1;
    			final int WIN_SOUND=2;
    			final int SAD_SOUND=3;
    			int sound=NO_SOUND;
				int X = (int) (event.getX()*XScalingRecripricol);
				int Y = (int) (event.getY()*YScalingRecripricol); 	
				for (int i = 0; i < collums; i++){
					for (int j = 0; j < rows;j++){
						
						final int top = (int) ((j*VERTICAL_SPACING+MARGIN_TOP-CARROT_RADIUS+CARROT_OFFSETY));
						final int left = (int) ((i*HORIZANTAL_SPACING+MARGIN_LEFT-CARROT_RADIUS+CARROT_OFFSETX));
						
						
						final int right = (int) ((i*HORIZANTAL_SPACING+MARGIN_LEFT+CARROT_RADIUS+CARROT_OFFSETX));
						final int bottom = (int) ((j*VERTICAL_SPACING+MARGIN_TOP+CARROT_RADIUS+CARROT_OFFSETY));
						
						Rect r = new Rect(left,top,right,bottom);
						if (r.contains(X, Y)) {
							if (isACarrot[i][j]==CAROT_SEEDLING ||isACarrot[i][j]==CAROT_READY_TO_PICK || isACarrot[i][j]==CAROT_VERY_READY_TO_PICK
							|| (moleStage[i][j] >= MOLE_1 && moleStage[i][j] <= MOLE_6)){
								sound=WIN_SOUND;
								mainBunny.animationInterval=50;//100;
								mainBunny.distanceOpen=160;
								//holdThatCarrot[i][j] = true;
								feedShowInterval -= FEED_SHOW_SPEEDUP;
								carrotPickCount++;
								removeAction(molePopingOut);
								isACarrot[i][j]=CAROT_SELECTED_FOR_PICKING;
								moleStage[i][j]=MOLE_NO_MOLE;
								
								if (whichWheelBarrel==0){
									wheelbarrel1Frame++;
									if (wheelbarrel1Frame>=10){
										wheelbarrel1Frame=9;
									}
								} else {
									wheelbarrel2Frame++;
									if (wheelbarrel2Frame>=10){
										wheelbarrel2Frame=9;
									}
								}
								
								
								
							} else {
								if (sound!=WIN_SOUND){
									sound=SAD_SOUND;
								}
							}
						}
					}	
				}
				switch (sound){
					case WIN_SOUND:bitmapCache.slotSound();
					break;
					case SAD_SOUND:bitmapCache.sadSound();
					break;
				}
    		}
		}
		
    	return true;
	}
	
	int canX=0;
	int canY=0;
	
	void stopWatteringCanAnimation() {
		if (watteringCanAnimation!=null){
			removeAction(watteringCanAnimation.parent);
			currentAnimation.remove(watteringCanAnimation);
			watteringCanAnimation=null;
		}
	}
	
	AnimationFrames watteringCanAnimation;
	void startWatteringCanAnimation() {
		if (watteringCanAnimation!=null)
			return;
		final LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		int X=canX*HORIZANTAL_SPACING;
		int Y=canY*VERTICAL_SPACING+15;
		
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[0],250,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[0],250,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[1],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[2],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[3],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[4],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[5],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[3],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[4],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[5],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[3],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[4],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[5],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[3],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[4],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[5],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[3],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[4],150,0));
		frames.add(new AnimationFrame(X,Y,bitmapCache.waterCan[5],150,0));
		
		
		frames.getLast().extraAction = new Runnable(){public void run() {
			canX++;
			if (canX == 3){
				canX=0;
				canY++;
				if (canY==3)
					canY=0;
			}
			Iterator<AnimationFrame> i = frames.iterator();
			while (i.hasNext()){
				AnimationFrame frame = i.next();
				frame.X = canX*HORIZANTAL_SPACING;
				frame.Y = canY*VERTICAL_SPACING+15;
			}
		}};
	
		watteringCanAnimation = new AnimationFrames();
		watteringCanAnimation.frames = frames;
		watteringCanAnimation.endless=true;
		playAnimationFrames(watteringCanAnimation);

	}
	
}
