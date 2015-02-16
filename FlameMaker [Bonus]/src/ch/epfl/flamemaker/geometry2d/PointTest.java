package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {
	private static double DELTA = 0.000000001;

	@Test
	public void testTheta() {
		Point p = new Point(0, 0);
		assertEquals(p.theta(), 0, DELTA);
	}

	@Test
	public void testR() {
		Point p = new Point(0, 0);
		assertEquals(p.r(), 0, DELTA);
	}
}
