package ch.epfl.flamemaker.gui;

import ch.epfl.flamemaker.color.Color;

public class FlameMaker {

	public static void main(String[] args) {
		
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new FlameMakerGUI().start();
	            }
	        });
	        
	}
	
}
