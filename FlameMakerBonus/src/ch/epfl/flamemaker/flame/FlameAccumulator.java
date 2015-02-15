package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.color.*;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Models flame fractals
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */

public final class FlameAccumulator {

	// Tables stocking values for each pixel
	private int[][] hitCount;
	private double[][] colorIndexSum;

	// Size of the accumulator
	private int width;
	private int height;

	// Number of hits of the pixel that is the most hits
	private int max;

	/**
	 * Builder of a flame accumulator.
	 * 
	 * @param hitCount
	 *            Table stocking the number of hits
	 * @param colorIndexSum
	 *            Table stocking the sum of all color indexes
	 */
	private FlameAccumulator(int[][] hitCount, double[][] colorIndexSum) {
		// At first there is no hit so max equals 0
		max = 0;

		// Declaration of both tables
		width = hitCount.length;
		height = hitCount[0].length;
		this.hitCount = new int[width][height];
		this.colorIndexSum = new double[width][height];

		// Deep copy of both tables
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				this.colorIndexSum[i][j] = colorIndexSum[i][j];
				this.hitCount[i][j] = hitCount[i][j];

				// Check if the current pixel is more hit than max
				if (hitCount[i][j] > max) {
					max = hitCount[i][j];
				}
			}
		}
	}

	/**
	 * Returns width of the accumulator.
	 * @return the width
	 */
	public int width() {
		return width;
	}

	/**
	 * Returns height of the accumulator.
	 * @return the height
	 */
	public int height() {
		return height;
	}

	/**
	 * Computes intensity of the case (x,y) of the accumulator.
	 * 
	 * @param x
	 *            Coordinate of the X-axis
	 * @param y
	 *            Coordinate of the Y-axis
	 * @return Intensity of the case (x,y) of the accumulator
	 * @throws IndexOufOfBoundsException
	 *             if the table doesn't contain the case
	 */
	public double intensity(int x, int y) throws IndexOutOfBoundsException {
		// Check coordinates x and y
		if (x > width - 1 || y > height - 1 || x < 0 || y < 0)
			throw new IndexOutOfBoundsException("Not in the list");

		// Formula given for intensity
		return Math.log(hitCount[x][y] + 1) / Math.log(max + 1);

	}

	/**
	 * Computes the color of the case depending on the number of hits.
	 * 
	 * @param palette
	 *            Color palette
	 * @param background
	 *            Background color
	 * @param x
	 *            Coordinate of the X-axis
	 * @param y
	 *            Coordinate of the Y-axis
	 * @return the color of the case (x,y) of the accumulator
	 * @throws IndexOutOfBoundsException
	 *             if the table doesn't contain the case
	 */
	public Color color(Palette palette, Color background, int x, int y)
			throws IndexOutOfBoundsException {
		// Check coordinates x and y
		if (x > width - 1 || y > height - 1 || x < 0 || y < 0)
			throw new IndexOutOfBoundsException("Not in the list");

		// Get the value of hits and colorIndex
		int hits = hitCount[x][y];
		double colorSum = colorIndexSum[x][y];
		Color mix;

		if (hits == 0) {
			// If there is no hit, pixel gets background color
			mix = background;
		} else {
			// Average colorIndex
			double averageColorIndex = colorSum / hits;

			// Gets the right color according to the palette
			Color onPalette = new Color(palette
					.colorForIndex(averageColorIndex).red(), palette
					.colorForIndex(averageColorIndex).green(), palette
					.colorForIndex(averageColorIndex).blue());

			// Mixes fractal color and background color
			mix = background.mixWith(onPalette, intensity(x, y));
		}

		return mix;
	}

	public static class Builder {

		// Frame where we look for points
		private Rectangle frame;

		// Tables stocking values for each pixel
		private int[][] hitCount;
		private double[][] colorIndexSum;

		AffineTransformation toAccumulator;

		/**
		 * Constructor of Builder.
		 * 
		 * @param frame
		 *            Frame of the builder we want to construct
		 * @param width
		 *            Width of the builder we want to construct
		 * @param height
		 *            Height of the builder we want to construct
		 * @throws IllegalArgumentException
		 *             if width or height are negative or zero
		 */
		public Builder(Rectangle frame, int width, int height) {
			// Check width and height
			if (width <= 0 || height <= 0)
				throw new IllegalArgumentException(
						"Strictly positive values only");

			// Deep copy of frame
			this.frame = frame;

			// Deep copy of both tables
			hitCount = new int[width][height];
			colorIndexSum = new double[width][height];

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					hitCount[i][j] = 0;
					colorIndexSum[i][j] = 0;
				}
			}
			/*
			 * These 3 transformations are made to assign each pixel of the
			 * accumulator to a single area of the frame
			 * 
			 * A : Translation that center the frame on the origin. 
			 * B : Scale the frame to the size of the accumulator 
			 * C : Translation that put the bottom-left corner on the case (0,0)
			 */
			AffineTransformation toAccumulatorA = AffineTransformation.newTranslation(-1
					* frame.center().x(), -1 * frame.center().y());
			AffineTransformation toAccumulatorB = AffineTransformation.newScaling(
					width / frame.width(), height / frame.height());
			AffineTransformation toAccumulatorC = AffineTransformation.newTranslation(width / 2.0,
					height / 2.0);

			// Composed transformation to reduce the code below
			toAccumulator = toAccumulatorC.composeWith(toAccumulatorB)
					.composeWith(toAccumulatorA);
		}

		/**
		 * Increments the case to which the p point belongs but doesn't do
		 * anything if the point doesn't take place in the frame.
		 * 
		 * @param p
		 *            Point of the frame
		 * @param coeff
		 *            Current color coefficient
		 */
		public void hit(Point p, double coeff) {
			// Check if the point is in the frame
			if (frame.contains(p)) {
				Point pAccu = toAccumulator.transformPoint(p);
				// Add values in each table
				hitCount[(int) Math.floor(pAccu.x())][(int) Math.floor(pAccu
						.y())]++;
				colorIndexSum[(int) Math.floor(pAccu.x())][(int) Math
				                                           .floor(pAccu.y())] += coeff;
			}

		}

		/**
		 * Returns accumulator that contains the points collected.
		 * @return the accumulator
		 */
		public FlameAccumulator build() {
			return new FlameAccumulator(hitCount, colorIndexSum);
		}

	}

}
