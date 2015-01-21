package se.throwthebombservice.data;

public class MathVector {

	double x;
	double y;
	
	public MathVector(double x, double y)  {
		
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		
		return x;
	}
	
	public double getY() {
		
		return y;
	}
	
	public double getNorm() {
		
		return (Math.sqrt((x * x) + (y * y)));
	}
}
