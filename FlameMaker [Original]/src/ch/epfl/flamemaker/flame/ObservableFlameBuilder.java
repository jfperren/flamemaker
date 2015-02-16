package ch.epfl.flamemaker.flame;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class ObservableFlameBuilder {

	private Flame.Builder builder;
	private Set<Observer> observers = new HashSet<Observer>();
	
	public ObservableFlameBuilder(Flame.Builder builder){
		this.builder = builder;
	}
	
	/**
	 * Returns Size of the list of transformations ( = the number of transformations ).
	 * @return the size 
	 */
	public int transformationCount() {
		return builder.transformationCount();
	}
	
	/**
	 * Adds a new transformation to the table of transformations.
	 * 
	 * @param transformation
	 *            Transformation we wants to add
	 */
	public void addTransformation(FlameTransformation transformation) {
		builder.addTransformation(transformation);
		notifyObservers();

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
	public AffineTransformation affineTransformation(int index){
			return builder.affineTransformation(index);
	}

	/**
	 * Changes the affine components of the flame transformation at the given
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
		builder.setAffineTransformation(index,newTransformation);
		notifyObservers();
	}

	/**
	 * Returns weight of the variation.
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
		return builder.variationWeight(index, variation);
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
		builder.setVariationWeight(index, variation, newWeight);
		notifyObservers();
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
		builder.removeTransformation(index);
		notifyObservers();
	}

	/**
	 * Return built flame.
	 * @return the built flame
	 */
	public Flame build() {
		return builder.build();
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
