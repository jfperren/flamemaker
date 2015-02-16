package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;




public class FlameMakerGUI {
	
	//Transformation List
	private ArrayList<FlameTransformation> transformationsList = new ArrayList<FlameTransformation>();
	//Builder that we modify with the interface ; Observable by the graphic components
	private ObservableFlameBuilder builder;
	
	private int selectedTransformationIndex = 0;
	private AffineTransformationComponent aftCompo;
	
	//Tables used to stock all components of transformation editing
	//Affine part :
	private JFormattedTextField[] affineTextFields;
	private JButton[] affineButtons;
	private JLabel[] affineLabels;
	//Flame part :
	private JFormattedTextField[] flameTextFields;
	private JLabel[] flameLabels;
	//Hashset stocking all listeners
	private HashSet<SelectedTransformationIndexListener> listeners = new HashSet<SelectedTransformationIndexListener>();
	
	public void start() {

		//Here is the method to use the Nimbus Look and Feel
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		// Defines all the transformations used to create 'shark-fin'
				AffineTransformation AT1 = new AffineTransformation((-1) * 0.4113504,
						-1 * 0.7124804, -1 * 0.4, 0.7124795, -1 * 0.4113508, 0.8);
				double[] AD1 = {1,0.1,0,0,0,0};
				FlameTransformation A1 = new FlameTransformation(AT1, AD1);

				AffineTransformation AT2 = new AffineTransformation((-1) * 0.3957339,
						0.0, -1.6, 0.0, (-1) * 0.3957337, 0.2);
				double[] AD2 = {0,0,0,0,0.8,1};

				FlameTransformation A2 = new FlameTransformation(AT2, AD2);

				AffineTransformation AT3 = new AffineTransformation(0.4810169, 0.0,
						1.0, 0.0, 0.4810169, 0.9);
				double[] AD3 = {1,0,0,0,0,0};
				
				FlameTransformation A3 = new FlameTransformation(AT3, AD3);

		transformationsList.add(A1);
		transformationsList.add(A2);
		transformationsList.add(A3);

		
		// Defines palette
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		Palette palette = new InterpolatedPalette(colors);
		
		builder = new ObservableFlameBuilder(new Flame.Builder(transformationsList));

		Rectangle fracFrame = new Rectangle(new Point(-0.25,0),5,4);
		
		/* -- Architecture of the Application -- */
		
		//Defining JFrame and that it ends the application on close.
		JFrame frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Definition of all JPanels used to organize the structure :
		JPanel jpaGraphics = new JPanel();
		JPanel jpaTransformList = new JPanel();
		JPanel jpaTransformList2 = new JPanel();
		JPanel jpaTransformList3 = new JPanel();
		JPanel jpaTransformEdit = new JPanel();
		JPanel jpaTransformEdit2 = new JPanel();
		
		
		//Panels for transformation list and editing
		
		
		//Settings of their layouts :
		jpaGraphics.setLayout(new GridLayout(1,2));
		
		jpaTransformList.setLayout(new BoxLayout(jpaTransformList, BoxLayout.LINE_AXIS));
		jpaTransformList2.setLayout(new BorderLayout());
		jpaTransformList3.setLayout(new GridLayout(1,2));
		jpaTransformEdit.setLayout(new BoxLayout(jpaTransformEdit,BoxLayout.PAGE_AXIS));
		
		//Finally, we need to add panels into one another
		frame.getContentPane().add(jpaGraphics, BorderLayout.CENTER);
		frame.getContentPane().add(jpaTransformList, BorderLayout.PAGE_END);
		
		//and into TransformList what belongs to TransformList
		jpaTransformList.add(jpaTransformList2);
		jpaTransformList2.add(jpaTransformList3, BorderLayout.PAGE_END);
		//add transformEdits too
		jpaTransformList.add(jpaTransformEdit);
		jpaTransformEdit.add(jpaTransformEdit2);
		
		/* -- Graphic Representation of Fractal and Transformations -- */
		
		//Panels in which we will draw :
		JPanel jpaGraphicsFractal = new JPanel();
		JPanel jpaGraphicsTransform = new JPanel();
		//As they have borders, we set them BorderLayouts, and write the title in it
		jpaGraphicsFractal.setLayout(new BorderLayout());
		jpaGraphicsTransform.setLayout(new BorderLayout());
		
		
		//We add into graphics what belongs to graphics
		jpaGraphics.add(jpaGraphicsTransform);
		jpaGraphics.add(jpaGraphicsFractal);
		
		//Then we create and add both Graphic components
		aftCompo = new AffineTransformationComponent(builder, fracFrame);
		jpaGraphicsTransform.add(aftCompo);
		jpaGraphicsFractal.add(new FlameBuilderPreviewComponent(builder,Color.BLACK, palette, 50, fracFrame));

		/* -- List showing all transformations -- */
		
		//List model that we will put into the JList (final because...)
		final TransformationsListModel listModel = new TransformationsListModel();
		//Creation and settings of the JList, using the JModel.
		final JList<String> jliTransformations = new JList<String>(listModel);
		jliTransformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jliTransformations.setVisibleRowCount(3);
		jliTransformations.setSelectedIndex(0);
		
		//Creation of the JScrollPane used to select transformations
		JScrollPane jspTransformations = new JScrollPane(jliTransformations);
		jpaTransformList2.add(jspTransformations, BorderLayout.CENTER);
		//Listeners to inform the programm when the user select another transformation
		jliTransformations.addListSelectionListener(new TransformationListSelectionListener());
		addSelectedTransformationIndexListener(new SelectedTransformationIndexListener(){
			@Override
			public void onSelectedTransformationIndexChange() {
				aftCompo.setHighlightedTransformationIndex(selectedTransformationIndex);
			}
			
		});
		
		
		//Button used to remove a transformation from the list
		final JButton jbuRemTrans = new JButton("Remove");
		jbuRemTrans.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	//Decide the item selected after the removal. same index if not last one (in this case we decrement it).
		    	int nextSelection = (getSelectedTransformationIndex()== listModel.getSize()-1) ? getSelectedTransformationIndex()-1 : getSelectedTransformationIndex();
	    		listModel.removeTransformation(getSelectedTransformationIndex());
	    		jliTransformations.setSelectedIndex(nextSelection);
	    		//Decide wether we can use it again or not (list should have a length >=2 to remove).
	    		jbuRemTrans.setEnabled(listModel.getSize()>=2);
		    	}
		});
		
		//Button used to add a transformation to the list
		final JButton jbuAddTrans = new JButton("Add");
		jbuAddTrans.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	//Add the new transformation and select it.
		    	listModel.addTransformation();
		    	jliTransformations.setSelectedIndex(listModel.getSize()-1);
		    	//After adding, the size should be at least 2.
		    	assert(listModel.getSize()>=2);
		    	//When we add, it automatically enables the remove button.
		    	jbuRemTrans.setEnabled(true);
		    }
		});
		
		//Finally we add them to the Panel.
		jpaTransformList3.add(jbuAddTrans);
		jpaTransformList3.add(jbuRemTrans);
		
		/* -- AffineTransformation Modification Interface -- */
		
		//Labels describing transformations
		String[] affineLabelValues = {"Translation", "Rotation", "Scaling", "Shearing"};
		affineLabels = new JLabel[affineLabelValues.length];
		
		for(int i=0;i<affineLabels.length;i++){
			affineLabels[i] = new JLabel(affineLabelValues[i]);
		}
		
		affineTextFields = new JFormattedTextField[affineLabels.length];
		
		//Textfields used to chose the value of a button click, and their format.
		for(int i=0;i<affineTextFields.length;i++){
			affineTextFields[i] = new JFormattedTextField(new DecimalFormat("#0.##"));
			affineTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
        affineTextFields[0].setValue(0.1);  
        affineTextFields[1].setValue(15);  
        affineTextFields[2].setValue(1.05);  
        affineTextFields[3].setValue(0.1);  
		
		String[] affineButtonValues = {"←","→","↑","↓","↺","↻","+ ↔","- ↔","+ ↔","- ↔","←","→","↑","↓"};
		affineButtons = new JButton[affineButtonValues.length];
		
		// We create each button with the right value picked in the table created above.
		for(int i=0;i<affineButtons.length;i++){
			affineButtons[i] = new JButton(affineButtonValues[i]);
		}
		
		JComponent[][] layoutOrganisation = 
			{
				{affineLabels[0], affineTextFields[0], affineButtons[0], affineButtons[1], affineButtons[2], affineButtons[3] },
				{affineLabels[1], affineTextFields[1], affineButtons[4], affineButtons[5], null , null },
				{affineLabels[2], affineTextFields[2], affineButtons[6], affineButtons[7], affineButtons[8], affineButtons[9]},
				{affineLabels[3], affineTextFields[3], affineButtons[10], affineButtons[11], affineButtons[12], affineButtons[13]},
			};
		
		jpaTransformEdit2.setLayout(createGroupLayout(jpaTransformEdit2, layoutOrganisation, false ));
		
		 
		
		//Affectation of all buttons
		for(int i=0;i<4;i++){
			int x = (i>1) ? 0 : ((i==0) ? -1 : 1);
			int y = (i<2) ? 0 : ((i==2) ? 1 : -1);
			final TranslationReturner t = new TranslationReturner(affineTextFields[0], x ,y);
			
			affineButtons[i].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                builder.setAffineTransformation(selectedTransformationIndex, builder.affineTransformation(getSelectedTransformationIndex()).composeWith(t.transformationWithValue()));
	            }
	        });
		}
		
		for(int i=4;i<6;i++){
			int theta = (i==4) ? 1 : -1;
			final RotationReturner r = new RotationReturner(affineTextFields[1], theta);
			
			affineButtons[i].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                builder.setAffineTransformation(selectedTransformationIndex, builder.affineTransformation(getSelectedTransformationIndex()).composeWith(r.transformationWithValue()));
	            }
	        });
		}
		
		for(int i=6;i<10;i++){
			int x = (i>7) ? 0 : ((i==6) ? 1 : -1);
			int y = (i<8) ? 0 : ((i==8) ? 1 : -1);
			final ScalingReturner s = new ScalingReturner(affineTextFields[2], x, y);
			
			affineButtons[i].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                builder.setAffineTransformation(selectedTransformationIndex, builder.affineTransformation(getSelectedTransformationIndex()).composeWith(s.transformationWithValue()));
	            }
	        });
		}
		
		for(int i=10;i<14;i++){
			int x = (i>11) ? 0 : ((i==10) ? -1 : 1);
			int y = (i<12) ? 0 : ((i==12) ? 1 : -1);
			final ShearingReturner s = new ShearingReturner(affineTextFields[3], x, y);
			
			affineButtons[i].addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                builder.setAffineTransformation(selectedTransformationIndex, builder.affineTransformation(getSelectedTransformationIndex()).composeWith(s.transformationWithValue()));
	            }
	        });
		}
		
        //We add the verifier to the Scaling TextField :
        affineTextFields[2].setInputVerifier(new InputVerifier() {
        	@Override
        	public boolean verify(JComponent input) {
        		//We create a copy of input, assuming it is a JFormattedTextField.
        		JFormattedTextField newInput = (JFormattedTextField)input;
        		JFormattedTextField.AbstractFormatter formatter = newInput.getFormatter();
    		 
        		System.out.print(newInput.getText());
        		//We try here to convert the text into a double
        		try {
        			//If there is no problem we can compare it to 0.
        			if(Double.parseDouble(newInput.getText())==0){
        				newInput.setValue(newInput.getValue());
        			}
        		} catch (Exception e){
        			//If there is a problem in the conversion, the content is obviously
        			//not a number so we also replace it with the last value.
        			newInput.setValue(newInput.getValue());
        		}
        		return true;
        	}});
        
        /* -- Flame Variations Editing Interface -- */
     
        
        
		// -- -- -- -- </Transformation Configuration> -- -- -- -- --
        
        String[] flameLabelValues = {"Linear", "Sinusoidal", "Spherical", "Swirl", "Horsehoe", "Bubble"};
        flameLabels = new JLabel[flameLabelValues.length];
        for(int i=0;i<flameLabels.length;i++){
        	flameLabels[i] = new JLabel(flameLabelValues[i]);
        }
        
        JPanel jpaFlameEdit = new JPanel();
        GroupLayout glyFlameEdit = new GroupLayout(jpaFlameEdit);
        flameTextFields = new JFormattedTextField[flameLabelValues.length];
        for(int i=0;i<flameTextFields.length;i++){
        	flameTextFields[i] = new JFormattedTextField(new DecimalFormat("#0.##"));
        	flameTextFields[i].setValue(builder.variationWeight(selectedTransformationIndex, Variation.ALL_VARIATIONS.get(i)));
        	final Variation newVariation = Variation.ALL_VARIATIONS.get(i);
        	final JFormattedTextField newJFormattedTextField = flameTextFields[i];
        	flameTextFields[i].addPropertyChangeListener("value", new PropertyChangeListener(){
    			@Override
    			public void propertyChange(PropertyChangeEvent arg0) {
    				builder.setVariationWeight(selectedTransformationIndex, newVariation, Double.parseDouble(newJFormattedTextField.getText()));
    			}
            });
        }
        
        JComponent[][] flameComponents = {
        		{flameLabels[0], flameTextFields[0], flameLabels[1], flameTextFields[1], flameLabels[2], flameTextFields[2]},
        		{flameLabels[3], flameTextFields[3], flameLabels[4], flameTextFields[4], flameLabels[5], flameTextFields[5]}
        };
        jpaTransformEdit.add(new JSeparator());
        jpaTransformEdit.add(jpaFlameEdit);
        jpaFlameEdit.setLayout(createGroupLayout(jpaFlameEdit, flameComponents, true));
        
        //Adding the border :
        jpaTransformEdit.setBorder(BorderFactory.createTitledBorder("Current Transformation"));
      	jpaTransformList2.setBorder(BorderFactory.createTitledBorder("Transformations"));
      	jpaGraphicsFractal.setBorder(BorderFactory.createTitledBorder("Fractal"));
		jpaGraphicsTransform.setBorder(BorderFactory.createTitledBorder("Affine Transformations"));
		
        addSelectedTransformationIndexListener(new SelectedTransformationIndexListener(){
			@Override
			public void onSelectedTransformationIndexChange(){
				  for(int i=0;i<flameTextFields.length;i++){
			        	flameTextFields[i].setValue(builder.variationWeight(selectedTransformationIndex, Variation.ALL_VARIATIONS.get(i)));
			        }
			}
		});
        
		// -- -- -- -- End component declaration -- -- -- -- -- -- --
		
		//Sets frame.pack and setVisible()
        frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Update the value of all Textfields corresponding to a Flame Variation.
	 * @param textFields List of all TextFields
	 */
	private void updateFlameTextFields(List<JFormattedTextField> textFields){
		//There should be as many textfields as Variations.
		for(int i=0; i < textFields.size();i++){
			textFields.get(i).setValue(builder.variationWeight(selectedTransformationIndex, Variation.ALL_VARIATIONS.get(i)));
		}
		
	}
	/**
	 * This method transforms a table of JComponents into a GroupLayout
	 * Its only use is to simplify the code inside this class.
	 * @param panel The JPanel you want to put the grouplayout on.
	 * @param components The table of JComponents that needs to be transformed into a layout.
	 * @return The GroupLayout containing every components at the same place as in the table.
	 */
	private GroupLayout createGroupLayout(JPanel panel, JComponent[][] components, boolean isPreferedGapNeeded){
		/* As we use Table instead of collections, components[i].length is the same
		 * For every i and we don't have to worry about the fact that it is possibly
		 * Not a rectangular table. Plus, the fields that are not initialized have the
		 * value none. */
		
		//Creation of the GroupLayout :
		GroupLayout newGroupLayout = new GroupLayout(panel);
		
		//Creation of 2 sequential macro-groups :
		SequentialGroup sHorizontalGroup = newGroupLayout.createSequentialGroup();
		SequentialGroup sVerticalGroup = newGroupLayout.createSequentialGroup();
		
		
		//We define tableHeight and tableWidth the sizes of the component table
		int tableHeight = components.length;
		int tableWidth = components[0].length;
		
		
		//Creation of 2 tables containing each vertical and horizontal groups
		ParallelGroup[] pVerticalGroups = new ParallelGroup[tableHeight];
		ParallelGroup[] pHorizontalGroups = new ParallelGroup[tableWidth];
		
		for(int i=0;i<tableHeight;i++){
			//For every column, we create i lines :
			pVerticalGroups[i] = newGroupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			//And we add the j elements of each line group :
			for(int j=0;j<tableWidth;j++){
				if(components[i][j] != null)
				pVerticalGroups[i].addComponent(components[i][j]);
			}
			//Finally we add the new group to the macro group :
			sVerticalGroup.addGroup(pVerticalGroups[i]);
		}
		
		for(int j=0;j<tableWidth;j++){
			//For every line, we create j columns : (First one need a special alignment)
			pHorizontalGroups[j] = (j==0)?newGroupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING):newGroupLayout.createParallelGroup();
			//And we add the i elements of each column group :
			for(int i=0;i<tableHeight;i++){
				if(components[i][j] != null)
				pHorizontalGroups[j].addComponent(components[i][j]);
			}
			//if a gap is needed (numbers of elements added is even but not 0), we add it :
			if (isPreferedGapNeeded && j%2==0 & j!=0) sHorizontalGroup.addPreferredGap(ComponentPlacement.UNRELATED);
			//Finally we add the new group to the macro group
			sHorizontalGroup.addGroup(pHorizontalGroups[j]);
		}
		
		//Adding both sequential groups to the layout :
		newGroupLayout.setVerticalGroup(sVerticalGroup);
		newGroupLayout.setHorizontalGroup(sHorizontalGroup);

		return newGroupLayout;
	}
	
	
	public int getSelectedTransformationIndex(){
		return selectedTransformationIndex;
	}
	
	public void setSelectedTransformationIndex(int newIndex){
		selectedTransformationIndex = newIndex;
		
		Iterator<SelectedTransformationIndexListener> it = listeners.iterator();
		while(it.hasNext()){
			it.next().onSelectedTransformationIndexChange();
		}
	}
	
	public void addSelectedTransformationIndexListener(SelectedTransformationIndexListener newListener){
		listeners.add(newListener);
	}
	
	public void removeSelectedTransformationIndexListener(SelectedTransformationIndexListener oldListener){
		listeners.remove(oldListener);
	}
	
	private class TransformationsListModel extends AbstractListModel {

		@Override
		public Object getElementAt(int index) {

			return "Transformation n° " + index;
		}

		@Override
		public int getSize() {
			return builder.transformationCount();
		}
		
		public void addTransformation(){
			double[] weights = {1,0,0,0,0,0};
			builder.addTransformation(new FlameTransformation(AffineTransformation.IDENTITY, weights));
			
			//Send information to the listeners
			fireIntervalAdded(this, 0, getSize()-1);
		}
		
		public void removeTransformation(int index){
			builder.removeTransformation(index);
			
			//Send information to the listeners
			fireIntervalRemoved(this, 0, getSize()-1);
		}
		
	}
	
	interface SelectedTransformationIndexListener{
		public void onSelectedTransformationIndexChange();
	}
	
	public class TransformationListSelectionListener implements ListSelectionListener{

		@Override
		public void valueChanged(ListSelectionEvent e) {
			setSelectedTransformationIndex(((JList)e.getSource()).getSelectedIndex());
		}
	}
	
	abstract class TransformationReturner{
		
		JFormattedTextField source;
		
		public TransformationReturner(JFormattedTextField source){
			this.source = source;
		}
		
		public abstract AffineTransformation transformationWithValue();
	}
	
	private class TranslationReturner extends TransformationReturner{

		int xFactor;
		int yFactor;
		
		public TranslationReturner(JFormattedTextField source, int xFactor, int yFactor){
			
			super(source);
			this.xFactor = xFactor;
			this.yFactor = yFactor;
			
		}
		
		@Override
		public AffineTransformation transformationWithValue() {
			return AffineTransformation.newTranslation(xFactor * Double.parseDouble(source.getText()), yFactor * Double.parseDouble(source.getText()));
		}
		
	}
	
	private class RotationReturner extends TransformationReturner{

		int thetaFactor;
		
		public RotationReturner(JFormattedTextField source, int thetaFactor){
			super(source);
			this.thetaFactor = thetaFactor;
		}
		
		@Override
		public AffineTransformation transformationWithValue() {
			return AffineTransformation.newRotation(thetaFactor * Double.parseDouble(source.getText())*Math.PI/180);
		}
		
	}
	
	private class ScalingReturner extends TransformationReturner{

		int xFactor;
		int yFactor;
		
		public ScalingReturner(JFormattedTextField source, int xFactor, int yFactor){
			
			super(source);
			this.xFactor = xFactor;
			this.yFactor = yFactor;
			
		}
		
		@Override
		public AffineTransformation transformationWithValue() {
			return AffineTransformation.newScaling(Math.pow(Double.parseDouble(source.getText()),xFactor), Math.pow(Double.parseDouble(source.getText()),yFactor));
		}
		
	}
	
	private class ShearingReturner extends TransformationReturner{

		int xFactor;
		int yFactor;
		
		public ShearingReturner(JFormattedTextField source, int xFactor, int yFactor){
			
			super(source);
			this.xFactor = xFactor;
			this.yFactor = yFactor;
			
		}
		
		@Override
		public AffineTransformation transformationWithValue() {
			return (yFactor==0)&&(xFactor!=0) ? AffineTransformation.newShearX(xFactor*Double.parseDouble(source.getText())) : AffineTransformation.newShearY(yFactor*Double.parseDouble(source.getText()));
		}
		
	}
}
