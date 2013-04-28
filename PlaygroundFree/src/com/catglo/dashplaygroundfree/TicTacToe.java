package com.catglo.dashplaygroundfree;

import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;

public class TicTacToe extends DrawingView {
	int whoAmI(){
		return BunnyGames.TIC_TAC_TOE;
	}
	int whereAmI(){
		return 1;
	}
	MediaPlayer help;
	void helpSystem(){
		super.helpSystem();
		if (help!=null){
			help.seekTo(0);
			help.start(); 
		}
	}
	protected void setupExitAreas(){
		addEventRect(new Rect(0,366,61,477), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.LEFT);
			}
        });
		addEventRect(new Rect(258,373,320,480), new MenuExecutor(){
			public void execute() {
				switcher.whichView(SwitchViews.RIGHT);
			}
        });
	}

	void onMenuExit(){
		super.onMenuExit();
		clearBoard();
	}
	
	private MediaPlayer player;
	final int CONFETTI_COUNT=15;
	
	static final int GAME_PIECE_NOTHING=0;
	static final int GAME_PIECE_X=1;
	static final int GAME_PIECE_O=-1;
	
	int[][] pieces = new int[3][3]; 

	protected void pause(){
		super.pause();
		if (player!=null && player.isPlaying()==true)
			player.pause();
	}
	 
	int skillLevel;
	protected void resume(){
		super.resume();
		if (player != null)
			player.start();
		skillLevel=0;
		
		
		
	}

	
	final int RADIUS=35;

	private int winning;
	int winningX;
	int winningY;
	private int shortClap;
	private int femaleCheer;
	private int familyClap;
	public TicTacToe(Context context,int width, int height, final BitmapCache bitmapCache,Point origin) {
		super(context,width,height,bitmapCache,origin);
		{ 
	       //  player=MediaPlayer.create(getContext(),  R.raw.mozart_andante);
	       //  player.setLooping(true);
	       //  player.setVolume(1f, 1f);
	     } //catch (Exception e)  { }// handle errors ..   } 
		
		help = MediaPlayer.create(getContext(), R.raw.tic_tac_toe_instrctions);
	    
	
		addAction(butterflyFlying);
		
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++){
			pieces[i][j] = GAME_PIECE_NOTHING;
			
			final int top = j*VERTICAL_SPACING+MARGIN_TOP-RADIUS;
			final int left = i*HORIZANTAL_SPACING+MARGIN_LEFT-RADIUS;		
			final int right = i*HORIZANTAL_SPACING+MARGIN_LEFT+RADIUS;
			final int bottom = j*VERTICAL_SPACING+MARGIN_TOP+RADIUS;
			final int y = j;
			final int x = i;
			addEventRect(new Rect(left,top,right,bottom),new MenuExecutor(){
				public void execute() {
					winningX=x;
					winningY=y;
					if (pieces[x][y] == GAME_PIECE_NOTHING){
						pieces[x][y]=GAME_PIECE_O;
						bitmapCache.rockTapSound(1);
						mainBunny.sendBunnyTo(160, 400);
						if (isThereAWinner()){
							//startWinningAnimation();
						} else {
							int nothings=0;
							for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++){
								if (pieces[i][j]==GAME_PIECE_NOTHING) {
									nothings++;
								}
							}
							if (nothings==0){
								startNobodyWon();
							} else {
								friendTakeTurn();
							}
						}
					}
					
				}	
			}); 
		}
	}
	
	private void clearBoard() {
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++){
			pieces[i][j] = GAME_PIECE_NOTHING;
		}
	}
	
	private void startNobodyWon() {
		mainBunny.sendBunnyTo(160, 400);
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++){
			pieces[i][j] = GAME_PIECE_NOTHING;
		}	
	}
	
	final float winVolume=0.5f;
	private boolean isThereAWinner(){
		boolean winner=false;
		//Check for 3 across
		for (int row = 0; row < 3; row++){
			if (pieces[0][row]==pieces[1][row] && pieces[1][row]==pieces[2][row] && pieces[0][row]!=GAME_PIECE_NOTHING) {
				winner=true;
				if (pieces[0][row]==GAME_PIECE_O){
					launchConfetti(CONFETTI_COUNT);
					if (winningX==2){
						startBunnyWinnerAnumation(mainBunny,-9,0);
					}else if (winningX==0){
						startBunnyWinnerAnumation(mainBunny,9,0);
					}else {
						startBunnyWinnerAnumation(mainBunny,0,0);
					}
					bitmapCache.winning(winVolume);
					playAudioReward();
					skillLevel++;
				} else {
					skillLevel--;
				//	if (friend.actualX>160){
					    startBunnyWinnerAnumation((CharactarAnimation)friend,0,0);
				//	} else {
				//		startBunnyWinnerAnumation((CharactarAnimation)friend, 9,0);
				//	}
				}
			}
		}
		
		//Check for 3 down
		for (int col = 0; col < 3; col++){
			if (pieces[col][0]==pieces[col][1] && pieces[col][1]==pieces[col][2] && pieces[col][0]!=GAME_PIECE_NOTHING) {
				winner=true;
				if (pieces[col][0]==GAME_PIECE_O){
					launchConfetti(CONFETTI_COUNT);
					switch (winningY){
					case 0:startBunnyWinnerAnumation(mainBunny,0,9);
					break;
					case 2:startBunnyWinnerAnumation(mainBunny, 0,-9);
					break;
					default:
						startBunnyWinnerAnumation(mainBunny, 0,0);
					}
					
					bitmapCache.winning(winVolume);
					playAudioReward();
					skillLevel++;
				} else {
					startBunnyWinnerAnumation((CharactarAnimation)friend,0,0);
					skillLevel--;
				}
			}
		}
		
		//right diagonal
		if (pieces[0][0]==pieces[1][1] && pieces[1][1]==pieces[2][2] && pieces[0][0]!=GAME_PIECE_NOTHING) {
			winner=true;
			if (pieces[1][1]==GAME_PIECE_O){
				launchConfetti(CONFETTI_COUNT);
				int yDir=0;
				int xDir=0;
				switch (winningY){
				case 0: yDir=9;
				break;
				case 2:yDir=-9;
				break;
				}
				switch (winningX){
				case 0: xDir=9;
				break;
				case 2: xDir=-9;
				}
				
				startBunnyWinnerAnumation(mainBunny,xDir,yDir);

				bitmapCache.winning(winVolume);
				playAudioReward();
				skillLevel++;
			} else {
				startBunnyWinnerAnumation((CharactarAnimation)friend,0,0);
				skillLevel--;
			}
		}
		
		//Left Diagonal
		if (pieces[0][2]==pieces[1][1] && pieces[1][1]==pieces[2][0] && pieces[0][2] !=GAME_PIECE_NOTHING){
			winner=true;
			if (pieces[1][1]==GAME_PIECE_O){
				launchConfetti(CONFETTI_COUNT);
				int yDir=0;
				int xDir=0;
				switch (winningY){
				case 0: yDir=9;
				break;
				case 2:yDir=-9;
				break;
				}
				switch (winningX){
				case 0: xDir=9;
				break;
				case 2: xDir=-9;
				}
				
				startBunnyWinnerAnumation(mainBunny,xDir,yDir);
				bitmapCache.winning(winVolume);
				playAudioReward();
				skillLevel++;
			} else {
				startBunnyWinnerAnumation((CharactarAnimation)friend,0,0);
				skillLevel--;
			}
		}
		
		return winner;
	}
	
	void playAudioReward(){
		if (skillLevel>6){
			bitmapCache.femaleCheer();
		}
		if (skillLevel>4){
			bitmapCache.familyClap();
		}
		if (skillLevel>2){
			bitmapCache.shortClap();
		}
	}
	
	int lastWinningAnimation=0;
	private void startBunnyWinnerAnumation(final CharactarAnimation bunny,int xShift, int yShift) {
		Runnable whenDone = new Runnable(){public void run() {
			bunny.startBunnyAtAttention(500);
			clearBoard();
		}};
		lastWinningAnimation++;
		if (lastWinningAnimation>=2)
			lastWinningAnimation=0;
		switch (lastWinningAnimation){
		case 0:bunny.jumpAndShake(xShift,yShift,whenDone); 
		break;
		case 1: bunny.cartwheelWiinner(xShift,yShift,whenDone); 
		break;
		}
		
		
	}

	private void friendTakeTurn() {
		int x=0;
		int y=0;
		int totalPlaced=0;
		
		//first check all the spaces to see which are used by me
		for (y = 0; y < 3; y++){
			 for (x = 0; x < 3; x++){
				if (pieces[x][y]==GAME_PIECE_X) {
					totalPlaced++;
				}
			}
		}
	
		/******************************************
		 * Skill Level 0
		 * 
		 * pick a random unused spot
		 */
		
	//	if (totalPlaced==0){
		final Random r = new Random();
		int row;
		int col;
		do {
			row = r.nextInt(3);
			col = r.nextInt(3);
		} while (pieces[col][row]!=GAME_PIECE_NOTHING);
		
	
	//	}
		
		if (skillLevel>2){
			/********************************************
			 * Skill 1
			 * 
			 * Try for the center piece or the corners
			 */
			if (pieces[1][1]==GAME_PIECE_NOTHING){
				row=1;
				col=1;
			}
			else if (pieces[0][0]==GAME_PIECE_NOTHING){
				row=0;
				col=0;
			}
			else if (pieces[2][2]==GAME_PIECE_NOTHING){
				row=2;
				col=2;
			}
			else if (pieces[2][0]==GAME_PIECE_NOTHING){
				row=0;
				col=2;
			}
			else if (pieces[0][2]==GAME_PIECE_NOTHING){
				row=2;
				col=0;
			}
		}	
		
		
		/*******************************************
		 * Skill Level 2 & 3
		 * 
		 * Check if we need to block the opponent
		 * 
		 * Try and win if we have 2 in a row ourselves
		 */
		
		int piece=GAME_PIECE_O;
		for (int i=0; i < 2; i++){
			if (i==1 && skillLevel>4 || skillLevel>6){
				//Check for 2 across
				for (int Y = 0; Y < 3; Y++){
					if (pieces[0][Y]==piece && pieces[1][Y]==piece && pieces[2][Y]==GAME_PIECE_NOTHING){
						row=Y;
						col=2;
					}
					if (pieces[1][Y]==piece && pieces[2][Y]==piece && pieces[0][Y]==GAME_PIECE_NOTHING){
						row=Y;
						col=0;
					}
					if (pieces[0][Y]==piece && pieces[2][Y]==piece && pieces[1][Y]==GAME_PIECE_NOTHING){
						row=Y;
						col=1;
					}	
				}
				//Check for 2 down
				for (int X = 0; X < 3; X++){
					if (pieces[X][0]==piece && pieces[X][1]==piece && pieces[X][2]==GAME_PIECE_NOTHING){
						row=2;
						col=X;
					}
					if (pieces[X][0]==piece && pieces[X][2]==piece && pieces[X][1]==GAME_PIECE_NOTHING){
						row=1;
						col=X;
					}
					if (pieces[X][1]==piece && pieces[X][2]==piece && pieces[X][0]==GAME_PIECE_NOTHING){
						row=0;
						col=X;
					}
				}
				//right diagonal
				if (pieces[0][0]==piece && pieces[1][1]==piece && pieces[2][2]==GAME_PIECE_NOTHING){
					row=2;
					col=2;
				}
				if (pieces[0][0]==piece && pieces[1][1]==GAME_PIECE_NOTHING && pieces[2][2]==piece){
					row=1;
					col=1;
				}
				if (pieces[0][0]==GAME_PIECE_NOTHING && pieces[1][1]==piece && pieces[2][2]==piece){
					row=0;
					col=0;
				}
				//left diagonal
				if (pieces[0][2]==piece && pieces[1][1]==piece && pieces[2][0]==GAME_PIECE_NOTHING){
					col=2;
					row=0;
				}
				if (pieces[0][2]==piece && pieces[1][1]==GAME_PIECE_NOTHING && pieces[2][0]==piece){
					col=1;
					row=1;
				}
				if (pieces[0][2]==GAME_PIECE_NOTHING && pieces[1][1]==piece && pieces[2][0]==piece){
					col=0;
					row=2;
				}
			}
			piece=GAME_PIECE_X;
		}
		
		x=col*VERTICAL_SPACING  +MARGIN_LEFT;
		y=row*HORIZANTAL_SPACING+MARGIN_TOP;
		
		removeAction(friend.freindAI);
		friend.sendBunnyTo(x, y);
		Log.i("BUNNY","friend sned to space");
		final int  _row = row;
		final int _col = col;
		friend.onDoneMovingHandler.add(new Runnable(){public void run() {
			friend.sendBunnyTo(50,400);
			Log.i("BUNNY","friend sned back down");
			pieces[_col][_row]=GAME_PIECE_X;
			if (isThereAWinner()){
				clearBoard();
			}
		}});
	}
	
	static final int VERTICAL_SPACING=80;
	static final int HORIZANTAL_SPACING=80;
	static final int MARGIN_TOP=110;
	static final int MARGIN_LEFT=70;
	
	protected void drawBackground(Canvas canvas){
		super.drawBackground(canvas);
		
		canvas.drawBitmap(bitmapCache.tictactoe_board,35,60,null);
	} 
	
	protected void drawSurface(Canvas canvas){
		int used=0;
		for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++){
			switch (pieces[i][j]){
			case GAME_PIECE_O:
				canvas.drawBitmap(bitmapCache.tictactoe_o, i*80+50,j*80+60,null);
				++used;
				break;
			case GAME_PIECE_X:
				canvas.drawBitmap(bitmapCache.tictactoe_x, i*80+50,j*80+60,null);
				++used;
				break;
			}
		}	
		
		
		
		super.drawSurface(canvas);
		
		
	}
}
