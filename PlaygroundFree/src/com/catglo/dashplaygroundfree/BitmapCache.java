package com.catglo.dashplaygroundfree;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.LinkedList;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Debug;
import android.os.Process;
import android.util.Log;



public class BitmapCache {
	class LinesAndColor{	
	
		int[] lines;
		int[] color;
		int width;
		int height;
		void debugFill(){
			for (int i = 0; i < width*height;i++){
				color[i]=0x77777777;
				lines[i]=0xEEEEEEEE;
			}
		}
		
		void scaleDown(float factor){
			int newWidth = (int)(width*factor);
			int newHeight = (int)(height*factor);
			float factorRecripicol = 1/factor;
			int[] newLines = new int[newWidth*newHeight];
			int[] newColor = new int[newWidth*newHeight];
			
			for (int y = 0; y < newHeight; y++){
				for (int x = 0; x < newWidth; x++){
				//	try {
						float X = x*factorRecripicol;
						float Y = y*factorRecripicol;
		
						int rX = (int)X;
						int rY = (int)Y;
						
						float factorLeft   = X-(float)rX;
						float factorRight  = (float)rX-(X-1);
						float factorTop    = Y-(float)rY;
						float factorBottom = (float)rY-(Y-1);
						
						//top left pixel
						int topLeft = (int) (rX+rY*width);
						int A1 = ((color[topLeft]>>24)&0xFF);
						int R1 = ((color[topLeft]>>16)&0xFF);
						int G1 = ((color[topLeft]>>8 )&0xFF);
						int B1 = ((color[topLeft]    )&0xFF);
						
						//top right pixel
						int topRight=topLeft+1;
						int A2 = ((color[topRight]>>24)&0xFF);
						int R2 = ((color[topRight]>>16)&0xFF);
						int G2 = ((color[topRight]>>8 )&0xFF);
						int B2 = ((color[topRight]    )&0xFF);
						
						//top pixel interpolated
						float topA = ((float)A1*factorLeft) + ((float)A2*factorRight);
						float topR = ((float)R1*factorLeft) + ((float)R2*factorRight);
						float topG = ((float)G1*factorLeft) + ((float)G2*factorRight);
						float topB = ((float)B1*factorLeft) + ((float)B2*factorRight);
						
						//bottom left pixel
						int bottomLeft = (int) (rX+(rY+1)*width);
						A1 = (color[bottomLeft]>>24)&0xFF;
						R1 = (color[bottomLeft]>>16)&0xFF;
						G1 = (color[bottomLeft]>>8) &0xFF;
						B1 = (color[bottomLeft])    &0xFF;
						
						//bottom right pixel
						int bottomRight=bottomLeft+1;
						A2 = (color[bottomRight]>>24)&0xFF;
						R2 = (color[bottomRight]>>16)&0xFF;
						G2 = (color[bottomRight]>>8) &0xFF;
						B2 = (color[bottomRight])    &0xFF;
					
						//bottom pixel interpolated
						float botA = ((float)A1*factorLeft) + ((float)A2*factorRight);
						float botR = ((float)R1*factorLeft) + ((float)R2*factorRight);
						float botG = ((float)G1*factorLeft) + ((float)G2*factorRight);
						float botB = ((float)B1*factorLeft) + ((float)B2*factorRight);
						
						int A = (int)(botA*factorBottom + topA+factorTop);
						int R = (int)(botR*factorBottom + topR+factorTop);
						int G = (int)(botG*factorBottom + topG+factorTop);
						int B = (int)(botB*factorBottom + topB+factorTop);
						if (A>255)A=255;
						if (A<0)A=0;
						if (R>255)R=255;
						if (R<0)R=0;
						if (G>255)G=255;
						
						newColor[x+y*newWidth] = (A<<24)|(R<<16)|(G<<8)|B;
						
					
						//LINES
						//top left pixel
						A1 = (lines[topLeft]>>24)&0xFF;
						R1 = (lines[topLeft]>>16)&0xFF;
						G1 = (lines[topLeft]>>8) &0xFF;
						B1 = (lines[topLeft])    &0xFF;
						
						//top right pixel
						A2 = (lines[topRight]>>24)&0xFF;
						R2 = (lines[topRight]>>16)&0xFF;
						G2 = (lines[topRight]>>8) &0xFF;
						B2 = (lines[topRight])    &0xFF;
						
						//top pixel interpolated
						topA = ((float)A1*factorLeft) + ((float)A2*factorRight);
						topR = ((float)R1*factorLeft) + ((float)R2*factorRight);
						topG = ((float)G1*factorLeft) + ((float)G2*factorRight);
						topB = ((float)B1*factorLeft) + ((float)B2*factorRight);
						
						//bottom left pixel
						A1 = (lines[bottomLeft]>>24)&0xFF;
						R1 = (lines[bottomLeft]>>16)&0xFF;
						G1 = (lines[bottomLeft]>>8) &0xFF;
						B1 = (lines[bottomLeft])    &0xFF;
						
						//bottom right pixel
						A2 = (lines[bottomRight]>>24)&0xFF;
						R2 = (lines[bottomRight]>>16)&0xFF;
						G2 = (lines[bottomRight]>>8) &0xFF;
						B2 = (lines[bottomRight])    &0xFF;
					
						//bottom pixel interpolated
						botA = ((float)A1*factorLeft) + ((float)A2*factorRight);
						botR = ((float)R1*factorLeft) + ((float)R2*factorRight);
						botG = ((float)G1*factorLeft) + ((float)G2*factorRight);
						botB = ((float)B1*factorLeft) + ((float)B2*factorRight);
						
						A = (int)(botA*factorBottom + topA+factorTop);
						R = (int)(botR*factorBottom + topR+factorTop);
						G = (int)(botG*factorBottom + topG+factorTop);
						B = (int)(botB*factorBottom + topB+factorTop);
						if (A>255)A=255;
						if (A<0)A=0;
						if (R>255)R=255;
						if (R<0)R=0;
						if (G>255)G=255;
						
						
						newLines[x+y*newWidth] = lines[topLeft]|lines[topRight]|lines[bottomLeft]|lines[bottomRight];// (A<<24)|(R<<16)|(G<<8)|B;
					
					//} catch (ArrayIndexOutOfBoundsException e){
					//}
				}
			}
			lines=newLines;
			color=newColor;
			width=newWidth;
			height=newHeight;
		}
		
	}
	
	class BunnyFrameRawData {
		BitmapFactory.Options ops = new BitmapFactory.Options();
		LinesAndColor bunnyWinnerLeft;
		LinesAndColor bunnyWinnerRight;
		LinesAndColor bunnyWinnerCenter;
		
		LinesAndColor  getPixels(int idLines, int idColor, Resources res) {
			LinesAndColor raw = new LinesAndColor();
			
			Bitmap b  = BitmapFactory.decodeResource(res, idLines ,null);
			raw.lines=new int[b.getWidth()*b.getHeight()];
			b.getPixels(raw.lines, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
	
			raw.width = b.getWidth();
			raw.height = b.getHeight();
			b.recycle();
			
			b  = BitmapFactory.decodeResource(res, idColor ,ops);
			raw.color = new int[b.getWidth()*b.getHeight()];
			b.getPixels(raw.color, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
				
			b.recycle();
			return raw;
		}
	
		BunnyFrameRawData(Resources res){
			ops.inPurgeable=true;
			ops.inInputShareable=true;
	
			hopTight  = getPixels(R.drawable.bunny__general_hoptightlines,R.drawable.bunny__general_hoptightcolor,res);
			hopOpen   = getPixels(R.drawable.bunny__general_hopopenlines,R.drawable.bunny__general_hopopencolor,res);
			attention = getPixels(R.drawable.bunny__general_attentionlines,R.drawable.bunny__general_attentioncolor,res);
			laying    = getPixels(R.drawable.bunny__general_layinglines,R.drawable.bunny__general_layingcolor,res);
			lookup    = getPixels(R.drawable.bunny__general_lookinguplines,R.drawable.bunny__general_lookingupcolor,res);
		    hop1      = getPixels(R.drawable.bunny__general_hop01lines,R.drawable.bunny__general_hop01color,res);
			hop2      = getPixels(R.drawable.bunny__general_hop2lines,R.drawable.bunny__general_hop02color,res);
			hop3      = getPixels(R.drawable.bunny__general_hop03lines,R.drawable.bunny__general_hop03color,res);
			
			hopFlat = getPixels(R.drawable.bunny__hopscotch_jump01lines,R.drawable.bunny__hopscotch_jump01color,res);
			hopFly  = getPixels(R.drawable.bunny__hopscotch_jump02lines,R.drawable.bunny__hopscotch_jump02color,res);
			hopRight= getPixels(R.drawable.bunny__hopscotch_jump03lines,R.drawable.bunny__hopscotch_jump03color,res);
			hopLeft = getPixels(R.drawable.bunny__hopscotch_jump05lines,R.drawable.bunny__hopscotch_jump05color,res);
			jmpFlat = getPixels(R.drawable.bunny__hopscotch_jump10lines,R.drawable.bunny__hopscotch_jump10color,res);
			jmpFly  = getPixels(R.drawable.bunny__hopscotch_jump07lines,R.drawable.bunny__hopscotch_jump07color,res);
			jmpLeft = getPixels(R.drawable.bunny__hopscotch_jump08lines,R.drawable.bunny__hopscotch_jump08color,res);
			jmpRight= getPixels(R.drawable.bunny__hopscotch_jump06lines,R.drawable.bunny__hopscotch_jump06color,res);
			pickUpLeft1 = getPixels(R.drawable.bunny__hopscotch_bend01lines,R.drawable.bunny__hopscotch_bend01color,res);
			pickUpLeft2= getPixels(R.drawable.bunny__hopscotch_bend02lines,R.drawable.bunny__hopscotch_bend02color,res);
			pickUpRight1= getPixels(R.drawable.bunny__hopscotch_bend03lines,R.drawable.bunny__hopscotch_bend03color,res);
			pickUpRight2= getPixels(R.drawable.bunny__hopscotch_bend04lines,R.drawable.bunny__hopscotch_bend04color,res);;
			
			
			bunnyWinnerLeft   = getPixels(R.drawable.bunny__winner_leftwigglelines,R.drawable.bunny__winner_leftwigglecolor,res);
			bunnyWinnerRight  = getPixels(R.drawable.bunny__winner_rightwigglelines,R.drawable.bunny__winner_rightwigglecolor,res);
			bunnyWinnerCenter = getPixels(R.drawable.bunny__winner_centerwigglelines,R.drawable.bunny__winner_centerwigglecolor,res);
			
			push1 = getPixels(R.drawable.bunny__puzzle_pushrightlines01,R.drawable.bunny__puzzle_pushrightcolors01,res);
			push2 = getPixels(R.drawable.bunny__puzzle_pushrightlines02,R.drawable.bunny__puzzle_pushrightcolors02,res);
			push3 = getPixels(R.drawable.bunny__puzzle_pushrightlines03,R.drawable.bunny__puzzle_pushrightcolor03,res);
			
			dance1Left  = getPixels(R.drawable.bunny__dance01_leftsidelines,R.drawable.bunny__dance01_leftsidecolor,res);
			dance2Left  = getPixels(R.drawable.bunny__dance02_leftsidelines,R.drawable.bunny__dance02_leftsidecolor,res);
			dance1Right = getPixels(R.drawable.bunny__dance01_rightsidelines,R.drawable.bunny__dance01_rightsidecolor,res);
			dance2Right = getPixels(R.drawable.bunny__dance02_rightsidelines,R.drawable.bunny__dance02_rightsidecolor,res);
			
			clapOpen    = getPixels(R.drawable.bunny__clap_openlines,R.drawable.bunny__clap_opencolor,res);
			clapClosed1 = getPixels(R.drawable.bunny__clap_closed01linse,R.drawable.bunny__clap_closed01color,res);
			clapClosed2 = getPixels(R.drawable.bunny__clap_closed02lines,R.drawable.bunny__clap_closed02color,res);
			
			eating1 = getPixels(R.drawable.babybunny__eating01_bending,R.drawable.babybunny__eating01_bendingcolor,res);
			eating2 = getPixels(R.drawable.babybunny__eating02_standing,R.drawable.babybunny__eating02_standingcolor,res);
			eating3 = getPixels(R.drawable.babybunny__eating03_firstbite,R.drawable.babybunny__eating03_firstbitecolor,res);
			eating4 = getPixels(R.drawable.babybunny__eating04_lastbite,R.drawable.babybunny__eating04_lastbitecolor,res);
			eating5 = getPixels(R.drawable.babybunny__eating05_allgone,R.drawable.babybunny__eating05_allgonecolor,res);
			
		
		}
		
		void scaleDown(float factor){
			hopTight.scaleDown(factor);
			hopOpen.scaleDown(factor);
			attention.scaleDown(factor);
			laying.scaleDown(factor);
			lookup.scaleDown(factor);
			hop1.scaleDown(factor);
			hop2.scaleDown(factor);
			hop3.scaleDown(factor);
			dance1Left.scaleDown(factor);
			dance2Left.scaleDown(factor);
			dance1Right.scaleDown(factor);
			dance2Right.scaleDown(factor);
			clapOpen.scaleDown(factor);
			clapClosed1.scaleDown(factor);
			clapClosed2.scaleDown(factor);
			eating1.scaleDown(factor);
			eating2.scaleDown(factor);
			eating3.scaleDown(factor);
			eating4.scaleDown(factor);
			eating5.scaleDown(factor);
			push1.scaleDown(factor);
			push2.scaleDown(factor);
			push3.scaleDown(factor);
			bunnyWinnerLeft.scaleDown(factor);
			bunnyWinnerCenter.scaleDown(factor);
			bunnyWinnerRight.scaleDown(factor);
			
			jmpFlat.scaleDown(factor);
			hopFlat.scaleDown(factor);
			jmpLeft.scaleDown(factor);
			hopLeft.scaleDown(factor);
			jmpRight.scaleDown(factor);
			hopRight.scaleDown(factor);
			
			
			
		}
		
		LinesAndColor hopTight;
		LinesAndColor hopOpen;
		LinesAndColor attention;
		LinesAndColor laying;
		LinesAndColor lookup;
		LinesAndColor hop1;
		LinesAndColor hop2;
		LinesAndColor hop3;
		
		LinesAndColor hopFlat;
		LinesAndColor hopFly;
		LinesAndColor hopRight;
		LinesAndColor hopLeft;
		LinesAndColor jmpFlat;
		LinesAndColor jmpFly;
		LinesAndColor jmpLeft;
		LinesAndColor jmpRight;
		LinesAndColor pickUpLeft1;
		LinesAndColor pickUpLeft2;
		LinesAndColor pickUpRight1;
		LinesAndColor pickUpRight2;
		
		LinesAndColor push1;
		LinesAndColor push2;
		LinesAndColor push3;
		
		LinesAndColor dance1Left;
		LinesAndColor dance1Right;
		LinesAndColor dance2Left;
		LinesAndColor dance2Right;
		
		LinesAndColor clapOpen;
		LinesAndColor clapClosed1;
		LinesAndColor clapClosed2;
		
		LinesAndColor eating1;
		LinesAndColor eating2;
		LinesAndColor eating3;
		LinesAndColor eating4;
		LinesAndColor eating5;
		
	}
	
	class BunnyFrames {
		BunnyFrames(BitmapCache cache){
			hopTight=cache.decodeAndScale(R.drawable.bunny__hoptight);
			hopOpen=cache.decodeAndScale(R.drawable.bunny__hopopen);
			laying=cache.decodeAndScale(R.drawable.bunny__laying);
		}
		
		void loadFramesFromRawData(int newColor, BunnyFrameRawData raw){
			hopTight   = createColorizedBitmap(raw.hopTight ,newColor);
	        hopOpen    = createColorizedBitmap(raw.hopOpen  ,newColor);
	        attention  = createColorizedBitmap(raw.attention,newColor);
	        laying     = createColorizedBitmap(raw.laying   ,newColor);
	        lookup     = createColorizedBitmap(raw.lookup   ,newColor);
	        hop1       = createColorizedBitmap(raw.hop1     ,newColor);
	        hop2       = createColorizedBitmap(raw.hop2     ,newColor);
	        hop3       = createColorizedBitmap(raw.hop3     ,newColor);
	        

			hopFlat = createColorizedBitmap(raw.hopFlat,newColor);
			hopFly  = createColorizedBitmap(raw.hopFly,newColor);
			hopRight= createColorizedBitmap(raw.hopRight,newColor);
			hopLeft = createColorizedBitmap(raw.hopLeft,newColor);
			jmpFlat = createColorizedBitmap(raw.jmpFlat,newColor);
			jmpFly  = createColorizedBitmap(raw.jmpFly,newColor);
			jmpLeft = createColorizedBitmap(raw.jmpLeft,newColor);
			jmpRight= createColorizedBitmap(raw.jmpRight,newColor);
			pickUpLeft1= createColorizedBitmap(raw.pickUpLeft1,newColor);
			pickUpLeft2= createColorizedBitmap(raw.pickUpLeft2,newColor);
			pickUpRight1= createColorizedBitmap(raw.pickUpRight1,newColor);
			pickUpRight2= createColorizedBitmap(raw.pickUpRight2,newColor);
			
			winnerLeft = createColorizedBitmap(raw.bunnyWinnerLeft,newColor);
			winnerRight = createColorizedBitmap(raw.bunnyWinnerRight,newColor);
			winnerCenter = createColorizedBitmap(raw.bunnyWinnerCenter,newColor);
			
			push1 = createColorizedBitmap(raw.push1,newColor);
			push2 = createColorizedBitmap(raw.push2,newColor);
			push3 = createColorizedBitmap(raw.push3,newColor);
			
			dance1Left   = createColorizedBitmap(raw.dance1Left,newColor);
			dance1Right  = createColorizedBitmap(raw.dance1Right,newColor);
			dance2Left   = createColorizedBitmap(raw.dance2Left,newColor);
			dance2Right  = createColorizedBitmap(raw.dance2Right,newColor);
			
			clapOpen  = createColorizedBitmap(raw.clapOpen,newColor);
			clapClosed1 = createColorizedBitmap(raw.clapClosed1,newColor);
			clapClosed2  = createColorizedBitmap(raw.clapClosed2,newColor);	
			
			eating[0] = createColorizedBitmap(raw.eating1,newColor);
			eating[1] = createColorizedBitmap(raw.eating2,newColor);
			eating[2] = createColorizedBitmap(raw.eating3,newColor);
			eating[3] = createColorizedBitmap(raw.eating4,newColor);
			eating[4] = createColorizedBitmap(raw.eating5,newColor);
		}
		
		BunnyFrames(int newColor, BunnyFrameRawData raw){
			loadFramesFromRawData(newColor,raw);
		}; 
		
		void mergeLines(int[] pixels, int[] lines,Bitmap b){ 			
			for (int i = 0; i < lines.length; i++){
				if ((lines[i]&0xFF000000) == 0xFF000000){
				//	Log.i("BUNNY","index"+i);
					pixels[i]=lines[i];
				}
			}
			b.setPixels(pixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
		}
		
		void mergeBitmaps(Bitmap b, int[] lines, int[] color, int newColor){
			allBitmaps.add(b);
			int[] pixels = new int[color.length];
			for (int i = 0; i < color.length; i++){
				if ((color[i] & 0xFF000000)==0xFF000000){
					pixels[i]= newColor;
					/* got this error but did not fix. It seemed to happen when the game was exited and restarted real quick
					 * presumably the bitmapCache thread did not complete
					 * 
					 * 07-04 08:52:38.869: ERROR/AndroidRuntime(21215): Uncaught handler: thread main exiting due to uncaught exception
07-04 08:52:39.199: ERROR/AndroidRuntime(21215): java.lang.OutOfMemoryError: bitmap size exceeds VM budget
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.graphics.Bitmap.nativeCreate(Native Method)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.graphics.Bitmap.createBitmap(Bitmap.java:464)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.createColorizedBitmap(BitmapCache.java:387)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.<init>(BitmapCache.java:346)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.catglo.dashplayground.BitmapCache.<init>(BitmapCache.java:613)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.catglo.dashplayground.BunnyGames.onCreate(BunnyGames.java:166)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1123)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2364)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2417)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.ActivityThread.access$2100(ActivityThread.java:116)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1794)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.os.Handler.dispatchMessage(Handler.java:99)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.os.Looper.loop(Looper.java:123)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at android.app.ActivityThread.main(ActivityThread.java:4203)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at java.lang.reflect.Method.invokeNative(Native Method)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at java.lang.reflect.Method.invoke(Method.java:521)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
07-04 08:52:39.199: ERROR/AndroidRuntime(21215):     at dalvik.system.NativeStart.main(Native Method)
*/
					
				} else {
					pixels[i] = color[i];
				}
			}
			mergeLines(pixels,lines,b);
		}
		
		protected Bitmap createColorizedBitmap(LinesAndColor image, int toColor){	
			Bitmap b = Bitmap.createBitmap(image.width,image.height, Bitmap.Config.ARGB_8888);
			/*07-05 13:09:02.808: ERROR/AndroidRuntime(28788): Uncaught handler: thread Thread-54 exiting due to uncaught exception
07-05 13:09:03.687: ERROR/AndroidRuntime(28788): java.lang.OutOfMemoryError: bitmap size exceeds VM budget
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at android.graphics.Bitmap.nativeCreate(Native Method)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at android.graphics.Bitmap.createBitmap(Bitmap.java:464)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.createColorizedBitmap(BitmapCache.java:414)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.<init>(BitmapCache.java:356)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at com.catglo.dashplayground.BitmapCache$BabyBunnyFrames.<init>(BitmapCache.java:462)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at com.catglo.dashplayground.BitmapCache$1.run(BitmapCache.java:905)
07-05 13:09:03.687: ERROR/AndroidRuntime(28788):     at java.lang.Thread.run(Thread.java:1060)
this happened switching screens fast on startup to get in and out of baby cage screen
also just exited and came back, the baby cage was open whne I got to it inicating the program had not fully exited.


this happend after getting new images from joyce
07-08 11:18:47.813: ERROR/AndroidRuntime(14797): Uncaught handler: thread Thread-38 exiting due to uncaught exception
07-08 11:18:47.973: ERROR/AndroidRuntime(14797): java.lang.OutOfMemoryError: bitmap size exceeds VM budget
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at android.graphics.Bitmap.nativeCreate(Native Method)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at android.graphics.Bitmap.createBitmap(Bitmap.java:464)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.createColorizedBitmap(BitmapCache.java:414)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at com.catglo.dashplayground.BitmapCache$BunnyFrames.<init>(BitmapCache.java:345)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at com.catglo.dashplayground.BitmapCache$BabyBunnyFrames.<init>(BitmapCache.java:476)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at com.catglo.dashplayground.BitmapCache$1.run(BitmapCache.java:914)
07-08 11:18:47.973: ERROR/AndroidRuntime(14797):     at java.lang.Thread.run(Thread.java:1060)

*/
			
			mergeBitmaps(b,image.lines,image.color,toColor);
			return b;
		}
		
		Bitmap hopTight;
		Bitmap hopOpen;
		Bitmap attention;
		Bitmap laying;
		Bitmap lookup;
		Bitmap hop1;
		Bitmap hop2;
		Bitmap hop3;
		
		Bitmap hopFlat;
		Bitmap hopFly;
		Bitmap hopRight;
		Bitmap hopLeft;
		Bitmap jmpFlat;
		Bitmap jmpFly;
		Bitmap jmpLeft;
		Bitmap jmpRight;
		Bitmap pickUpLeft1;
		Bitmap pickUpLeft2;
		Bitmap pickUpRight1;
		Bitmap pickUpRight2;
		
		Bitmap winnerLeft;
		Bitmap winnerRight;
		Bitmap winnerCenter;
		
		Bitmap push1,push2,push3;

		Bitmap dance1Left;
		Bitmap dance1Right;
		Bitmap dance2Left;
		Bitmap dance2Right;
		
		Bitmap clapOpen;
		Bitmap clapClosed1;
		Bitmap clapClosed2;	
		
		Bitmap[] eating = new Bitmap[5];
		
	}
	
	class BabyBunnyFrames extends BunnyFrames {
		BabyBunnyFrames(int newColor, BunnyFrameRawData raw) {
			super(newColor, raw);
		}
	}
	
	BunnyFrames main; 
	BunnyFrames friend;
	
	Bitmap noCarrot;
	Bitmap seedling;
	Bitmap bush;
	Bitmap carrots;
	Bitmap bigBush;

	//Hopscotch
	Bitmap ball;

	
	
	Resources res;
	 
	//Road segments
	Bitmap[] road = new Bitmap[3];
	Bitmap steeringWheel;
	Bitmap busTopView;
	//Bitmap busHardLeft;
	//Bitmap busRight;
	//Bitmap busHardRight;
	Bitmap stopButton;
	Bitmap goButton;
	Bitmap postIt;
	
	//big numbers for hopscotch reward
	Bitmap[] bigNumber = new Bitmap[11];
	
	 
	LinkedList<Bitmap> allBitmaps = new LinkedList<Bitmap>();
	Bitmap mole1;
	Bitmap mole2;
	Bitmap mole3;
	Bitmap mole4;
	Bitmap mole5;
	
	Bitmap[] wheelbarrow;
	
	//mini golg
	Bitmap golfBall;
	Bitmap windmillTower;
	Bitmap windmillBlades;
	
	
	Bitmap[] confetti = new Bitmap[25];
	
	//Tic-tac-toe
	Bitmap tictactoe_board;
	Bitmap tictactoe_x;
	Bitmap tictactoe_o;
	
	//Baby Bunny Cage
	Bitmap[] cage = new Bitmap[5];
	Bitmap cageBackground;
	

	
	
	Bitmap[] exit = new Bitmap[4];
	
	Bitmap skunk;
	
	
	Bitmap[] treasures;
	
	class Charactar implements Iterator<Bitmap>{
		Bitmap waiting;
		private Bitmap[] walking;
		Bitmap[] hurray;
		Bitmap[] stepping;
		Bitmap[] jumping;
		Bitmap missedJump;
		
		private Bitmap[] current;
		private int step;
		Iterator<Bitmap> getWalking(){
			current = walking;
			step=0;
			return this;
		}
		Iterator<Bitmap> getHurray(){
			current = hurray;
			step=0;
			return this;
		}
		public boolean hasNext() {
			return true;
		}
		public Bitmap next() {
			Bitmap retVal = current[step];
			step++;
			if (step>=current.length){
				step=0;
			}
			return retVal;
		}
		public void remove() {
			
		}
	}
	Charactar[] friends;
	
	Bitmap jumpButton;
	Bitmap jumpButtonPressed;
	Bitmap jumpButtonOff;
	
	
	Bitmap[] hatAndStars = new Bitmap[2];
	
	Bitmap busSideView;
	
	Bitmap babyMenuFeed;
	Bitmap babyMenuFollow;
	Bitmap babyMenuCage;
	Bitmap babyMenuDance;
	Bitmap[] menuButton = new Bitmap[8];
	 
	
	Bitmap decodeAndScale(int id){
		BitmapFactory.Options ops = new BitmapFactory.Options(); //Workaround for an android garbage collection issue with bitmaps.
		ops.inPurgeable=true;
		ops.inInputShareable=true;
		ops.inPreferredConfig = Config.ARGB_4444;
			
		Bitmap b = BitmapFactory.decodeResource(res,id,ops);
		allBitmaps.add(b);
		return b;
	}
	
	Boolean initDone=false;
	Thread initThread;
	Bitmap[] busSide = new Bitmap[3];
	Bitmap[] busFriends = new Bitmap[12];
	Bitmap busStopSign;
	Bitmap newsStand;
	
	Bitmap[] waterCan = new Bitmap[6];
	Bitmap[] swing1 = new Bitmap[8];
	Bitmap[] swing2 = new Bitmap[8];
	Bitmap[] flag = new Bitmap[3];
	Bitmap[] butterfly;
	Bitmap[] seesaw;
	
	boolean feedTheBunnyLoaded=false;
	private Object hopSound;
	private int ballInCup;
	private int ballTap;
	private int shortClap;
	private int femaleCheer;
	private int familyClap;
	private int slotsound;
	private int rockTapSound;
	private int step1;
	private int step2;
	private int step3;
	private int winning;
	private int sad;
	private int boing1;
	private int boing2;
	
	public void loadFeedTheBunny(){
		if (feedTheBunnyLoaded)
			return;
		feedTheBunnyLoaded=true;
		
		//Feed The Bunny
		bush     = decodeAndScale(R.drawable.feedbunny__bush);
	    noCarrot = decodeAndScale(R.drawable.feedbunny__nocarrot);
	    seedling = decodeAndScale(R.drawable.feedbunny__carrot01);
	    bigBush  = decodeAndScale(R.drawable.feedbunny__carrot03);
	    mole1    = decodeAndScale(R.drawable.feedbunny__mole_01);
	    mole2    = decodeAndScale(R.drawable.feedbunny__mole_02);
	    mole3    = decodeAndScale(R.drawable.feedbunny__mole_03);
	    mole4    = decodeAndScale(R.drawable.feedbunny__mole_04);
	    mole5    = decodeAndScale(R.drawable.feedbunny__mole_05);	
	}
	
	BitmapCache(Context context, int width, int height){
		this.res = context.getResources();
		
		main = new BunnyFrames(this);
		friend = new BunnyFrames(this);
				
		initThread = new Thread(new Runnable(){public void run() {
			synchronized (BitmapCache.this)
			{
				BunnyFrameRawData bunnyPixels = new BunnyFrameRawData(BitmapCache.this.res);
				main.loadFramesFromRawData(0xFFFFFFFF,bunnyPixels);
				friend.loadFramesFromRawData(0xFFd9d9d9/*91af9d*/,bunnyPixels);
	
				confetti[0] =  decodeAndScale(R.drawable.numbers__confetti_blue01);
				confetti[1] =  decodeAndScale(R.drawable.numbers__confetti_blue02);
				confetti[2] =  decodeAndScale(R.drawable.numbers__confetti_blue03);
				confetti[3] =  decodeAndScale(R.drawable.numbers__confetti_green01);
				confetti[4] =  decodeAndScale(R.drawable.numbers__confetti_green02);
				confetti[5] =  decodeAndScale(R.drawable.numbers__confetti_green03);
				confetti[6] =  decodeAndScale(R.drawable.numbers__confetti_orange01);
				confetti[7] =  decodeAndScale(R.drawable.numbers__confetti_orange02);
				confetti[8] =  decodeAndScale(R.drawable.numbers__confetti_orange03);
				confetti[9] =  decodeAndScale(R.drawable.numbers__confetti_purple01);
				confetti[10] =  decodeAndScale(R.drawable.numbers__confetti_purple02);
				confetti[11] =  decodeAndScale(R.drawable.numbers__confetti_purple03);
				confetti[12] =  decodeAndScale(R.drawable.numbers__confetti_red01);
				confetti[13] =  decodeAndScale(R.drawable.numbers__confetti_red02);
				confetti[14] =  decodeAndScale(R.drawable.numbers__confetti_red03);
				confetti[15] =  decodeAndScale(R.drawable.numbers__moon_blue01);
				confetti[16] =  decodeAndScale(R.drawable.numbers__moon_red01);
				confetti[17] =  decodeAndScale(R.drawable.numbers__moons_02);
				confetti[18] =  decodeAndScale(R.drawable.numbers__moons_blue02);
				confetti[19] =  decodeAndScale(R.drawable.numbers__moons_green01);
				confetti[20] =  decodeAndScale(R.drawable.numbers__moons_green02);
				confetti[21] =  decodeAndScale(R.drawable.numbers__moons_orange01);
				confetti[22] =  decodeAndScale(R.drawable.numbers__moons_orange02);
				confetti[23] =  decodeAndScale(R.drawable.numbers__stars_red01);
				confetti[24] =  decodeAndScale(R.drawable.numbers__stars_red02);	
				
				if (initThread.isInterrupted())
					return;
				
				bigNumber[1] = decodeAndScale(R.drawable.hopscotch__number1);
				bigNumber[2] = decodeAndScale(R.drawable.hopscotch__number2);
				bigNumber[3] = decodeAndScale(R.drawable.hopscotch__number3);
				bigNumber[4] = decodeAndScale(R.drawable.hopscotch__number4);
				bigNumber[5] = decodeAndScale(R.drawable.hopscotch__number5);
				bigNumber[6] = decodeAndScale(R.drawable.hopscotch__number6);
				bigNumber[7] = decodeAndScale(R.drawable.hopscotch__number7);
				bigNumber[8] = decodeAndScale(R.drawable.hopscotch__number8);
				bigNumber[9] = decodeAndScale(R.drawable.hopscotch__number9);
				bigNumber[10] = decodeAndScale(R.drawable.hopscotch__number10);
				
				if (initThread.isInterrupted())
					return;
				
				wheelbarrow=new Bitmap[11];
				wheelbarrow[0] = decodeAndScale(R.drawable.feed__bunny_wheel00);
				wheelbarrow[1] = decodeAndScale(R.drawable.feed__bunny_wheel01);
				wheelbarrow[2] = decodeAndScale(R.drawable.feed__bunny_wheel02);
			    wheelbarrow[3] = decodeAndScale(R.drawable.feed__bunny_wheel03);
			    wheelbarrow[4] = decodeAndScale(R.drawable.feed__bunny_wheel04);
			    wheelbarrow[5] = decodeAndScale(R.drawable.feed__bunny_wheel06);
			    wheelbarrow[6] = decodeAndScale(R.drawable.feed__bunny_wheel06);
			    wheelbarrow[7] = decodeAndScale(R.drawable.feed__bunny_wheel07);
			    wheelbarrow[8] = decodeAndScale(R.drawable.feed__bunny_wheel08);
			    wheelbarrow[9] = decodeAndScale(R.drawable.feed__bunny_wheel10);
			    wheelbarrow[10]= decodeAndScale(R.drawable.feed__bunny_wheel10);
			    
			    if (initThread.isInterrupted())
					return;
				
			    busFriends[2]   = decodeAndScale(R.drawable.passengers_1);
				busFriends[3]   = decodeAndScale(R.drawable.passengers_2);
				busFriends[4]   = decodeAndScale(R.drawable.passengers_3);
				busFriends[5]   = decodeAndScale(R.drawable.passengers_4);
				busFriends[6]   = decodeAndScale(R.drawable.passengers_5);
				busFriends[7]   = decodeAndScale(R.drawable.passengers_6);
				busFriends[8]   = decodeAndScale(R.drawable.passengers_7);
				busFriends[9]   = decodeAndScale(R.drawable.passengers_8);
				busFriends[10]  = decodeAndScale(R.drawable.passengers_9);
				busFriends[11]  = decodeAndScale(R.drawable.passengers_10);
				
				if (initThread.isInterrupted())
					return;
				
				treasures = new Bitmap[16];
				treasures[0] = decodeAndScale(R.drawable.treasurehunt__images_basket);
				treasures[1] = decodeAndScale(R.drawable.treasurehunt__images_flowerpot);
				treasures[2] = decodeAndScale(R.drawable.treasurehunt__images_bucket);
				treasures[3] = decodeAndScale(R.drawable.treasurehunt__images_gardenbag);
				treasures[4] = decodeAndScale(R.drawable.treasurehunt__images_hose);
				treasures[5] = decodeAndScale(R.drawable.toreasurehunt__imgaes_watercan);
				treasures[6] = decodeAndScale(R.drawable.background__babybunnies_carrotpatch);
				treasures[7] = decodeAndScale(R.drawable.treasure_carrotbasket);
				treasures[8] = decodeAndScale(R.drawable.treasure_tomatobasket);
				treasures[9] = decodeAndScale(R.drawable.treasures_busdriving_bench);
				treasures[10] = decodeAndScale(R.drawable.treasures_chalk);
				treasures[11] = decodeAndScale(R.drawable.treasures_gardenbag);
				treasures[12] = decodeAndScale(R.drawable.treasures_newstand);
				treasures[13] = decodeAndScale(R.drawable.purpleflowers);
				treasures[14] = decodeAndScale(R.drawable.blueflowers01);
				treasures[15] = decodeAndScale(R.drawable.yellowflowers);
				
				if (initThread.isInterrupted())
					return;
				
				
				friends[1] = new Charactar();
				friends[1].jumping = new Bitmap[3];
				friends[1].jumping[0] = decodeAndScale(R.drawable.friends__walking_piggy03);
				friends[1].jumping[1] = decodeAndScale(R.drawable.friends__jumping_piggy01);
				friends[1].jumping[2] = decodeAndScale(R.drawable.friends__jumping_piggy02);
				friends[1].missedJump = decodeAndScale(R.drawable.friends__stepping_piggy01);		
				friends[1].walking = new Bitmap[2];
				friends[1].walking[0] = decodeAndScale(R.drawable.friends__walking_piggy02);
				friends[1].walking[1] = decodeAndScale(R.drawable.friends__walking_piggy03);
				friends[1].stepping = new Bitmap[2];
				friends[1].stepping[0] = decodeAndScale(R.drawable.friends__stepping_piggy01);
				friends[1].stepping[1] = decodeAndScale(R.drawable.friends__stepping_piggy01);  //TODO: second frame
				friends[1].waiting = decodeAndScale(R.drawable.friends__original_piggy);
				friends[1].hurray = new Bitmap[3];
				friends[1].hurray[0] = friends[1].jumping[0]; 
				friends[1].hurray[1] = friends[1].jumping[1]; 
				friends[1].hurray[2] = friends[1].jumping[2]; 
			
				if (initThread.isInterrupted())
					return;
				
				friends[2] = new Charactar();
				friends[2].jumping = new Bitmap[3];
				friends[2].jumping[0] = decodeAndScale(R.drawable.friends__original_kitty);
				friends[2].jumping[1] = decodeAndScale(R.drawable.friends__jumping_kitty01);
				friends[2].jumping[2] = decodeAndScale(R.drawable.friends__jumping_kitty02);
				friends[2].missedJump = decodeAndScale(R.drawable.friends__walking_kitty01);
				friends[2].walking = new Bitmap[3];
				friends[2].walking[0] = decodeAndScale(R.drawable.friends__walking_kitty01);
				friends[2].walking[1] = decodeAndScale(R.drawable.friends__walking_kitty02);
				friends[2].walking[2] = decodeAndScale(R.drawable.friends__walking_kitty02);
				friends[2].stepping = new Bitmap[2];
				friends[2].stepping[0] = decodeAndScale(R.drawable.friends__stepping_kitty01);
				friends[2].stepping[1] = decodeAndScale(R.drawable.friends__stepping_kitty01); 
				friends[2].waiting = decodeAndScale(R.drawable.friends__original_kitty);
				friends[2].hurray = new Bitmap[3];
				friends[2].hurray[0] = friends[2].jumping[0]; 
				friends[2].hurray[1] = friends[2].jumping[1]; 
				friends[2].hurray[2] = friends[2].jumping[2]; 
			
				if (initThread.isInterrupted())
					return;
				
				
				friends[3] = new Charactar();
				friends[3].jumping = new Bitmap[3];
				friends[3].jumping[0] = decodeAndScale(R.drawable.friends__original_raccoon);
				friends[3].jumping[1] = decodeAndScale(R.drawable.friends__jumping_raccoon01);
				friends[3].jumping[2] = decodeAndScale(R.drawable.friends__jumping_raccoon02);
				friends[3].missedJump = decodeAndScale(R.drawable.friends__original_raccoon);
			    friends[3].walking = new Bitmap[2];
				friends[3].walking[0] = decodeAndScale(R.drawable.friends__walking_racoon01);
				friends[3].walking[1] = decodeAndScale(R.drawable.friends__walking_raccoon02); 
				friends[3].stepping = new Bitmap[2];
				friends[3].stepping[0] = decodeAndScale(R.drawable.friends__stepping_raccoon);
				friends[3].stepping[1] = decodeAndScale(R.drawable.friends__stepping_raccoon);  //TODO: second frame
				friends[3].waiting = decodeAndScale(R.drawable.friends__original_raccoon);
				friends[3].hurray = new Bitmap[3];
				friends[3].hurray[0] = friends[3].jumping[0]; 
				friends[3].hurray[1] = friends[3].jumping[1]; 
				friends[3].hurray[2] = friends[3].jumping[2]; 
			
				if (initThread.isInterrupted())
					return;
				
				
				friends[4] = new Charactar();
				friends[4].jumping = new Bitmap[3];
				friends[4].jumping[0] = decodeAndScale(R.drawable.friends__original_possom);
				friends[4].jumping[1] = decodeAndScale(R.drawable.friends__jumping_possom01);
				friends[4].jumping[2] = decodeAndScale(R.drawable.friends__jumping_possom01);
				friends[4].missedJump = decodeAndScale(R.drawable.friends__walking_possom01);
			    friends[4].walking = new Bitmap[3];
				friends[4].walking[0] = decodeAndScale(R.drawable.friends__walking_possom01);
				friends[4].walking[1] = decodeAndScale(R.drawable.friends__walking_possom02);
				friends[4].walking[2] = decodeAndScale(R.drawable.friends__walking_possom03);
				friends[4].stepping = new Bitmap[2];
				friends[4].stepping[0] = decodeAndScale(R.drawable.friends__original_possom);  //TODO: Correct bitmap
				friends[4].stepping[1] = decodeAndScale(R.drawable.friends__original_possom); //TODO: Correct bitmap
				friends[4].waiting = decodeAndScale(R.drawable.friends__original_possom);
				friends[4].hurray = new Bitmap[3];
				friends[4].hurray[0] = friends[4].jumping[0]; 
				friends[4].hurray[1] = friends[4].jumping[1]; 
				friends[4].hurray[2] = friends[4].jumping[2]; 
			
				if (initThread.isInterrupted())
					return;
				
				
				friends[5] = new Charactar();
				friends[5].jumping = new Bitmap[3];
				friends[5].jumping[0] = decodeAndScale(R.drawable.friends__original_squirrel);
				friends[5].jumping[1] = decodeAndScale(R.drawable.friends__jumping_squirrel01);
				friends[5].jumping[2] = decodeAndScale(R.drawable.friends__jumping_squirrel02);
				friends[5].missedJump = decodeAndScale(R.drawable.friends__walking_squirrel01);
			    friends[5].walking = new Bitmap[2];
				friends[5].walking[0] = decodeAndScale(R.drawable.friends__walking_squirrel01);
				friends[5].walking[1] = decodeAndScale(R.drawable.friends__walking_squirrel02);
				friends[5].stepping = new Bitmap[2];
				friends[5].stepping[0] = decodeAndScale(R.drawable.friends__stepping_squirrel);
				friends[5].stepping[1] = decodeAndScale(R.drawable.friends__stepping_squirrel);  //TODO: second frame
				friends[5].waiting = decodeAndScale(R.drawable.friends__original_squirrel);
				friends[5].hurray = new Bitmap[3];
				friends[5].hurray[0] = friends[5].jumping[0]; 
				friends[5].hurray[1] = friends[5].jumping[1]; 
				friends[5].hurray[2] = friends[5].jumping[2]; 
					
				if (initThread.isInterrupted())
					return;
				
				
				friends[6] = new Charactar();
				friends[6].jumping = new Bitmap[3];
				friends[6].jumping[0] = decodeAndScale(R.drawable.friends__original_mouse);
				friends[6].jumping[1] = decodeAndScale(R.drawable.friends__jumping_mouse01);
				friends[6].jumping[2] = decodeAndScale(R.drawable.friends__jumping_mouse01);
				friends[6].missedJump = decodeAndScale(R.drawable.friends__original_mouse);
			    friends[6].walking = new Bitmap[2];
				friends[6].walking[0] = decodeAndScale(R.drawable.friends__walking_mouse01);
				friends[6].walking[1] = decodeAndScale(R.drawable.friends__walking_mouse02);
				friends[6].stepping = new Bitmap[2];
				friends[6].stepping[0] = decodeAndScale(R.drawable.friends__stepping_mouse01);
				friends[6].stepping[1] = decodeAndScale(R.drawable.friends__stepping_mouse01);  //TODO: second frame
				friends[6].waiting = decodeAndScale(R.drawable.friends__original_mouse);
				friends[6].hurray = new Bitmap[3];
				friends[6].hurray[0] = friends[6].jumping[0]; 
				friends[6].hurray[1] = friends[6].jumping[1]; 
				friends[6].hurray[2] = friends[6].jumping[2]; 
				 
				if (initThread.isInterrupted())
					return;
				
				
				friends[7] = new Charactar();
				friends[7].jumping = new Bitmap[3];
				friends[7].jumping[0] = decodeAndScale(R.drawable.schoolbus__animal__skunk);
				friends[7].jumping[1] = decodeAndScale(R.drawable.friends__jumping_skunk01);
				friends[7].jumping[2] = decodeAndScale(R.drawable.friends__jumping_skunk02);
				friends[7].missedJump = decodeAndScale(R.drawable.schoolbus__animal__skunk);
			    friends[7].walking = new Bitmap[3];
				friends[7].walking[0] = decodeAndScale(R.drawable.friends__walking_skunk01);
				friends[7].walking[1] = decodeAndScale(R.drawable.friends__walking_skunk_02);
				friends[7].walking[2] = decodeAndScale(R.drawable.friends__walking_skunk03);
				friends[7].stepping = new Bitmap[2];
				friends[7].stepping[0] = decodeAndScale(R.drawable.friends__jumping_skunk01);
				friends[7].stepping[1] = decodeAndScale(R.drawable.friends__jumping_skunk02);  
				friends[7].waiting = decodeAndScale(R.drawable.friends__walking_skunk03);
				friends[7].hurray = new Bitmap[3];
				friends[7].hurray[0] = friends[7].jumping[0]; 
				friends[7].hurray[1] = friends[7].jumping[1]; 
				friends[7].hurray[2] = friends[7].jumping[2]; 
			
				if (initThread.isInterrupted())
					return;
				
				
				//owl
				friends[8] = new Charactar();
				friends[8].jumping = new Bitmap[3];
				friends[8].jumping[0] = decodeAndScale(R.drawable.schoolbus__friends_owl01);
				friends[8].jumping[1] = decodeAndScale(R.drawable.friends__jump_owl01);
				friends[8].jumping[2] = decodeAndScale(R.drawable.friends__jump_owl02);
				friends[8].missedJump = decodeAndScale(R.drawable.schoolbus__friends_owl03);
			    friends[8].walking = new Bitmap[4];
				friends[8].walking[0] = decodeAndScale(R.drawable.schoolbus__friends_owl01);
				friends[8].walking[1] = decodeAndScale(R.drawable.schoolbus_friends_owl02);
				friends[8].walking[2] = decodeAndScale(R.drawable.schoolbus__friends_owl03);
				friends[8].walking[3] = decodeAndScale(R.drawable.schoolbus__friends_owl04);
				friends[8].stepping = new Bitmap[2];
				friends[8].stepping[0] = decodeAndScale(R.drawable.friends__step_owl);
				friends[8].stepping[1] = decodeAndScale(R.drawable.friends__step_owl);  
				friends[8].waiting = decodeAndScale(R.drawable.schoolbus__friends_owl01);
				friends[8].hurray = new Bitmap[3];
				friends[8].hurray[0] = friends[8].jumping[0]; 
				friends[8].hurray[1] = friends[8].jumping[1]; 
				friends[8].hurray[2] = friends[8].jumping[2]; 
				
				if (initThread.isInterrupted())
					return;
				
				
				//flamingo
				friends[9] = new Charactar();
				friends[9].jumping = new Bitmap[3];
				friends[9].jumping[0] = decodeAndScale(R.drawable.friends__jumpfrontview_flamingo01);
				friends[9].jumping[1] = decodeAndScale(R.drawable.friends__jumpfrontview_flamingo02);
				friends[9].jumping[2] = decodeAndScale(R.drawable.friends__jumpfrontview_flamingo03);
				friends[9].missedJump = decodeAndScale(R.drawable.friends__jumpfrontview04);
			    friends[9].walking = new Bitmap[2];
				friends[9].walking[0] = decodeAndScale(R.drawable.friends__walking_flamingo01);
				friends[9].walking[1] = decodeAndScale(R.drawable.friends__walk02_flamingo);
				friends[9].stepping = new Bitmap[2];
				friends[9].stepping[0] = decodeAndScale(R.drawable.friends__busstep_flamingo01);
				friends[9].stepping[1] = decodeAndScale(R.drawable.friends__busstep_flamingo02);  
				friends[9].waiting = decodeAndScale(R.drawable.friends__jumpfrontview_flamingo01);
				friends[9].hurray = new Bitmap[5];
				friends[9].hurray[0] = friends[9].jumping[0]; 
				friends[9].hurray[1] = friends[9].jumping[1]; 
				friends[9].hurray[2] = friends[9].jumping[2]; 
				friends[9].hurray[3] = friends[9].stepping[0];
				friends[9].hurray[4] = friends[9].stepping[1]; 
				if (initThread.isInterrupted())
					return;
				 
			    bunnyPixels.scaleDown(0.6f);
				DrawingView.babyInfo[0].frames = new BabyBunnyFrames(0xFFFFFFFF,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[1].frames = new BabyBunnyFrames(0xFF8c99f6/*caf75d/*e04f4e*/,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[2].frames = new BabyBunnyFrames(0xFFf4b54f/*f4f293*/,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[3].frames = DrawingView.babyInfo[0].frames;//new BabyBunnyFrames(0xFFb3e8ca,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[4].frames = DrawingView.babyInfo[1].frames;//new BabyBunnyFrames(0xFF9098ff,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[5].frames = DrawingView.babyInfo[2].frames;//new BabyBunnyFrames(0xFF555a8e,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[6].frames = DrawingView.babyInfo[0].frames;//new BabyBunnyFrames(0xFFb3b8e8,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[7].frames = DrawingView.babyInfo[1].frames;//new BabyBunnyFrames(0xFFf8b850,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[8].frames = DrawingView.babyInfo[2].frames;//new BabyBunnyFrames(0xFFcaf75d,bunnyPixels);
				if (initThread.isInterrupted())
					return;
				DrawingView.babyInfo[9].frames = DrawingView.babyInfo[0].frames;//new BabyBunnyFrames(0xFF91af9d,bunnyPixels);	
				if (initThread.isInterrupted())
					return;
				
				Log.i("BUNNY","Stage 2 init done");
				initDone=true;	
			}
		}});
		initThread.setPriority(Thread.MIN_PRIORITY);
		

		
		//Debug.stopMethodTracing();
		
      
	    //Hopscotch
		ball     = decodeAndScale(R.drawable.ball);
		
		bigNumber[0] = decodeAndScale(R.drawable.hopscotch__number0gif);
		
		
	   
	    //Road
		road[RoadSegment.STREIGHT] = decodeAndScale(R.drawable.h_road_streight);
		road[RoadSegment.LEFT]     = decodeAndScale(R.drawable.h_road_left);
		road[RoadSegment.RIGHT]    = decodeAndScale(R.drawable.h_road_right);
		
		
		Log.i("BUNNY","Loaded road segments");
		
		busTopView      = decodeAndScale(R.drawable.schoolbus__slightleft_01);
		busSide[0]      = decodeAndScale(R.drawable.bus__sideview_open);
		busSide[1]      = decodeAndScale(R.drawable.bus__sideview_halfopened);
		busSide[2]      = decodeAndScale(R.drawable.bus__sideview_closed);
		
		busFriends[0]   = decodeAndScale(R.drawable.passengers_none);
		busFriends[1]   = decodeAndScale(R.drawable.passengers_0);
		//load the rest in the thread
	
		steeringWheel= decodeAndScale(R.drawable.steering_wheel);
		
		stopButton = decodeAndScale(R.drawable.button_stop);
		goButton   = decodeAndScale(R.drawable.button_go);
		postIt     = decodeAndScale(R.drawable.post_it_big);
		busStopSign= decodeAndScale(R.drawable.background__busdriving_busstop);
		newsStand  = decodeAndScale(R.drawable.background__busdriving_newstand);
		
		//mini golf
		golfBall      = decodeAndScale(R.drawable.golf_ball);
		windmillTower = decodeAndScale(R.drawable.minigolf__windmill_building);
		windmillBlades= decodeAndScale(R.drawable.minigolf__windmill_blades);
		flag[0]       = decodeAndScale(R.drawable.background__minigolf_flag01);
		flag[1]       = decodeAndScale(R.drawable.background__minigolf_flag02);
		flag[2]       = decodeAndScale(R.drawable.background__mingolf_flag03);
		
		
		
		
		//tic-tac-toe
		tictactoe_board = decodeAndScale(R.drawable.tictactoe_board);
		tictactoe_x     = decodeAndScale(R.drawable.tictactoe_carrots);
		tictactoe_o     = decodeAndScale(R.drawable.tictactoe_cabbage);
		
		//baby bunny cage
		cage[0] = decodeAndScale(R.drawable.bunnycage__doorsonly_closed);
		cage[1] = decodeAndScale(R.drawable.bunnycage__doorsonly_dooropen01);
		cage[2] = decodeAndScale(R.drawable.bunnycage__doorsonly_dooropen02);
		cage[3] = decodeAndScale(R.drawable.bunnycage__doorsonly_dooropen03);
		cage[4] = decodeAndScale(R.drawable.bunnycage__doorsonly_dooropen04);
		cageBackground = decodeAndScale(R.drawable.bunnycage__open_nodoorswithbkrd); 
		
		jumpButton = decodeAndScale(R.drawable.jumprope__jump_button);
		jumpButtonPressed = decodeAndScale(R.drawable.jumprope__jump_button_pressed);
		jumpButtonOff     = decodeAndScale(R.drawable.jumprope__jump_button_off);
		
		Log.i("BUNNY","Loaded up to jump");
		
		
		
		
		friends = new Charactar[10];//TODO make this 10
		
		friends[0] = new Charactar();
		friends[0].jumping = new Bitmap[3];
		friends[0].jumping[0] = decodeAndScale(R.drawable.friends__jumping_dog0);
		friends[0].jumping[1] = decodeAndScale(R.drawable.friends__jumping_dog01);
		friends[0].jumping[2] = decodeAndScale(R.drawable.friends__jumping_dog02);
		friends[0].missedJump = decodeAndScale(R.drawable.friends__jumping_dogmisses);
		friends[0].walking = new Bitmap[2];
		friends[0].walking[0] = decodeAndScale(R.drawable.friends__walking_dog01);
		friends[0].walking[1] = decodeAndScale(R.drawable.friends__walking_dog02);
		friends[0].stepping = new Bitmap[2];
		friends[0].stepping[0] = decodeAndScale(R.drawable.friends__stepping_dog01);
		friends[0].stepping[1] = decodeAndScale(R.drawable.friends__stepping_dog02);
		friends[0].waiting = decodeAndScale(R.drawable.friends__original_dog);
		friends[0].hurray = new Bitmap[3];
		friends[0].hurray[0] = friends[0].jumping[0]; 
		friends[0].hurray[1] = friends[0].jumping[1]; 
		friends[0].hurray[2] = friends[0].jumping[2]; 
		
		hatAndStars[0] = decodeAndScale(R.drawable.menubutton__magichat01);
		hatAndStars[1] = decodeAndScale(R.drawable.menubutton__magichat2);
		
		BitmapFactory.Options ops = new BitmapFactory.Options(); //Workaround for an android garbage collection issue with bitmaps.
		ops.inPurgeable=true;
		ops.inInputShareable=true;
		ops.inPreferredConfig = Bitmap.Config.RGB_565;
		busSideView = BitmapFactory.decodeResource(context.getResources(),R.drawable.bus,ops);
		allBitmaps.add(busSideView);
		
		babyMenuFeed = decodeAndScale(R.drawable.baby_menu_feed);
		babyMenuFollow = decodeAndScale(R.drawable.baby_menu_follow);
		babyMenuCage = decodeAndScale(R.drawable.baby_menu_cage);
		babyMenuDance = decodeAndScale(R.drawable.baby_menu_dance);
		
		
		menuButton[0] = decodeAndScale(R.drawable.menu__newbuttons_feedthebunny);
		menuButton[1] = decodeAndScale(R.drawable.menu__newbuttons_tictactoe);
		menuButton[2] = decodeAndScale(R.drawable.menu__newbutton_busdriving);
		menuButton[3] = decodeAndScale(R.drawable.menu__newbuttons_minigolf);
		menuButton[4] = decodeAndScale(R.drawable.menu__newbuttons_babybunnies);
		menuButton[5] = decodeAndScale(R.drawable.menu__newbuttons_jumprope);
		menuButton[6] = decodeAndScale(R.drawable.menu__newbuttons__hopscotch);
		menuButton[7] = decodeAndScale(R.drawable.menu__newbuttons_questions);
		
		
		waterCan[0] = decodeAndScale(R.drawable.background__feedthebunny_watercan01);
		waterCan[1] = decodeAndScale(R.drawable.background__feedthebunny_watercan02);
		waterCan[2] = decodeAndScale(R.drawable.backgorund__feedthebunny_watercan03);
		waterCan[3] = decodeAndScale(R.drawable.backgorund__feedthebunny_watercan04);
		waterCan[4] = decodeAndScale(R.drawable.backgorund__feedthebunny_watercan05);
		waterCan[5] = decodeAndScale(R.drawable.backgorund__feedthebunny_watercan06);
		
		swing1[0] = decodeAndScale(R.drawable.swinger__red__1);
		swing1[1] = decodeAndScale(R.drawable.swinger__red__2);
		swing1[2] = decodeAndScale(R.drawable.swinger__red__3);
		swing1[3] = decodeAndScale(R.drawable.swinger__red__4);
		swing1[4] = decodeAndScale(R.drawable.swinger__red__5);
		swing1[5] = decodeAndScale(R.drawable.swinger__red__6);
		swing1[6] = decodeAndScale(R.drawable.swinger__red__7);
		swing1[7] = decodeAndScale(R.drawable.swinger__red__8);
		
		swing2[0] = decodeAndScale(R.drawable.background__swings_greenbunny01);
		swing2[1] = decodeAndScale(R.drawable.background__swings_greenbunny02);
		swing2[2] = decodeAndScale(R.drawable.background__swings_greenbunny03);
		swing2[3] = decodeAndScale(R.drawable.background__swings_greenbunny04);
		swing2[4] = decodeAndScale(R.drawable.background__swing_greenbunny05);
		swing2[5] = decodeAndScale(R.drawable.background__swings_greenbunny06);
		swing2[6] = decodeAndScale(R.drawable.background__swings_greenbunny07);
		swing2[7] = decodeAndScale(R.drawable.background__swings_greenbunny08);
		
		butterfly = new Bitmap[6];
		butterfly[0] = decodeAndScale(R.drawable.butterfly01);
		butterfly[1] = decodeAndScale(R.drawable.butterfly02);
		butterfly[2] = decodeAndScale(R.drawable.butterfly03);
		butterfly[3] = decodeAndScale(R.drawable.butterfly04);
		butterfly[4] = decodeAndScale(R.drawable.butterfly05);
		butterfly[5] = decodeAndScale(R.drawable.butterfly06);
		
		carrots  = decodeAndScale(R.drawable.feedbunny__carrots);
	    
		seesaw = new Bitmap[4];
		seesaw[0] = decodeAndScale(R.drawable.seesaw01);
		seesaw[1] = decodeAndScale(R.drawable.seesaw02);
		seesaw[2] = decodeAndScale(R.drawable.seesaw03);
		seesaw[3] = seesaw[1];
		
		              
		Log.i("BUNNY","Loaded initial bitmaps");
		
		sound = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
	    hopSound    = sound.load(context, R.raw.boing , 1);
	    ballInCup = sound.load(context	, R.raw.ball_in_cup,1);
		ballTap = sound.load(context,R.raw.golf_tap,1);
		shortClap = sound.load(context, R.raw.short_clap,1);
		femaleCheer = sound.load(context, R.raw.female_cheer,1);
		familyClap = sound.load(context,R.raw.audience_clappig,1);
		slotsound = sound.load(context, R.raw.slot_machine1, 1);
		rockTapSound = sound.load(context, R.raw.rock_tap, 1);
		step1 = sound.load(context, R.raw.foot_sound_step1,1);
		step2 = sound.load(context, R.raw.foot_sound_step2,1);
		step3 = sound.load(context, R.raw.foot_sound_step3,1);
		winning = sound.load(context, R.raw.happy_sound, 1);
		sad = sound.load(context,R.raw.sad_sound, 1);
		boing1 = sound.load(context, R.raw.boing1,1);
		boing2 = sound.load(context, R.raw.boing2,1);
		
		
		if (!initDone){
			initThread.start();
		}
	}
	
	SoundPool sound;
	

	int backgroundMap[] = {
		R.drawable.background__feedthebunny_fixed,
		R.drawable.backgrounds__tictactoe_fixed,
		R.drawable.background__busdriving_fixed,
		R.drawable.background__minigolf_fixed,
		R.drawable.background__babybunnies_fixed,
		R.drawable.background__jumprope_fixed,
		R.drawable.background__hopscotch_fixed
	};

	Bitmap[] backgroundBitmaps = new Bitmap[7];
	
		
	Bitmap getBackground(int X){
		if (X < backgroundBitmaps.length && X >= 0 &&
			backgroundBitmaps[X]!=null){
			return backgroundBitmaps[X];
		}
		return null;
	}
	
	void loadGridPositionBackgrounds(int X){
		BitmapFactory.Options ops = new BitmapFactory.Options(); //Workaround for an android garbage collection issue with bitmaps.
		ops.inPurgeable=true;
		ops.inInputShareable=true;
		ops.inPreferredConfig = Bitmap.Config.RGB_565;
		if (X < backgroundBitmaps.length && X >= 0){
			if (backgroundBitmaps[X]==null) {
				Bitmap b = BitmapFactory.decodeResource(res,backgroundMap[X],ops);
				backgroundBitmaps[X] = b;
			}
		}
	}
	
	void freeCache(){
		initThread.interrupt();
		synchronized (this){
			sound.release();
			initDone=false;
			Iterator<Bitmap> i = allBitmaps.iterator();
			int count=0;
			while (i.hasNext()){
				Bitmap b = i.next();
				if (b!=null){
					b.recycle();
					count++;
				}
			}
			Log.i("BITMAP","Recycling "+count);
		}
		/*07-13 13:24:11.182: ERROR/AndroidRuntime(3934): Uncaught handler: thread main exiting due to uncaught exception
07-13 13:24:11.392: ERROR/AndroidRuntime(3934): java.lang.RuntimeException: Unable to destroy activity {com.catglo.dashplayground/com.catglo.dashplayground.BunnyGames}: java.util.ConcurrentModificationException
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:3364)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread.handleDestroyActivity(ActivityThread.java:3382)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread.access$2700(ActivityThread.java:116)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1826)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.os.Handler.dispatchMessage(Handler.java:99)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.os.Looper.loop(Looper.java:123)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread.main(ActivityThread.java:4203)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at java.lang.reflect.Method.invokeNative(Native Method)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at java.lang.reflect.Method.invoke(Method.java:521)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at dalvik.system.NativeStart.main(Native Method)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934): Caused by: java.util.ConcurrentModificationException
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at java.util.LinkedList$LinkIterator.next(LinkedList.java:119)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at com.catglo.dashplayground.BitmapCache.freeCache(BitmapCache.java:1148)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at com.catglo.dashplayground.BunnyGames.onDestroy(BunnyGames.java:198)
07-13 13:24:11.392: ERROR/AndroidRuntime(3934):     at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:3351)

* I got this on exit and added synchronized in bitmapCache thread
*/
		
	    
    	for (int X = 0; X < backgroundBitmaps.length; X++){
    		if (backgroundBitmaps[X]!=null) {
				backgroundBitmaps[X].recycle();
    		}
    	}
	}

	public void freeGridPositionBackground(int X) {
		
		if (X < backgroundBitmaps.length && X > 0 &&
		    backgroundBitmaps[X]!=null){
			backgroundBitmaps[X].recycle();
			backgroundBitmaps[X]=null;
		}	
	}

	public void sadSound(){
		sound.play(sad, 1, 1, 1, 0, 1);
	}
	
	public void femaleCheer() {
		sound.play(femaleCheer, 1, 1, 1, 0, 1);
	}

	public void familyClap() {
		sound.play(familyClap, 1, 1, 1, 0, 1);
	}

	public void shortClap() {
		sound.play(shortClap, 1, 1, 1, 0, 1);
	}

	public void slotSound() {
		//For feed the bunny when he picks a carrot
		//sound.play(slotsound, 1, 1, 1, 0, 1);
		sound.play(winning,  1, 1, 1, 0, 1);
	}

	public void ballTap() {
		sound.play(ballTap, 1, 1, 1, 0, 1);
	}

	public void ballInCup() {
		sound.play(ballInCup, 1, 1, 1, 0, 1);
	}
	
	public void play(int s){
		sound.play(s, 1, 1, 1, 0, 1);
	}

	public void rockTapSound(float f) {
		sound.play(rockTapSound, f, f, 1, 0, 1);
	}
	
	public void boing1() {
		sound.play(boing1, 1, 1, 1, 0, 1);
	}
	
	public void boing2() {
		sound.play(boing2, 1, 1, 1, 0, 1);
	}
	
	public void winning(float vol){
		sound.play(winning,  vol, vol, 1, 0, 1);
	}
	
	/*07-09 15:55:50.117: ERROR/AndroidRuntime(3526): java.lang.RuntimeException: Unable to destroy activity {com.catglo.dashplayground/com.catglo.dashplayground.BunnyGames}: java.util.ConcurrentModificationException
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:3364)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread.handleDestroyActivity(ActivityThread.java:3382)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread.access$2700(ActivityThread.java:116)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1826)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.os.Handler.dispatchMessage(Handler.java:99)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.os.Looper.loop(Looper.java:123)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread.main(ActivityThread.java:4203)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at java.lang.reflect.Method.invokeNative(Native Method)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at java.lang.reflect.Method.invoke(Method.java:521)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at dalvik.system.NativeStart.main(Native Method)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526): Caused by: java.util.ConcurrentModificationException
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at java.util.LinkedList$LinkIterator.next(LinkedList.java:119)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at com.catglo.dashplayground.BitmapCache.freeCache(BitmapCache.java:1105)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at com.catglo.dashplayground.BunnyGames.onDestroy(BunnyGames.java:193)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     at android.app.ActivityThread.performDestroyActivity(ActivityThread.java:3351)
07-09 15:55:50.117: ERROR/AndroidRuntime(3526):     ... 11 more

while exiting 
*
*/
}