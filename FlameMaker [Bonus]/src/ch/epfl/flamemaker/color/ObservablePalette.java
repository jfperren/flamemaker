package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ObservablePalette implements Palette {

	private List<Color> colors;
	private Palette palette;
	private HashSet<Observer> observers;

	public ObservablePalette(List<Color> colors) {
		this.colors = colors;
		this.palette = new InterpolatedPalette(colors);
		observers = new HashSet<Observer>();
	}
	
	public ObservablePalette(){
		this.colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		this.palette = new InterpolatedPalette(colors);
		observers = new HashSet<Observer>();
	}

	@Override
	public Color colorForIndex(double index) throws IllegalArgumentException {
		return palette.colorForIndex(index);
	}

	/**
	 * Add color to the list and recreate the palette.
	 * 
	 * @param color
	 *            Color added
	 */
	public void addColor(int selectedColorIndex, Color color) {
		colors.add(selectedColorIndex, color);
		palette = new InterpolatedPalette(colors);
		notifyObservers();
	}

	/**
	 * Remove color from the list and recreate the palette.
	 * 
	 * @param index
	 *            Index where we have to remove the color
	 */
	public void removeColor(int index) {
		colors.remove(index);
		palette = new InterpolatedPalette(colors);
		notifyObservers();
	}

	public void invertColor(int index1, int index2) {
		Color buffer = colors.get(index1);
		colors.set(index1, colors.get(index2));
		colors.set(index2, buffer);
		
		palette = new InterpolatedPalette(colors);
		
		notifyObservers();
	}

	public Color getColor(int index) {
		return colors.get(index);
	}
	
	public void setColor(int index, Color newColor){
		colors.set(index, newColor);
		palette = new InterpolatedPalette(colors);
		
		notifyObservers();
	}

	public List<Color> getColors() {
		return colors;
	}

	public void setPalette(List<Color> colors) {
		palette = new ObservablePalette(colors);
		notifyObservers();
	}

	public void setRandomColors() {
		for (int i = 0; i < colors.size(); i++) {
			colors.set(i,
					new Color(Math.random(), Math.random(), Math.random()));
		}
		palette = new InterpolatedPalette(colors);
		notifyObservers();
	}

	public void addObserver(Observer newObserver) {
		observers.add(newObserver);
	}

	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers() {
		for (Observer o : observers) {
			o.update();
		}
	}

	public interface Observer {
		void update();
	}
}
