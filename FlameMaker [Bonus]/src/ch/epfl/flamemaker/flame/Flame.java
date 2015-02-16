package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;
import java.util.*;

/**
 * Models a 2-d flame fractals
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public final class Flame {
	// List of Flame Transformations
	private final List<FlameTransformation> transformations;
	// Array of color coefficients for Transformations

	/**
	 * Builder of flame.
	 * 
	 * @param transformations
	 *            list of transformations
	 */
	public Flame(List<FlameTransformation> transformations) {

		this.transformations = new ArrayList<FlameTransformation>();
		// We make a deep copy of the Transformation lists using an iterator.
		Iterator<FlameTransformation> i = transformations.iterator();
		while (i.hasNext()) {
			this.transformations.add(i.next());
		}
	}

	/**
	 * Computes the coefficient (for the color sum) of any transformation.
	 * 
	 * @param index
	 *            The index of the transformation
	 * @return The color coefficient of the transformation
	 */
	public static double getCoeff(int index) {

		double coeff;
		double denominator = 1;
		double numerator = 1;
		
		// We need to separates the cases were index is 0 or 1 because it
		// doesn't fit in the formula below.
		if (index == 0 || index == 1) {
			coeff = index;
		} else {
			/*
			 * Here, we compute the nominator and the denominator of the
			 * fraction Separately, because it was easier for us.
			 */
			denominator = Math.pow(2,
					(int) (Math.floor(Math.log(index - 1) / Math.log(2))) + 1);
			numerator = (2 * (index - 1) - (denominator - 1));
			coeff = numerator / denominator;
		}
		return coeff;
	}

	/**
	 * Computes with the chaos algorithms the fractal in the limited plan
	 * "frame", keep and return the result in a new accumulator width x height
	 * chosen. The number of iterations to do is computed with the density.
	 * 
	 * @param frame
	 *            Delimited plan
	 * @param width
	 *            Width of the new accumulator
	 * @param height
	 *            Height of the new accumulator
	 * @param density
	 *            Density used to compute the number of iterations to do
	 * @return A new accumulator
	 */
	public FlameAccumulator compute(Rectangle frame, int width, int height,
			int density) {
		// Array of color coefficients for Transformations
		double[] coeffs = new double[transformations.size()];
		// We fill the array with the coefficients
		for(int j=0;j<transformations.size();j++){
			coeffs[j] = getCoeff(j);
		}
				
		if(transformations.size()<1) throw new IllegalArgumentException("List size must be 1 or greater");
		// We first compute the number of iterations needed with the formula
		// below
		int m = density * width * height;

		// Then we take the step 0, which is the origin point with coefficient
		// 0.
		Point p = Point.ORIGIN;
		double colorCoeff = 0;

		// And we use the random given in the assignment.
		Random rand = new Random(2013);

		// We begin here the building of the accumulator, using the builder
		FlameAccumulator.Builder Flamepoints = new FlameAccumulator.Builder(
				frame, width, height);

		// Here are steps 0 to 19 of the chaos algorithm
		for (int count = 0; count < 20; count++) {
			// We take a random transformation in the list,
			int i = rand.nextInt(transformations.size());
			// then apply it to the point,
			p = transformations.get(i).transformPoint(p);
			// and finally we calculate the color coefficient
			colorCoeff = (colorCoeff + coeffs[i]) / 2;
		}
		// And here steps 20 to m-1
		for (int count = 0; count < m; count++) {
			// Same as before here,
			int i = rand.nextInt(transformations.size());
			p = transformations.get(i).transformPoint(p);
			colorCoeff = (colorCoeff + coeffs[i]) / 2;

			// But this time we need to hit the point !
			Flamepoints.hit(p, colorCoeff);
		}

		return Flamepoints.build();
	}

	public static class Builder {

		// List of Flame Transformations
		private final List<FlameTransformation> flameTransformations;

		/**
		 * Builder
		 * 
		 * @param flame
		 *            Flame you want to be built step-by-step.
		 */
		public Builder(Flame flame) {
			
			this.flameTransformations = new ArrayList<FlameTransformation>();
			
			// We need here a deep copy
			for(FlameTransformation f : flame.transformations){
				this.flameTransformations.add(f);
			}
		}

		/**
		 * Returns size of the list of transformations ( = the number of transformations ).
		 * @return the size of the list
		 */
		public int transformationCount() {
			return flameTransformations.size();
		}

		/**
		 * Adds a new transformation to the table of transformations.
		 * 
		 * @param transformation
		 *            Transformation we wants to add
		 */
		public void addTransformation(FlameTransformation transformation) {
			flameTransformations.add(transformation);

		}

		/**
		 * Returns the affine transformation that take place at a certain index
		 * in the table of affineTranformations.
		 * 
		 * @param index
		 *            Certain index where we look for the affine transformation.
		 * @return Affine transformation at the index
		 * @throws IndexOutOfBoundsException
		 *             if index isn't in the table
		 */
		public AffineTransformation affineTransformation(int index)
				throws IndexOutOfBoundsException {
			// Check index
			checkIndex(index);

			//Here we must create a builder in order to extract the affineTransformation out of a flameTr.
			return new FlameTransformation.Builder(flameTransformations.get(index)).getAffineTransformation();
		}

		/**
		 * change the affine components of the flame transformation at the given
		 * index.
		 * 
		 * @param index
		 *            Index of the flame transformation
		 * @param newTransformation
		 *            New component for the affine part of the flame
		 *            transformation
		 * @throws IndexOutOfBoundsException
		 *             if index isn't in the transformations list
		 */
		public void setAffineTransformation(int index,
				AffineTransformation newTransformation)
						throws IndexOutOfBoundsException {
			// Check index
			checkIndex(index);

			FlameTransformation.Builder flameTransformationBuilder = new FlameTransformation.Builder(
					flameTransformations.get(index));
			flameTransformationBuilder
			.setAffineTransformation(newTransformation);
			flameTransformations
			.set(index, flameTransformationBuilder.build());
		}

		/**
		 * Returns weight of the variation.
		 * 
		 * @param index
		 *            Index where the variation is
		 * @param variation
		 *            Variation we wants the weight
		 * @return the weight
		 * @throws IndexOutOfBoundsException
		 *             if index isn't in the transformations list
		 */
		public double variationWeight(int index, Variation variation)
				throws IndexOutOfBoundsException {
			// Check index
			checkIndex(index);

			FlameTransformation.Builder flameTransformationBuilder = new FlameTransformation.Builder(
					flameTransformations.get(index));
			double weight = flameTransformationBuilder
					.getVariationWeight(variation);
			return weight;
		}

		/**
		 * Changes the weight of the variation at a certain index.
		 * 
		 * @param index
		 *            Index where the variation, we want to change the weight,
		 *            is
		 * @param variation
		 *            Variation we want to change the weight
		 * @param newWeight
		 *            New weight we want to give to the variation
		 * @throws IndexOutOfBoundsException
		 *             if the index isn't in the transformations list
		 */
		public void setVariationWeight(int index, Variation variation,
				double newWeight) throws IndexOutOfBoundsException {
			// Check index
			checkIndex(index);

			FlameTransformation.Builder flameTransformationBuilder = new FlameTransformation.Builder(
					flameTransformations.get(index));
			flameTransformationBuilder.setVariationWeight(variation, newWeight);
			flameTransformations
			.set(index, flameTransformationBuilder.build());
		}

		/**
		 * Removes the transformation at the index given.
		 * 
		 * @param index
		 *            Index of the transformation we want to remove
		 * @throws IndexOutOfBoundsException
		 *             If the index isn't in the transformations list
		 */
		public void removeTransformation(int index)
				throws IndexOutOfBoundsException {
			// Check index
			checkIndex(index);

			flameTransformations.remove(index);
		}

		private void checkIndex(int index){
			if (index < 0 || index >= transformationCount()) throw new IndexOutOfBoundsException("Not in the list");
		}
		/**
		 * Returns built flame.
		 * @return the built flame
		 */
		public Flame build() {
			return new Flame(flameTransformations);
		}
	}
}
