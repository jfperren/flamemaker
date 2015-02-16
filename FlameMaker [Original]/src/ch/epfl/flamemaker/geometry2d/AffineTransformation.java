package ch.epfl.flamemaker.geometry2d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * Simulates a 2d affine transformation using a 3x3 matrix.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public class AffineTransformation implements Transformation {
	/**
	 * This Transformation doesn't change anything
	 */
	public static final AffineTransformation IDENTITY = new AffineTransformation(
			1, 0, 0, 0, 1, 0);

	// Parameters of the matrix
	private final double A;
	private final double B;
	private final double C;
	private final double D;
	private final double E;
	private final double F;

	/**
	 * Builder
	 * 
	 * @param a
	 *            Parameter of the matrix
	 * @param b
	 *            Parameter of the matrix
	 * @param c
	 *            Parameter of the matrix
	 * @param d
	 *            Parameter of the matrix
	 * @param e
	 *            Parameter of the matrix
	 * @param f
	 *            Parameter of the matrix
	 */
	public AffineTransformation(double a, double b, double c, double d,
			double e, double f) {

		this.A = a;
		this.B = b;
		this.C = c;
		this.D = d;
		this.E = e;
		this.F = f;

	}

	/**
	 * Creates a deep copy of the transformation you want.
	 * 
	 * @param clone
	 *            the transformation you want to copy.
	 */
	public AffineTransformation(AffineTransformation clone) {

		this.A = clone.A;
		this.B = clone.B;
		this.C = clone.C;
		this.D = clone.D;
		this.E = clone.E;
		this.F = clone.F;

	}

	@Override
	public Point transformPoint(Point p) {
		return new Point(A * p.x() + B * p.y() + C, D * p.x() + E * p.y() + F);
	}

	/**
	 * Make a new affineTransformation by composing two using matrix
	 * multiplication.
	 * 
	 * @param that
	 *            Transformation you want to compose with.
	 * @return Result of the composition
	 */
	public AffineTransformation composeWith(AffineTransformation that) {
		// Here is just matrix multiplication
		double newA = this.A * that.A + this.B * that.D;
		double newB = this.A * that.B + this.B * that.E;
		double newC = this.A * that.C + this.B * that.F + this.C;
		double newD = this.D * that.A + this.E * that.D;
		double newE = this.D * that.B + this.E * that.E;
		double newF = this.D * that.C + this.E * that.F + this.F;

		return new AffineTransformation(newA, newB, newC, newD, newE, newF);

	}

	/**
	 * Creates a new Translation.
	 * 
	 * @param dx
	 *            X-Axis shift
	 * @param dy
	 *            Y-Axis shift
	 * @return a new Translation
	 */
	public static AffineTransformation newTranslation(double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 0, 1, dy);
	}

	/**
	 * Creates a new Rotation.
	 * 
	 * @param theta
	 *            angle
	 * @return a new Rotation
	 */
	public static AffineTransformation newRotation(double theta) {
		return new AffineTransformation(Math.cos(theta), -Math.sin(theta), 0,
				Math.sin(theta), Math.cos(theta), 0);
	}

	/**
	 * Creates a new Scaling.
	 * 
	 * @param sx
	 *            scaling parrallel to the X-Axis
	 * @param sy
	 *            scaling parrallel to the Y-Axis
	 * @return a new Scaling
	 */
	public static AffineTransformation newScaling(double sx, double sy) {
		return new AffineTransformation(sx, 0, 0, 0, sy, 0);
	}

	/**
	 * Creates a new Shear parrallel to the X-Axis.
	 * 
	 * @param sx
	 *            factor
	 * @return a new Shear on X-axis
	 */
	public static AffineTransformation newShearX(double sx) {
		return new AffineTransformation(1, sx, 0, 0, 1, 0);
	}

	/**
	 * Creates a new Shear parrallel to the Y-Axis.
	 * 
	 * @param sy
	 *            factor
	 * @return a new Shear on Y-axis
	 */
	public static AffineTransformation newShearY(double sy) {
		return new AffineTransformation(1, 0, 0, sy, 1, 0);
	}

	/**
	 * Returns horizontal component of the transformation, a.k.a. f-factor of the matrix.
	 * @return the horizontal component
	 */
	public double translationX() {
		return C;
	}

	/**
	 * Returns vertical component of the transformation, a.k.a. f-factor of the matrix.
	 * @return the vertical component
	 */
	public double translationY() {
		return F;
	}

	/**
	 * Draws a 2D-representation of the affine transformation using two arrows.
	 * 
	 * @param g0 Graphic context in which you want to draw
	 * @param toDrawing Transformation that transforms a point in the plan into a point on the panel
	 */
	public void draw(Graphics g0, AffineTransformation toDrawing) {

		// Casts the graphic context into Graphics2D
		Graphics2D g2d = (Graphics2D) g0;

		// Horizontal arrow important points after transformation
		Point hBegin = toDrawing.transformPoint(transformPoint(new Point(-1, 0)));
		Point hEnd = toDrawing.transformPoint(transformPoint(new Point(1, 0)));
		Point hRight = toDrawing.transformPoint(transformPoint(new Point(0.9, 0.1)));
		Point hLeft = toDrawing.transformPoint(transformPoint(new Point(0.9, -0.1)));

		// Vertical arrow important points after transformation
		Point vBegin = toDrawing.transformPoint(transformPoint(new Point(0, -1)));
		Point vEnd = toDrawing.transformPoint(transformPoint(new Point(0, 1)));
		Point vRight = toDrawing.transformPoint(transformPoint(new Point(0.1, 0.9)));
		Point vLeft = toDrawing.transformPoint(transformPoint(new Point(-0.1, 0.9)));

		// Draws the horizontal arrow
		g2d.draw(new Line2D.Double(hBegin.x(), hBegin.y(), hEnd.x(), hEnd.y()));
		g2d.draw(new Line2D.Double(hEnd.x(), hEnd.y(), hRight.x(), hRight.y()));
		g2d.draw(new Line2D.Double(hEnd.x(), hEnd.y(), hLeft.x(), hLeft.y()));

		// Draws the vertical arrow
		g2d.draw(new Line2D.Double(vBegin.x(), vBegin.y(), vEnd.x(), vEnd.y()));
		g2d.draw(new Line2D.Double(vEnd.x(), vEnd.y(), vRight.x(), vRight.y()));
		g2d.draw(new Line2D.Double(vEnd.x(), vEnd.y(), vLeft.x(), vLeft.y()));
	}
}
