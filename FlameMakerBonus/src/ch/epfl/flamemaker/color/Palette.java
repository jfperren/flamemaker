package ch.epfl.flamemaker.color;

/**
 * Simulates a color palette on which you can get intermediate tones of colors.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */
public interface Palette {

	/**
	 * Returns color associated to the index.
	 * @param index
	 *            The index (0->1) of the Color on the palette.
	 * @return The color
	 * @throws IllegalArgumentException
	 *             is index > 1 or index < 0
	 */
	public Color colorForIndex(double index) throws IllegalArgumentException;
}
