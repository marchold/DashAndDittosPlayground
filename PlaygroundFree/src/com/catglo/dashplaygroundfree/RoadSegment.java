package com.catglo.dashplaygroundfree;

import android.graphics.Point;

/***************************************************************
 * 
 * RoadSegment class is responsible for defining the pieces 
 * that the road can be build out of for the BusDrivingGame.
 *
 ***************************************************************/
class RoadSegment{
	//Member variables
	int startX;
	int y;
	int type;
	int length;
	int endX;
	PollyLine leftSide;
	PollyLine rightSide;
	
	
	//Types of roads
	static final int STREIGHT=0;
	static final int RIGHT=1;
	static final int LEFT=2;
	static final int WIDE_STREIGHT= 3;
	static final int WIDE_RIGHT   = 4;
	static final int WIDE_LEFT    = 5;
	static final int WIDE_TO_NARROW = 6;
	static final int NARROW_TO_WIDE = 7;
	
	//road types length and top/bottom horizantal offset                     Wide     Wide  Wide  to      to
	//                                                   Straight Right Left Straight Right Left  narrow  wide
	        static final int[] ROAD_LENGTH =              {219    ,203, 203 ,221     ,378 , 380  ,290,    288}; 
	private static final int[] ROAD_HORIZANTAL_MOVE =     {0      ,132,-123,0        ,255 ,-239  ,99 ,    -1/*-128*/};
	private static final int[] ROAD_HORIZANTAL_PRE_MOVE = {0      ,1  ,-122,0        ,1   ,-238  ,1  ,    1/*-128*/};
	 
	//Points that map to the left and right sides of the various road types
	private static final Point[] WIDE_STREIGHT_ROAD_LEFTSIDE = {new Point(0,0)  , new Point(0  ,158)};
	private static final Point[] WIDE_STRIEGHT_ROAD_RIGHTSIDE = {new Point(250,0), new Point(250,158)};
	
	private static final Point[] STREIGHT_ROAD_LEFTSIDE  = {new Point(0,0)  , new Point(0  ,217)};
	private static final Point[] STREIGHT_ROAD_RIGHTSIDE = {new Point(144,0), new Point(144,217)};
	
	private static final Point[] RIGHT_ROAD_LEFTSIDE =  {new Point(129,0),new Point(123,36),new Point(15,157),new Point(0,197)};
	private static final Point[] RIGHT_ROAD_RIGHTSIDE = {new Point(278,0),new Point(263,38),new Point(150,160),new Point(135,197)};
	
	private static final Point[] WIDE_RIGHT_ROAD_LEFTSIDE  = {new Point(258,0),new Point(249,55),new Point(36,287),new Point(0,378)};
	private static final Point[] WIDE_RIGHT_ROAD_RIGHTSIDE = {new Point(508,0),new Point(488,78),new Point(280,296),new Point(248,378)};
	
	
	private static final Point[] LEFT_ROAD_LEFTSIDE = {new Point(0,0),new Point(13,37),new Point(115,166),new Point(121,199)};
	private static final Point[] LEFT_ROAD_RIGHTSIDE = {new Point(138,0),new Point(152,41),new Point(257,177),new Point(261,202)};
	
	private static final Point[] WIDE_LEFT_ROAD_LEFTSIDE  = {new Point(0,0)  ,new Point(2,32)  ,new Point(37,99) ,new Point(220,290) ,new Point(241,324),new Point(253,354),new Point(257,389)};
	private static final Point[] WIDE_LEFT_ROAD_RIGHTSIDE = {new Point(238,0),new Point(257,66),new Point(455,288),new Point(491,357),new Point(497,388)};
	
	private static final Point[] WIDE_TO_NARROW_LEFTSIDE  = {new Point(100,0),new Point(97,52),new Point(7,224),new Point(0,288)};
	private static final Point[] WIDE_TO_NARROW_RIGHTSIDE = {new Point(242,0),new Point(242,288)};
	
	private static final Point[] NARROW_TO_WIDE_LEFTSIDE = {new Point(0,0),new Point(0,287)};
	private static final Point[] NARROW_TO_WIDE_RIGHTSIDE= {new Point(239,0),new Point(235,91),new Point(140,251),new Point(139,288)};
	
		
	RoadSegment(int X, int Y, int type){
		this.startX = X+ROAD_HORIZANTAL_PRE_MOVE[type];
		this.y = Y;
		this.type =type;
		this.length = ROAD_LENGTH[type];
		this.endX = X+ROAD_HORIZANTAL_MOVE[type];
		switch (type){
		case STREIGHT:
			leftSide = new PollyLine(STREIGHT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(STREIGHT_ROAD_RIGHTSIDE);
		break;
		case RIGHT:
			leftSide  = new PollyLine(RIGHT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(RIGHT_ROAD_RIGHTSIDE);
		break;
		case LEFT:
			leftSide  = new PollyLine(LEFT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(LEFT_ROAD_RIGHTSIDE);
		break;	
		case NARROW_TO_WIDE:
			leftSide = new PollyLine(NARROW_TO_WIDE_LEFTSIDE);
			rightSide = new PollyLine(NARROW_TO_WIDE_RIGHTSIDE);
		break;
		case WIDE_TO_NARROW:
			leftSide = new PollyLine(WIDE_TO_NARROW_LEFTSIDE);
			rightSide = new PollyLine(WIDE_TO_NARROW_RIGHTSIDE);
		break;
		case WIDE_STREIGHT:
			leftSide = new PollyLine(WIDE_STREIGHT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(WIDE_STRIEGHT_ROAD_RIGHTSIDE);
		break;
		case WIDE_RIGHT:
			leftSide = new PollyLine(WIDE_RIGHT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(WIDE_RIGHT_ROAD_RIGHTSIDE);
		break;
		case WIDE_LEFT:
			leftSide = new PollyLine(WIDE_LEFT_ROAD_LEFTSIDE);
			rightSide = new PollyLine(WIDE_LEFT_ROAD_RIGHTSIDE);
		break;
		}
	}

	
}
