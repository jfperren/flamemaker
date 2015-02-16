package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Creator of .ppm
 * 
 * @author Alain Milliet & Julien Perrenoud
 * @SCIPER 228408 & 227924
 */

public class FlamePPMMaker {

	public static void main(String[] args) throws FileNotFoundException {
		// Defines all the transformations used to create 'shark-fin'
		AffineTransformation AT1 = new AffineTransformation((-1) * 0.4113504,
				-1 * 0.7124804, -1 * 0.4, 0.7124795, -1 * 0.4113508, 0.8);
		double[] AD1 = new double[6];
		AD1[0] = 1;
		AD1[1] = 0.1;
		AD1[2] = 0;
		AD1[3] = 0;
		AD1[4] = 0;
		AD1[5] = 0;
		FlameTransformation A1 = new FlameTransformation(AT1, AD1);

		AffineTransformation AT2 = new AffineTransformation((-1) * 0.3957339,
				0.0, -1.6, 0.0, (-1) * 0.3957337, 0.2);
		double[] AD2 = new double[6];
		AD2[0] = 0;
		AD2[1] = 0;
		AD2[2] = 0;
		AD2[3] = 0;
		AD2[4] = 0.8;
		AD2[5] = 1;
		FlameTransformation A2 = new FlameTransformation(AT2, AD2);

		AffineTransformation AT3 = new AffineTransformation(0.4810169, 0.0,
				1.0, 0.0, 0.4810169, 0.9);
		double[] AD3 = new double[6];
		AD3[0] = 1;
		AD3[1] = 0;
		AD3[2] = 0;
		AD3[3] = 0;
		AD3[4] = 0;
		AD3[5] = 0;
		FlameTransformation A3 = new FlameTransformation(AT3, AD3);

		// Defines all the transformations used to create 'turbulence'
		AffineTransformation AT4 = new AffineTransformation(0.7124807,
				-0.4113509, -0.3, 0.4113513, 0.7124808, -0.7);
		double[] AD4 = new double[6];
		AD4[0] = 0.5;
		AD4[1] = 0;
		AD4[2] = 0;
		AD4[3] = 0.4;
		AD4[4] = 0;
		AD4[5] = 0;
		FlameTransformation A4 = new FlameTransformation(AT4, AD4);

		AffineTransformation AT5 = new AffineTransformation(0.3731079,
				-0.6462417, 0.4, 0.6462414, 0.3731076, 0.3);
		double[] AD5 = new double[6];
		AD5[0] = 1;
		AD5[1] = 0;
		AD5[2] = 0.1;
		AD5[3] = 0;
		AD5[4] = 0;
		AD5[5] = 0;
		FlameTransformation A5 = new FlameTransformation(AT5, AD5);

		AffineTransformation AT6 = new AffineTransformation(0.0842641,
				-0.314478, -0.1, 0.314478, 0.0842641, 0.3);
		double[] AD6 = new double[6];
		AD6[0] = 1;
		AD6[1] = 0;
		AD6[2] = 0;
		AD6[3] = 0;
		AD6[4] = 0;
		AD6[5] = 0;
		FlameTransformation A6 = new FlameTransformation(AT6, AD6);

		// Creation of transformations list
		ArrayList<FlameTransformation> transformationsA = new ArrayList<FlameTransformation>();
		ArrayList<FlameTransformation> transformationsB = new ArrayList<FlameTransformation>();

		transformationsA.add(A1);
		transformationsA.add(A2);
		transformationsA.add(A3);

		transformationsB.add(A4);
		transformationsB.add(A5);
		transformationsB.add(A6);

		// Defines flames
		Flame Shark = new Flame(transformationsA);
		Flame Turbulence = new Flame(transformationsB);

		// Computes the .ppm images of both fractals
		createPPM("shark-fin", 5, 4, new Point(-0.25, 0), 50, Shark, 500, 400,
				Color.BLACK);
		createPPM("turbulence", 3, 3, new Point(0.1, 0.1), 50, Turbulence, 500,
				500, Color.BLACK);
	}

	/**
	 * Create a new .ppm file of a flame fractal
	 * 
	 * @param name
	 *            Name of the file
	 * @param frameWidth
	 *            Width of the frame
	 * @param frameHeight
	 *            Height of the frame
	 * @param center
	 *            Center of the frame
	 * @param density
	 *            Density (quality of the image)
	 * @param fractale
	 *            Fractal to be drawn
	 * @param width
	 *            Width of the image
	 * @param height
	 *            Height of the image
	 * @param background
	 *            Background color
	 * @throws FileNotFoundException
	 *             if the file is inaccessible
	 */
	public static void createPPM(String name, int frameWidth, int frameHeight,
			Point center, int density, Flame fractale, int width, int height,
			Color background) throws FileNotFoundException {
		// Every 'System.out.println()' are just to show in the console at which
		// step the computing is
		System.out.println("Fractale : " + name);
		// Maximum color component (given in the assignment)
		int max = 100;
		// Computes the flame
		FlameAccumulator accu = fractale.compute(new Rectangle(center,
				frameWidth, frameHeight), width, height, density);
		System.out.println("Computing OK");
		// Creates the stream in which the file will be written
		PrintStream stream = new PrintStream(name + ".ppm");
		// Writes parameters of the .ppm file
		stream.println("P3");
		stream.println(width + " " + height);
		stream.println(max);
		// Writes each pixel
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {

				List<Color> colors = new ArrayList<Color>();
				colors.add(Color.RED);
				colors.add(Color.GREEN);
				colors.add(Color.BLUE);
				// Computes the pixel color
				Color color = accu.color(new InterpolatedPalette(colors),
						background, x, y);
				// Writes it in the file
				stream.print(color.sRGBString(max));

			}
			stream.println("");
		}
		System.out.println("Writing OK");
		// Closing the file stream
		stream.close();
		System.out.println("Over.");
		System.out.println("");
	}
}
