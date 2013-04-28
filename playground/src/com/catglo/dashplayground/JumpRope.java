package com.catglo.dashplayground;

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
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Path.FillType;
import android.media.MediaPlayer;
import android.os.Debug;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;



public class JumpRope extends DrawingView{
	int whoAmI(){
		return BunnyGames.JUMP_ROPE;
	}
	int whereAmI(){
		return 5;
	}
	MediaPlayer mp;
	void helpSystem(){
		mp.start(); 
	}
	protected void setupExitAreas(){
		addEventRect(new Rect(0,212,60,330), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
		addEventRect(new Rect(260,227,320,332), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
	}

	boolean hideJumpButton=false;
	
	synchronized void onMenuExit(){
		super.onMenuExit();
		removeAction(countdown);
		ropeSpinMode=ROPE_NOT_SPINNING;
		drawJumper=false;
		jumpFrame=0;
		suspendClick=false;
		friend.supressAI = false;
		countdownCurrent=0;
		hideJumpButton=false;
		jumpOffset=0;
		jumpingStep=0;
		currentAnimation.remove(jumperWalkAway);
		currentAnimation.remove(jumperWalkingUpAnimation);
	}
	
	private AnimationFrame[] frames;
	
	int jumper=0;
	int jumpFrame=0;

	private Rect avoidRope;

	private int shortClap;

	private int familyClap;

	private int femaleCheer;

	public JumpRope(Context context,int width, int height, BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		startSwingSetAnimation();
		
		jumpButton = bitmapCache.jumpButton; 
		mp = MediaPlayer.create(getContext(), R.raw.jumprope_help);
		 
		
		
		ropeSpinningLoop.actionTime = System.currentTimeMillis()+ROPE_SPIN_INTERVAL;
		//synchronized (actions) 
		//{
			addAction(ropeSpinningLoop);
		//}
		
		jumpButton = bitmapCache.jumpButton; 
	
	
		avoidRope = new Rect(ROPE_LEFT-10,ROPE_TOP-10,ROPE_RIGHT+10,ROPE_BOTTOM+10);
		Rect avoidButton = new Rect(JUMPBUTTON_X,JUMPBUTTON_Y,JUMPBUTTON_X+bitmapCache.jumpButton.getWidth(),JUMPBUTTON_Y+bitmapCache.jumpButton.getHeight());
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].areasToAvoid.add(avoidButton);
	    }
		addAction(seeSaw);
	}
	
	static final int SEESAW_INTERVAL=300;
	static int seesawStage=0;
	final AnimationAction seeSaw = new AnimationAction(SEESAW_INTERVAL,new Runnable(){public void run() {
		synchronized (JumpRope.this){
			JumpRope.seesawStage++;
			if (JumpRope.seesawStage>= bitmapCache.seesaw.length)
				JumpRope.seesawStage=0;
			postInvalidate();
			seeSaw.actionTime=System.currentTimeMillis()+SEESAW_INTERVAL;
		}
	}});
	
	
	protected void pause(){
		super.pause();
	}
	
	synchronized protected void resume(){
		super.resume();
		ropeSpinMode=ROPE_NOT_SPINNING;
		drawJumper=false;
		jumpFrame=0;
	}


	
	final static int ROPE_TOP=50;
	final static int ROPE_BOTTOM=300;
	final static int ROPE_SPIN_INTERVAL=50;
	final static int ROPE_MID_POINT=(((ROPE_BOTTOM-ROPE_TOP)/2)+ROPE_TOP);
	final static int ROPE_LEFT = 70;
	final static int ROPE_RIGHT = 250;
	
	int ropeSpinStage=0;
	static final int ROPE_TOP_DOWN=1;
	static final int ROPE_MIDPOINT_DOWN=2;
	static final int ROPE_BOTTOM_UP=3;
	static final int ROPE_MIDPOINT_UP=4;
	
	static final int ROPE_NOT_SPINNING=5;
	static final int ROPE_STUCK_DOWN = 6;
	static final int ROPE_STUCK_ON_FEET = 7;
	boolean soundBoingFlip=true;
	
	
	int ropeSpinMode=ROPE_NOT_SPINNING;
	int atRope=0;
	int ropeSpeed=15;
	
	boolean drawJumper=false;
	int numberOfJumps;
	final AnimationAction ropeSpinningLoop = new AnimationAction(ROPE_SPIN_INTERVAL,new Runnable(){public void run() {
		synchronized (JumpRope.this){
			if (ropeSpinMode!=ROPE_NOT_SPINNING 
			&&  ropeSpinMode!=ROPE_STUCK_DOWN 
			&&  ropeSpinMode!=ROPE_STUCK_ON_FEET) 
			{
				ropeSpinStage+=ropeSpeed;
				int ropeRange = ((ROPE_BOTTOM-ROPE_TOP)/2);
				int lastRopeSpinMode = ropeSpinMode;
				
				if (ropeSpinStage>ropeRange){
					ropeSpinStage=0;
					ropeSpinMode++;
					if (ropeSpinMode>ROPE_MIDPOINT_UP) {
						ropeSpinMode=ROPE_TOP_DOWN;
						numberOfJumps++;
						if ((numberOfJumps&3)==3){
							ropeSpeed+=1;
						}
					}
				}
				if (atRope>1){
					mainBunny.setRopeSpinStage(ropeSpinStage);
					friend.setRopeSpinStage(ropeSpinStage);
				}
				if (lastRopeSpinMode==ROPE_MIDPOINT_DOWN && ropeSpinMode==ROPE_BOTTOM_UP ){
					if (jumpFrame==0) {
	 					failedToJump();
						bitmapCache.sadSound();
					} else {
						//if (soundBoingFlip){
					//		soundBoingFlip=false;
						//	bitmapCache.boing1();
						//} else {
						//	soundBoingFlip=true;
							bitmapCache.boing2();
					//	}	
					}
				}
				postInvalidate();
			}	
		}
		ropeSpinningLoop.actionTime = System.currentTimeMillis()+ROPE_SPIN_INTERVAL;
	}});
	
	int jumpingStep=0;
	static final int JUMP_INTERVAL=50;
	static final int JUMP_STEP_LENGTH=10;
	static final int JUMP_SPEED=20;
	final AnimationAction jumpUpAndDown = new AnimationAction(JUMP_INTERVAL,new Runnable(){public void run() {
		synchronized (JumpRope.this){
			jumpingStep++;
		
			if (jumpingStep <= (JUMP_STEP_LENGTH/2)){
				jumpOffset+=JUMP_SPEED;
			} else {
				jumpOffset-=JUMP_SPEED;
			}
			
			if (jumpingStep > (JUMP_STEP_LENGTH/4) && jumpingStep < ((JUMP_STEP_LENGTH/4)*3) ){
				jumpFrame=2;
			} else {
				jumpFrame=1;
			}
			
			
			postInvalidate();		
			
			if (jumpingStep>JUMP_STEP_LENGTH){
				removeAction(jumpUpAndDown);
				jumpOffset=0;
				jumpingStep=0;
				jumpFrame=0;
			} else {
				jumpUpAndDown.actionTime = System.currentTimeMillis()+JUMP_INTERVAL;
			}
		}
	}});
	
	
	int jumperX = 140;
	int jumperY = 290;
	int jumpOffset=0;
	
	static final int JUMPBUTTON_X=80;
	static final int JUMPBUTTON_Y=310;
	static final int SEESAW_X = -75;
	static final int SEESAW_Y = 50;
	
	protected void drawSurface(Canvas canvas){
		if (hideJumpButton || bitmapCache.initDone==false){
			canvas.drawBitmap(bitmapCache.jumpButtonOff, JUMPBUTTON_X, JUMPBUTTON_Y, null);	
		}else{
			canvas.drawBitmap(jumpButton, JUMPBUTTON_X, JUMPBUTTON_Y, null);
		}
		
		canvas.drawBitmap(bitmapCache.seesaw[JumpRope.seesawStage], SEESAW_X,SEESAW_Y, null);
		
		Paint p = new Paint();
		Path path = new Path();
		p.setStrokeWidth(9);
		switch (ropeSpinMode){
		case ROPE_TOP_DOWN:
			path.arcTo(new RectF(ROPE_LEFT,ROPE_TOP+ropeSpinStage,ROPE_RIGHT,ROPE_BOTTOM-ropeSpinStage), 180, 180);
			break;
		case ROPE_MIDPOINT_DOWN:
			path.arcTo(new RectF(ROPE_LEFT,ROPE_MID_POINT-ropeSpinStage,ROPE_RIGHT,ROPE_MID_POINT+ropeSpinStage), 0, 180);
			break;
		case ROPE_BOTTOM_UP:
			path.arcTo(new RectF(ROPE_LEFT,ROPE_TOP+ropeSpinStage,ROPE_RIGHT,ROPE_BOTTOM-ropeSpinStage), 0, 180);
			break;
		case ROPE_MIDPOINT_UP:
			path.arcTo(new RectF(ROPE_LEFT,ROPE_MID_POINT-ropeSpinStage,ROPE_RIGHT,ROPE_MID_POINT+ropeSpinStage), 180, 180);
			break;
		case ROPE_STUCK_DOWN:
		case ROPE_STUCK_ON_FEET:
			path.arcTo(new RectF(ROPE_LEFT,ROPE_TOP,ROPE_RIGHT,ROPE_BOTTOM), 0, 180);
			break;
		}
		
		if (ropeSpinMode > ROPE_MIDPOINT_DOWN){
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(3);
			canvas.drawPath(path, p);
		}
		
		super.drawSurface(canvas);
		
		if (drawJumper) {
			canvas.drawBitmap(bitmapCache.friends[jumper].jumping[jumpFrame], 
					          jumperX,
					          jumperY-jumpOffset-bitmapCache.friends[jumper].jumping[jumpFrame].getHeight(), null);
		}
		
		if (ropeSpinMode <= ROPE_MIDPOINT_DOWN){
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(3);
			canvas.drawPath(path, p);
		}
		
		if (countdownCurrent>0 && bitmapCache.initDone){
			int extraX=0;
			if (countdownCurrent==1)extraX=50;
			canvas.drawBitmap(bitmapCache.bigNumber[countdownCurrent], 40+extraX, 10,null);
		}
		
		//int ropeRange = ((ROPE_BOTTOM-ROPE_TOP)/2);
		
		//if (ropeSpinMode==ROPE_MIDPOINT_DOWN && ropeSpinStage > ((ropeRange-(ropeRange*0.1)))){
		//	p = new Paint();
		//	p.setStrokeWidth(50);
		//	canvas.drawPoint(100, 300, p);
		//}
		
		
		
		
		if (numberOfJumps>0 && bitmapCache.initDone){
			Matrix matrix=new Matrix();
			matrix.postScale(.15f, .15f);
			matrix.postTranslate(10, 10);
			if (numberOfJumps<10){
				canvas.drawBitmap(bitmapCache.bigNumber[numberOfJumps], matrix, null);
			} else {
				String s = new String(""+numberOfJumps);
				for (int i = 0; i < s.length(); i++){
					Integer n =  new Integer(""+s.charAt(i));
					canvas.drawBitmap(bitmapCache.bigNumber[n], matrix, null);
					matrix.postTranslate((bitmapCache.bigNumber[n].getWidth()*0.15f)+15, 0);
				}
			}
		}
		
	}
	
	AnimationFrames jumperWalkAway;
	synchronized protected void failedToJump() {
		if (numberOfJumps > 2){
			launchConfetti(numberOfJumps/2);
		}
		if (numberOfJumps > 20){
			 bitmapCache.femaleCheer();
		}
		if (numberOfJumps > 10){
			bitmapCache.familyClap();
		}
		if (numberOfJumps > 5) {
			bitmapCache.shortClap();
		}
		
		numberOfJumps=0;
		hideJumpButton=true;
		ropeSpinMode=ROPE_STUCK_ON_FEET;
		//jumpFrame = null;//bitmapCache.friends[jumper].jumping.length-1;
		drawJumper=false;
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].areasToAvoid.remove(avoidRope);
	    }
		Iterator<Bitmap> walk = bitmapCache.friends[jumper].getWalking();
		Bitmap[] step = bitmapCache.friends[jumper].stepping;
		Bitmap[] jump = bitmapCache.friends[jumper].jumping;
		Bitmap missed = bitmapCache.friends[jumper].missedJump;
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		frames.add(new AnimationFrame(220-missed.getWidth(),140,missed,450,0));
		frames.add(new AnimationFrame(260,140,missed,250,0,true,0));
		frames.add(new AnimationFrame(260,140,step[1],100,0,true,0));
		frames.add(new AnimationFrame(280,180,step[0],100,0,true,0));
		frames.add(new AnimationFrame(300,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(320,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(340,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(360,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(380,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(400,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(420,180,walk.next(),150,0,true,0));
		frames.add(new AnimationFrame(440,180,walk.next(),150,0,true,0));	
		
		frames.getLast().extraAction = new Runnable(){public void run() {
			hideJumpButton=false;
			if (bitmapCache.initDone)
				jumper++;
			if (jumper == bitmapCache.friends.length)
				jumper=0;
		}};

		currentAnimation.remove(jumperWalkAway);
		jumperWalkAway = new AnimationFrames();
		jumperWalkAway.frames = frames;
		playAnimationFrames(jumperWalkAway);
		
		
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].areasToAvoid.remove(avoidRope);
	    }
		friend.supressAI = false;
		friend.numberedDance(mainBunny.randomDance());
		ropeSpinMode=ROPE_NOT_SPINNING;
	}

	long tapTime=0;
	int tapCount=0;
	private Bitmap jumpButton;
	Point mainBunnyPoint = new Point(40,190); //location of main bunny while holding jump jope
	Point friendPoint = new Point(290,190);
	
	Runnable bunniesGotToJumpRope = new Runnable(){public void run() {
		synchronized(JumpRope.this){
			atRope++;
			if (atRope>1) {
				mainBunny.startBunnySpinningJumprope(true,mainBunnyPoint.x,mainBunnyPoint.y);
				friend.startBunnySpinningJumprope(false,friendPoint.x,friendPoint.y);
				startCountDown();
			}
		}
	}};
	
	synchronized public boolean onTouchEvent (MotionEvent event){
    	Rect jumpButtonRect = new Rect(JUMPBUTTON_X,
                JUMPBUTTON_Y,
                (int)(JUMPBUTTON_X+bitmapCache.jumpButton.getWidth()),
                (int)(JUMPBUTTON_Y+bitmapCache.jumpButton.getHeight()));

    	if (hideJumpButton || bitmapCache.initDone==false){
    		jumpButton=bitmapCache.jumpButton;//CHECKCHECK
    	}else {
	    	if (event.getAction() == MotionEvent.ACTION_DOWN){
	    		if (jumpButtonRect.contains((int)(event.getX()*XScalingRecripricol),(int)(event.getY()*YScalingRecripricol))){
	    			jumpButton = bitmapCache.jumpButtonPressed; 
	    		}
	    	} 
    	
    	
	    	if (event.getAction() == MotionEvent.ACTION_UP){
	    		jumpButton = bitmapCache.jumpButton;
	    		if (jumpButtonRect.contains((int)(event.getX()*XScalingRecripricol),(int)(event.getY()*YScalingRecripricol))
	    		|| (avoidRope.contains((int)(event.getX()*XScalingRecripricol),(int)(event.getY()*YScalingRecripricol))
	    				&& ropeSpinMode!=ROPE_NOT_SPINNING)){
	    			if (ropeSpinMode==ROPE_NOT_SPINNING){
	    				atRope=0;
	    				suspendClick=true;
	    				mainBunny.sendBunnyTo(mainBunnyPoint.x, mainBunnyPoint.y);
	    				jumperWalkUp();
	    				friend.sendBunnyTo(friendPoint.x, friendPoint.y);
	    				mainBunny.onDoneMovingHandler.add(bunniesGotToJumpRope);
	    				friend.onDoneMovingHandler.add(bunniesGotToJumpRope);
	    				friend.supressAI = true;
								
					} else {
	    				if (jumpingStep==0 && jumpFrame != bitmapCache.friends[jumper].jumping.length-1){
	    					jumpingStep++;
	    					jumpUpAndDown.actionTime = System.currentTimeMillis()+JUMP_INTERVAL;
	    					addAction(jumpUpAndDown);
	    					removeOnMenuExit.add(jumpUpAndDown);
	    				}
	    			}	
	    		} else {
	    			if (ropeSpinMode<=ROPE_MIDPOINT_UP){
	    				mainBunny.startBunnyAtAttention(500);
	    				for (int i = 0; i < NUMBER_OF_BABIES;i++){
	    					baby[i].areasToAvoid.remove(avoidRope);
	    			    }
	    			
		    			ropeSpinMode=ROPE_NOT_SPINNING;
		    			drawJumper=false;
		    			jumpFrame=0;
		    			suspendClick=false;
		    			friend.supressAI = false;
	    			}
	    		}
	    	}
    	}
    	boolean retVal = super.onTouchEvent(event);
    	
    	return true;
	}
	
	
	
	
	
	
	static final int COUNTDOWN_INTERVAL=800;
	int countdownCurrent=0;
	final AnimationAction countdown = new AnimationAction(COUNTDOWN_INTERVAL,new Runnable(){public void run() {
		synchronized (JumpRope.this){
			if (countdownCurrent>0){
				countdownCurrent--;
				postInvalidate();
				countdown.actionTime = System.currentTimeMillis()+COUNTDOWN_INTERVAL;	
			} else {
				ropeSpinMode=ROPE_BOTTOM_UP;
				removeAction(countdown);
				hideJumpButton=false;
			}
		}
	}});
	synchronized protected void startCountDown() {
		for (int i = 0; i < NUMBER_OF_BABIES;i++){
			baby[i].areasToAvoid.add(avoidRope);
	    }
		hideJumpButton=true;
		countdownCurrent=3;
		ropeSpinMode=ROPE_STUCK_DOWN;
		countdown.actionTime = System.currentTimeMillis()+COUNTDOWN_INTERVAL;	
		addAction(countdown);
		ropeSpeed=15;
		numberOfJumps=0;
		suspendClick=true;
	}
	
	AnimationFrames jumperWalkingUpAnimation;
	synchronized protected void jumperWalkUp(){
		hideJumpButton=true;
		Iterator<Bitmap> walk = bitmapCache.friends[jumper].getWalking();
		Bitmap[] step = bitmapCache.friends[jumper].stepping;
		LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		frames.add(new AnimationFrame(300,180,walk.next(),150,0));
		frames.add(new AnimationFrame(280,180,walk.next(),150,0));
		frames.add(new AnimationFrame(260,180,walk.next(),150,0));
		frames.add(new AnimationFrame(240,180,walk.next(),150,0));
		frames.add(new AnimationFrame(220,180,walk.next(),150,0));
		frames.add(new AnimationFrame(200,180,walk.next(),150,0));
		frames.add(new AnimationFrame(180,180,walk.next(),150,0));
		frames.add(new AnimationFrame(160,180,walk.next(),150,0));
		frames.add(new AnimationFrame(120,180,step[0],200,0));
		frames.add(new AnimationFrame(120,140,step[1],200,0));
		frames.add(new AnimationFrame(110,120,step[1],200,0));
		frames.add(new AnimationFrame(150,120,bitmapCache.friends[jumper].jumping[0],800,0));
		
		AnimationFrame f = new AnimationFrame(150,120,bitmapCache.friends[jumper].jumping[0],0,0);
		f.extraAction = new Runnable(){public void run() {
			drawJumper=true;
		}};
		frames.add(f);
		
		currentAnimation.remove(jumperWalkingUpAnimation);
		jumperWalkingUpAnimation = new AnimationFrames();
		jumperWalkingUpAnimation.frames = frames;
		playAnimationFrames(jumperWalkingUpAnimation);
	}

	AnimationFrames swingSwtAnimation1;
	AnimationFrames swingSwtAnimation2;
	synchronized void startSwingSetAnimation() {
		
		final LinkedList<AnimationFrame> frames = new LinkedList<AnimationFrame>();
		int x1=220;
		int t1=150;
		
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[0],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[1],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[2],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[3],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[4],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[5],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[6],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[7],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[6],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[5],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[4],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[3],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[2],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[1],t1,0));
		
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[0],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[1],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[2],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[3],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[4],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[5],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[6],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[7],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[6],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[5],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[4],t1,0));
		frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[3],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[2],t1,0));
	    frames.add(new AnimationFrame(x1,56,bitmapCache.swing1[1],t1,0));
	
					
	

		swingSwtAnimation1 = new AnimationFrames();
		swingSwtAnimation1.frames = frames;
		swingSwtAnimation1.background=true;
		swingSwtAnimation1.endless=true;
		playAnimationFrames(swingSwtAnimation1);
		
		
		final LinkedList<AnimationFrame> frames2 = new LinkedList<AnimationFrame>();
		x1=260;
		t1=250;
		
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[0],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[1],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[2],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[3],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[4],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[5],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[6],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[7],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[6],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[5],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[4],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[3],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[2],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[1],t1,0));
		
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[0],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[1],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[2],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[3],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[4],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[5],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[6],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[7],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[6],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[5],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[4],t1,0));
		frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[3],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[2],t1,0));
	    frames2.add(new AnimationFrame(x1,54,bitmapCache.swing2[1],t1,0));
	
	
		swingSwtAnimation2 = new AnimationFrames();
		swingSwtAnimation2.frames = frames2;
		swingSwtAnimation2.background=true;
		swingSwtAnimation2.endless=true;
		playAnimationFrames(swingSwtAnimation2); 
	}
	
}
