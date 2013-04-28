package com.catglo.dashplayground;

import java.util.ArrayList;
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
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.SoundPool;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;


/***************************************************************
 * AnimationActions is a class for describing timings and 
 * actions that drive animations in a DrawingView
 *
 *
 */
class AnimationAction implements Comparable<AnimationAction> {
	AnimationAction(long actionTime, Runnable action){
		this.actionTime = actionTime;
		this.action = action;
	}
	long actionTime;
	Runnable action;
	String name;
	
	public int compareTo(AnimationAction arg0) {
		AnimationAction other = arg0;
		if (other.actionTime<actionTime) {
			return 1;
		} else if (other.actionTime>actionTime){
			return -1;
		} 
		return 0;
	}
}


abstract public class DrawingView extends View implements Runnable,OnGestureListener {
	abstract int whoAmI();
	abstract int whereAmI();
	
	boolean menuMode=false;
	LinkedList<Integer> menuForBabies = new LinkedList<Integer>();
	Point babyMenuPoint = null; //set to null to hide menu
	Long babyMenuStartTime;
	private static final int BABY_MENU_ZOOM_TIME=250;
	Rect babyMenuRect=null;
	
	public Bitmap backgroundImage;
	
	//The upper left corner of the window in big grid coordinates
	public Point origin;
    
	//The position of the friend bunny on the big map
	protected static Point friendPoint = new Point();
	AnimationAction friendAction;
	FriendBunnyAnimation friend;
	
	static final int NUMBER_OF_BABIES=10;
	protected static Point[] babyPoint = {new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200),new Point(300,200)};
	AnimationAction[] babyAction = new AnimationAction[NUMBER_OF_BABIES];
	BabyBunnyAnimation[] baby = new  BabyBunnyAnimation[NUMBER_OF_BABIES];
	static BabyInfo[] babyInfo = {new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo(),new BabyInfo()};

	public static boolean showDebug=false;
	boolean suspendClick=false;      //flag to suspend onTouch events 
	
	MainBunnyAnimation mainBunny;
	boolean drawMainBunny=true;
	
	
	boolean supressBabyActions=false;
	
	long timeStoppedMoving=-1;
	//long timeStartMoving=-1;
	public LinkedList<MenuItem> actionAreas = new LinkedList<MenuItem>();
	public void addEventRect(Rect rect, MenuExecutor executor) {
		actionAreas.add(new MenuItem(rect,executor));
	}
	
	boolean processedActionAreas=false;
	
	private static final int MAX_ACTIONS=255;
	private static final int SYSTEM_MINIMUM_THREAD_WAKEUP=10; 
	private AnimationAction[] actions = new AnimationAction[MAX_ACTIONS];
	long currentWaitTime=0;
	int actionsLength=0;
	void addAction(AnimationAction action){
		if (containsAction(action)){
			//throw (new IllegalStateException());
			return;
		}
		synchronized(actions){
			if (actionsLength>=MAX_ACTIONS){
				throw(new IllegalStateException());
			}
			actions[actionsLength++]=action;
			if (action.actionTime < currentWaitTime){
				synchronized (thread) {
					thread.notify();
				}
			}
		}
	//	Log.i("BUNNY","ADD Action count = "+actionsLength);
	}
	
	void removeAction(AnimationAction action){
		synchronized(actions){
			boolean removing=false;
			for (int i = 0; i < actionsLength;i++){
				if (removing){
					actions[i-1]=actions[i];
				} else {
					if (action == actions[i]) {
						removing=true;
					}
				} 
			}
			if (removing){
				actionsLength--;
			}
		}
	}
	
	boolean containsAction(AnimationAction action){
		boolean contains=false;
		synchronized(actions){
			for (int i = 0; i < actionsLength;i++){
				if (action==actions[i]){
					contains=true;
				}
			}
		}
		return contains;
	}
	
	protected AnimationAction bunnyAction;

	//New action list plan
	// 
	/************************************
	 * in action loop - determine time threshold for actions to run now
	 * do a 1 pass through entire action items and make a new list of 
	 * items to run right now, which have a timestame of now + 5 or 10mills
	 * also find the smallest timestame. We can save the smallest item.
	 * lock the list while doing this pass
	 * 
	 * run the run now list or if it is empty wait wait the smallest timestamp
	 * the list is unlocked while waiting. after waiting we just loop again 
	 *  
	 * add function need to lock the list and cancel the current wait, if our
	 * wait is less than current minimun also notify the thread
	 * 
	 * delete just locks the list and removes it.
	 */
	
	
	public void run(){
		long time = System.currentTimeMillis();
		
		if (actionsLength==0)
			return;
		
		AnimationAction[] actionsToRun = new AnimationAction[MAX_ACTIONS];
		int runCount=0;
		long now = System.currentTimeMillis();
		
		synchronized(actions){
			currentWaitTime=Long.MAX_VALUE;
			for (int i = 0; i < actionsLength;i++){
				if (actions[i].actionTime < currentWaitTime){
					currentWaitTime=actions[i].actionTime;
				}
				if (actions[i].actionTime < now+SYSTEM_MINIMUM_THREAD_WAKEUP) {
					actionsToRun[runCount++] = actions[i];
					actions[i].actionTime=Long.MAX_VALUE;
				}
			}
		}
		
		if (runCount==0){
			try {
				synchronized (thread) {
					thread.wait(currentWaitTime-now);
				}
			} catch (InterruptedException e) {}
		} else {
			for (int i = 0; i < runCount;i++){
				actionsToRun[i].action.run();	
			}
		}
	}
	
	protected void onBunnyMoved(){}
	protected void onBunnyDoneMoving(){}
	
	protected void drawBackground(Canvas canvas){
		if (bitmapCache.backgroundBitmaps[whereAmI()]==null || bitmapCache.backgroundBitmaps[whereAmI()].isRecycled()) {
			bitmapCache.loadGridPositionBackgrounds(whereAmI());
		}
		
		canvas.drawBitmap(bitmapCache.backgroundBitmaps[whereAmI()], 0, 0, null);
	}
	
	
	boolean drawBunnies=true;
	protected void drawSurface(Canvas canvas){
		if (drawBunnies){
			Rect friendRect = new Rect(friendPoint.x-50,friendPoint.y-50,friendPoint.x+100,friendPoint.y+100);//TODO: une the size of the bitmap instead of 100
			Rect frame = new Rect(origin.x-1,origin.y-1,origin.x+321,origin.y+481);			
			if (Rect.intersects(frame,friendRect)){
				friend.draw(canvas);        
			}
			
			if (drawMainBunny==true)
				mainBunny.draw(canvas);
			
			if (bitmapCache.initDone){
				for (int i = 0; i < NUMBER_OF_BABIES;i++){
					Rect babyRect = new Rect(babyPoint[i].x-50,babyPoint[i].y-50,babyPoint[i].x+100,babyPoint[i].y+100);
					if (Rect.intersects(frame,babyRect)){
						/*Paint p = new Paint();
						Paint pp = new Paint();
						pp.setStyle(Style.STROKE);
							
						if (
							baby[i].info.mode==BabyBunnyAnimation.MODE_WANDERING
							|| baby[i].info.mode==BabyBunnyAnimation.MODE_FOLLOWING)
						{
						
						} else {
						    pp.setColor(Color.RED);	
						}
						int ay = baby[i].actualY;
						int ax = baby[i].actualX;
						Rect r = new Rect(ax-45,ay-60,ax+30,ay);
						
						
						canvas.drawRect(r, pp);*/
						baby[i].draw(canvas);
					}	
		        }
			}
			
			
		}
      
		
        /***********************************************************************
         * Draw triggerable actions based on bunny movement for debug
         * 
         **********************************************************************/
    	for (int i = 0; i < actionAreas.size();i++){
    
        	MenuItem m = actionAreas.get(i);
        	Rect r = scaleRect(m.area);
        	Paint p1 = new Paint();
        	p1.setColor(Color.GREEN);
        	p1.setAlpha(80);
        	Paint p2 = new Paint();
        	p2.setColor(Color.RED);
        	p2.setAlpha(80);
        	
        	if (m.inverted){
        		if (showDebug) canvas.drawRect(r, p2);
        	} else {
        		if (showDebug) canvas.drawRect(r, p1);
        	}
     	}
    	
    	if (butterFlyStage>=0){
    		for (int i =0; i < 3; i++){
	    		Matrix m = new Matrix();
    			float angle = butterFlyDirection[i].getAngle()+90; 
    			m.postTranslate(-bitmapCache.butterfly[butterFlyStage].getWidth()/2, -bitmapCache.butterfly[butterFlyStage].getHeight()/2);
	    		m.postRotate(angle);
	    		m.postTranslate(bitmapCache.butterfly[butterFlyStage].getWidth()/2, bitmapCache.butterfly[butterFlyStage].getHeight()/2);
	    		m.postTranslate(butterflyPoint[i].x,butterflyPoint[i].y);
	    		canvas.drawBitmap(bitmapCache.butterfly[butterFlyStage], m,null);
    		}
    	}
    	
        //End draw
    	if (bitmapCache.initDone){
	    	for (int i = 0; i < howMannyConfettiPieces/2; i++){
				if (confeti[i] != null)
					canvas.drawBitmap(confeti[i].bitmap, (int)confeti[i].X, (int)confeti[i].Y, null);
			}
    	}
          
	}
	
	SwitchViews switcher;
	public void setSwitcher(SwitchViews switcher) {
		this.switcher = switcher;
	}
	
	long menuButtonAnimationStartTime;
	int menuAnimationStep=-1;
	static final int MENU_ANIM_DONE=5;
	static final Point menuButtonStartPoint = new Point(230,27);
	static final Point[] menuButtonFinalPoint = {
		new Point(60,80),
		new Point(190,80),
		new Point(60,180),
		new Point(190,180),
		new Point(60,280),
		new Point(190,280),
		new Point(60,380),
		new Point(190,380)
	};
	static final int MENU_ICON_ANIMATION_INTERVAL = 50;
	final AnimationAction menuIconAnimation = new AnimationAction(MENU_ICON_ANIMATION_INTERVAL,new Runnable(){public void run() {			
		synchronized (DrawingView.this) {
			float ratio = (float)menuAnimationStep/(float)MENU_ANIM_DONE;
			float invRatio = 1f - ratio;
			float zoom = 0.5f + ((menuAnimationStep/MENU_ANIM_DONE)*0.5f);
			
			for (int i = 0; i < 8; i++){
				menuButtonMatrix[i].reset();
				int x = (int) (menuButtonStartPoint.x*invRatio + menuButtonFinalPoint[i].x*ratio);
				int y = (int) (menuButtonStartPoint.y*invRatio + menuButtonFinalPoint[i].y*ratio);
				menuButtonMatrix[i].postScale(zoom,zoom);
				menuButtonMatrix[i].postTranslate(x, y);
			}
			menuMode=true;
			menuAnimationStep++;
			if (menuAnimationStep > MENU_ANIM_DONE){
				removeAction(menuIconAnimation);
				menuAnimationStep=-1;
			}else {
				menuIconAnimation.actionTime = System.currentTimeMillis()+MENU_ICON_ANIMATION_INTERVAL;
			}
			postInvalidate();
		}
	}});

	void startMenuIconAnimation(){
		menuButtonMatrix[0] = new Matrix();
		menuButtonMatrix[1] = new Matrix();
		menuButtonMatrix[2] = new Matrix();
		menuButtonMatrix[3] = new Matrix();
		menuButtonMatrix[4] = new Matrix();
		menuButtonMatrix[5] = new Matrix();
		menuButtonMatrix[6] = new Matrix();
		menuButtonMatrix[7] = new Matrix();
		menuButtonAnimationStartTime = System.currentTimeMillis();
		menuAnimationStep=0;
		menuIconAnimation.actionTime = System.currentTimeMillis()+MENU_ICON_ANIMATION_INTERVAL;
		addAction(menuIconAnimation);
		
	}
	
	
	Matrix[] menuButtonMatrix = new Matrix[8];
	synchronized protected void onDraw(Canvas c){
		
		c.scale(XScaling, YScaling);
		drawBackground(c);
		
		if (currentAnimation!=null){
			Iterator<AnimationFrames> i = currentAnimation.iterator();
			while (i.hasNext()){
				AnimationFrames frames = i.next();
				if (frames.background){
					AnimationFrame animationFrame = frames.frames.get(frames.current);
					Matrix matrix = new Matrix();
					matrix.postTranslate(animationFrame.X, animationFrame.Y);
					if (animationFrame.mirror){
						matrix.preScale(-1.0f,1.0f);
					}
					if (animationFrame.angle!=0){
						matrix.postRotate(animationFrame.angle);
					}
					c.drawBitmap(animationFrame.image, matrix, null);
				}
			}
		}
		
		drawSurface(c);
		  
		if (currentAnimation!=null){
			Iterator<AnimationFrames> i = currentAnimation.iterator();
			while (i.hasNext()){
				AnimationFrames frames = i.next();
				if (frames.background==false){
					AnimationFrame animationFrame = frames.frames.get(frames.current);
					Matrix matrix = new Matrix();
					matrix.postTranslate(animationFrame.X, animationFrame.Y);
					if (animationFrame.mirror){
						matrix.preScale(-1.0f,1.0f);
					}
					if (animationFrame.angle!=0){
						matrix.postRotate(animationFrame.angle);
					}
					c.drawBitmap(animationFrame.image, matrix, null);
				}
			}
		}
		
		if (bitmapCache.initDone) {
			for (int i = howMannyConfettiPieces/2; i < howMannyConfettiPieces; i++){
				if (confeti[i] != null)
					c.drawBitmap(confeti[i].bitmap, (int)confeti[i].X, (int)confeti[i].Y, null);
			}
		}
		
		
		if (babyMenuPoint!=null) {
			Region r = new Region();
			Rect rect = new Rect(0,0,bitmapCache.babyMenuFeed.getWidth(),bitmapCache.babyMenuFeed.getHeight()*4);
			r.set(rect);
			
			
			Matrix m = new Matrix();
			if (babyMenuPoint.x>160){
				m.preTranslate(-bitmapCache.babyMenuFeed.getWidth(), 0);
				r.translate(-bitmapCache.babyMenuFeed.getWidth(), 0);
			}
			if (babyMenuPoint.y>220){
				m.preTranslate(0,-bitmapCache.babyMenuFeed.getHeight()*4);
				r.translate(0,-bitmapCache.babyMenuFeed.getHeight()*4);
			}
			
			long time = System.currentTimeMillis();
			if (time-babyMenuStartTime < BABY_MENU_ZOOM_TIME){
				float zoom = ((float)(time-babyMenuStartTime))/BABY_MENU_ZOOM_TIME;
				m.postScale(zoom, zoom);
				invalidate();
			}
			m.postTranslate(babyMenuPoint.x, babyMenuPoint.y);
			r.translate(babyMenuPoint.x, babyMenuPoint.y);
			babyMenuRect = r.getBounds();
			
			Paint pt = new Paint();
			pt.setAlpha(128+64);
			Paint[] ptt = {pt,pt,pt,pt};
			if (currentBabyMenuItem>=0){
				ptt[currentBabyMenuItem]=null;
			}
			if (currentBabyMenuItem<0){
				ptt[0]=ptt[1]=ptt[2]=ptt[3]=null;
			}
			
			c.drawBitmap(bitmapCache.babyMenuFeed, m, ptt[0]);
			m.preTranslate(0, bitmapCache.babyMenuFeed.getHeight());
			c.drawBitmap(bitmapCache.babyMenuCage, m, ptt[1]);
			m.preTranslate(0, bitmapCache.babyMenuCage.getHeight());
			//c.drawBitmap(bitmapCache.babyMenuFollow, m, ptt[2]);
			//m.preTranslate(0, bitmapCache.babyMenuFollow.getHeight());
			c.drawBitmap(bitmapCache.babyMenuDance, m, ptt[3]);
			m.preTranslate(0, bitmapCache.babyMenuDance.getHeight());
		}
		
		if (menuMode){
			c.drawBitmap(bitmapCache.menuButton[0], menuButtonMatrix[0], null);
			c.drawBitmap(bitmapCache.menuButton[1], menuButtonMatrix[1], null);
			c.drawBitmap(bitmapCache.menuButton[2], menuButtonMatrix[2], null);
			c.drawBitmap(bitmapCache.menuButton[3], menuButtonMatrix[3], null);
			c.drawBitmap(bitmapCache.menuButton[4], menuButtonMatrix[4], null);
			c.drawBitmap(bitmapCache.menuButton[5], menuButtonMatrix[5], null);
			c.drawBitmap(bitmapCache.menuButton[6], menuButtonMatrix[6], null);
			c.drawBitmap(bitmapCache.menuButton[7], menuButtonMatrix[7], null);
		}
		
		
		Matrix hatAndStarsMatrix = new Matrix();
		if (lastDownOnMagicHat || menuAnimationStep!=-1) {
			if (menuAnimationStep!=-1){
				hatAndStarsMatrix.preTranslate(-(131/2), -(166/2));
				hatAndStarsMatrix.preRotate(180);
				hatAndStarsMatrix.preTranslate((131/2), (166/2));
			}
			hatAndStarsMatrix.postTranslate(320-120,-20);
		
			c.drawBitmap(bitmapCache.hatAndStars[1], hatAndStarsMatrix,null);
		} else {
			hatAndStarsMatrix.postTranslate(320-60,-20);
			c.drawBitmap(bitmapCache.hatAndStars[0], hatAndStarsMatrix,null);
		}	
		
	}
	
			
	public Rect scaleRect(Rect area) {
		Rect ret = new Rect((int)(area.left),(int)(area.top), (int)(area.right), (int)(area.bottom));
		return ret;
	}

	protected void pause(){
		thread._pause();
		Iterator<AnimationAction> itterator = removeOnMenuExit.iterator();
		while (itterator.hasNext()){
			removeAction(itterator.next());
			itterator.remove();
		}
	}
	
	protected void resume(){
		thread._unpause();
		friend.actualX = friendPoint.x-origin.x;
		friend.actualY = friendPoint.y-origin.y;
			
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].actualX = baby[i].myPoint.x-origin.x;
			baby[i].actualY = baby[i].myPoint.y-origin.y;
			baby[i].supressAI=false;
        }
		babyMenuPoint = null; //to hide baby menu
	
	}
	
	public void destrory(){
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].parent=null;
		}
		mainBunny.parent=null;
		friend.parent=null;
		thread.interrupt();
	
		
	}
	
	class DrawingThread extends Thread {
        boolean currentlyRunning=true;
     	
        public DrawingThread() {
        }
 
        public void _pause() {
            currentlyRunning=false;
        }
      
        @Override
        public void run() {
        	while (!thread.isInterrupted()){
        		if (currentlyRunning) {
        			DrawingView.this.run();
        		} else {
        			try {
						synchronized(thread) {
							thread.wait();
						}
					} catch (InterruptedException e) {
						return;
					}
        		}
        	}
        }
   
        public void _unpause() {
           	currentlyRunning=true;  
           	synchronized(thread){
           		thread.notify();
           	}
        }
	}	
	DrawingThread thread = new DrawingThread();
	protected int hopSound;
	
	long touchTime=System.currentTimeMillis();
	private float XScaling;
	private float YScaling;
	protected float XScalingRecripricol;
	protected float YScalingRecripricol;
	
	int width;

	int height;
	BitmapCache bitmapCache;
	
	protected void setupExitAreas(){
		addEventRect(new Rect(0,30,30,450), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
		
		addEventRect(new Rect(290,30,320,450), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
		
		/*addEventRect(new Rect(0,450,320,480), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.BOTTOM);
			}
        });
		
		addEventRect(new Rect(0,0,320,30), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.TOP);
			}
        });*/
	}

	ConfettiAnimation[] confeti = new ConfettiAnimation[100];

	private GestureDetector gestureDector;
	private OnTouchListener gestureListener;

	public DrawingView(Context context,int width,int height, BitmapCache bitmapCache, Point origin) {
		super(context);
	
		this.origin = origin;
		this.bitmapCache = bitmapCache;
		
	    
	  
		thread.start();
		thread._pause();
        
        XScaling = (float)width/320f;
        YScaling = (float)height/480f;
        XScalingRecripricol = 1/XScaling;
        YScalingRecripricol = 1/YScaling;
        
        this.width = width;  
        this.height = height;
    	
    	       
    	
        //Dummy animation which we will replace
        bunnyAction = new AnimationAction(Long.MAX_VALUE,new Runnable(){public void run() {}});
        bunnyAction.name="BUNNY MOVEMENT";
        
        friendAction = new AnimationAction(Long.MAX_VALUE,new Runnable(){public void run() {}});
        friendAction.name="friend";
        
    
        
        BunnyInfo info = new BunnyInfo();
		info.frames =  bitmapCache.main;
        mainBunny = new MainBunnyAnimation(this,bunnyAction, info);
	    
        info = new BunnyInfo();
		info.frames =  bitmapCache.friend;
        friend = new FriendBunnyAnimation(friendAction, info, friendPoint);
	  
        
        addAction(bunnyAction);
        addAction(friendAction);
        
        
        for (int i = 0; i < NUMBER_OF_BABIES;i++){
        	babyAction[i] = new AnimationAction(Long.MAX_VALUE,new Runnable(){public void run() {}});
        	babyAction[i].name="baby"+i;
            baby[i] = new BabyBunnyAnimation(babyAction[i],babyPoint[i],babyInfo[i]);
            baby[i].parent=this;
            addAction(babyAction[i]);
            startBabyAI(babyPoint[i],baby[i]);
        }
        mainBunny.parent=this;
		friend.parent=this;
		 
        
        mainBunny.addBunnyNotMovingAnimations();
        
        setupExitAreas();
        
        
        startFriendAI();    
        
        
        gestureDector = new GestureDetector(this);
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(final View v, final MotionEvent event) {
				if (gestureDector.onTouchEvent(event)) return true;
				return false;
			}
		};
		setOnTouchListener(gestureListener);
		
		mainBunny.supressActions=false;
		
	      
	}
	
	void babyEnterCage(){
		
	}
	
	void helpSystem(){
		
	}
	
	Point debugTap = new Point();

	static final int BUTTON_HEIGHT=75;
	static final int BUTTON_WIDTH=75;
	
	long tapTime;
	int tapCount=0;
	boolean lastDownOnMagicHat=false;
	boolean processBabyMenu=true;
	int currentBabyMenuItem=-1;
    synchronized public boolean onTouchEvent (MotionEvent event){
    	super.onTouchEvent(event);
    	boolean handled=false;
    	int x = (int) (event.getX()*XScalingRecripricol);
    	int y = (int) (event.getY()*YScalingRecripricol);
    	
    	if (x > 320-75 && y < (75) && event.getAction() == MotionEvent.ACTION_DOWN){
    		lastDownOnMagicHat=true;
    		postInvalidate();
    		return true;
    	}
    	if ((x < 320-75 || y > (75)) && event.getAction() == MotionEvent.ACTION_MOVE){
    		lastDownOnMagicHat=false;
    		postInvalidate();
    	}
        	
    	if (x > 320-75 && y < (75) && event.getAction() == MotionEvent.ACTION_UP && lastDownOnMagicHat==true){
    		lastDownOnMagicHat=false;
    		if (menuMode==false){
    			startMenuIconAnimation();
    			invalidate();
    			mainBunny.sendBunnyTo(165, 220);
    			mainBunny.supressActions=true;
    		} else {
    			menuMode=false;
    			mainBunny.supressActions=false;
    			invalidate();
    		}
    		return true;
    	}
    	
    	if (menuMode){
    		if (event.getAction() == MotionEvent.ACTION_DOWN){
	    		if (y > menuButtonFinalPoint[0].y && y < menuButtonFinalPoint[0].y+BUTTON_HEIGHT) { //first row of buttons
	    		
	    			if (x > menuButtonFinalPoint[0].x && x < menuButtonFinalPoint[0].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.FEED_THE_BUNNY,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			} else if (x > menuButtonFinalPoint[1].x && x < menuButtonFinalPoint[1].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.TIC_TAC_TOE,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			} 
	    		} else if (y > menuButtonFinalPoint[2].y && y < menuButtonFinalPoint[2].y+BUTTON_HEIGHT) { //first row of buttons
	    			if (x > menuButtonFinalPoint[2].x && x < menuButtonFinalPoint[2].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.BUS_DRIVING_GAME,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			} else if (x > menuButtonFinalPoint[3].x && x < menuButtonFinalPoint[3].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.MINI_GOLF,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			}
	    		} else if (y > menuButtonFinalPoint[4].y && y < menuButtonFinalPoint[4].y+BUTTON_HEIGHT) { //first row of buttons
	    			if (x > menuButtonFinalPoint[4].x && x < menuButtonFinalPoint[4].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.BABY_BUNNIES,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			} else if (x > menuButtonFinalPoint[5].x && x < menuButtonFinalPoint[5].x+BUTTON_WIDTH) {
	    				switcher.switchToView(BunnyGames.JUMP_ROPE,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			}
	    		} else if (y > menuButtonFinalPoint[6].y && y < menuButtonFinalPoint[6].y+BUTTON_HEIGHT) { //first row of buttons
	    			if (x > menuButtonFinalPoint[6].x && x < menuButtonFinalPoint[6].x+BUTTON_WIDTH){
	    				switcher.switchToView(BunnyGames.HOPSCOTCH,whoAmI());
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    				onMenuExit();
	    			} else if (x > menuButtonFinalPoint[7].x && x < menuButtonFinalPoint[7].x+BUTTON_WIDTH) {
	    				//help system
	    				helpSystem();
	    				menuMode=false;
	    				mainBunny.supressActions=false;
	    			}		
	    		}
    		}
    		return handled;
    	}
    	
    	
    	if (babyMenuPoint!=null && babyMenuRect!=null && processBabyMenu==true){
    		int menuItemY = babyMenuRect.top;
			int height = bitmapCache.babyMenuFeed.getHeight();
			int yLocationForMenuBabies=babyMenuPoint.y;
    		int xLocationForMenuBabies=babyMenuPoint.x;
    		if (System.currentTimeMillis()-babyMenuStartTime < BABY_MENU_ZOOM_TIME){
    			return true;
    		}
    		
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
    			if (x > babyMenuRect.left && x < babyMenuRect.right){
    				
    				if (babyMenuRect.right>300){
    					xLocationForMenuBabies = babyMenuRect.left-20;
    				} else {
    					xLocationForMenuBabies = babyMenuRect.right+20;
    				}
    			
    				if (y > menuItemY && y < menuItemY+height){
    					//Feed
    					currentBabyMenuItem=0;
    					yLocationForMenuBabies = menuItemY+height;
    				} else if (y > menuItemY+height && y < menuItemY+(2*height)){
    					//CAGE
    					currentBabyMenuItem=1;
    					yLocationForMenuBabies = menuItemY+height*2;
        			} else if (y > menuItemY+(2*height) && y < menuItemY+(3*height)){
        				//Dance
        				currentBabyMenuItem=2;
    					yLocationForMenuBabies = menuItemY+height*3;
    				}
    			} else if (event.getAction() == MotionEvent.ACTION_DOWN){
    				currentBabyMenuItem=-1;
    			}
    			Iterator<Integer> iterator = menuForBabies.iterator();
				while (iterator.hasNext()){
					Integer b = iterator.next();
					baby[b].actualX = xLocationForMenuBabies;
					//baby[b].myPoint.x = xLocationForMenuBabies+origin.x;
					baby[b].actualY = yLocationForMenuBabies;
					//baby[b].myPoint.y = yLocationForMenuBabies+origin.y;
					//baby[b].setTarget(xLocationForMenuBabies, yLocationForMenuBabies);
					baby[b].startBunnyAtAttention(100000);
					if (babyMenuPoint.x>160){
    					xLocationForMenuBabies -=20;
    				} else {
    					xLocationForMenuBabies +=20;
    				}
					postInvalidate();
				}
    		} else
    		if (event.getAction() == MotionEvent.ACTION_UP){
    			if (x > babyMenuRect.left && x < babyMenuRect.right){
    				
    				if (y > menuItemY && y < menuItemY+(3*height)){
    					babyMenuPoint=null;
    					mainBunny.supressActions=false;
    					Iterator<Integer> iterator = menuForBabies.iterator();
    					while (iterator.hasNext()){
    						Integer b = iterator.next();
    						baby[b].addBunnyNotMovingAnimations();
    						baby[b].supressAI=false;
    					}
    				}
    				
    				if (y > menuItemY && y < menuItemY+height && currentBabyMenuItem==0){
    					//Feed
    					Iterator<Integer> iterator = menuForBabies.iterator();
    					while (iterator.hasNext()){
    						final Integer b = iterator.next();
    						baby[b].actualX = baby[b].myPoint.x-origin.x;
    						baby[b].actualY = baby[b].myPoint.y-origin.y;
    						baby[b].eat();
    					}
    				} else if (y > menuItemY+height && y < menuItemY+(2*height) && currentBabyMenuItem==1){
    					//CAGE
    					Iterator<Integer> iterator = menuForBabies.iterator();
    					while (iterator.hasNext()){
    						final Integer b = iterator.next();
    						baby[b].actualX = baby[b].myPoint.x-origin.x;
    						baby[b].actualY = baby[b].myPoint.y-origin.y;
    						baby[b].supressAI=true;
    						if (whoAmI()==BunnyGames.BABY_BUNNIES){
    							baby[b].sendBunnyTo(150, 150);	
    							baby[b].onDoneMovingHandler.add(new Runnable(){public void run(){
    								babyEnterCage();
    							}});
    						} else {
    							if (whereAmI()<4){
    								baby[b].sendBunnyTo(400, baby[b].actualY);
    							}else{
    								baby[b].sendBunnyTo(0, baby[b].actualY);
    							}
    						}	
    						baby[b].onDoneMovingHandler.add(new Runnable(){public void run(){
								baby[b].info.mode = BabyBunnyAnimation.MODE_IN_CAGE;
								baby[b].babyWanderingAI.actionTime=System.currentTimeMillis()+250;
								//BabysAndCarotPile.cageStage=0;
								baby[b].myPoint.x = 75 + 320*4;
								baby[b].myPoint.y = 50;	
								baby[b].supressAI=false;
							}});
    					}
        			} else if (y > menuItemY+(2*height) && y < menuItemY+(3*height) && currentBabyMenuItem==2){
        		    	//Dance
    					Iterator<Integer> iterator = menuForBabies.iterator();
    					while (iterator.hasNext()){
    						Integer b = iterator.next();
    						baby[b].actualX = baby[b].myPoint.x-origin.x;
    						baby[b].actualY = baby[b].myPoint.y-origin.y;
    						baby[b].randomDance();
    					}
    				}
    			} else if (currentBabyMenuItem==-1){
    				//clear the baby menu if we tap off the menu
    				babyMenuPoint=null;
    				mainBunny.supressActions=false;
    				Iterator<Integer> iterator = menuForBabies.iterator();
    				while (iterator.hasNext()){
						final Integer b = iterator.next();
						baby[b].supressAI=false;
    				}
    				
    			}
    		}
    		else {
    			currentBabyMenuItem=-2;
    		}
    		
    		return true;
    	}
    	
    	
    	
    	/*long time=System.currentTimeMillis();
		if (time-tapTime < 300){
			switch (tapCount){
			case 1: doubleTap();//mainBunny.whoWhoLeftRight();//mainBunny.clap(5);
			break;
			case 2: tripleTap();//mainBunny.rightLeftSpinWhoWho();
			break;
			}
			tapCount++;
		} else {
			tapCount=0;
		}
		tapTime=time;*/
		
    	
    
    	if (suspendClick==true){
    		//Log.i("BUNNY","suspend click on");
    		return handled;
    	}
    	
    	
    	long t = System.currentTimeMillis();
    	if (!supressBabyActions && babyMenuPoint==null){
    		if (event.getAction() == MotionEvent.ACTION_DOWN) {		
    			menuForBabies.clear();
    			debugTap.x = x;
				debugTap.y = y;
				
				//Check for tap on baby bunny
				for (int i = 0; i < NUMBER_OF_BABIES;i++){
					if (baby[i].myPoint.x > origin.x && baby[i].myPoint.x < origin.x+320){
						int ay = baby[i].actualY;
						int ax = baby[i].actualX;
						Rect frame = new Rect(origin.x-1,origin.y-1,origin.x+321,origin.y+481);			
						Rect babyRect = new Rect(babyPoint[i].x-50,babyPoint[i].y-50,babyPoint[i].x+100,babyPoint[i].y+100);
						if (Rect.intersects(frame,babyRect)){
						
							if (x > ax-45 && x < ax+30 
								&& y > ay-60 && y < ay	
								&& baby[i].info.mode==BabyBunnyAnimation.MODE_WANDERING
								|| baby[i].info.mode==BabyBunnyAnimation.MODE_FOLLOWING)
							{
					/*	if (){
							if ((baby[i].info.mode == BabyBunnyAnimation.MODE_WANDERING && 
							     baby[i].state == CharactarAnimation.STATE_STIITNG) 
						 	  || baby[i].info.mode == BabyBunnyAnimation.MODE_FOLLOWING) {*/
							
								baby[i].startBunnyAtAttention(1000);
								menuForBabies.add(i);
								babyMenuPoint = new Point(ax,ay);
								babyMenuStartTime=System.currentTimeMillis();
								currentBabyMenuItem=-1;
								baby[i].supressAI=true;
								baby[i].lastAnimationMirrored=false;
								mainBunny.supressActions=true;
							}
						}
					}
					if (babyMenuPoint!=null){
						return true;
					}
				}
			}
			
			mainBunny.setTarget( (int) (event.getX()*XScalingRecripricol) , (int) (event.getY()*YScalingRecripricol));
			
			
		//	timeStartMoving = initThread;
		//	done=false;
			
			//final Random r = new Random();
			//friend.targetX = r.nextInt(300);
			//friend.targetY = r.nextInt(460);
			
			
			mainBunny.bunnyRunningAnimation();
			synchronized(thread){
           		//thread.notify();
           		thread.notify();
           	}
			////Log.i("BUNNY","done = false");
			handled=true;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
		    float xDiff = mainBunny.actualX- (event.getX()*XScalingRecripricol);
		    float yDiff = mainBunny.getTargetY() - (event.getY()*YScalingRecripricol); 
		    float dist = (float) Math.sqrt(xDiff*xDiff+yDiff*yDiff);
		    if (dist > 100) {
		    	mainBunny.setTarget((int)(event.getX()*XScalingRecripricol),(int)(event.getY()*YScalingRecripricol));
			//	done=false;
			//	//Log.i("BUNNY","done = false");
			
		    }
		    if (mainBunny.getTargetX()==mainBunny.actualX && mainBunny.getTargetY() == mainBunny.actualY){
		    	mainBunny.startBunnyAtAttention(1200);
		    }
	//	    atAttention = true;//TODO: fix this
		    
		//    if (timeStartMoving==-1) {
		    	//timeStartMoving=System.currentTimeMillis();
		  //  	timeStartMoving = initThread;
		//    	done=false;
		    //	//Log.i("BUNNY","done = false");
		  //  }
		    handled=true;
		} else {
		//	atAttention = false;
		}
		
	
		touchTime=t;
		return handled;
    }
    
    LinkedList<AnimationAction> removeOnMenuExit = new LinkedList<AnimationAction>();
	void onMenuExit(){
		addAction(friend.freindAI);
		suspendClick=false;
		mainBunny.supressActions = false;
		
		mainBunny.startBunnyAtAttention(100);
		Iterator<AnimationFrames> i = currentAnimation.iterator();
		while (i.hasNext()){
			AnimationFrames animation = i.next();
			if (animation.endless==false){
				i.remove();
			}
		}
		babyMenuPoint=null;
		mainBunny.supressActions=false;
	}
	
    
    void doubleTap(){
    	if (suspendClick){
    	
    	}else {//mainBunny.whoWhoLeftRight();
    		mainBunny.clap(5);
    	}
    }
    
    void tripleTap(){
    	if (suspendClick){
        	
    	}else {
    		mainBunny.rightLeftSpinWhoWho();
    	}
    }
    
    void enter(int x, int y, boolean mirrored){
    	mainBunny.actualX = (int) x;
		mainBunny.actualY = (int) y;
		mainBunny.setTarget(x,y);
		mainBunny.lastAnimationMirrored = mirrored;
    }
    
   
	private void startFriendAI() {
		friend.setTarget(friendPoint.x-origin.x,friendPoint.y-origin.y);
		friend.freindAI.actionTime=FriendBunnyAnimation.FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis();
		addAction(friend.freindAI);
	} 
	
	private void startBabyAI(Point babyPoint, BabyBunnyAnimation baby) {
		baby.myPoint.x = 320*4+80;
		baby.myPoint.y = 60;
	//	baby.info.mode = BabyBunnyAnimation.MODE_IN_CAGE;
		baby.babyWanderingAI.actionTime=WanderingBunnyAnimation.FRIEND_AT_ACTION_INTERVAL+System.currentTimeMillis();
		addAction(baby.babyWanderingAI);
	}
	
	final Random r = new Random();
	private int howMannyConfettiPieces=0;
	protected void launchConfetti(int howManny) {
		synchronized (DrawingView.this) {
			if (howManny > bitmapCache.confetti.length){
				howManny = bitmapCache.confetti.length;
			}
			howMannyConfettiPieces = howManny;
			for (int i = 0; i < howManny; i++){
				confeti[i] = new ConfettiAnimation(this,r.nextInt(430)-60,480,r.nextInt(20)-10,bitmapCache.confetti[r.nextInt(bitmapCache.confetti.length)],50*i);
				addAction(confeti[i]);
			}

		}
	}
	
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		long time=System.currentTimeMillis();
		if (time-tapTime < 300 && !suspendClick){
			if (velocityX > 0 && Math.abs(velocityY) < 600) { //Tap And Right Fling
				mainBunny.danceSpinnLeft2();
			} 
			if (velocityX < 0 && Math.abs(velocityY) < 600){ //Tap And Left Fling
				mainBunny.danceSpinnRight2();
			} 	
		} else if (!suspendClick) {
			if (velocityX > 0 && Math.abs(velocityY) < 600) { //Right Fling
				mainBunny.danceSpinnLeft();
			} 
			if (velocityX < 0 && Math.abs(velocityY) < 600){ //Left Fling
				mainBunny.danceSpinnRight();
			} 
		}
		tapTime=0;
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	//For fixed animations, I need to make this work for more than 1 at a time
	LinkedList<AnimationFrames> currentAnimation= new LinkedList<AnimationFrames>();

	synchronized void playAnimationFrames(final AnimationFrames animation){
		Iterator<AnimationFrames> i = currentAnimation.iterator();
		while (i.hasNext()){
			if (animation == i.next()){
				return;
			}
		}
		AnimationAction playFixedAnimation = new AnimationAction(0,new Runnable(){public void run() {
			//TODO: deal with this exception
			//06-23 21:08:20.433: ERROR/AndroidRuntime(16254): Uncaught handler: thread Thread-16 exiting due to uncaught exception
			//06-23 21:08:20.463: ERROR/AndroidRuntime(16254): java.lang.NullPointerException
			//06-23 21:08:20.463: ERROR/AndroidRuntime(16254):     at com.catglo.dashplayground.DrawingView$1.run(DrawingView.java:697)
			//06-23 21:08:20.463: ERROR/AndroidRuntime(16254):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:153)
			//06-23 21:08:20.463: ERROR/AndroidRuntime(16254):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:300)

			synchronized(DrawingView.this) {
	
				final AnimationFrame frame = animation.frames.get(animation.current);
				int time;
				
				if (animation.current==0)
					time=0;
				else 
					time = animation.frames.get(animation.current-1).time;
				
				animation.parent.actionTime=System.currentTimeMillis()+time;
				
				
				postInvalidate();
				if (animation.current == animation.frames.size()-1) {
					if (animation.endless){
						animation.current=0;
					} else {
						removeAction(animation.parent);
						currentAnimation.remove(animation);
					}
				} else {
					animation.current++;
				}
				
				if (frame.extraAction!=null)
					frame.extraAction.run();
				
			}
		}});

		//Iterator<AnimationFrame> i = animation.frames.iterator();
		//while (i.hasNext()){
		//	i.next().parent = animation;
		//}
		animation.parent = playFixedAnimation;
		animation.current=0;
		/*07-02 11:06:52.623: ERROR/AndroidRuntime(7661): Uncaught handler: thread Thread-25 exiting due to uncaught exception
		07-02 11:06:52.693: ERROR/AndroidRuntime(7661): java.lang.NullPointerException
		07-02 11:06:52.693: ERROR/AndroidRuntime(7661):     at com.catglo.dashplayground.DrawingView.playAnimationFrames(DrawingView.java:835)
		07-02 11:06:52.693: ERROR/AndroidRuntime(7661):     at com.catglo.dashplayground.BusDrivingGame$3.run(BusDrivingGame.java:458)
		07-02 11:06:52.693: ERROR/AndroidRuntime(7661):     at com.catglo.dashplayground.DrawingView.run(DrawingView.java:204)
		07-02 11:06:52.693: ERROR/AndroidRuntime(7661):     at com.catglo.dashplayground.DrawingView$DrawingThread.run(DrawingView.java:370)
		*/
		
		currentAnimation.add(animation);
		playFixedAnimation.actionTime=System.currentTimeMillis()+animation.frames.get(0).time;
		addAction(playFixedAnimation);
		if (animation.endless==false)
			removeOnMenuExit.add(playFixedAnimation);
	}
	
	static final int BUTTERFLY_INTERVAL =100;
	static Point[] butterflyPoint = {new Point(175,320),new Point(175,50),new Point(50,220)};
	static Velocity[] butterFlyDirection = {new Velocity(5,5),new Velocity(5,5),new Velocity(5,5)};
	int butterFlyStage=-1;
	int butterFlyMode=0;
	final AnimationAction butterflyFlying = new AnimationAction(BUTTERFLY_INTERVAL,new Runnable(){public void run() {			
		synchronized (DrawingView.this){
			butterFlyMode++;
			if (butterFlyMode>12) {
				butterFlyMode=0;
				butterFlyStage=0;
			}
			switch (butterFlyMode){
			case 0: butterFlyStage=0;
			break;
			case 1: butterFlyStage=1;
			break;
			case 2: butterFlyStage=0;
			break;
			case 3: butterFlyStage=1;
			break;
			case 4: butterFlyStage=2;
			break;
			case 5: butterFlyStage=3;
			break;
			case 6: butterFlyStage=4;
			break;
			}
			
			switch (butterFlyMode){
			case 0: 
			case 1:
			case 2:
			case 3:
			case 4:
				for (int i = 0; i < 3; i++){
					butterFlyDirection[i].setAngle(butterFlyDirection[i].getAngle() + r.nextInt(40)-20);
				};
			
			case 11:
			case 9:
			case 7: butterFlyStage = 5;
			break;
			
			case 12:
			case 8:
			case 10:
				butterFlyStage = 4;
			break;
			}
			
			for (int i = 0; i < 3; i++){
				butterflyPoint[i].x += butterFlyDirection[i].x;
				butterflyPoint[i].y += butterFlyDirection[i].y;
		
				if (butterflyPoint[i].x<0)
					butterflyPoint[i].x=320;
				if (butterflyPoint[i].x>320)
					butterflyPoint[i].x=0;
				if (butterflyPoint[i].y<0)
					butterflyPoint[i].y=480;
				if (butterflyPoint[i].y>480)
					butterflyPoint[i].y=0;
			}
			
			if (butterFlyStage >= bitmapCache.butterfly.length){
				butterFlyStage=0;
			}
			postInvalidate();
			butterflyFlying.actionTime = System.currentTimeMillis()+BUTTERFLY_INTERVAL;
		}
	}});
}
