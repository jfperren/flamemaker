package ch.epfl.flamemaker.color;

import java.util.*;

/**
 * Simulates a color palette on which you can choose any color you want and it
 * spaces them equally
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public class InterpolatedPalette implements Palette {

	private List<Color> colors;

	/**
	 * Builder of an interpolated palette.
	 * 
	 * @param colors
	 *            The list of colors you want to put on the palette.
	 * @throws IllegalArgumentException
	 *             if colors contains one or less items.
	 */
	public InterpolatedPalette(List<Color> colors) {

		// Check the size of the color list.
		if (colors.size() < 2)
			throw new IllegalArgumentException("List too small (2 colors min.)");

		// Makes a deep copy of the color list.
		this.colors = new ArrayList<Color>();
		for (Color color : colors) {
			this.colors.add(color);
		}
	}
	public InterpolatedPalette() {
		// Makes a deep copy of the color list.
		colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
	}

	@Override
	public Color colorForIndex(double index) throws IllegalArgumentException {

		// Check the index
		if (index > 1 || index < 0)
			throw new IllegalArgumentException(
					"Index must be between 0 and 1 (included)");

		// The color that will be returned.
		Color mix;

		// Here, if index = 0 or 1 the color doesn't need to be mixed.
		if (index == 1) {
			mix = colors.get(colors.size() - 1);
		} else if (index == 0) {
			mix = colors.get(0);
		} else {

			// Compute the index of the color under the index.
			int indexUnder = (int) Math.floor(index * (colors.size() - 1));
			// Get the colors under and over the index.
			Color under = colors.get(indexUnder);
			Color over = colors.get(indexUnder + 1);

			/*
			 * Here, by scaling indexOnPalette to the size of the list and then
			 * subtracting the index of the color under the index, we
			 * automatically get the distance between the index and the color
			 * under in the range [0, 1], that we can later use to get the right
			 * mix.
			 */
			double indexOnPalette = index * (colors.size() - 1);
			double proportion = indexOnPalette - indexUnder;

			// Now we can mix the two colors with the right proportion.
			mix = under.mixWith(over, proportion);

		}

		return mix;
	}
}
