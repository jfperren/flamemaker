package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * Models flame transformations
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */

public class FlameTransformation implements Transformation {

	private AffineTransformation affineTransformation;
	private double[] variationWeight;

	/**
	 * Builder 1 : Builds a Flame transformation composed with the affine
	 * transformation and the given weights.
	 * 
	 * @param affineTransformation
	 *            Affine transformation (used to build the Flame transformation)
	 * @param variationWeight
	 *            Table of weights
	 * @throws IllegalArgumentException
	 *             if the length of the list of weight is not equals to 6
	 */
	public FlameTransformation(AffineTransformation affineTransformation,
			double[] variationWeight) throws IllegalArgumentException {
		// Check list length
		if (variationWeight.length != Variation.ALL_VARIATIONS.size())
			throw new IllegalArgumentException("List has not the appropriate length");
		// Deep copy of the Affine transformation
		this.affineTransformation = new AffineTransformation(
				affineTransformation);
		// Deep copy of the variation list
		this.variationWeight = new double[Variation.ALL_VARIATIONS.size()];
		for (int i = 0; i < variationWeight.length; i++) {
			this.variationWeight[i] = variationWeight[i];
		}
	}

	@Override
	public Point transformPoint(Point p) {
		// Coordinates of the point
		double x = 0;
		double y = 0;
		
		// Applies each variation to the point
		for (int i = 0; i < Variation.ALL_VARIATIONS.size(); i++) {
			if(variationWeight[i]!=0){
			x = x
					+ variationWeight[i]
							* Variation.ALL_VARIATIONS
							.get(i)
							.transformPoint(
									affineTransformation.transformPoint(p)).x();
			y = y
					+ variationWeight[i]
							* Variation.ALL_VARIATIONS
							.get(i)
							.transformPoint(
									affineTransformation.transformPoint(p)).y();
			}
		}

		return new Point(x, y);
	}

	public static class Builder {
		//AffineTransformation and weight table
		private AffineTransformation affineTransformation;
		private double[] variationWeight;
		/**
		 * Builder
		 * 
		 * @param flameTransformation
		 *            Flame transformation to be built
		 */
		public Builder(FlameTransformation flameTransformation) {
			//Copies the two fields
			this.affineTransformation = flameTransformation.affineTransformation;
			this.variationWeight = flameTransformation.variationWeight.clone();
			
		}

		/**
		 * Sets a new affine transformation.
		 * 
		 * @param affineTransformation
		 *            Affine transformation to be set
		 */
		public void setAffineTransformation(
				AffineTransformation affineTransformation) {
			this.affineTransformation = affineTransformation;
		}

		/**
		 * Returns affine transformation.
		 * @return the affine transformation
		 */
		public AffineTransformation getAffineTransformation() {
			return this.affineTransformation;
		}

		/**
		 * Sets a new variation list.
		 * 
		 * @param variationWeight
		 *            List of variation weights
		 */
		public void setVariationWeight(double[] variationWeight) {
			this.variationWeight = variationWeight;
		}

		/**
		 * Sets a new variation.
		 * 
		 * @param variation
		 *            Variation that will change
		 * @param weight
		 *            New value of the variation
		 */
		public void setVariationWeight(Variation variation, double weight) {
			this.variationWeight[variation.index()] = weight;
		}

		/**
		 * Returns variation weight.
		 * @param variation
		 *            Variation that will be get
		 * @return the variation weight
		 */
		public double getVariationWeight(Variation variation) {
			return this.variationWeight[variation.index()];
		}

		/**
		 * Builds a flame transformation.
		 * 
		 * @return Flame transformation
		 */
		public FlameTransformation build() {
			return new FlameTransformation(affineTransformation, variationWeight);
		}

	}

}
