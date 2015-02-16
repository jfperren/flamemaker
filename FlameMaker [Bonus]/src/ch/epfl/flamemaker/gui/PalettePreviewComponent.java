package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.ObservablePalette;

@SuppressWarnings("serial")
public class PalettePreviewComponent extends JComponent{

	private int width;
	private int height;
	private ObservablePalette palette;
	public PalettePreviewComponent(int width, int height, ObservablePalette palette) {
		this.width = width;
		this.height = height;
		this.palette = palette;
		
		palette.addObserver(new ObservablePalette.Observer(){
			@Override
			public void update() {
				repaint();
			}
		});
	}
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(width, height);
	}
	
	@Override
	public void paintComponent(Graphics g0) {

		// We must cast g0 into a Graphics2D to use it correctly
		Graphics2D g2d = (Graphics2D) g0;

		// Then we create the bufferedImage in which we are going to draw the
		// fractal
		BufferedImage buffImg = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < getWidth(); x++) {
			Color colorAtThisX = palette.colorForIndex((double) x/(double)getWidth());
			
			for (int y = 0; y < getHeight(); y++) {
				
				// Colors the point on the BufferedImage
				buffImg.setRGB(x, y, colorAtThisX.asPackedRGB());
				
			}
		}
		//Now that the image is created, we can draw it :
		g2d.drawImage(buffImg, 0, 0, null);
	}


	
}
