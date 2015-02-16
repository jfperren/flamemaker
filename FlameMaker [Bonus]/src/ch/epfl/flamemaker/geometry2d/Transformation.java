package ch.epfl.flamemaker.geometry2d;

/**
 * Interface defining a 2-d geometrical transformation.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public interface Transformation {
	/**
	 * Transform the point through the transformation.
	 * 
	 * @param p
	 *            Point you want to transform.
	 * @return Point p after applying the transformation
	 */
	abstract Point transformPoint(Point p);

}
