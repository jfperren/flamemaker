package ch.epfl.flamemaker.gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import ch.epfl.flamemaker.*;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

@SuppressWarnings("serial")
public class AffineTransformationComponent extends JComponent {

	private Rectangle frame;
	private int highlightedTransformationIndex;
	private ObservableFlameBuilder builder;
	
	private AffineTransformation toDrawing;

	public AffineTransformationComponent(ObservableFlameBuilder builder, Rectangle frame){
		
		this.builder = builder;
		this.frame = frame;
		
		addObserver();

		//We put the index at 0.
		highlightedTransformationIndex = 0;
	}
	
	public int getHighlightedTransformationIndex(){
		return highlightedTransformationIndex;
	}
	
	public void setHighlightedTransformationIndex(int index){
		this.repaint();
		highlightedTransformationIndex = index;
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200,100);
	}
	
	@Override
	public void paintComponent(Graphics g0){
		
		//We cast g0 into a Graphics2D
		Graphics2D g2d = (Graphics2D) g0;
		
		//Here we expand the frame of the accumulator to the ratio of the component
		Rectangle expandedFrame = frame.expandToAspectRatio((double)getWidth()/(double)getHeight());
		
		//and define the transformation from basic coordinate axis to the ones used in the drawing part
		toDrawing = AffineTransformation.newScaling((double)getWidth()/(double)expandedFrame.width(), -(double)getWidth()/(double)expandedFrame.width()).composeWith(AffineTransformation.newTranslation(-expandedFrame.left(),-expandedFrame.top()));
				
		//Changes the color to light grey
		g2d.setColor(new java.awt.Color((float)0.9,(float)0.9,(float)0.9));
		
		//Here we draw every secondary line
		for(int x=(int)Math.floor(expandedFrame.left())+1;x<=(int)Math.floor(expandedFrame.right());x++){
			
			if(x!=0){
				Point top = toDrawing.transformPoint(new Point(x, expandedFrame.top()));
				Point bottom = toDrawing.transformPoint(new Point(x, expandedFrame.bottom()));
				g2d.draw(new Line2D.Double(top.x(), top.y(), bottom.x(), bottom.y()));
			}
		}
		
		for(int y=(int)Math.floor(expandedFrame.bottom())+1;y<=Math.floor(expandedFrame.top());y++){
			if(y!=0){
				Point left = toDrawing.transformPoint(new Point(expandedFrame.left(), y));
				Point right = toDrawing.transformPoint(new Point(expandedFrame.right(), y));
				
				g2d.draw(new Line2D.Double(left.x(), left.y(), right.x(), right.y()));
			}
		}
		
		//Changes de color to white
		g2d.setColor(java.awt.Color.WHITE);
		//And draw both axis
		Point top = toDrawing.transformPoint(new Point(0, expandedFrame.top()));
		Point bottom = toDrawing.transformPoint(new Point(0, expandedFrame.bottom()));
		Point left = toDrawing.transformPoint(new Point(expandedFrame.left(), 0));
		Point right = toDrawing.transformPoint(new Point(expandedFrame.right(), 0));
		g2d.draw(new Line2D.Double(left.x(), left.y(), right.x(), right.y()));
		g2d.draw(new Line2D.Double(bottom.x(), bottom.y(), top.x(), top.y()));
		
		//Changes the color to black
		g2d.setColor(java.awt.Color.BLACK);
		
		//Draws all transformations except the highlighted one
		for(int i=0;i< builder.transformationCount();i++){
			if(i!=highlightedTransformationIndex) builder.affineTransformation(i).draw(g2d, toDrawing);
		}
		//Changes the color to red
		g2d.setColor(java.awt.Color.RED);
		
		//Draws the highlighted transformation
		builder.affineTransformation(highlightedTransformationIndex).draw(g2d, toDrawing);
		
		
		
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
