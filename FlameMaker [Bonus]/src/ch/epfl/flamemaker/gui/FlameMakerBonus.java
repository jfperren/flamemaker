package ch.epfl.flamemaker.gui;

public class FlameMakerBonus {

	public static void main(String[] args) {
		
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new FlameMakerGUIBonus().start();
	            }
	        });
	}
}
