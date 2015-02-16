package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.epfl.flamemaker.flame.*;
import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.gui.AffineTransformationComponent.FlameObserver;
import ch.epfl.flamemaker.color.*;

@SuppressWarnings("serial")
public class FlameBuilderPreviewComponent extends JComponent {

	ObservableFlameBuilder builder;
	Color backgroundColor;
	Palette palette;
	int density;
	
	Rectangle frame;
	
	public FlameBuilderPreviewComponent(ObservableFlameBuilder builder, Color backgroundColor, Palette palette, int density, Rectangle frame){
		
		this.builder = builder;
		this.backgroundColor = backgroundColor;
		this.palette = palette;
		this.density = density;
		this.frame = frame;
		
		addObserver();
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200,100);
	}
	
	@Override
	public void paintComponent(Graphics g0){
		
		//We must cast g0 into a Graphics2D to use it correctly
				Graphics2D g2d = (Graphics2D)g0;
				
		//Here we expand the frame of the accumulator to the ratio of the component
		Rectangle expandedFrame = frame.expandToAspectRatio((double)getWidth()/(double)getHeight());
		
		//And we use this new frame to compute the fractal
		FlameAccumulator accu = builder.build().compute(expandedFrame, getWidth(), getHeight(), density);
		
		//Then we create the bufferedImage in which we are going to draw the fractal 
		BufferedImage buffImg = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				
				//Color of the point (x,y)
				Color color = accu.color(palette, backgroundColor , x, y);
				
				//Colors the point on the BufferedImage
				buffImg.setRGB(x, getHeight()-y-1, color.asPackedSRGB());
			}
		}
		
		//Now that the image is created, we can draw it :
		g2d.drawImage(buffImg, 0, 0, null);
	}
	
	public void addObserver(){
		builder.addObserver(new FlameObserver());
	}
	
	public class FlameObserver implements ObservableFlameBuilder.Observer{

		@Override
		public void update() {
			repaint();
		}
		
	}
}
