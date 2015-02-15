package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.epfl.flamemaker.color.ObservablePalette;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.Rectangle;

@SuppressWarnings("serial")
public final class NavigableFlameBuilderPreviewComponent extends AbstractNavigableComponent {

	private ObservableFlameBuilder builder;
	private ch.epfl.flamemaker.color.Color backgroundColor;
	private ObservablePalette palette;
	private int density;

	// Index of the currently selected transformation (Paint in red)
	private int highlightedTransformationIndex;

	public NavigableFlameBuilderPreviewComponent(
			ObservableFlameBuilder builder,
			ch.epfl.flamemaker.color.Color backgroundColor, ObservablePalette palette,
			int density, Rectangle.ObservableBuilder frame) {

		super(frame);
		
		this.builder = builder;
		this.backgroundColor = backgroundColor;
		this.palette = palette;
		this.density = density;

		builder.addObserver(new ObservableFlameBuilder.Observer() {
			@Override
			public void update() {
				repaint();
			}
		});
		// We put the index at 0.
		highlightedTransformationIndex = 0;

		frame.addObserver(new Rectangle.ObservableBuilder.Observer() {
			@Override
			public void update() {
				repaint();
			}
		});
		
		palette.addObserver(new ObservablePalette.Observer(){
			@Override
			public void update(){
				repaint();
			}
		});
	}

	/**
	 * Index of the highlighted transformation (paint in red over all).
	 * 
	 * @return Index of the highlighted transformation.
	 */
	public int getHighlightedTransformationIndex() {
		return highlightedTransformationIndex;
	}

	/**
	 * Set another Transformation as Highlighted
	 * 
	 * @param index
	 *            Index of the chosen Transformation
	 */
	public void setHighlightedTransformationIndex(int index) {
		highlightedTransformationIndex = index;
		this.repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 100);
	}
	
	@Override
	public void paintComponent(Graphics g) {

		// We must cast g0 into a Graphics2D to use it correctly
		Graphics2D g2d = (Graphics2D) g;

		// Here we expand the frame of the accumulator to the ratio of the
		// component
		Rectangle expandedFrame = expandedFrame();

		// And we use this new frame to compute the fractal
		FlameAccumulator accu = builder.build().compute(expandedFrame,
				getWidth(), getHeight(), density);

		// Then we create the bufferedImage in which we are going to draw the
		// fractal
		BufferedImage buffImg = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {

				// Color of the point (x,y)
				ch.epfl.flamemaker.color.Color color = accu.color(palette, backgroundColor, x, y);

				// Colors the point on the BufferedImage
				buffImg.setRGB(x, getHeight() - y - 1, color.asPackedSRGB());
			}
		}

		// Now that the image is created, we can draw it :
		g2d.drawImage(buffImg, 0, 0, null);
		
		if(isFrameVisible){
			
			paintFrame(g2d);
		}
	}
	
	public void setDensity(int newDensity){
		density = newDensity;
		repaint();
	}
}
