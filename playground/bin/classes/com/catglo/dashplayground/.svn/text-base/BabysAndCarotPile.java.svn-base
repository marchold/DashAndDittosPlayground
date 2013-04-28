package com.catglo.dashplayground;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

public class BabysAndCarotPile extends DrawingView {
	int whoAmI(){
		return BunnyGames.BABY_BUNNIES;
	}
	int whereAmI(){
		return 4;
	}
	MediaPlayer incage;// = MediaPlayer.create(getContext(), R.raw.babies_incage_help);
	MediaPlayer outcage;/// = MediaPlayer.create(getContext(), R.raw.babies_outcage_help);
	
	void helpSystem(){
		super.helpSystem();
		int count =0;
		for (int i = 0; i < DrawingView.NUMBER_OF_BABIES;i++){
			if (baby[i].info.mode == BabyBunnyAnimation.MODE_IN_CAGE || baby[i].info.mode == BabyBunnyAnimation.MODE_STUCK_IN_CAGE){
				count++;
			}
		}
		if (count==DrawingView.NUMBER_OF_BABIES) {
			incage.start(); 
		} else {
			outcage.start(); 
		}
	}
	protected void setupExitAreas(){
		addEventRect(new Rect(0,292,59,387), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
		addEventRect(new Rect(263,220,318,329), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
	}

	private MediaPlayer player;
	private static final int CAROT_GONE=1;
	private static final int CAROT_SEEDLING=2;
	private static final int CAROT_READY_TO_PICK=3;
	private static final int CAROT_VERY_READY_TO_PICK=4;
	
	int isACarrot=CAROT_VERY_READY_TO_PICK;
	private Matrix numberHintMatrix;
	
	static final int CARROT_X_POS=75;
	static final int CARROT_Y_POS=300;
	
	void onMenuExit(){
		super.onMenuExit();
		cageStage=0;
	}

	
	protected void pause(){
		super.pause();
		if (player!=null && player.isPlaying()==true)
			player.pause();
	}
	 
	protected void resume(){
		super.resume();
		if (player != null)
			player.start();
	}

	
	public int cageStage;
	public BabysAndCarotPile(Context context,int width, int height, final BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		cageStage=0;
		//try 
		{ 
	      //   player=MediaPlayer.create(getContext(),  R.raw.mozart_andante);
	      //   player.setLooping(true);
	      //   player.setVolume(1f, 1f);
	     } //catch (Exception e)  { }// handle errors ..   } 
	    
		incage = MediaPlayer.create(getContext(), R.raw.babies_incage_help);
		outcage = MediaPlayer.create(getContext(), R.raw.babies_outcage_help);
		
		
		numberHintMatrix=new Matrix();
		numberHintMatrix.postScale(.15f, .15f);
		numberHintMatrix.postTranslate(10, 10);
		
		
		addEventRect(new Rect(0,0,320,200), new MenuExecutor(){
			public void execute() {
				if (bitmapCache.initDone) {
					int count =0;
					for (int i = 0; i < DrawingView.NUMBER_OF_BABIES;i++){
						if (baby[i].info.mode == BabyBunnyAnimation.MODE_IN_CAGE || baby[i].info.mode == BabyBunnyAnimation.MODE_STUCK_IN_CAGE){
							count++;
						}
					}
					if (count==DrawingView.NUMBER_OF_BABIES) {
						startCageDoorOpening();
					}
				}
			}
        });
		

		Rect avoidRect = new Rect(-100,-300,350,150);
		int curX=80;
		int curY=85;
        for (int i = 0; i < NUMBER_OF_BABIES;i++){
        	baby[i].myPoint.x = origin.x+curX;
        	baby[i].myPoint.y = origin.y+curY;
        	curX+=15+r.nextInt(15);
        	if (curX>225){
        		curX=80+r.nextInt(5);
        		curY+=40;
        	}
        	baby[i].info.mode = BabyBunnyAnimation.MODE_IN_CAGE;
        	baby[i].addAreaToAvoid(avoidRect);
        }
        friend.addAreaToAvoid(avoidRect);
        mainBunny.addAreaToAvoid(avoidRect);
       
        /*addEventRect(new Rect(CARROT_X_POS,CARROT_Y_POS,CARROT_X_POS+60,CARROT_Y_POS+60),new MenuExecutor(){
			public void execute() {
				if (isACarrot!=CAROT_GONE)
				{
					mainBunny.sendBunnyTo(160,380);
					postInvalidate();
					growNewCarrot();
				}
			}			
		}); */
        
        addAction(seeSaw);
	}
	
	final AnimationAction seeSaw = new AnimationAction(JumpRope.SEESAW_INTERVAL,new Runnable(){public void run() {
		synchronized (BabysAndCarotPile.this){
			JumpRope.seesawStage++;
			if (JumpRope.seesawStage>= bitmapCache.seesaw.length)
				JumpRope.seesawStage=0;
			postInvalidate();
			seeSaw.actionTime=System.currentTimeMillis()+JumpRope.SEESAW_INTERVAL;
		}
	}});
	
	static final int CARROT_GROWING_INTERVAL=2000;
	final AnimationAction carotGrowing = new AnimationAction(CARROT_GROWING_INTERVAL,new Runnable(){public void run() {			
		synchronized (BabysAndCarotPile.this) {
			isACarrot++;
			if (isACarrot==CAROT_READY_TO_PICK){
				removeAction(carotGrowing);
			}
			postInvalidate();
			carotGrowing.actionTime=CARROT_GROWING_INTERVAL+System.currentTimeMillis();
		}
	}});
	private void growNewCarrot() {
		isACarrot=CAROT_GONE;
		addAction(carotGrowing);
	}

	private void startCageDoorOpening() {
		if (cageStage==0){
			cageOpening.actionTime=CAGE_OPEN_INTERVAL+System.currentTimeMillis();
			addAction(cageOpening);
		}
	}
	static final int CAGE_OPEN_INTERVAL = 100;

	final AnimationAction cageOpening = new AnimationAction(CAGE_OPEN_INTERVAL,new Runnable(){public void run() {			
		synchronized (BabysAndCarotPile.this) {
			cageStage++;
			if (cageStage>=bitmapCache.cage.length){
				cageStage=bitmapCache.cage.length-1;
				removeAction(cageOpening);
				for (int i = 0; i < NUMBER_OF_BABIES;i++){
			        baby[i].info.mode=BabyBunnyAnimation.MODE_EXITING_CAGE;
				}
				synchronized (thread){
			       thread.notify();
			    }
			}
			postInvalidate();
			cageOpening.actionTime=CAGE_OPEN_INTERVAL+System.currentTimeMillis();
		}
	}});

	protected void drawSurface(Canvas canvas){
		canvas.drawBitmap(bitmapCache.cageBackground,60-6,20-8,null);
				
		canvas.drawBitmap(bitmapCache.seesaw[JumpRope.seesawStage], JumpRope.SEESAW_X+320,JumpRope.SEESAW_Y, null);
		
		
	/*	switch (isACarrot){
	    case CAROT_GONE:
	    	canvas.drawBitmap(bitmapCache.noCarrot,CARROT_X_POS , CARROT_Y_POS, null); 
	    	break;
	    case CAROT_SEEDLING:
	    	canvas.drawBitmap(bitmapCache.seedling,CARROT_X_POS , CARROT_Y_POS, null); 
	    	break;
	    case CAROT_READY_TO_PICK:
	    	canvas.drawBitmap(bitmapCache.bush, CARROT_X_POS,CARROT_Y_POS,null);
	    	break;
	    case CAROT_VERY_READY_TO_PICK:
	    	canvas.drawBitmap(bitmapCache.bigBush,CARROT_X_POS,CARROT_Y_POS,null);
	    	break;
	    }*/
		
		super.drawSurface(canvas);
		
		canvas.drawBitmap(bitmapCache.cage[cageStage],60,20,null);
		
		int count =0;
		for (int i = 0; i < DrawingView.NUMBER_OF_BABIES;i++){
			if (baby[i].info.mode == BabyBunnyAnimation.MODE_IN_CAGE || baby[i].info.mode == BabyBunnyAnimation.MODE_STUCK_IN_CAGE){
				count++;
			}
		}
		if (count > 0 && bitmapCache.bigNumber[count]!=null)
			canvas.drawBitmap(bitmapCache.bigNumber[count], numberHintMatrix, null);
	
	}
	
	void babyEnterCage(){
		super.babyEnterCage();
		cageStage=2;
		addAction(new AnimationAction(System.currentTimeMillis()+1500,new Runnable(){public void run() {			
			synchronized (BabysAndCarotPile.this) {
				cageStage=0;
			}
		}}));
		
		postInvalidate();
		
	}
	
	
}
