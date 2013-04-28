package com.catglo.dashplaygroundfree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;


class AFHB {
	int xoffset;
    int yoffset;
	public AFHB(int X,int Y,Bitmap bitmap, int time, int sound, int xoffset, int yoffset){
		this.X = X;
		this.Y = Y;
		this.image = bitmap;
		this.time = time;
		this.sound = sound;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
	}
	public AFHB(AFHB other){
		X = other.X;
		Y = other.Y;
		image = other.image;
		time =  other.time;
		sound = other.sound;
		xoffset = other.xoffset;
		yoffset = other.yoffset;
	}
	int X;
	int Y;
	Bitmap image;
	int time;
	int sound;
}

class TossSegment extends Object {
	float deflectionProbibility;
	public TossSegment(float xVelocityScaling,
			           float yVelocityScaling,
			           float deceleration,
			           int duration, 
			           float deflectionProb) 
	{
		velocityScalingX=xVelocityScaling;
		velocityScalingY=yVelocityScaling;
		this.deceleration = deceleration;
		this.duration = duration;
		this.deflectionProbibility = deflectionProb;
	}
	public TossSegment(TossSegment other) {
		velocityScalingX = other.velocityScalingX;
		velocityScalingY = other.velocityScalingY;
		deceleration = other.deceleration;
		duration = other.duration;
		deflectionProbibility = other.deflectionProbibility;
	}
	//float velocityX;
	//float velocityY;
	float velocityScalingX;
	float velocityScalingY;
	float deceleration;
	int duration;
	//float bounceDamp;
	//float bounceRedirect;
}

public class HopScotch extends DrawingView implements OnGestureListener {
	int whoAmI(){
		return BunnyGames.HOPSCOTCH;
	}
	int whereAmI(){
		return 6;
	}
	MediaPlayer mp;// = MediaPlayer.create(getContext(), R.raw.hopscotch_help);
	void helpSystem(){
		if (mp!=null){
			mp.start(); 
		}
	}
	protected void setupExitAreas(){
		
		addEventRect(new Rect(0,214,82,322), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
	}

	
	LinkedList<AnimationAction> removeOnMenuExit = new LinkedList<AnimationAction>();
	void onMenuExit(){
		super.onMenuExit();
		gameMode = GAME_MODE_THROWING;
		number=null;
		numberRewardStage=-1;
	}
	
	private MainBunnyAnimation hopScotchBunny;
	
	
	private MediaPlayer player;
	float ballX = 100;
	float ballY = 100;
	
	private int step1;
	private int step2;
	private int step3;
	
	private int gameMode = GAME_MODE_THROWING;
	static final int GAME_MODE_NORMAL=1;
	static final int GAME_MODE_THROWING=2;
	static final int GAME_MODE_BALLFLYING=3;
	static final int GAME_MODE_HOPPING=4;
	static final int GAME_MODE_JUMPING = 5;
	static final int GAME_MODE_GOING_TO_GET_THE_BALL=6;
	static final int GAME_MODE_JUST_GOT_THE_BALL=7;
		
	private static final int BALL_OFFSETY = 12;
	private static final int BALL_OFFSETX = 12;
	private static final int BALL_RADIUS = 20;
	private GestureDetector gestureDector;
	private OnTouchListener gestureListener;
	//private long timeBallThrowStart;
	private float velocityY;
	private float velocityX;
	//private long timeBallThrowStartInterval;
	private int jumpSquare;
	protected long jumpingTimeStart;
	protected long jumpingInterval;
	protected int jumpFrame;
	protected int pickUpFrame;
	
	protected int lastHopscotchNumber=0;

	protected void pause(){
		super.pause();
		if (player!=null && player.isPlaying()==true)
			player.pause();
	}
	 
	protected void resume(){
		super.resume();
		if (player != null)
			player.start();
		mainBunny.supressActions=false;
		
		
	}

	protected void onBunnyDoneMoving(){
		super.onBunnyDoneMoving();
		if (gameMode == GAME_MODE_GOING_TO_GET_THE_BALL){
			gameMode = GAME_MODE_JUST_GOT_THE_BALL;
			mainBunny.sendBunnyTo(190,450);
		} else if (gameMode == GAME_MODE_JUST_GOT_THE_BALL){
			gameMode = GAME_MODE_THROWING;
			mainBunny.supressActions = false;
		}
	}
	
	
	protected void onBunnyMoved(){
		super.onBunnyMoved();
		if (gameMode==GAME_MODE_THROWING || gameMode==GAME_MODE_JUST_GOT_THE_BALL){
			ballX=mainBunny.actualX;
			ballY=mainBunny.actualY;			
		}
	}
	
	
	AFHB[] frames;
	AFHB[] framesDefault;
	Rect[] hopSquares;
	private int rockTapSound;
		
	Matrix numberRewardMatrix;
	int numberRewardStage=-1;
	Bitmap number=null;
	float numberRewardXSize;
	float numberRewardYSize;
	static final int NUMBER_REWARD_INTERVAL=100;
	Paint numberPaint;
	private int shortClap;
	private int femaleCheer;
	private int familyClap;
	Matrix numberHintMatrix;
	
	public HopScotch(Context context,final int width, final int height, BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		 
		mp = MediaPlayer.create(getContext(), R.raw.hopscotch_help);
		
		numberHintMatrix=new Matrix();
		numberHintMatrix.postScale(.15f, .15f);
		numberHintMatrix.postTranslate(10, 10);
		
		hopSquares = new Rect[11];
		hopSquares[1]  = new Rect(25+127,360,  25+191,419);
		hopSquares[2]  = new Rect(25+86 ,301,  25+159,360);
		hopSquares[3]  = new Rect(25+161,302,  25+232,360);
		hopSquares[4]  = new Rect(25+126,245,  25+193,303);
		hopSquares[5]  = new Rect(25+83 ,186,  25+159,246);
		hopSquares[6]  = new Rect(25+156,187,  25+234,245);
		hopSquares[7]  = new Rect(25+127,131,  25+198,187);
		hopSquares[8]  = new Rect(25+86 ,71,   25+164,130);
		hopSquares[9]  = new Rect(25+164,71,   25+237,130);
		hopSquares[10] = new Rect(25+128,12 ,  25+203,70);
		
		
		gestureDector = new GestureDetector(this);
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(final View v, final MotionEvent event) {
				if (gestureDector.onTouchEvent(event)) return true;
				return false;
			}
		};
		setOnTouchListener(gestureListener);
		
		
		//try 
		{ 
	     //    player=MediaPlayer.create(getContext(),  R.raw.mozart_487);
	      //   player.setLooping(true);
	       //  player.setVolume(.2f, .2f);
	     } //catch (Exception e)  { }// handle errors ..   } 
		
		
		//frames[0] = new AnimationFrame
		
		
		
		
		
	    
	}
    int jumpInterval=160;
	
	long tossTime=0;
	
	
	
	

	int sq=1;
	protected void drawSurface(Canvas canvas){
		super.drawSurface(canvas);
		
		canvas.drawBitmap(bitmapCache.ball, ballX, ballY, null);
		
		if (number!=null)
			canvas.drawBitmap(number, numberRewardMatrix, numberPaint);
		
		
		if (numberRewardStage==-1 && bitmapCache.bigNumber[lastHopscotchNumber+1]!=null)
			canvas.drawBitmap(bitmapCache.bigNumber[lastHopscotchNumber+1], numberHintMatrix, null);
		
		
	
	}
	
	public boolean onTouchEvent (MotionEvent event){
    	super.onTouchEvent(event);
    /*
    	if (gameMode==GAME_MODE_NORMAL) {
	    	if (event.getAction() == MotionEvent.ACTION_DOWN) {		
				int X = (int) (event.getX());
				int Y = (int) (event.getY()); 	
			
				final int top = (int) ((ballY-BALL_RADIUS+BALL_OFFSETY)*YScaling);
				final int left = (int) ((ballX-BALL_RADIUS+BALL_OFFSETX)*XScaling);
				final int right = (int) ((ballX+BALL_RADIUS+BALL_OFFSETX)*YScaling);
				final int bottom = (int) ((ballY+BALL_RADIUS+BALL_OFFSETY)*XScaling);
				
				Rect r = new Rect(left,top,right,bottom);
				if (r.contains(X, Y)){ 
					sendBunnyHome(160,380);
				}
	    	}
		}
    	*/
    	if (gameMode==GAME_MODE_THROWING){
    		if (event.getAction() == MotionEvent.ACTION_MOVE) {		
				int X = (int) (event.getX()*XScalingRecripricol);
				int Y = (int) (event.getY()*YScalingRecripricol);
				ballX = X;
				ballY = Y;
				postInvalidate();
				
    		}			
    	}
    	if (friend.actualX<150 && friend.actualX>40)
			friend.sendBunnyTo(40, friend.actualY);
		else if (friend.actualX>150 && friend.actualX<280)
			friend.sendBunnyTo(280, friend.actualY);
		
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	    return true;
	}
	
	
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	synchronized void bunnyJumpHopscotch(){
	
		int time=0;
		final AFHB frame = frames[jumpFrame];
		if (jumpFrame==0)
			time=0;
		else
			time = frames[jumpFrame-1].time;
		
		mainBunny.setNextBunnyAnimation(time,"hopscothc",new Runnable(){public void run() {
			synchronized (HopScotch.this){
				suspendClick=true;
				mainBunny.actualX=frame.X;
				mainBunny.actualY=frame.Y;
				mainBunny.setBunnyBitmap(frame.image, new Matrix());
				
				if (jumpFrame>pickUpFrame) {
					ballX = (mainBunny.actualX + frame.xoffset);
					ballY = (mainBunny.actualY + frame.yoffset);
				}
				
				if (frame.sound!=-1)
					bitmapCache.play(frame.sound);
				jumpFrame++;
				
		//		Log.i("BUNNY","ACTIONS "+actions.size());
				
				if (jumpFrame < frames.length){
					bunnyJumpHopscotch();
				} else {
					suspendClick=false;
					if (lastHopscotchNumber==0 && number == bitmapCache.bigNumber[10]) {
						mainBunny.jumpForJoy(0);
						mainBunny.onDoneMovingHandler.add(new Runnable(){public void run(){mainBunny.sendBunnyTo((int)ballX,(int)ballY);}});
					}
					else {
						mainBunny.sendBunnyTo((int)ballX,(int)ballY);
					}
					addAction(friend.freindAI);
					gameMode=GAME_MODE_THROWING;
					mainBunny.supressActions=false;
				}
			}
		}});
	}
	
	protected void drawBackground(Canvas canvas){
		super.drawBackground(canvas);
	}
	
	static final float XvolScale=.03f;
	static final float YvolScale=.04f;
	static final float tossDamp=0.8f;
	static final int TOSS_INTERVAL=05;
	static final float TOSS_TIME_DAMP=0.4f;//0.4f;
	static final float BOUNCE_DAMP=0.8f;
	
	
	final TossSegment[] blueBall = {  
			//          how fast      decel   time              deflection prob
		new TossSegment(0.0005f,0.0005f   ,1f   ,70,                0.0f),	
		new TossSegment(0.000f,0.000f     ,1f   ,10 ,               0.0f),	
		new TossSegment(0.0005f,0.0005f  ,.4f   ,30,                01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.01f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.0f),	
		new TossSegment(0.0005f,0.0005f  ,.8f   ,5 ,                0.0f),	
		new TossSegment(0.0005f,0.0005f  ,.4f   ,5 ,                0.00f),	
		new TossSegment(0.0005f,0.0005f  ,.4f   ,5 ,                0.00f),	
		new TossSegment(0.0005f,0.0005f  ,.4f   ,5 ,                0.00f),	
		new TossSegment(0.0005f,0.0005f  ,.4f   ,5 ,                0.00f),	
		new TossSegment(0.0008f,0.0008f  ,0.1f  ,Integer.MAX_VALUE, 0.0f),	
	};
	
	TossSegment[] thisBall;
	int thisBallStage;
	long timeStartTossStage;
	float ballStartX;
	float ballStartY;
	
	final Random rand = new Random();
	//Next do the returning animation
	final int[] stopSquareMap = {0,2,4,4,6,7,7,9,10,10,10};
	
	final AnimationAction tossing = new AnimationAction(TOSS_INTERVAL,new Runnable(){public void run() {			
		synchronized (HopScotch.this) {
			
			//ball travels at a near constant speed 
			//each bounce changes its tragectory and dampens speed
			//1 maybe 2 bounces
			TossSegment tossSegment = thisBall[thisBallStage];
			int delta = (int)(System.currentTimeMillis()-timeStartTossStage);
			
			//delta between 1 and 200 or 300
			//velocity between 50 and 3000
			
			ballX = ballStartX + (delta * velocityX * tossSegment.velocityScalingX);
			ballY = ballStartY + (delta * velocityY * tossSegment.velocityScalingY);
			
		//	ballX += velocityX*tossSegment.velocityScalingX;
		//	ballY += velocityY*tossSegment.velocityScalingY;
			
		//	Log.i("BUNNY","BALL "+ballX+" "+ballY+"   "+(System.currentTimeMillis()-timeStartToss));
			
			if (System.currentTimeMillis() >= timeStartTossStage+tossSegment.duration){
				thisBallStage++;
				if (thisBallStage==1)
					bitmapCache.rockTapSound(1f);
					
				if (thisBallStage==3)
					bitmapCache.rockTapSound(0.3f);
				
				
				timeStartTossStage=System.currentTimeMillis();
				Log.i("BUNNY","BALL new toss segment"+thisBallStage);
				ballStartX=ballX;
				ballStartY=ballY;
				velocityX*=tossSegment.deceleration;
				velocityY*=tossSegment.deceleration;
				if (rand.nextFloat() < tossSegment.deflectionProbibility){
					float deflect = rand.nextFloat();
					//if (rand.nextFloat() < 0.9){ //big deflection
					//	deflect-=0.5f;
					//	deflect *= 0.8;
					//}// else { //small deflection
						deflect*=2;
						deflect-=1f;
						deflect *= tossSegment.deflectionProbibility;
						if (deflect!=0)
							Log.i("BUNNY","BALL deflect "+deflect);
					//}
					float diff = velocityY*deflect;
					//velocityY += diff;
					Log.i("BUNNY","BALL deflect xv"+velocityX);
					velocityX -= diff;
					Log.i("BUNNY","BALL deflect xv"+velocityX);
					
				}
			}
			
//			ballX += velocityX*XvolScale;
//			ballY += velocityY*YvolScale;
			//velocityX*=tossDamp;
			//velocityY*=tossDamp;
		
			if (ballX < 10) {
				ballX=10;
				velocityX*=0.1;
				velocityY*=0.1;
			}
			if (ballX>width-10){
				ballX=width-10;
				velocityX*=0.1;
				velocityY*=0.1;
			}
			if (ballY < 10) {
				ballY = 10;
				velocityX*=0.1;
				velocityY*=0.1;
			}
			if (ballY > height-10) {
				ballY=height-10;
				velocityX*=0.1;
				velocityY*=0.1;
			}
			
			
			
			postInvalidate();
			if (System.currentTimeMillis() >= tossTime){
				gameMode=GAME_MODE_JUMPING;
				jumpFrame=0;
				jumpSquare=0;
				for (int i = 1; i <= 10; i++){
					if (hopSquares[i].contains((int)ballX, (int)ballY)){
						gameMode=GAME_MODE_JUMPING;
						jumpSquare=i;
					}
				}
				removeAction(tossing);
				
				if (jumpSquare>0){
					startNumberRewardAnimation(jumpSquare);
					
					/**********************************************************
					 * Here we copy the animation array, removing the 2 frames
					 * for the jump the bunny does not do and modifying the XY
					 * values for the hopFly frame
					 *********************************************************/
					int i = 0;
					int thisSquare = ((jumpSquare)*2); //find frames to remove
					frames = new AFHB[framesDefault.length];
					for (i =0; i < thisSquare-1;i++){
						frames[i] = framesDefault[i];
					}
					//replace the hopFly x/y coordinates 
					frames[i] = new AFHB(framesDefault[i]);
					frames[i].X = (framesDefault[i-1].X + framesDefault[i+3].X)/2;
					frames[i].Y = (framesDefault[i-1].Y + framesDefault[i+3].Y)/2;
					i++;
					//copy the frames up to where we need to inser the pick up frame
					thisSquare = frames.length-(stopSquareMap[jumpSquare]*2)-2;
					pickUpFrame = thisSquare;
					for (; i < thisSquare;i++){
						frames[i] = framesDefault[i+2];
					}
					
					int bunnyStopY = hopSquares[stopSquareMap[jumpSquare]].bottom;
					
					if (ballX > frames[i-1].X){
						frames[i] = new AFHB(framesDefault[i]);
						frames[i].image = hopScotchBunny.bunnies.frames.pickUpRight1;
						frames[i].X = (int) ballX-3;
						frames[i].Y = (int) bunnyStopY;
						frames[i].time = 200;
						
						frames[i+1] = new AFHB(framesDefault[i+1]);
						frames[i+1].image = hopScotchBunny.bunnies.frames.pickUpRight2;
						frames[i+1].X = (int) ballX-3;
						frames[i+1].Y = (int) bunnyStopY;
						frames[i+1].time = 200;
					}else{
						frames[i] = new AFHB(framesDefault[i]);
						frames[i].image = hopScotchBunny.bunnies.frames.pickUpLeft1;
						frames[i].X = (int) ballX+10;
						frames[i].Y = (int) bunnyStopY;
						frames[i].time = 200;
						
						frames[i+1] = new AFHB(framesDefault[i+1]);
						frames[i+1].image = hopScotchBunny.bunnies.frames.pickUpLeft2;
						frames[i+1].X = (int) ballX+10;
						frames[i+1].Y = (int) bunnyStopY;
						frames[i+1].time = 200;	
					}
					
					//copy the rest of the frames
					for (; i < framesDefault.length-2;i++){
						frames[i+2] = framesDefault[i+2];
					}
					
					
					
					//Play the animation
					bunnyJumpHopscotch();
				} else {
					mainBunny.sendBunnyTo((int)ballX,(int)ballY);
					mainBunny.supressActions=true;	
					gameMode=GAME_MODE_GOING_TO_GET_THE_BALL;
				}
				
			} else {
				tossing.actionTime=TOSS_INTERVAL+System.currentTimeMillis();
			}
		} 
	}
	
	
	
	
	Matrix lastMatrix;
	final AnimationAction numberReward = new AnimationAction(NUMBER_REWARD_INTERVAL,new Runnable(){public void run() {			
		synchronized (HopScotch.this) {
			numberRewardXSize++;
			numberRewardYSize++;
			numberRewardXSize=1.1f;
			numberRewardYSize=1.1f;
			
			
			//numberRewardMatrix.postRotate(numberRewardStage*10);
			int scale=numberRewardStage*numberRewardStage*numberRewardStage/10;
			numberRewardMatrix.setRectToRect(new RectF(0,0,150,150), new RectF(-(scale*5),25-(scale*5),150+(scale*10),300+(scale*10)), Matrix.ScaleToFit.CENTER);
			
			if ((numberRewardStage & 0x7)==7)
				numberPaint.setAlpha(255-(numberRewardStage*9));
			
			//Log.i("BUNNY","NUMBER animate");
			numberRewardStage++;
			if (numberRewardStage==20){
				removeAction(numberReward);
				number=null;
				numberRewardStage=-1;
			}
			
			postInvalidate();
			numberReward.actionTime=NUMBER_REWARD_INTERVAL+System.currentTimeMillis();
		}
	}});
	private void startNumberRewardAnimation(final int jumpSquare) {
		if (jumpSquare==lastHopscotchNumber+1) {
			launchConfetti(jumpSquare*3);
				
			switch (jumpSquare){
			case 10: bitmapCache.femaleCheer();
			case 9:
			case 8:
			case 7: bitmapCache.familyClap();
			case 6:
			case 5:
			case 4: bitmapCache.shortClap();
			}
			
			friend.sendBunnyTo(50, 410);
			friend.onDoneMovingHandler.add(new Runnable(){public void run(){
				switch (jumpSquare){
				case 10: friend.dance1();break;
				case 9: friend.jumpForJoy(0); break; 
				case 8: friend.clap(20); break;
				case 7: friend.clap(15); break;
				case 6: friend.jumpForJoy(0); break;
				case 5: friend.jumpForJoy(0); break;
				case 4: friend.jumpForJoy(0); break;
				case 3: friend.clap(9); break;
				case 2: friend.clap(6); break;
				case 1: friend.clap(3); break;
				}
			}});
			
	
			lastHopscotchNumber=jumpSquare;
			if (lastHopscotchNumber==10)
				lastHopscotchNumber=0;
		}
		numberRewardXSize=0.01f; 
		numberRewardYSize=0.01f;
		numberPaint = new Paint();
		//numberRewardMatrix.preTranslate(-75, -75);
		//numberRewardMatrix.preScale(numberRewardXSize, numberRewardYSize);
		//lastMatrix=numberRewardMatrix;
		numberRewardMatrix = new Matrix();
		numberRewardStage=0;
		int scale=numberRewardStage*numberRewardStage/50;
		numberRewardMatrix.setRectToRect(new RectF(0,0,150,150), new RectF(-(scale*5),25-(scale*5),150+(scale*10),300+(scale*10)), Matrix.ScaleToFit.CENTER);
		
		number = bitmapCache.bigNumber[jumpSquare];
		numberReward.actionTime=NUMBER_REWARD_INTERVAL+System.currentTimeMillis();
		addAction(numberReward);
		removeOnMenuExit.add(numberReward);
	}});

	int showQ=10;
	private long timeStartToss;
	synchronized public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		if (bitmapCache.initDone==false)
			return false;
		if (gameMode==GAME_MODE_THROWING/* && velocityY<-50*/){
			mainBunny.supressActions=true;
			
			this.velocityX=velocityX;
			this.velocityY=velocityY;
			gameMode=GAME_MODE_BALLFLYING;
			Log.i("BUNNY","BALL velocity="+velocityX+"   "+velocityY);
	    	
			tossTime= (System.currentTimeMillis()+(long)((Math.abs(velocityY)+Math.abs(velocityX))*TOSS_TIME_DAMP));
			
			tossing.name="BALL MOVEMENT";
			tossing.actionTime=TOSS_INTERVAL+System.currentTimeMillis();
			
			timeStartToss=timeStartTossStage=System.currentTimeMillis();
			thisBallStage=0;
			TossSegment[] myBall = new TossSegment[blueBall.length];
			for (int i = 0; i < blueBall.length; i++){
				myBall[i] = new TossSegment(blueBall[i]);
			}
			myBall[0].duration = Math.abs((int) (velocityY*0.1f));
			myBall[2].duration = Math.abs((int) (velocityY*0.1f));
			
			Log.i("BUNNY","BALL duration "+myBall[0].duration+"  "+myBall[1].duration+"  "+myBall[2].duration);
			
			thisBall=myBall;
			ballStartX = ballX;
			ballStartY = ballY;
			
			removeAction(friend.freindAI);
			if (friend.actualX<150 && friend.actualX>40) {
				friend.sendBunnyTo(40, friend.actualY);
				friend.onDoneMovingHandler.add(new Runnable(){public void run(){
					friend.startBunnyAtAttention(20000);
				}});
				
			}else if (friend.actualX>150 && friend.actualX<280){
				friend.sendBunnyTo(280, friend.actualY);
				friend.onDoneMovingHandler.add(new Runnable(){public void run(){
					friend.startBunnyAtAttention(20000);
				}});
				
			} else {
				friend.startBunnyAtAttention(20000);
			}
			
			
			hopScotchBunny = mainBunny;
			framesDefault = new AFHB[] {
					new AFHB(25+113,410,hopScotchBunny.bunnies.frames.hopFlat, 350, -1,0,0), //
					new AFHB(25+145,412,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+159,376,hopScotchBunny.bunnies.frames.hopFlat, 350, step2,0,0), //1    127,366
					new AFHB(25+129,330,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+110,274,hopScotchBunny.bunnies.frames.hopLeft, 350, step1,0,0), //2
					new AFHB(25+145,270,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+179,264,hopScotchBunny.bunnies.frames.hopRight,350, step1,0,0),//3    159,308
					new AFHB(25+163,259,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+152,251,hopScotchBunny.bunnies.frames.hopFlat, 350, step3,0,0), //4    126,251
					new AFHB(25+162,214,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+111,161,hopScotchBunny.bunnies.frames.hopLeft, 350, step2,0,0), //5    
					new AFHB(25+145,159,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+177,157,hopScotchBunny.bunnies.frames.hopRight,350, step1,0,0),//6    157,191
					new AFHB(25+169,151,hopScotchBunny.bunnies.frames.hopFly  ,100, -1,0,0),  //
					new AFHB(25+159,142,hopScotchBunny.bunnies.frames.hopFlat, 350, step3,0,0), //7    132,135    
					new AFHB(25+145,128,hopScotchBunny.bunnies.frames.hopFly,  100, -1,0,0),  //
					new AFHB(25+111,42,hopScotchBunny.bunnies.frames.hopLeft,  350, step2,0,0), //8
					new AFHB(25+145,51,hopScotchBunny.bunnies.frames.hopFly,   100, -1,0,0),  //
					new AFHB(25+177,38,hopScotchBunny.bunnies.frames.hopRight, 350, step1,0,0),//9
					new AFHB(25+165,22,hopScotchBunny.bunnies.frames.hopFly,   100, -1,   15,-25),  //
					new AFHB(25+159,22,hopScotchBunny.bunnies.frames.hopFlat,  350, step3,0,0), //10   
					new AFHB(25+159,22,hopScotchBunny.bunnies.frames.hopFly,   100, -1,   15,-25),  //
					new AFHB(25+159,22,hopScotchBunny.bunnies.frames.jmpFlat,  350, step1,15,-20), //10   
					new AFHB(25+165,27,hopScotchBunny.bunnies.frames.jmpFly,   100, -1,   15,-25),  //
					new AFHB(25+177,36,hopScotchBunny.bunnies.frames.jmpRight, 350, step1,20,0),//9
					new AFHB(25+145,40,hopScotchBunny.bunnies.frames.jmpFly,   100, -1,   15,-25),  //
					new AFHB(25+111,42,hopScotchBunny.bunnies.frames.jmpLeft,  350, step2,23,-13), //8
					new AFHB(25+145,128,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+159,142,hopScotchBunny.bunnies.frames.jmpFlat, 350, step3,15,-20), //7     
					new AFHB(25+169,151,hopScotchBunny.bunnies.frames.jmpFly  ,100, -1,   15,-25),  //
					new AFHB(25+177,157,hopScotchBunny.bunnies.frames.jmpRight,350, step1,20,0),//6    157,191
					new AFHB(25+145,159,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+111,161,hopScotchBunny.bunnies.frames.jmpLeft, 350, step1,23,-13), //5    
					new AFHB(25+162,214,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+152,251,hopScotchBunny.bunnies.frames.jmpFlat, 350, step2,15,-20), //4    126,251
					new AFHB(25+163,279,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+179,274,hopScotchBunny.bunnies.frames.jmpRight,350, step3,20,0),//3    159,308
					new AFHB(25+145,270,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+110,274,hopScotchBunny.bunnies.frames.jmpLeft, 350, step2,23,-13), //2
					new AFHB(25+129,330,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+150,366,hopScotchBunny.bunnies.frames.jmpFlat, 350, step2,15,-20), //1    127,366
					new AFHB(25+145,412,hopScotchBunny.bunnies.frames.jmpFly,  100, -1,   15,-25),  //
					new AFHB(25+139,420,hopScotchBunny.bunnies.frames.jmpFlat, 350, step2,15,-20), //
					
					new AFHB(25+143,428,hopScotchBunny.bunnies.frames.hopFlat, 100, -1,15,-20), //end   
					  
			};
			
			addAction(tossing);
			synchronized (thread) {//TODO:remove this notify
				thread.notify();
			}
		}
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
}
