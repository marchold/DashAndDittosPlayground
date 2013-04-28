package com.catglo.dashplaygroundfree;

import android.graphics.Point;

public class StreightLine {
	StreightLine(Point A, Point B){
		this.A = new Point(A);
		this.B = new Point(B);
	}
	public StreightLine(Point start, float angle, float distance) {
		this.A = start;
		float theta = (float) Math.toRadians(angle);
		this.B = new Point();
		B.x = A.x + (int) (distance * Math.cos( theta ));
		B.y = A.y + (int) (distance * Math.sin( theta ));  
	
	}
	Point A;
	Point B;
	
	

	Point intersects(StreightLine other){
		Point intersect=null;
		
    	Point C = other.A;
		Point D = other.B;
			
		float g = getHforABCD(C,D,A,B);
		float h = getHforABCD(A,B,C,D);
		if (h >=0 && h <= 1 && g >=0 && g <=1){
			intersect=_intersection;
		}	
		return intersect;
	}
	
	private Point _intersection;
	private float getHforABCD(Point A, Point B, Point C, Point D){
		
		Point E = new Point(B.x-A.x, B.y-A.y );
		Point F = new Point(D.x-C.x, D.y-C.y );
		Point P = new Point(-E.y,E.x);//P = ( -Ey, Ex )
		//h = ( (A-C) * P ) / ( F * P )
		//Point Q = new Point()
		
		
		//F*P (Fx*Px)+(Fy*Py)
		float FxP = (F.x*P.x)+(F.y*P.y);
		if (FxP==0){
			//The lines are parallel
		} else {
			Point AmC = new Point(A.x-C.x,A.y-C.y);
			float AmCxP = (AmC.x*P.x)+(AmC.y*P.y);
			float h = AmCxP/FxP;
			//If h is exactly 0 or 1 the lines touch at an end-point. You can consider this an "intersection" or not as you see fit.
			//Specifically, h is how much you have to multiply the length of the line in order to exactly touch the other line.
			//Therefore, If h<0, it means the rectangle line is "behind" the given line (with "direction" being "from A to B"), and if h>1 the rectangle line is "in front" of the given line.
			if (h>=0 && h <=1){
				_intersection = new Point((int)(C.x+F.x*h),(int)(C.y+F.y*h));//C + F*h.
				return h;
				
			} 
		}
		return Float.MAX_VALUE;
	}
	public float[] getPoints() {
		float[] points = new float[4];
		points[0] = A.x;
		points[1] = A.y;
		points[2] = B.x;
		points[3] = B.y;
		return points;
	}
}
