package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.epfl.flamemaker.color.Color;

@SuppressWarnings("serial")
public class ColorPreviewComponent extends JComponent{

	private int width;
	private Color color;
	public ColorPreviewComponent(int width, Color color) {
		this.width = width;
		this.color = color;
	}
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(width,width);
	}
	
	public void setColor(Color newColor){
		color = newColor;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g0) {

		// We must cast g0 into a Graphics2D to use it correctly
		Graphics2D g2d = (Graphics2D) g0;

		// Then we create the bufferedImage in which we are going to draw the
		// fractal
		BufferedImage buffImg = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {

				// Colors the point on the BufferedImage
				buffImg.setRGB(x, y, color.asPackedRGB());
				
			}
		}
		//Now that the image is created, we can draw it :
		g2d.drawImage(buffImg, 0, 0, null);
	}


	
}
