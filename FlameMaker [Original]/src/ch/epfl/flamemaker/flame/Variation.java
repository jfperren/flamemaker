package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;
import java.util.*;

/**
 * Models variations.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */

public abstract class Variation implements Transformation {
	// Defines name and index of the variation
	private final String name;
	private final int index;

	/**
	 * Builder of a variation.
	 * 
	 * @param index
	 *            Index in the list
	 * @param name
	 *            Name of the variation
	 */
	private Variation(int index, String name) {
		this.name = name;
		this.index = index;
	}

	/**
	 * Returns name of the variation.
	 * @return the name 
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns index of the variation.
	 * @return the index
	 */
	public int index() {
		return index;
	}

	@Override
	abstract public Point transformPoint(Point p);

	/**
	 * List of all variations
	 */
	public final static List<Variation> ALL_VARIATIONS = Arrays.asList(
			new Variation(0, "Linear") {
				public Point transformPoint(Point p) {
					return p;
				}
			}, new Variation(1, "Sinusoidal") {
				public Point transformPoint(Point p) {
					return new Point(Math.sin(p.x()), Math.sin(p.y()));
				}
			}, new Variation(2, "Spherical") {
				public Point transformPoint(Point p) {
					return new Point(p.x() / p.rPow2(), p.y() / p.rPow2());
				}
			}, new Variation(3, "Swirl") {
				public Point transformPoint(Point p) {
					return new Point(p.x() * Math.sin(p.rPow2()) - p.y()
							* Math.cos(p.rPow2()), p.x() * Math.cos(p.rPow2()) + p.y()
							* Math.sin(p.rPow2()));
				}
			}, new Variation(4, "Horsehoe") {
				public Point transformPoint(Point p) {
					return new Point((p.x() - p.y()) * (p.x() + p.y()) / p.r(), 2
							* p.x() * p.y() / p.r());
				}
			}, new Variation(5, "Bubble") {
				public Point transformPoint(Point p) {
					return new Point(4 * p.x() / (p.rPow2() + 4), 4 * p.y() / (p.rPow2() + 4));
				}
			});
}
