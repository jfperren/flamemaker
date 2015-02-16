package ch.epfl.flamemaker.geometry2d;

/**
 * Simulates a 2-d point.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public class Point {

	// Origin of the 2-d plan (0,0)
	public static Point ORIGIN = new Point(0, 0);

	private final double x; // X coordinate
	private final double y; // Y coordinate
	private final double rPow2;

	/**
	 * Builder of a Point.
	 * 
	 * @param x
	 *            x-axis coordinate
	 * @param y
	 *            y-axis coordinate
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
		this.rPow2 = Math.pow(r(), 2);
	}
	/**
	 * 
	 * @return
	 */
	public double rPow2(){
		return rPow2;
	}

	/**
	 * Returns X-axis coordinate of the point.
	 * @return the X-Axis coordinate
	 */
	public double x() {
		return x;
	}

	/**
	 * Returns Y-Axis coordinate of the point.
	 * @return the Y-Axis coordinate
	 */
	public double y() {
		return y;
	}

	/**
	 * Returns distance from point to origin.
	 * @return the distance
	 */
	public double r() {
		return Math.sqrt(Math.pow(x,2)+ Math.pow(y, 2));
	}

	/**
	 * Returns angle between the vector(ORIGIN to POINT) and the X-axis.
	 * @return the angle
	 */
	public double theta() {
		return Math.atan2(y, x);
	}

	/**
	 * Returns String giving the two coordinates.
	 * @return the String
	 */
	public String toString() {
		String s = "(" + x + "," + y + ")";
		return s;
	}
}
