package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

@SuppressWarnings("serial")
public abstract class AbstractNavigableComponent extends JComponent {

	private Point firstPoint;
	private Point lastPoint;
	
	boolean isFrameVisible;
	
	private Rectangle.ObservableBuilder frame;
	
	public AbstractNavigableComponent(Rectangle.ObservableBuilder f){
		this.frame = f;
		isFrameVisible = false;
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				firstPoint = new Point(e.getX(), e.getY());
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				lastPoint = new Point(e.getX(), e.getY());
				translateFrameCenter();
			}
			
		});
	}
	
	private void translateFrameCenter(){
		
		// Here we expand the frame of the accumulator to the ratio of the component
		Rectangle expandedFrame = expandedFrame();
		Rectangle panel = new Rectangle(new Point(this.getWidth()/2, this.getHeight()/2),this.getWidth(), this.getHeight());
		
		AffineTransformation fromPanelToFrame = AffineTransformation.scaleToRectangle(panel, expandedFrame);
		
		Point lastPointOnFrame = fromPanelToFrame.transformPoint(lastPoint);
		Point firstPointOnFrame = fromPanelToFrame.transformPoint(firstPoint);
		
		double dx = lastPointOnFrame.x() - firstPointOnFrame.x();
		double dy = lastPointOnFrame.y() - firstPointOnFrame.y();
		
		frame.setCenter(new Point(frame.build().center().x() - dx, frame.build().center().y() - dy));
	}

	/**
	 * @return  The frame expanded to the ratio of the JComponent
	 */
	public Rectangle expandedFrame(){
		return frame.build().expandToAspectRatio((double)getWidth()/(double)getHeight());
	}
	
	
	public void setFrameVisibility(boolean newVisibility){
		isFrameVisible = newVisibility;
		repaint();
	}
	
	protected void paintFrame(Graphics2D g2d){
		
		Rectangle panel = new Rectangle(new Point(this.getWidth()/2, this.getHeight()/2),this.getWidth(), this.getHeight());
		// We define here the AffineTransformation corresponding to the change from frame to panel.
		// (Due to the function composeWith(), we must define them in reverse order).
		AffineTransformation toDrawing = AffineTransformation.scaleToRectangle(expandedFrame(), panel);
		
		Point topLeft = new Point(frame.build().left(), frame.build().top());
		Point bottomRight = new Point(frame.build().right(), frame.build().bottom());
		
		
		topLeft = toDrawing.transformPoint(topLeft);
		bottomRight = toDrawing.transformPoint(bottomRight);
		
		g2d.setColor(Color.ORANGE);
		g2d.drawRect((int)topLeft.x(), (int)topLeft.y(), (int)(bottomRight.x()-topLeft.x())-1, (int)(bottomRight.y()-topLeft.y()-1));
	}
}
