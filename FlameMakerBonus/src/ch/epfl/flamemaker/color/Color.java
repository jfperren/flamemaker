package ch.epfl.flamemaker.color;

/**
 * Simulates a color with a triplet RGB
 * 
 * @author Alain Milliet & Julien Perrenoud
 * SCIPER : 228408 & 227924
 * 
 */
public final class Color {

	// Predefined colors
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(1, 1, 1);
	public static final Color RED = new Color(1, 0, 0);
	public static final Color GREEN = new Color(0, 1, 0);
	public static final Color BLUE = new Color(0, 0, 1);

	// Color components
	private final double red, green, blue;
	public enum Component{
		RED("Red"),GREEN("Green"), BLUE("Blue");
		
		private String name;
		
		private Component(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	};

	/**
	 * Builder of a color.
	 * 
	 * @param r
	 *            red component
	 * @param g
	 *            green component
	 * @param b
	 *            blue component
	 * @throws IllegalArgumentException
	 *             if any component is not between 0 and 1 (included)
	 */
	public Color(double r, double g, double b) throws IllegalArgumentException {
		if ((r < 0) || (r > 1) || (g < 0) || (g > 1) || (b < 0) || (b > 1)) {
			throw new IllegalArgumentException(
					"Components must be between 0 and 1 (included)");
		}
		this.red = r;
		this.green = g;
		this.blue = b;
	}

	/**
	 * Mixes two differents color into one.
	 * 
	 * @param that
	 *            Color you want to mix with the instance
	 * @param proportion
	 *            Proportion of the color argument
	 * @return The color obtained by taking proportion of the argument and
	 *         (1-proportion) of the instance
	 * @throws IllegalArgumentException
	 *             if proportion is not between 0 and 1 (included)
	 */
	public Color mixWith(Color that, double proportion)
			throws IllegalArgumentException {
		// Check the proportion
		if (proportion < 0 || proportion > 1)
			throw new IllegalArgumentException("Non-compliant proportion");

		// Creates a new color with the right amount of both old colors
		Color newColor = new Color(that.red() * proportion + red
				* (1 - proportion), that.green() * proportion + green
				* (1 - proportion), that.blue() * proportion + blue
				* (1 - proportion));
		return newColor;
	}

	/**
	 * This is a method we did to simplify the writing into the .ppm file.
	 * 
	 * @param max
	 *            Maximum value of the color
	 * @return a String containing every component sRGBEncoded with a space
	 *         between them, used to write the pixel in .ppm format
	 */
	public String sRGBString(int max) {
		return "" + sRGBEncode(red, max) + " " + sRGBEncode(green, max) + " "
				+ sRGBEncode(blue, max) + " ";
	}

	/**
	 * Encodes a component in a sRGB format ( gamma encoding ).
	 * 
	 * @param v
	 *            Value to be encoded
	 * @param max
	 *            Maximum value of the interval
	 * @return Value gamma-encoded and transformed as an integer between 0 and
	 *         max
	 * @throws IllegalArgumentException
	 *             if v is not between 0 and 1 (included)
	 */
	public static int sRGBEncode(double v, int max)
			throws IllegalArgumentException {
		// Check the value
		if (v > 1 || v < 0)
			throw new IllegalArgumentException("Non-compliant value");

		double gammaValue;
		// Formula given in the assignment
		if (v <= 0.0031308) {
			gammaValue = (v * 12.92);
		} else {
			gammaValue = (Math.pow(v, 1 / 2.4) * 1.055 - 0.055);
		}
		// Transform the gammaValue into an integer in the interval [0,max]
		int gammaColor = (int) Math.floor(gammaValue * max);

		return gammaColor;
	}

	/**
	 * Returns an unique integer coding the color.
	 * @return the integer
	 */
	public int asPackedRGB() {

		/*
		 * Here we convert all the component from [0,1] to [0, 255]. We had two
		 * different choices, either take (int)Math.floor(component*256) or
		 * (int)Math.round(component*255) (as we did here). We chose to take the
		 * second option because you said to take "l'arrondi" in the assignment,
		 * but the option with Math.floor seemed a better choice for us, as this
		 * way 0 and 255 get as many chances as every other values.
		 */
		int r = (int) Math.round(red * 255);
		int g = (int) Math.round(green * 255);
		int b = (int) Math.round(blue * 255);

		/*
		 * Here we used the ShiftExpression << to make a 8-digit left shift and
		 * that way every component is on a different byte.
		 */
		int asPackedRGB = r;
		asPackedRGB = asPackedRGB << 8;
		asPackedRGB += g;
		asPackedRGB = asPackedRGB << 8;
		asPackedRGB += b;

		return asPackedRGB;
	}
	
	public int asPackedSRGB(){
		
		int asPackedSRGB = sRGBEncode(red, 255);
		asPackedSRGB = asPackedSRGB << 8;
		asPackedSRGB += sRGBEncode(green, 255);
		asPackedSRGB = asPackedSRGB << 8;
		asPackedSRGB += sRGBEncode(blue, 255);
		
		return asPackedSRGB;
	}

	/**
	 * Returns red component of the color.
	 * @return the red component
	 */
	public double red() {
		return red;
	}

	/**
	 * Returns green component of the color.
	 * @return the green component
	 */
	public double green() {
		return green;
	}

	/**
	 * Returns blue component of the color.
	 * @return the blue component
	 */
	public double blue() {
		return blue;
	}
}
