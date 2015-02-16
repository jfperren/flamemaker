package ch.epfl.flamemaker.color;

import java.util.*;

/**
 * Simulates a color palette with n colors randomly chosen and equally spaced.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public class RandomPalette implements Palette {

	private InterpolatedPalette palette;

	/**
	 * Builder of a random palette.
	 * 
	 * @param n
	 *            The number of random colors on palette.
	 * @throws IllegalArgumentException
	 *             if n < 2
	 */
	public RandomPalette(int n) {
		// Check the number of colors
		if (n < 2)
			throw new IllegalArgumentException("2 colors min.");

		List<Color> colors = new ArrayList<Color>();
		// Add n random colors to the list
		for (int i = 0; i < n; i++) {
			colors.add(new Color(Math.random(), Math.random(), Math.random()));
		}
		palette = new InterpolatedPalette(colors);
	}

	@Override
	public Color colorForIndex(double index) throws IllegalArgumentException {

		// We use the method of InterpolatedPalette
		return palette.colorForIndex(index);
	}
}