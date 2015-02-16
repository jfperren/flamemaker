package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class AffineTransformationTest {
	private static double DELTA = 0.000000001;

	@Test
	public void ComposeWithIdentityTest() {

		AffineTransformation t = AffineTransformation.newRotation(1);
		AffineTransformation t2 = t.composeWith(AffineTransformation.IDENTITY);

		assertEquals(t.translationX(), t2.translationX(), DELTA);
		assertEquals(t.translationY(), t2.translationY(), DELTA);

	}
}
