package ch.epfl.flamemaker.geometry2d;

import java.util.HashSet;

/**
 * Simulates a 2-d rectangle.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public class Rectangle {
	// Variables defining a rectangle
	private final Point center;
	private final double height;
	private final double width;

	/**
	 * Builder of a rectangle
	 * 
	 * @param center
	 *            Rectangle Center
	 * @param width
	 *            Rectangle width
	 * @param height
	 *            Rectangle height
	 */
	public Rectangle(Point center, double width, double height)
			throws IllegalArgumentException {
		// Check width and height
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("Strictly positive values only");

		// Deep copy of center, we don't want any mutations
		this.height = height;
		this.width = width;
		this.center = new Point(center.x(), center.y());
	}

	/**
	 * Tests if a point is in the rectangle.
	 * 
	 * @param p
	 *            Point to be tested
	 * @return true if and only if left <= x < right and bottom <= y < top.
	 *         (Note that a rectangle only contains the left-bottom corner).
	 */
	public boolean contains(Point p) {
		return ((p.x() >= this.left()) && (p.x() < this.right())
				&& (p.y() >= this.bottom()) && (p.y() < this.top()));
	}

	/**
	 * Creates a new Rectangle with the same center, and a different aspect
	 * ratio, which contains every point in the base rectangle.
	 * 
	 * @param aspectRatio
	 *            Ratio of the new rectangle
	 * @return Rectangle created.
	 * @throws IllegalArgumentException
	 *             if ratio is negative or zero.
	 */
	public Rectangle expandToAspectRatio(double aspectRatio)
			throws IllegalArgumentException {
		if (aspectRatio <= 0)
			throw new IllegalArgumentException("Strictly positive ratio only");
		/*
		 * if aspectRatio is between 0 and 1, the first test pass and the width
		 * is kept while expanding the height. if aspectRatio is greater than 1,
		 * only the second test pass and the height is kept while expanding the
		 * width. if aspectRatio equals 1, nothing changes.
		 */
		double finalWidth = height * aspectRatio;
		if (finalWidth < width)
			finalWidth = width;

		double finalHeight = width / aspectRatio;
		if (finalHeight < height)
			finalHeight = height;

		return new Rectangle(center, finalWidth, finalHeight);
	}

	/**
	 * Returns ratio width/height of the rectangle.
	 * @return the ratio
	 */
	public double aspectRatio() {
		return width / height;
	}

	/**
	 * Returns rectangle width.
	 * @return the width of the rectangle
	 */
	public double width() {
		return width;
	}

	/**
	 * Returns rectangle height.
	 * @return the height of the rectangle
	 */
	public double height() {
		return height;
	}

	/**
	 * Returns the smallest x coordinate of the rectangle.
	 * @return the smallest x coordinate
	 */
	public double left() {
		return center.x() - width / 2;
	}

	/**
	 * Returns the biggest x coordinate of the rectangle.
	 * @return the biggest x coordinate
	 */
	public double right() {
		return center.x() + width / 2;
	}

	/**
	 * Returns the smallest y coordinate of the rectangle.
	 * @return the smallest y coordinate
	 */
	public double bottom() {
		return center.y() - height / 2;
	}

	/**
	 * Returns the biggest y coordinate of the rectangle.
	 * @return the biggest y coordinate
	 */
	public double top() {
		return center.y() + height / 2;
	}

	/**
	 * Returns the center of the rectangle.
	 * @return the center
	 */
	public Point center() {
		return center;
	}

	/**
	 * Returns String containing informations on the rectangle.
	 * @return the String
	 */
	public String toString() {
		return "(" + center.toString() + "," + width + "," + height + ")";
	}
	
	public static class ObservableBuilder{
		
		private double height;
		private double width;
		private Point center;
		
		private HashSet<Observer> observers;
		
		public ObservableBuilder(Rectangle rectangle)
				throws IllegalArgumentException {
			
			this.height = rectangle.height;
			this.width = rectangle.width;
			this.center = rectangle.center;
			
			observers = new HashSet<Observer>();
		}
		
		public void setHeight(double newHeight){
			// Check height
			if (newHeight <= 0)
				throw new IllegalArgumentException("Strictly positive values only");
			
			this.height = newHeight;
			notifyObservers();
		}
		
		public void setWidth(double newWidth){
			// Check height
			if (newWidth <= 0)
				throw new IllegalArgumentException("Strictly positive values only");
			
			this.width = newWidth;
			notifyObservers();
		}
		
		public void setCenter(Point newCenter){
			this.center = newCenter;
			notifyObservers();
		}
		
		public void zoom(double ratio){
			width = width/ratio;
			height = height/ratio;
			notifyObservers();
		}
	
		public double width(){
			return width;
		}
		
		public double height(){
			return height;
		}
		
		public Point center(){
			return center;
		}
		
		public Rectangle build(){
			return new Rectangle(center, width, height);
		}
		
		public void addObserver(Observer newObserver){
			observers.add(newObserver);
		}
		
		public void removeObserver(Observer observer){
			observers.remove(observer);
		}
		
		public void notifyObservers(){
			for(Observer o:observers){
				o.update();
			}
		}
		
		public  interface Observer{
			void update();
		}
	}
}
