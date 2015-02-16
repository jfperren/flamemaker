package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.ObservablePalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation.AffineSign;
import ch.epfl.flamemaker.geometry2d.AffineTransformation.AffineType;
import ch.epfl.flamemaker.geometry2d.AffineTransformation.affineCreator;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameMakerGUIBonus {

	// Transformation List
	private ArrayList<FlameTransformation> transformationsList = new ArrayList<FlameTransformation>();
	// Builder that we modify with the interface ; Observable by the graphic
	// components
	private ObservableFlameBuilder builder;
	private ObservablePalette palette;
	private Rectangle.ObservableBuilder fractalFrame;
	// Index of the currently selected flame transformation :
	private int selectedTransformationIndex = 0;
	private int selectedColorIndex = 0;
	private int density = 50;
	private Color previewColor;

	// Hashset stocking all listeners :
	private HashSet<SelectedTransformationIndexObserver> selectedTransformationIndexObservers = new HashSet<SelectedTransformationIndexObserver>();
	private HashSet<DensityObserver> densityObservers = new HashSet<DensityObserver>();
	private HashMap<JComponent, Boolean> componentVisibility = new HashMap<JComponent, Boolean>();
	private HashSet<SelectedColorIndexObserver> selectedColorIndexObservers = new HashSet<SelectedColorIndexObserver>();
	private HashSet<PreviewColorObserver> previewColorObservers = new HashSet<PreviewColorObserver>();

	// JComponents used in the programm - Description in the void start
	// We thought it was better to define them here even if it takes more memory
	// space
	// Because with the future implementations of the programm we may need them
	// in nested classes,
	// and that way we don't need to put them as final which is less clean than
	// that.

	// - Step 1 :
	private JFrame frame;
	private JPanel jpaGraphics;
	private JPanel jpaGraphicsFractal;
	private JPanel jpaGraphicsTransform;

	private JPanel jpaEdit;
	private JPanel jpaEditBoxes;

	private JPanel jpaEditList;
	private JPanel jpaEditListButtons;

	private JPanel jpaEditValues;
	private JPanel jpaEditValuesAffine;
	private JPanel jpaEditValuesFlame;

	private JPanel jpaEditColor; // (Bonus)
	private JPanel jpaEditColorList;
	private JPanel jpaEditColorListButtonsH;
	private JPanel jpaEditColorListButtonsV;
	private JPanel jpaEditColorChange;

	// - Step 2 :
	private NavigableAffineTransformationComponent affineTransformationComponent;
	private NavigableFlameBuilderPreviewComponent flameBuilderPreviewComponent;
	// - Step 3 :
	private TransformationsListModel listModel;
	private JList<String> jliTransformations;
	private JScrollPane jspTransformList;
	private JButton jbuAddTrans;
	private JButton jbuRemTrans;
	// - Step 4 :
	private JFormattedTextField[] affineTextFields;
	private JButton[][] affineButtons;
	private JLabel[] affineLabels;
	// - Step 5 :
	private JFormattedTextField[] flameTextFields;
	private JLabel[] flameLabels;
	// - Step 6 (Bonus) :
	private JPanel jpaEditFrame;
	private JPanel jpaEditFrameValues;
	private JPanel jpaEditFrameZoom;
	private JFormattedTextField jtfFrameCenterX;
	private JFormattedTextField jtfFrameCenterY;
	private JFormattedTextField jtfFrameWidth;
	private JFormattedTextField jtfFrameHeight;
	private JCheckBox jcbShowFrame;
	private JButton jbuCenterFrame;

	// - Step 7 (Bonus) :
	private ColorsListModel colorsListModel;
	private JList<String> jliColors;
	private JScrollPane jspColorList;
	private JButton jbuAddColor;
	private JButton jbuRemColor;
	private JButton jbuColorListUp;
	private JButton jbuColorListDown;
	private JButton jbuColorListReplace;
	private JButton jbuColorListRandom;
	private PalettePreviewComponent palettePreviewComponent;
	private ColorPreviewComponent colorSelectedPreviewComponent;
	private ColorPreviewComponent colorPreviewComponent;
	private ColorSlider jslRedComponent;
	private ColorSlider jslBlueComponent;
	private ColorSlider jslGreenComponent;
	private JLabel jlaRedComponent;
	private JLabel jlaGreenComponent;
	private JLabel jlaBlueComponent;

	// - Step 8 (Bonus)
	private JButton jbuDensity;
	private JSlider jslDensity;
	private JPanel jpaEditDensity;

	public void start() {

		// Here is the method to use the Nimbus Look and Feel
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		/*
		 * STEP 0 : DEFINING FRACTAL, FRAME AND PALETTE :
		 */

		// Defines all the transformations used to create 'shark-fin'...
		// - Transformation 1 :
		AffineTransformation AT1 = new AffineTransformation((-1) * 0.4113504,
				-1 * 0.7124804, -1 * 0.4, 0.7124795, -1 * 0.4113508, 0.8);
		double[] AD1 = { 1, 0.1, 0, 0, 0, 0 };
		FlameTransformation A1 = new FlameTransformation(AT1, AD1);
		// - Transformation 2 :
		AffineTransformation AT2 = new AffineTransformation((-1) * 0.3957339,
				0.0, -1.6, 0.0, (-1) * 0.3957337, 0.2);
		double[] AD2 = { 0, 0, 0, 0, 0.8, 1 };
		FlameTransformation A2 = new FlameTransformation(AT2, AD2);
		// - Transformation 3 :
		AffineTransformation AT3 = new AffineTransformation(0.4810169, 0.0,
				1.0, 0.0, 0.4810169, 0.9);
		double[] AD3 = { 1, 0, 0, 0, 0, 0 };
		FlameTransformation A3 = new FlameTransformation(AT3, AD3);

		// ...and add them in the list
		transformationsList.add(A1);
		transformationsList.add(A2);
		transformationsList.add(A3);

		// Defines the builder using the transformation list :
		builder = new ObservableFlameBuilder(new Flame.Builder(new Flame(
				transformationsList)));

		// Creates a palette using the default builder :
		palette = new ObservablePalette(); // (Red, Green, Blue)
		previewColor = palette.getColor(selectedColorIndex);
		// Finally, defines the default frame as a 5x4 Rectangles centered on
		// (-0.25,0)
		fractalFrame = new Rectangle.ObservableBuilder(new Rectangle(new Point(
				-0.25, 0), 5, 4));

		/*
		 * STEP 1 : CREATING ARCHITECTURE OF APPLICATION
		 */
		/*
		 * Our application is organized like this :
		 * 
		 * 										JFRAME
		 *    					/                     			  \
		 *    			jpaGraphics					 				jpaEdit
		 *    		/                 \             			/             	\
		 * jpaGraphicsFractal  jpaGraphicsTransform		jpaEditDensity		   jpaEditBoxes
		 * 																/    |     			|    	\
		 * 													jpaEditList	jpaEditValues jpaEditFrame jpaEditColor
		 */

		// Defining JFrame and that it ends the application on close.
		frame = new JFrame("Flame Maker");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Definition of all JPanels used to organize the structure :
		jpaGraphics = new JPanel(); // Contains jpaGraphicsFractal and Transform
		jpaGraphicsFractal = new JPanel(); // Contains
											// FlameBuilderPreviewComponent
		jpaGraphicsTransform = new JPanel(); // Contains
												// AffineTransformationComponent

		jpaEdit = new JPanel(); // Contains jpaEditDensity and jpaEditBoxes
		jpaEditBoxes = new JPanel(); // Contains all panels of modification

		jpaEditList = new JPanel(); // Contains the JList
		jpaEditListButtons = new JPanel(); // Contains ...ListShow and buttons

		jpaEditValues = new JPanel(); // Contains ..AffineValuesEdit and
										// ...FlameValuesEdit
		jpaEditValuesAffine = new JPanel(); // Contains buttons for affine part
		jpaEditValuesFlame = new JPanel(); // Contains buttons for flame part

		jpaEditFrame = new JPanel(); // Contains everythin to modify the frame
		jpaEditFrameValues = new JPanel();
		jpaEditFrameZoom = new JPanel();

		jpaEditDensity = new JPanel();

		jpaEditColor = new JPanel(); // (Bonus)
		jpaEditColorList = new JPanel();
		jpaEditColorListButtonsH = new JPanel();
		jpaEditColorListButtonsV = new JPanel();
		jpaEditColorChange = new JPanel();

		// Settings of their layouts (in the same order) :
		jpaGraphics.setLayout(new GridLayout(1, 2));
		jpaGraphicsFractal.setLayout(new BorderLayout());
		jpaGraphicsTransform.setLayout(new BorderLayout());

		jpaEdit.setLayout(new BorderLayout());
		jpaEditBoxes
				.setLayout(new BoxLayout(jpaEditBoxes, BoxLayout.LINE_AXIS));

		jpaEditList.setLayout(new BorderLayout());
		jpaEditListButtons.setLayout(new GridLayout(1, 2));

		jpaEditValues.setLayout(new BoxLayout(jpaEditValues,
				BoxLayout.PAGE_AXIS));

		jpaEditFrame
				.setLayout(new BoxLayout(jpaEditFrame, BoxLayout.PAGE_AXIS));
		jpaEditDensity.setLayout(new BorderLayout());

		jpaEditColor.setLayout(new BorderLayout());
		jpaEditColorList.setLayout(new BorderLayout());
		jpaEditColorListButtonsH.setLayout(new GridLayout(1, 2));
		jpaEditColorListButtonsV.setLayout(new GridLayout(2, 1));
		jpaEditColorChange.setLayout(new BoxLayout(jpaEditColorChange,
				BoxLayout.PAGE_AXIS));

		// Adding both main JPanels into the Frame content pane :
		frame.getContentPane().add(jpaGraphics, BorderLayout.CENTER);
		frame.getContentPane().add(jpaEdit, BorderLayout.PAGE_END);

		// Adding both jpaGraphicsFractal and jpaGraphicsTransform into their
		// parent :
		jpaGraphics.add(jpaGraphicsTransform);
		jpaGraphics.add(jpaGraphicsFractal);

		// Adding everyting in the right place into jpaTransformOptions (from
		// leaf to root)
		// 1) list editing :
		jpaEditList.add(jpaEditListButtons, BorderLayout.PAGE_END);
		jpaEditBoxes.add(jpaEditList);
		// 2) affine/flame values editing :
		jpaEditValues.add(jpaEditValuesAffine);
		jpaEditValues.add(new JSeparator());
		jpaEditValues.add(jpaEditValuesFlame);
		jpaEditBoxes.add(jpaEditValues);
		// 3) frame editing
		jpaEditBoxes.add(jpaEditFrame);
		jpaEditFrame.add(jpaEditFrameValues);
		jpaEditFrame.add(new JSeparator());
		jpaEditFrame.add(jpaEditFrameZoom);
		jpaEdit.add(jpaEditBoxes, BorderLayout.CENTER);
		jpaEdit.add(jpaEditDensity, BorderLayout.NORTH);
		// 7) Palette editing
		jpaEditBoxes.add(jpaEditColor);

		// Finally we set all borders :
		jpaEditValues.setBorder(BorderFactory
				.createTitledBorder("Current Transformation"));
		jpaEditList.setBorder(BorderFactory
				.createTitledBorder("Transformation List"));
		jpaGraphicsFractal.setBorder(BorderFactory
				.createTitledBorder("Fractal"));
		jpaGraphicsTransform.setBorder(BorderFactory
				.createTitledBorder("Transformation Display"));
		jpaEditFrame.setBorder(BorderFactory.createTitledBorder("Frame"));
		jpaEditDensity.setBorder(BorderFactory.createTitledBorder("Density : "
				+ density));
		jpaEditColor
				.setBorder(BorderFactory.createTitledBorder("Palette Edit"));

		/*
		 * STEP 2 : GRAPHIC REPRESENTATION OF FRACTAL AND TRANSFORMATIONS :
		 */

		// We just need to create both component and add them in their
		// corresponding panels :
		flameBuilderPreviewComponent = new NavigableFlameBuilderPreviewComponent(
				builder, Color.BLACK, palette, density, fractalFrame);
		affineTransformationComponent = new NavigableAffineTransformationComponent(
				builder, fractalFrame);

		jpaGraphicsFractal.add(flameBuilderPreviewComponent);
		jpaGraphicsTransform.add(affineTransformationComponent);

		// Adds an observer that notifies the affine component when the index of
		// highlight changes :
		addSelectedTransformationIndexObserver(new SelectedTransformationIndexObserver() {
			@Override
			public void onSelectedTransformationIndexChange() {
				affineTransformationComponent
						.setHighlightedTransformationIndex(selectedTransformationIndex);
			}

		});

		addDensityObserver(new DensityObserver() {
			@Override
			public void onDensityChange() {
				flameBuilderPreviewComponent.setDensity(getDensity());
			}
		});

		/*
		 * STEP 3 : LIST OF TRANSFORMATIONS AND BUTTONS ADD/REMOVE
		 */

		// List model that we will put into the JList (final because otherwise
		// can't be used in nested classes)
		listModel = new TransformationsListModel();

		// Creation and settings of the JList, using the JModel.
		jliTransformations = new JList<String>(listModel);
		jliTransformations
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jliTransformations.setVisibleRowCount(3);
		jliTransformations.setSelectedIndex(selectedTransformationIndex);

		// Creation of the JScrollPane used to select transformations, and
		// adding into the Panel :
		jspTransformList = new JScrollPane(jliTransformations);
		jpaEditList.add(jspTransformList, BorderLayout.CENTER);

		// This Listener changes the value of "selectedTransformationIndex" when
		// the
		// user selects another one in the list. (in order to respect MVC design
		// pattern).
		jliTransformations
				.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						setSelectedTransformationIndex(((JList<?>) e
								.getSource()).getSelectedIndex());
					}
				});

		// Button used to remove a transformation from the list
		jbuRemTrans = new ListEditButton("Remove",
				listEditButtonFunction.REMOVE);
		// Button used to add a transformation to the list
		jbuAddTrans = new ListEditButton("Add", listEditButtonFunction.ADD);

		// Finally we add them to the Panel.
		jpaEditListButtons.add(jbuAddTrans);
		jpaEditListButtons.add(jbuRemTrans);

		/*
		 * STEP 4 : AFFINE PART EDITING PANEL :
		 */

		// If anyone changes the code here to add a transformation type, all the
		// arrays should have the same
		// length, i.e affineTypeCount :
		int affineTypeCount = 4; // We have here 4 different types of affine
									// Transformation

		// For each values affected to an affine transformation, we create a
		// table :
		String[] affineTransformationNames = { "Translation", "Rotation",
				"Scaling", "Shearing" };
		double[] affineTextFieldsDefaultValues = { 0.1, 15, 1.05, 0.1 };
		// Table of the texts on the JButtons
		// (Note that we had encoding issues with rotation symbols).
		String[][] affineButtonValues = { { "←", "→", "↑", "↓" },
				{ "↺", "↻" }, { "+ ↕", "- ↕", "+ ↔", "- ↔" },
				{ "←", "→", "↑", "↓" } };

		/*
		 * Here, for the implementation of the Strategy pattern, we define for
		 * each AffineEditButton the type of transformation it should add, and
		 * if it is positive or negative. Then, at the creation of the
		 * affineEditButton, the ActionListener is automatically created with
		 * the right arguments for the static method
		 * createAffineTransformationWithValue of the class AffineCreator.
		 */
		AffineTransformation.AffineType[][] affineButtonTypes = {
				{ AffineType.TRANSLATIONX, AffineType.TRANSLATIONX,
						AffineType.TRANSLATIONY, AffineType.TRANSLATIONY },
				{ AffineType.ROTATION, AffineType.ROTATION },
				{ AffineType.SCALINGX, AffineType.SCALINGX,
						AffineType.SCALINGY, AffineType.SCALINGY },
				{ AffineType.SHEARX, AffineType.SHEARX, AffineType.SHEARY,
						AffineType.SHEARY } };

		AffineTransformation.AffineSign[][] affineButtonSigns = {
				{ AffineSign.NEGATIVE, AffineSign.POSITIVE,
						AffineSign.POSITIVE, AffineSign.NEGATIVE },
				{ AffineSign.POSITIVE, AffineSign.NEGATIVE },
				{ AffineSign.POSITIVE, AffineSign.NEGATIVE,
						AffineSign.POSITIVE, AffineSign.NEGATIVE },
				{ AffineSign.NEGATIVE, AffineSign.POSITIVE,
						AffineSign.POSITIVE, AffineSign.NEGATIVE } };

		// Then we create the tables for the JComponents
		affineLabels = new JLabel[affineTypeCount];
		affineTextFields = new JFormattedTextField[affineTypeCount];
		affineButtons = new JButton[affineButtonValues.length][];

		int greaterNumberOfAlignedButtons = 0;

		// As there must be the same amount of label than textfields, we can use
		// a little shortcut :

		for (int i = 0; i < affineTypeCount; i++) {
			// For each affine transformation, defines the label :
			affineLabels[i] = new JLabel(affineTransformationNames[i]);
			// ...and the corresponding textfield, with the right format :
			affineTextFields[i] = new JFormattedTextField(new DecimalFormat(
					"#0.##"));
			affineTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
			// ...into which we set the right default value :
			affineTextFields[i].setValue(affineTextFieldsDefaultValues[i]);

			// Finally, we create every button corresponding to the affine
			// Transformation :
			affineButtons[i] = new JButton[affineButtonValues[i].length];
			for (int j = 0; j < affineButtons[i].length; j++) {
				AffineTransformation.affineCreator c = new affineCreator(
						affineButtonTypes[i][j], affineButtonSigns[i][j]);
				affineButtons[i][j] = new AffineEditButton(
						affineButtonValues[i][j], affineTextFields[i], c);
			}

			// And we keep trace of the highest number of aligned buttons
			greaterNumberOfAlignedButtons = (affineButtons[i].length > greaterNumberOfAlignedButtons) ? affineButtons[i].length
					: greaterNumberOfAlignedButtons;
		}

		// Finally we must create the table layoutOrganisation, that will
		// contain every JComponent define above at the
		// exact spot we want on the layout, so we can then create it with the
		// method createGroupLayout.

		JComponent[][] layoutOrganisation = new JComponent[affineTypeCount][2 + greaterNumberOfAlignedButtons];

		for (int i = 0; i < layoutOrganisation.length; i++) {
			// For each row, the first component is the label, then the
			// textfield :
			layoutOrganisation[i][0] = affineLabels[i];
			layoutOrganisation[i][1] = affineTextFields[i];
			for (int j = 2; j < layoutOrganisation[i].length; j++) {
				// Then we put the buttons, and if the table still has place we
				// put null :
				layoutOrganisation[i][j] = (j - 2 < affineButtons[i].length) ? affineButtons[i][j - 2]
						: null;
			}
		}

		// Finally we create the layout with the table layoutOrganisation. (no
		// preferedGap)
		jpaEditValuesAffine.setLayout(createGroupLayout(jpaEditValuesAffine,
				layoutOrganisation, groupLayoutDisposition.NO_GAP,
				GroupLayout.Alignment.TRAILING));

		// We add the verifier to the Scaling TextField :
		affineTextFields[2].setInputVerifier(new noZeroInputVerifier());

		/*
		 * STEP 5 : FLAME VARIATIONS EDITING PANEL
		 */

		// We do the same method here as in step 4, defining all tables :
		int flameTypeCount = Variation.ALL_VARIATIONS.size();

		String[] flameLabelValues = { "Linear", "Sinusoidal", "Spherical",
				"Swirl", "Horsehoe", "Bubble" };

		flameLabels = new JLabel[flameTypeCount];
		flameTextFields = new JFormattedTextField[flameTypeCount];

		for (int i = 0; i < flameTypeCount; i++) {
			// Creates the i-th JLabel
			flameLabels[i] = new JLabel(flameLabelValues[i]);
			// Creates and sets the i-th JFormattedTextField
			flameTextFields[i] = new JFormattedTextField(new DecimalFormat(
					"#0.##"));
			flameTextFields[i].setValue(builder.variationWeight(
					selectedTransformationIndex,
					Variation.ALL_VARIATIONS.get(i)));

			// We define those two variables as final to be able to put them in
			// the Listener below :
			final Variation newVariation = Variation.ALL_VARIATIONS.get(i);
			final JFormattedTextField newJFormattedTextField = flameTextFields[i];
			// Adds a listener to each textfields that sets the right value to
			// the variation in the builder when the
			// user changes it :
			flameTextFields[i].addPropertyChangeListener("value",
					new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent arg0) {
							builder.setVariationWeight(
									selectedTransformationIndex, newVariation,
									Double.parseDouble(newJFormattedTextField
											.getText()));
						}
					});
		}
		// Here is what our GroupLayout should look like :
		JComponent[][] flameComponents = {
				{ flameLabels[0], flameTextFields[0], flameLabels[1],
						flameTextFields[1], flameLabels[2], flameTextFields[2] },
				{ flameLabels[3], flameTextFields[3], flameLabels[4],
						flameTextFields[4], flameLabels[5], flameTextFields[5] } };

		// Creates and assigns the GroupLayout with our method createGroupLayout
		// :
		jpaEditValuesFlame.setLayout(createGroupLayout(jpaEditValuesFlame,
				flameComponents, groupLayoutDisposition.COLUMNS_OF_TWO,
				GroupLayout.Alignment.TRAILING));

		// We add a new Observer on the attribut selectedTransformationIndex
		// that changes the values
		// in the textfield when the user selects another transformation :
		addSelectedTransformationIndexObserver(new SelectedTransformationIndexObserver() {
			@Override
			public void onSelectedTransformationIndexChange() {
				updateFlameTextFields();
			}
		});

		/*
		 * STEP 6 (BONUS) : FRAME EDITING PANEL
		 */

		// Labels to see which textfield correspond to which value :
		JLabel jlaFrameCenter = new JLabel("Center ");
		JLabel jlaFrameDimension = new JLabel("Dimension");

		// Creating the subpanel containing ... ; ... (Center)
		jtfFrameCenterX = new FrameEditTextField(new DecimalFormat("#0.##"),
				FrameEditTextFieldFunction.CHANGE_CENTERX);
		JLabel jlaFrameCenter2 = new JLabel(" ; ");
		jtfFrameCenterY = new FrameEditTextField(new DecimalFormat("#0.##"),
				FrameEditTextFieldFunction.CHANGE_CENTERY);

		JPanel jpaEditFrameCenter = new JPanel();
		jpaEditFrameCenter.setLayout(new BoxLayout(jpaEditFrameCenter,
				BoxLayout.LINE_AXIS));
		jpaEditFrameCenter.add(jtfFrameCenterX);
		jpaEditFrameCenter.add(jlaFrameCenter2);
		jpaEditFrameCenter.add(jtfFrameCenterY);

		// Same with ... X ... (Dimension)
		jtfFrameWidth = new FrameEditTextField(new DecimalFormat("#0.##"),
				FrameEditTextFieldFunction.CHANGE_WIDTH);
		JLabel jlaFrameDimension1 = new JLabel(" x ");
		jtfFrameHeight = new FrameEditTextField(new DecimalFormat("#0.##"),
				FrameEditTextFieldFunction.CHANGE_HEIGHT);

		JPanel jpaEditFrameDimension = new JPanel();
		jpaEditFrameDimension.setLayout(new BoxLayout(jpaEditFrameDimension,
				BoxLayout.X_AXIS));
		jpaEditFrameDimension.add(jtfFrameWidth);
		jpaEditFrameDimension.add(jlaFrameDimension1);
		jpaEditFrameDimension.add(jtfFrameHeight);

		// Setting all initial values for textfields
		jtfFrameCenterX.setValue((double) fractalFrame.center().x());
		jtfFrameCenterY.setValue((double) fractalFrame.center().y());
		jtfFrameWidth.setValue((double) fractalFrame.width());
		jtfFrameHeight.setValue((double) fractalFrame.height());

		fractalFrame.addObserver(new Rectangle.ObservableBuilder.Observer() {
			@Override
			public void update() {
				updateFrameTextFields();
			}
		});

		// CheckBox used to show the frame on the graphic Components :
		jcbShowFrame = new JCheckBox("Show", false);
		jcbShowFrame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				affineTransformationComponent.setFrameVisibility(jcbShowFrame
						.isSelected());
				flameBuilderPreviewComponent.setFrameVisibility(jcbShowFrame
						.isSelected());
			}
		});

		// Button used to center the frame on the Origin :
		jbuCenterFrame = new JButton("Center on origin");
		jbuCenterFrame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fractalFrame.setCenter(Point.ORIGIN);
			}
		});

		// Creating the GroupLayout :
		JComponent[][] fractalFrameComponents = {
				{ jlaFrameCenter, jpaEditFrameCenter },
				{ jlaFrameDimension, jpaEditFrameDimension },
				{ jcbShowFrame, jbuCenterFrame } };

		jpaEditFrameValues.setLayout(createGroupLayout(jpaEditFrameValues,
				fractalFrameComponents, groupLayoutDisposition.NO_GAP,
				GroupLayout.Alignment.LEADING));

		// Zoom buttons
		jpaEditFrameZoom.setLayout(new GridLayout(2, 2));
		jpaEditFrameZoom.add(new ZoomButton("50%", 0.5));
		jpaEditFrameZoom.add(new ZoomButton("75%", 0.75));
		jpaEditFrameZoom.add(new ZoomButton("150%", 1.5));
		jpaEditFrameZoom.add(new ZoomButton("200%", 2));

		// We need to have InputVerifiers on width and height textfields, since
		// it can't accept a negative or zero value :
		jtfFrameWidth.setInputVerifier(new higherThanZeroInputVerifier());
		jtfFrameHeight.setInputVerifier(new higherThanZeroInputVerifier());

		/*
		 * STEP 7 (BONUS) : PALETTE EDIT
		 */

		// List model that we will put into the JList.
		colorsListModel = new ColorsListModel();

		// Creation and settings of the JList of colors, using the JModel.
		jliColors = new JList<String>(colorsListModel);
		jliColors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jliColors.setVisibleRowCount(3);
		jliColors.setSelectedIndex(0);

		// Creation of the JScrollPane used to select colors, and adding into
		// the Panel :
		jspColorList = new JScrollPane(jliColors);

		// This Listener changes the value of "selectedColorIndex" when
		// the user selects another one in the list. (in order to respect MVC
		// design pattern).
		jliColors.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setSelectedColorIndex(((JList<?>) e.getSource())
						.getSelectedIndex());
			}
		});

		// ColorPreviewComponents for the current color and modified one :
		colorSelectedPreviewComponent = new ColorPreviewComponent(50, palette
				.getColors().get(selectedColorIndex));
		colorPreviewComponent = new ColorPreviewComponent(50, previewColor);
		// Component that display the palette
		palettePreviewComponent = new PalettePreviewComponent(350, 20, palette);
		// JSlider for color values :
		jslRedComponent = new ColorSlider(JSlider.HORIZONTAL, 0, 255,
				Color.Component.RED);
		jslGreenComponent = new ColorSlider(JSlider.HORIZONTAL, 0, 255,
				Color.Component.GREEN);
		jslBlueComponent = new ColorSlider(JSlider.HORIZONTAL, 0, 255,
				Color.Component.BLUE);

		// JLabel for slider color and values :
		jlaRedComponent = new JLabel("Red : " + jslRedComponent.getValue(),
				JLabel.CENTER);
		jlaGreenComponent = new JLabel("Green : "
				+ jslGreenComponent.getValue(), JLabel.CENTER);
		jlaBlueComponent = new JLabel("Blue : " + jslBlueComponent.getValue(),
				JLabel.CENTER);

		// Adds an observer that notifies the palette component when the index
		// of
		// highlight changes :
		addSelectedColorIndexObserver(new SelectedColorIndexObserver() {
			@Override
			public void onSelectedColorIndexChange() {
				// Enable the 'up' button when the index is not 0.
				jbuColorListUp.setEnabled(selectedColorIndex != 0);
				// Enable the 'down' button when the selection is not on the
				// last item in the list.
				jbuColorListDown.setEnabled(selectedColorIndex != palette
						.getColors().size() - 1);
				// Update values of sliders.
				updateColorSliders(palette.getColor(selectedColorIndex));
				// Set new color to selectedPreviewComponent
				colorSelectedPreviewComponent.setColor(palette
						.getColor(selectedColorIndex));
				setPreviewColor(palette.getColor(getSelectedColorIndex()));
			}
		});

		// Observer that changes the color of the preview component when the
		// preview color changes
		addPreviewColorObserver(new PreviewColorObserver() {
			@Override
			public void onPreviewColorChange() {
				// Set new color to previewComponent.
				colorPreviewComponent.setColor(getPreviewColor());
			}
		});

		// Button used to remove/add/move up/move down/replace the current color
		// from the list
		jbuRemColor = new ColorListEditButton("Remove",
				ColorListEditButtonFunction.REMOVE);
		jbuAddColor = new ColorListEditButton("Add",
				ColorListEditButtonFunction.ADD);
		jbuColorListUp = new ColorListEditButton("↑",
				ColorListEditButtonFunction.UP);
		jbuColorListDown = new ColorListEditButton("↓",
				ColorListEditButtonFunction.DOWN);
		jbuColorListReplace = new ColorListEditButton("Replace",
				ColorListEditButtonFunction.REPLACE);
		// Button used to get a random palette
		jbuColorListRandom = new ColorListEditButton("Random",
				ColorListEditButtonFunction.RANDOM);
		// The button to up a color isn't enabled if the selected index is 0
		jbuColorListUp.setEnabled(selectedColorIndex != 0);
		// and the one to down a color isn't if we are on the last row.
		jbuColorListDown.setEnabled(selectedColorIndex != palette.getColors()
				.size() - 1);

		// Add component in general label.
		jpaEditColor.add(palettePreviewComponent, BorderLayout.PAGE_START);
		jpaEditColor.add(jpaEditColorList, BorderLayout.CENTER);
		jpaEditColor.add(jpaEditColorChange, BorderLayout.LINE_END);

		// Here is what our GroupLayout should look like :
		JComponent[][] colorEditComponents = {
				{ jlaRedComponent, jslRedComponent },
				{ jlaGreenComponent, jslGreenComponent },
				{ jlaBlueComponent, jslBlueComponent },
				{ colorSelectedPreviewComponent, colorPreviewComponent },
				{ jbuColorListReplace, jbuColorListRandom } };

		// Set layout with our methods CreateGroupLayout
		jpaEditColorChange.setLayout(createGroupLayout(jpaEditColorChange,
				colorEditComponents, groupLayoutDisposition.COLUMNS_OF_ONE,
				GroupLayout.Alignment.LEADING));

		// Add button where they belongs
		jpaEditColorListButtonsH.add(jbuAddColor);
		jpaEditColorListButtonsH.add(jbuRemColor);
		jpaEditColorListButtonsV.add(jbuColorListUp);
		jpaEditColorListButtonsV.add(jbuColorListDown);
		// Add group to list layout
		jpaEditColorList.add(jpaEditColorListButtonsV, BorderLayout.LINE_END);
		jpaEditColorList.add(jpaEditColorListButtonsH, BorderLayout.PAGE_END);
		jpaEditColorList.add(jspColorList, BorderLayout.CENTER);

		/*
		 * STEP 8 (BONUS) : DENSITY
		 */

		// We create a slider from 1 to 100 to chose the density :
		jslDensity = new JSlider(1, 100, density);
		jpaEditDensity.add(jslDensity);

		jbuDensity = new JButton("Recompute");
		jbuDensity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Update density in fractale
				setDensity(jslDensity.getValue());
			}
		});

		// When we change the value of the density, it shows the value on the
		// border
		jslDensity.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// As we show the density in the title, we update the title of
				// our JPanel.
				jpaEditDensity.setBorder(BorderFactory
						.createTitledBorder("Density : "
								+ jslDensity.getValue()));
			}
		});

		// Finally we add the button at the end of the line.
		jpaEditDensity.add(jbuDensity, BorderLayout.LINE_END);

		/*
		 * STEP 9 (BONUS) : MENU BAR
		 */

		// Creating a menu bar and setting it to the frame :
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		// Adding all menus and menuItems :
		// 1) File
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		// - File > Exit
		JMenuItem menuFileExit = new JMenuItem("Exit");
		menuFile.add(menuFileExit);
		// exit actionListener
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// Accelerator ALT + F4
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				KeyEvent.VK_ALT));

		// 2) Display
		JMenu menuDisplay = new JMenu("Display");
		menuBar.add(menuDisplay);
		// - Display > Fractal
		JCheckBoxMenuItem menuDisplayFractal = new JComponentVisibilityCheckBoxMenuItem(
				jpaGraphicsFractal, "Fractal", true);
		menuDisplay.add(menuDisplayFractal);
		// - Display > Transformations
		JCheckBoxMenuItem menuDisplayTransformations = new JComponentVisibilityCheckBoxMenuItem(
				jpaGraphicsTransform, "Transformations", true);
		menuDisplay.add(menuDisplayTransformations);
		// - Display > Transformation List
		JCheckBoxMenuItem menuDisplayListEdit = new JComponentVisibilityCheckBoxMenuItem(
				jpaEditList, "Transformation Options", true);
		menuDisplay.add(menuDisplayListEdit);
		// - Display > Transformation Options
		JCheckBoxMenuItem menuDisplayTransformEdit = new JComponentVisibilityCheckBoxMenuItem(
				jpaEditValues, "Transformation Options", true);
		menuDisplay.add(menuDisplayTransformEdit);
		// - Display > Frame Edit
		JCheckBoxMenuItem menuDisplayFrameEdit = new JComponentVisibilityCheckBoxMenuItem(
				jpaEditFrame, "Frame Options", true);
		menuDisplay.add(menuDisplayFrameEdit);
		// - Display > Palette Edit
		JCheckBoxMenuItem menuDisplayColorEdit = new JComponentVisibilityCheckBoxMenuItem(
				jpaEditColor, "Palette Options", true);
		menuDisplay.add(menuDisplayColorEdit);
		// - Display > Density
		JCheckBoxMenuItem menuDisplayDensityEdit = new JComponentVisibilityCheckBoxMenuItem(
				jpaEditDensity, "Density", true);
		menuDisplay.add(menuDisplayDensityEdit);

		// - Project
		JMenu menuProject = new JMenu("Project");
		menuBar.add(menuProject);
		// - Project > Web Page
		JMenuItem jmiWebPage = new JMenuItem("Web Page");
		jmiWebPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				try {
					Desktop d = Desktop.getDesktop();
					// Goes to the web page of the project
					d.browse(new URI("http://lampwww.epfl.ch/~schinz/13/pti/"));
				} catch (URISyntaxException e) {
				} catch (IOException e) {
				}
			}
		});
		menuProject.add(jmiWebPage);

		/*
		 * FINAL STEP : PACKING AND SETVISIBLE
		 */

		// Sets frame.pack and setVisible()
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Update the value of all Textfields corresponding to a Flame Variation.
	 */
	private void updateFlameTextFields() {
		// There should be as many textfields as Variations.
		for (int i = 0; i < flameTextFields.length; i++) {
			flameTextFields[i].setValue(builder.variationWeight(
					selectedTransformationIndex,
					Variation.ALL_VARIATIONS.get(i)));
		}
	}

	/**
	 * Update the value of all Sliders used to edit colors.
	 * 
	 * @param color
	 *            current color shown on sliders
	 */
	private void updateColorSliders(Color color) {
		jslRedComponent.updateValue(color);
		jslGreenComponent.updateValue(color);
		jslBlueComponent.updateValue(color);
	}

	/**
	 * Update the value of all Textfields used to edit the Frame.
	 */
	private void updateFrameTextFields() {
		jtfFrameCenterY.setValue(fractalFrame.center().y());
		jtfFrameCenterX.setValue(fractalFrame.center().x());
		jtfFrameWidth.setValue(fractalFrame.width());
		jtfFrameHeight.setValue(fractalFrame.height());
	}

	/**
	 * Enum used to know if we add Gap to group layout.
	 */
	private enum groupLayoutDisposition {
		NO_GAP, COLUMNS_OF_TWO, COLUMNS_OF_ONE
	};

	/**
	 * This method transforms a table of JComponents into a GroupLayout Its only
	 * use is to simplify the code inside this class.
	 * 
	 * @param panel
	 *            - The JPanel you want to put the grouplayout on.
	 * @param components
	 *            - The table of JComponents that needs to be transformed into a
	 *            layout.
	 * @param isPreferedGapNeeded
	 *            - True is you want to introduce a vertical gap every 2
	 *            components.
	 * @return The GroupLayout containing every components at the same place as
	 *         in the table.
	 */
	private GroupLayout createGroupLayout(JPanel panel,
			JComponent[][] components, groupLayoutDisposition d,
			GroupLayout.Alignment rowAlignment) {
		/*
		 * As we use Table instead of collections, components[i].length is the
		 * same For every i and we don't have to worry about the fact that it is
		 * possibly Not a rectangular table. Plus, the fields that are not
		 * initialized have the value none.
		 */

		// Creation of the GroupLayout :
		GroupLayout newGroupLayout = new GroupLayout(panel);

		// Creation of 2 sequential macro-groups :
		SequentialGroup sHorizontalGroup = newGroupLayout
				.createSequentialGroup();
		SequentialGroup sVerticalGroup = newGroupLayout.createSequentialGroup();

		// We define tableHeight and tableWidth the sizes of the component table
		int tableHeight = components.length;
		int tableWidth = components[0].length;

		// Creation of 2 tables containing each vertical and horizontal groups
		ParallelGroup[] pVerticalGroups = new ParallelGroup[tableHeight];
		ParallelGroup[] pHorizontalGroups = new ParallelGroup[tableWidth];

		for (int i = 0; i < tableHeight; i++) {
			// For every column, we create i lines :
			pVerticalGroups[i] = newGroupLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE);
			// And we add the j elements of each line group :
			for (int j = 0; j < tableWidth; j++) {
				if (components[i][j] != null)
					pVerticalGroups[i].addComponent(components[i][j]);
			}
			// Finally we add the new group to the macro group :
			sVerticalGroup.addGroup(pVerticalGroups[i]);
		}

		for (int j = 0; j < tableWidth; j++) {
			// For every line, we create j columns : (First one need a special
			// alignment)
			pHorizontalGroups[j] = newGroupLayout
					.createParallelGroup(rowAlignment);
			// And we add the i elements of each column group :
			for (int i = 0; i < tableHeight; i++) {
				if (components[i][j] != null)
					pHorizontalGroups[j].addComponent(components[i][j]);
			}
			// if a gap is needed (numbers of elements added is even but not 0),
			// we add it :
			if (d.equals(groupLayoutDisposition.COLUMNS_OF_TWO) && j % 2 == 0
					& j != 0)
				sHorizontalGroup.addPreferredGap(ComponentPlacement.UNRELATED);
			else if (d.equals(groupLayoutDisposition.COLUMNS_OF_ONE) && j != 0)
				sHorizontalGroup.addPreferredGap(ComponentPlacement.UNRELATED);
			// Finally we add the new group to the macro group
			sHorizontalGroup.addGroup(pHorizontalGroups[j]);
		}

		// Adding both sequential groups to the layout :
		newGroupLayout.setVerticalGroup(sVerticalGroup);
		newGroupLayout.setHorizontalGroup(sHorizontalGroup);

		return newGroupLayout;
	}

	/**
	 * Index of the transformation currently being selected.
	 * 
	 * @return Index of the currently selected transformation
	 */
	public int getSelectedTransformationIndex() {
		return selectedTransformationIndex;
	}

	/**
	 * Changes the value of the attribute selectedTransformationIndex. Notifies
	 * all observers when done.
	 * 
	 * @param newIndex
	 *            - index of the transformation to be selected.
	 */
	public void setSelectedTransformationIndex(int newIndex) {
		selectedTransformationIndex = newIndex;

		// Notify all observers on any change of selectedTransformationIndex.
		Iterator<SelectedTransformationIndexObserver> it = selectedTransformationIndexObservers
				.iterator();
		while (it.hasNext()) {
			it.next().onSelectedTransformationIndexChange();
		}
	}

	/**
	 * Adds an observer on the attribute selectedTransformationIndex. Gets
	 * notified when it changes via the method setSelectedTransformationIndex.
	 * 
	 * @param newObserver
	 *            - observer to be added to the list.
	 */
	public void addSelectedTransformationIndexObserver(
			SelectedTransformationIndexObserver newObserver) {
		selectedTransformationIndexObservers.add(newObserver);
	}

	/**
	 * Removes an old observer on the attribute selectedTransformationIndex.
	 * 
	 * @param oldObserver
	 *            - observer to be removed from the list.
	 */
	public void removeSelectedTransformationIndexObserver(
			SelectedTransformationIndexObserver oldObserver) {
		selectedTransformationIndexObservers.remove(oldObserver);
	}

	/**
	 * Density of the current flame fractal for the next repaints.
	 * 
	 * @return Index of the currently selected transformation
	 */
	public int getDensity() {
		return density;
	}

	/**
	 * Changes the value of the attribute selectedTransformationIndex. Notifies
	 * all observers when done.
	 * 
	 * @param newIndex
	 *            - index of the transformation to be selected.
	 */
	public void setDensity(int newDensity) {
		density = newDensity;

		// Notify all observers on any change of selectedTransformationIndex.
		Iterator<DensityObserver> it = densityObservers.iterator();
		while (it.hasNext()) {
			it.next().onDensityChange();
		}
	}

	/**
	 * Adds an observer on the attribute selectedTransformationIndex. Gets
	 * notified when it changes via the method setSelectedTransformationIndex.
	 * 
	 * @param newObserver
	 *            - observer to be added to the list.
	 */
	public void addDensityObserver(DensityObserver newObserver) {
		densityObservers.add(newObserver);
	}

	/**
	 * Removes an old observer on the attribute selectedTransformationIndex.
	 * 
	 * @param oldObserver
	 *            - observer to be removed from the list.
	 */
	public void removeDensityObserver(DensityObserver oldObserver) {
		densityObservers.remove(oldObserver);
	}

	/**
	 * Index of the color currently being selected.
	 * 
	 * @return Index of the currently selected color
	 */
	public int getSelectedColorIndex() {
		return selectedColorIndex;
	}

	/**
	 * Changes the value of the attribute selectedColorIndex. Notifies all
	 * observers when done.
	 * 
	 * @param newIndex
	 *            - index of the color to be selected.
	 */
	public void setSelectedColorIndex(int newIndex) {
		selectedColorIndex = newIndex;

		// Notify all observers on any change of selectedColorIndex.
		Iterator<SelectedColorIndexObserver> it = selectedColorIndexObservers
				.iterator();
		while (it.hasNext()) {
			it.next().onSelectedColorIndexChange();
		}
	}

	/**
	 * Adds an observer on the attribute selectedColorIndex. Gets notified when
	 * it changes via the method setSelectedColorIndex.
	 * 
	 * @param newObserver
	 *            - observer to be added to the list.
	 */
	public void addSelectedColorIndexObserver(
			SelectedColorIndexObserver newObserver) {
		selectedColorIndexObservers.add(newObserver);
	}

	/**
	 * Removes an old observer on the attribute selectedColorIndex.
	 * 
	 * @param oldObserver
	 *            - observer to be removed from the list.
	 */
	public void removeSelectedColorIndexObserver(
			SelectedColorIndexObserver oldObserver) {
		selectedColorIndexObservers.remove(oldObserver);
	}

	/**
	 * Preview of the color currently shown.
	 * 
	 * @return Index of the currently preview color
	 */
	public Color getPreviewColor() {
		return previewColor;
	}

	/**
	 * Changes the value of the attribute previewColor. Notifies all observers
	 * when done.
	 * 
	 * @param newColor
	 *            - color to be replaced.
	 */
	public void setPreviewColor(Color newColor) {
		previewColor = newColor;

		// Notify all observers on any change of previewColor.
		Iterator<PreviewColorObserver> it = previewColorObservers.iterator();
		while (it.hasNext()) {
			it.next().onPreviewColorChange();
		}
	}

	/**
	 * Adds an observer on the attribute previewColor.
	 * 
	 * @param newObserver
	 *            - observer to be added to the list.
	 */
	public void addPreviewColorObserver(PreviewColorObserver newObserver) {
		previewColorObservers.add(newObserver);
	}

	/**
	 * Removes an old observer on the attribute previewColor.
	 * 
	 * @param oldObserver
	 *            - observer to be removed from the list.
	 */
	public void removeSelectedColorIndexObserver(
			PreviewColorObserver oldObserver) {
		previewColorObservers.remove(oldObserver);
	}

	/**
	 * Returns palette in use.
	 * 
	 * @return the palette in use.
	 */
	public Palette getPalette() {
		return palette;
	}

	/**
	 * Models an AbstractListModel that contains Flame Transformations.
	 */
	@SuppressWarnings("serial")
	private class TransformationsListModel extends AbstractListModel<String> {

		@Override
		public String getElementAt(int index) {
			return "Transformation #" + (index + 1);
		}

		@Override
		public int getSize() {
			return builder.transformationCount();
		}

		/**
		 * Adds a flame transformation with all weights to 0 except Linear (1),
		 * and which has the affine part Affine.IDENTITY. (Notifies observers).
		 */
		public void addTransformation() {
			double[] weights = { 1, 0, 0, 0, 0, 0 };
			builder.addTransformation(new FlameTransformation(
					AffineTransformation.IDENTITY, weights));

			// Send information to the listeners
			fireIntervalAdded(this, getSize() - 1, getSize() - 1);
		}

		/**
		 * Removes a transformation from the list. (Notifies observers)
		 * 
		 * @param index
		 *            - the index of the transformation to be removed.
		 */
		public void removeTransformation(int index) {
			builder.removeTransformation(index);

			// Send information to the listeners
			fireIntervalRemoved(this, index + 1, index + 1);
		}
	}

	@SuppressWarnings("serial")
	private class ColorsListModel extends AbstractListModel<String> {

		@Override
		public String getElementAt(int index) {
			return "Color #" + (index + 1);
		}

		@Override
		public int getSize() {
			return palette.getColors().size();
		}

		/**
		 * Adds color.WHITE ( new Color(1,1,1) ) to the palette. (Notifies
		 * Observers)
		 */
		public void addColor() {
			palette.addColor(selectedColorIndex, Color.WHITE);

			// Send information to the listeners
			fireIntervalAdded(this, getSize() - 1, getSize() - 1);
		}

		/**
		 * Removes a color from the list. (Notifies observers)
		 * 
		 * @param index
		 *            - the index of the color to be removed.
		 */
		public void removeColor(int index) {
			palette.removeColor(index);

			// Send information to the listeners
			fireIntervalRemoved(this, index + 1, index + 1);
		}

		/**
		 * Invert elements of index1 and index2
		 * 
		 * @param index1
		 *            index of the color that will go up or down.
		 * @param index2
		 *            index of the color that will go up or down.
		 */
		public void invertColor(int index1, int index2) {
			palette.invertColor(index1, index2);
		}
	}

	/**
	 * This interface describes the behaviour of any Observer that must be
	 * notified when selectedTransformationIndex changes.
	 */
	interface SelectedTransformationIndexObserver {
		public void onSelectedTransformationIndexChange();
	}

	/**
	 * This interface describes the behaviour of any Observer that must be
	 * notified when Density changes.
	 */
	interface DensityObserver {
		public void onDensityChange();
	}

	/**
	 * This interface describes the behaviour of any Observer that must be
	 * notified when selectedColorIndex changes.
	 */
	interface SelectedColorIndexObserver {
		public void onSelectedColorIndexChange();
	}

	/**
	 * This interface describes the behaviour of any Observer that must be
	 * notified when PreviewColor changes.
	 */
	interface PreviewColorObserver {
		public void onPreviewColorChange();
	}

	/**
	 * Extension of JButton which on click takes the value of the
	 * JFormattedTextField and creates an affineTransformation with it, that it
	 * then compose with the current transformation.
	 */
	@SuppressWarnings("serial")
	private class AffineEditButton extends JButton {

		public AffineEditButton(String value, final JFormattedTextField source,
				final AffineTransformation.affineCreator c) {
			super(value);

			// Adding an ActionListener to the button which purpose is to modify
			// the value
			// of the currently selected transformation, using the textfield and
			// the affinecreator :
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					// Defines the new transformation in 3 steps :
					// old.composewith(change) = new
					AffineTransformation oldTransformation = builder
							.affineTransformation(selectedTransformationIndex);
					AffineTransformation changeInTransformation = c
							.createTransformationWithValue(Double
									.parseDouble(source.getText()));
					AffineTransformation newTransformation = oldTransformation
							.composeWith(changeInTransformation);
					// Sets the new transformation at the place of the old one.
					builder.setAffineTransformation(
							selectedTransformationIndex, newTransformation);
				}
			});
		}
	}

	/**
	 * Extension of JButton which on click zoom or dezoom the fractalframe.
	 */
	@SuppressWarnings("serial")
	private class ZoomButton extends JButton {

		private double r;

		public ZoomButton(String value, double ratio) {
			super(value);
			r = ratio;
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Use function to zoom in rectangle.
					fractalFrame.zoom(r);
				}
			});
		}
	}

	/**
	 * Verify if the value entered is bigger than zero, if not the old value is
	 * set back.
	 */
	private class higherThanZeroInputVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			// We create a copy of input, assuming it is a JFormattedTextField.
			JFormattedTextField newInput = (JFormattedTextField) input;

			// We try here to convert the text into a double
			try {
				// If there is no problem we can compare it to 0.
				if (Double.parseDouble(newInput.getText()) <= 0) {
					newInput.setValue(newInput.getValue());
				}
			} catch (Exception e) {
				// If there is a problem in the conversion, the content is
				// normally
				// not a number so we also replace it with the last value.
				newInput.setValue(newInput.getValue());
			}
			return true;
		}
	}

	/**
	 * Verify if the value entered is different of zero, if not the old value is
	 * set back.
	 */
	private class noZeroInputVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			// We create a copy of input, assuming it is a JFormattedTextField.
			JFormattedTextField newInput = (JFormattedTextField) input;
			// We try here to convert the text into a double

			try {
				// If there is no problem we can compare it to 0.
				if (Double.parseDouble(newInput.getText()) == 0) {
					newInput.setValue(newInput.getValue());
				}
			} catch (Exception e) {
				// If there is a problem in the conversion, the content is
				// normally
				// not a number so we also replace it with the last value.
				newInput.setValue(newInput.getValue());
			}
			return true;
		}
	}

	/**
	 * Changes the visibility of a JComponent
	 * 
	 * @param component
	 *            Component you want to make visible/not visible
	 */
	private void toggleVisibility(JComponent component) {
		if (!componentVisibility.containsKey(component))
			throw new IllegalArgumentException(
					"Component not in the visibility map");

		if (componentVisibility.get(component)) {
			// If component is visible we set it not visible
			component.setVisible(false);
			componentVisibility.put(component, new Boolean(false));
		} else {
			// If component is not Visible we set it visible
			component.setVisible(true);
			componentVisibility.put(component, new Boolean(true));
		}
	}

	@SuppressWarnings("serial")
	private class JComponentVisibilityCheckBoxMenuItem extends
			JCheckBoxMenuItem {

		public JComponentVisibilityCheckBoxMenuItem(final JComponent component,
				String name, boolean value) {
			super(name, value);

			componentVisibility.put(component, value);

			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					toggleVisibility(component);
				}
			});
		}
	}

	/**
	 * Extension of JSlider used to easily Sliders of colors using enum
	 * Component in Color.
	 */
	@SuppressWarnings("serial")
	private class ColorSlider extends JSlider {

		Color.Component component;

		public ColorSlider(int orientation, int min, int max,
				Color.Component component) {
			super(min, max);
			this.component = component;
			this.updateValue(palette.getColor(selectedColorIndex));

			this.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// Update colorPreviewComponent and Label where value of
					// each slider is shown.
					updatePreviewColor();
					updateLabelColor();
				}
			});
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(10, 30);
		}

		/**
		 * Update the value of sliders when a new color is selected, added or
		 * remove.
		 * 
		 * @param color
		 *            - the color that must appear on sliders
		 */
		public void updateValue(Color color) {
			double newValue = 0;
			switch (component) {
			case RED:
				newValue = color.red();
				break;
			case GREEN:
				newValue = color.green();
				break;
			case BLUE:
				newValue = color.blue();
				break;
			}

			this.setValue((int) Math.floor(newValue
					* (double) this.getMaximum()));
		}

		/**
		 * Update colorPreviewComponent when any of the sliders change.
		 */
		private void updatePreviewColor() {
			switch (component) {
			case RED:
				setPreviewColor(new Color(getFractionOfMaxValue(),
						previewColor.green(), previewColor.blue()));
				return;
			case GREEN:
				setPreviewColor(new Color(previewColor.red(),
						getFractionOfMaxValue(), previewColor.blue()));
				return;
			case BLUE:
				setPreviewColor(new Color(previewColor.red(),
						previewColor.green(), getFractionOfMaxValue()));
				return;
			}
		}

		/**
		 * Update value of the label when any of the sliders change.
		 */
		private void updateLabelColor() {
			switch (component) {
			case RED:
				jlaRedComponent.setText("Red : " + jslRedComponent.getValue());
				return;
			case GREEN:
				jlaGreenComponent.setText("Green : "
						+ jslGreenComponent.getValue());
				return;
			case BLUE:
				jlaBlueComponent.setText("Blue : "
						+ jslBlueComponent.getValue());
				return;
			}
		}

		/**
		 * Returns the value between 0 and 1 of the color selected.
		 * 
		 * @return value between 0 and 1 of color selected
		 */
		private double getFractionOfMaxValue() {
			return (double) this.getValue() / (double) this.getMaximum();
		}

	}

	/**
	 * Enum of functions of the button used to edit Color List.
	 */
	private enum ColorListEditButtonFunction {
		ADD, REMOVE, UP, DOWN, REPLACE, RANDOM
	};

	/**
	 * Extension of JButton that on click does the right function on
	 * palette/Color list.
	 */
	@SuppressWarnings("serial")
	private class ColorListEditButton extends JButton {

		public ColorListEditButton(String value,
				ColorListEditButtonFunction function) {
			super(value);

			ActionListener a = null;
			switch (function) {

			case ADD:
				a = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Add the new color and select it.
						colorsListModel.addColor();
						jliColors.setSelectedIndex(selectedColorIndex);
						// After adding, the size should be at least 2.
						assert (colorsListModel.getSize() >= 2);
						colorSelectedPreviewComponent.setColor(palette
								.getColor(selectedColorIndex));
						// When we add, it automatically enables the remove
						// button.
						jbuRemColor.setEnabled(palette.getColors().size() >= 2);
						jbuColorListDown
								.setEnabled(selectedColorIndex != palette
										.getColors().size() - 1);
					}
				};
				break;
			case REMOVE:
				a = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Decide the item selected after the removal. same
						// index if not
						// last one (in this case we decrement it).
						int nextSelection = (getSelectedColorIndex() == colorsListModel
								.getSize() - 1) ? getSelectedColorIndex() - 1
								: getSelectedColorIndex();
						jliColors.setSelectedIndex(nextSelection);
						// Removes color in the list.
						colorsListModel.removeColor(getSelectedColorIndex());
						// Update colorSelectedPreviewComponent with new
						// selected color.
						colorSelectedPreviewComponent.setColor(palette
								.getColor(selectedColorIndex));
						// Decide wether we can use it again or not (list should
						// have a
						// length >=2 to remove).
						jbuRemColor.setEnabled(colorsListModel.getSize() > 2);
						// Enable Down Button if we don't remove the last item.
						jbuColorListDown
								.setEnabled(selectedColorIndex != palette
										.getColors().size() - 1);
						// Enable Up Button if we don't remove the first item.
						jbuColorListUp.setEnabled(selectedColorIndex != 0);
					}
				};
			case UP:
				a = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// We switch color selected with the one under
						colorsListModel.invertColor(selectedColorIndex - 1,
								selectedColorIndex);
						// We change the selectedColorIndex to follow the color
						// selected
						jliColors.setSelectedIndex(selectedColorIndex - 1);
						// Enable Up Button if we don't remove the first item.
						jbuColorListUp.setEnabled(selectedColorIndex != 0);
						// Enable Down Button.
						jbuColorListDown.setEnabled(true);
					}
				};
				break;
			case DOWN:
				a = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// We switch color selected with the one under
						colorsListModel.invertColor(selectedColorIndex,
								selectedColorIndex + 1);
						// We change the selectedColorIndex to follow the color
						// selected
						jliColors.setSelectedIndex(selectedColorIndex + 1);
						// Enable Down Button if we don't remove the last item.
						jbuColorListDown
								.setEnabled(selectedColorIndex != palette
										.getColors().size() - 1);
						// Enable Up Button.
						jbuColorListUp.setEnabled(true);
					}
				};
				break;
			case REPLACE:
				a = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// If SelectedColor = previewColor we don't replace
						// anything,
						if (palette.getColor(selectedColorIndex) != getPreviewColor()) {
							// else we set new color into palette.
							palette.setColor(selectedColorIndex,
									getPreviewColor());
							// and the new color is set on the
							// colorSelectedPreviewComponent.
							colorSelectedPreviewComponent.setColor(palette
									.getColor(selectedColorIndex));
						}
					}
				};
				break;
			case RANDOM:
				a = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Sets random colors into the palette.
						palette.setRandomColors();
						// Update colorSelectedPreviewComponent with new
						// selected color.
						colorSelectedPreviewComponent.setColor(palette
								.getColor(selectedColorIndex));
						// Update PreviewComponent with new selected color.
						colorPreviewComponent.setColor(palette
								.getColor(selectedColorIndex));
						// Update slider with values of the new selected color.
						updateColorSliders(palette.getColor(selectedColorIndex));
					}
				};
			}

			this.addActionListener(a);
		}
	}

	/**
	 * Enum of funcion a frameEditTextField can have.
	 */
	private enum FrameEditTextFieldFunction {
		CHANGE_WIDTH, CHANGE_HEIGHT, CHANGE_CENTERX, CHANGE_CENTERY
	};

	/**
	 * Extension of JFormatted that on click does the right function on
	 * fractaleFrame and frame.
	 */

	@SuppressWarnings("serial")
	private class FrameEditTextField extends JFormattedTextField {
		public FrameEditTextField(DecimalFormat df,
				FrameEditTextFieldFunction function) {
			super(df);
			switch (function) {

			case CHANGE_WIDTH:
				this.addPropertyChangeListener("value",
						new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent arg0) {
								// Sets new width to the frame/fractaleFrame.
								fractalFrame.setWidth(Double
										.parseDouble(jtfFrameWidth.getText()));
							}
						});
				break;
			case CHANGE_HEIGHT:
				this.addPropertyChangeListener("value",
						new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent arg0) {
								// Sets new height to the frame/fractaleFrame.
								fractalFrame.setHeight(Double
										.parseDouble(jtfFrameHeight.getText()));
							}
						});
				break;
			case CHANGE_CENTERX:
				this.addPropertyChangeListener("value",
						new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent arg0) {
								// Sets new X to the center of the
								// frame/fractaleFrame.
								fractalFrame.setCenter(new Point(
										Double.parseDouble(jtfFrameCenterX
												.getText()), fractalFrame
												.center().y()));
							}
						});
				break;
			case CHANGE_CENTERY:
				this.addPropertyChangeListener("value",
						new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent arg0) {
								// Sets new Y to the center of the
								// frame/fractaleFrame.
								fractalFrame.setCenter(new Point(fractalFrame
										.center().x(), Double
										.parseDouble(jtfFrameCenterY.getText())));
							}
						});
				break;
			}
		}
	}

	/**
	 * Enum of functions a ListEditButton can have.
	 */
	public enum listEditButtonFunction {
		ADD, REMOVE
	};

	/**
	 * Extension of JButton that add or removes items of the JList
	 * jliTransformation.
	 */
	@SuppressWarnings("serial")
	private class ListEditButton extends JButton {

		public ListEditButton(String value, listEditButtonFunction f) {

			super(value);
			ActionListener l = null;
			switch (f) {
			case ADD:
				l = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Add the new transformation and select it.
						listModel.addTransformation();
						jliTransformations
								.setSelectedIndex(listModel.getSize() - 1);
						// After adding, the size should be at least 2.
						assert (listModel.getSize() >= 2);
						// When we add, it automatically enables the remove
						// button.
						jbuRemTrans.setEnabled(true);
					}
				};
				break;
			case REMOVE:
				l = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// Decide the item selected after the removal. same
						// index if not last one (in this case we decrement it).
						int nextSelection = (getSelectedTransformationIndex() == listModel
								.getSize() - 1) ? getSelectedTransformationIndex() - 1
								: getSelectedTransformationIndex();
						listModel
								.removeTransformation(getSelectedTransformationIndex());
						jliTransformations.setSelectedIndex(nextSelection);
						// Decide wether we can use it again or not (list should
						// have a length >=2 to remove).
						jbuRemTrans.setEnabled(listModel.getSize() >= 2);
					}
				};
				break;
			}

			this.addActionListener(l);

		}
	}
}
