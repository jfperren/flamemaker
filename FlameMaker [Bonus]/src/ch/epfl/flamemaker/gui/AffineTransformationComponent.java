package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * JComponent that shows every transformation of the builder
 * as a pair of arrows on a scaled 2-d plan.
 * 
 * @author Alain Milliet & Julien Perrenoud
 * SCIPER : 228408 & 227924 
 */
@SuppressWarnings("serial")
public class AffineTransformationComponent extends JComponent {

	//Index of the currently selected transformation (Paint in red)
	private int highlightedTransformationIndex;
	//Transformation that transforms a point in the plan to a point on the drawing
	private AffineTransformation toDrawing;
	//Builder and frame, needed to draw the component.
	private ObservableFlameBuilder builder;
	private Rectangle frame;
	
	public AffineTransformationComponent(ObservableFlameBuilder builder, Rectangle frame){
		
		this.builder = builder;
		this.frame = frame;

		builder.addObserver(new ObservableFlameBuilder.Observer(){
			@Override
			public void update(){
				repaint();
			}
		});
		
		//We put the index at 0.
		highlightedTransformationIndex = 0;
		
	}
	
	/**
	 * Index of the highlighted transformation (paint in red over all).
	 * @return Index of the highlighted transformation.
	 */
	public int getHighlightedTransformationIndex(){
		return highlightedTransformationIndex;
	}
	
	
	/**
	 * Set another Transformation as Highlighted
	 * @param index Index of the chosen Transformation
	 */
	public void setHighlightedTransformationIndex(int index){
		highlightedTransformationIndex = index;
		this.repaint();
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200,100);
	}
	
	@Override
	public void paintComponent(Graphics g0){
		
		// We cast g0 into a Graphics2D
		Graphics2D g2d = (Graphics2D) g0;
		
		// Here we expand the frame of the accumulator to the ratio of the component
		Rectangle expandedFrame = expandedFrame();
		Rectangle panel = new Rectangle(new Point(this.getWidth()/2, this.getHeight()/2),this.getWidth(), this.getHeight());
		// We define here the AffineTransformation corresponding to the change from frame to panel.
		// (Due to the function composeWith(), we must define them in reverse order).
		toDrawing = AffineTransformation.scaleToRectangle(expandedFrame, panel);

		// Changes the color to light grey
		g2d.setColor(new java.awt.Color((float)0.9,(float)0.9,(float)0.9));
		
		// Drawing of every vertical secondary line :
		for(int x=(int)Math.floor(expandedFrame.left())+1;x<=(int)Math.floor(expandedFrame.right());x++){
			
			if(x!=0){
				Point top = toDrawing.transformPoint(new Point(x, expandedFrame.top()));
				Point bottom = toDrawing.transformPoint(new Point(x, expandedFrame.bottom()));
				
				g2d.draw(new Line2D.Double(top.x(), top.y(), bottom.x(), bottom.y()));
			}
		}
		
		// Drawing of every horizontal secondary line :
		for(int y=(int)Math.floor(expandedFrame.bottom())+1;y<=Math.floor(expandedFrame.top());y++){
			if(y!=0){
				Point left = toDrawing.transformPoint(new Point(expandedFrame.left(), y));
				Point right = toDrawing.transformPoint(new Point(expandedFrame.right(), y));
				
				g2d.draw(new Line2D.Double(left.x(), left.y(), right.x(), right.y()));
			}
		}
		
		//Changes the color to white
		g2d.setColor(java.awt.Color.WHITE);
		
		// Drawing of both primary axis (vertical then horizontal)
		Point top = toDrawing.transformPoint(new Point(0, expandedFrame.top()));
		Point bottom = toDrawing.transformPoint(new Point(0, expandedFrame.bottom()));
		g2d.draw(new Line2D.Double(bottom.x(), bottom.y(), top.x(), top.y()));
		
		Point left = toDrawing.transformPoint(new Point(expandedFrame.left(), 0));
		Point right = toDrawing.transformPoint(new Point(expandedFrame.right(), 0));
		g2d.draw(new Line2D.Double(left.x(), left.y(), right.x(), right.y()));
		
		
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
	
	/**
	 * @return  The frame expanded to the ratio of the JComponent
	 */
	private Rectangle expandedFrame(){
		return frame.expandToAspectRatio((double)getWidth()/(double)getHeight());
	}
}
