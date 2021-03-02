package de.topobyte.jeography.tools.bboxaction;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.viewer.util.Borders;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.mapocado.styles.classes.ClassFileHandler;
import de.topobyte.mapocado.styles.classes.ClassFileReader;
import de.topobyte.mapocado.styles.labels.LabelFileHandler;
import de.topobyte.mapocado.styles.labels.LabelFileReader;
import de.topobyte.mapocado.swing.rendering.MapImageCreator;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.swing.util.ComponentPanel;
import de.topobyte.swing.util.DocumentAdapter;
import de.topobyte.system.utils.SystemPaths;

public class Test
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 500);

		Test test = new Test();

		frame.setContentPane(test.panel);
		frame.setVisible(true);
	}

	private BBox boundingBox = new BBox(13.38529, 52.51434, 13.40641, 52.52259);
	private Path fileMapfile = null;
	private Path fileStyle = null;
	private int width = 700;
	private int height = 500;

	private JPanel panel;
	private SelectBboxAction selectBboxAction;
	private SelectFileAction selectFileActionStylefile;
	private SelectFileAction selectFileActionMapfile;
	private ComponentPanel<JTextField> fieldWidth, fieldHeight;

	public Test()
	{
		panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;

		JPanel panelBbox = createBboxPanel();
		panelBbox.setBorder(BorderFactory.createTitledBorder("Bounding box"));
		panel.add(panelBbox, c);

		JPanel panelStyle = createStylePanel();
		panelStyle.setBorder(BorderFactory.createTitledBorder("Map style"));
		panel.add(panelStyle, c);

		JPanel panelMapfile = createMapfilePanel();
		panelMapfile.setBorder(BorderFactory.createTitledBorder("Map file"));
		panel.add(panelMapfile, c);

		JPanel panelOptions = createOptionsPanel();
		panelOptions.setBorder(BorderFactory.createTitledBorder("Options"));
		panel.add(panelOptions, c);

		JButton button = new JButton("Go");
		panel.add(button, c);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				try {
					go();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidBundleException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel createBboxPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Select a bounding box:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		final BboxPanel bboxPanel = new BboxPanel(boundingBox);
		setLabelBorder(bboxPanel, false);
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(bboxPanel, c);

		selectBboxAction = new SelectBboxAction(boundingBox, panel) {

			private static final long serialVersionUID = 1L;

			@Override
			public void bboxSelected(BBox bbox)
			{
				boundingBox = bbox;
				bboxPanel.setBoundingBox(bbox);
				selectBboxAction.setBbox(bbox);
			}
		};

		PanelButton button = new PanelButton(selectBboxAction);
		setButtonBorder(button);
		c.gridx = 1;
		c.weightx = 0.0;
		panel.add(button, c);

		return panel;
	}

	private JPanel createStylePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Select a map style:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		fileStyle = SystemPaths.HOME.resolve(
				"github/topobyte/mapocado/config/citymaps/rendertheme-v2/styles/style-default.zip");
		final FilePanel filePanel = new FilePanel(fileStyle.toFile());
		setLabelBorder(filePanel, false);

		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(filePanel, c);

		selectFileActionStylefile = new SelectFileAction(fileStyle.toFile(),
				panel) {

			private static final long serialVersionUID = 1L;

			@Override
			public void fileSelected(File file)
			{
				fileStyle = file.toPath();
				filePanel.setFile(file);
				selectFileActionStylefile.setFile(file);
			}

		};

		PanelButton button = new PanelButton(selectFileActionStylefile);
		setButtonBorder(button);
		c.gridx = 1;
		c.weightx = 0.0;
		panel.add(button, c);

		return panel;
	}

	private JPanel createMapfilePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Select a map file:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		fileMapfile = Paths.get(
				"/data/oxygen/hafnium/mapfiles/planet-191007/germany/Berlin.xmap");
		final FilePanel filePanel = new FilePanel(fileMapfile.toFile());
		setLabelBorder(filePanel, false);
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(filePanel, c);

		selectFileActionMapfile = new SelectFileAction(fileMapfile.toFile(),
				panel) {

			private static final long serialVersionUID = 1L;

			@Override
			public void fileSelected(File file)
			{
				fileMapfile = file.toPath();
				filePanel.setFile(file);
				selectFileActionMapfile.setFile(file);
			}

		};

		PanelButton button = new PanelButton(selectFileActionMapfile);
		setButtonBorder(button);
		c.gridx = 1;
		c.weightx = 0.0;
		panel.add(button, c);

		return panel;
	}

	private JPanel createOptionsPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		JLabel label = new JLabel("Set the scale factor:");
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(label, c);

		ComponentPanel<JTextField> field = new ComponentPanel<>(
				new JTextField("1.0"));
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(field, c);

		JLabel labelWidth = new JLabel("Set the image width:");
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(labelWidth, c);

		fieldWidth = new ComponentPanel<>(new JTextField("" + width));
		c.gridy = 3;
		c.gridwidth = 1;
		panel.add(fieldWidth, c);

		JLabel labelHeight = new JLabel("Set the image height:");
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(labelHeight, c);

		fieldHeight = new ComponentPanel<>(new JTextField("" + height));
		c.gridy = 5;
		c.gridwidth = 1;
		panel.add(fieldHeight, c);

		field.getComponent().setBorder(Borders.validityBorder(true));
		fieldWidth.getComponent().setBorder(Borders.validityBorder(true));
		fieldHeight.getComponent().setBorder(Borders.validityBorder(true));

		fieldWidth.getComponent().getDocument()
				.addDocumentListener(new DocumentAdapter() {

					@Override
					public void update(DocumentEvent e)
					{
						String text = fieldWidth.getComponent().getText();
						boolean valid = true;
						try {
							width = Integer.parseInt(text);
						} catch (NumberFormatException ex) {
							valid = false;
						}
						fieldWidth.getComponent()
								.setBorder(Borders.validityBorder(valid));
					}
				});

		fieldHeight.getComponent().getDocument()
				.addDocumentListener(new DocumentAdapter() {

					@Override
					public void update(DocumentEvent e)
					{
						String text = fieldHeight.getComponent().getText();
						boolean valid = true;
						try {
							height = Integer.parseInt(text);
						} catch (NumberFormatException ex) {
							valid = false;
						}
						fieldHeight.getComponent()
								.setBorder(Borders.validityBorder(valid));
					}
				});

		return panel;
	}

	private void setButtonBorder(JComponent component)
	{
		component.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	private void setLabelBorder(JComponent component, boolean drop)
	{
		if (drop) {
			component
					.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createCompoundBorder(
									BorderFactory.createEmptyBorder(5, 5, 5, 5),
									BorderFactory.createBevelBorder(
											BevelBorder.LOWERED)),
							BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		} else {
			component
					.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createCompoundBorder(
									BorderFactory.createEmptyBorder(5, 5, 5, 5),
									BorderFactory.createEtchedBorder(
											EtchedBorder.LOWERED)),
							BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		}
	}

	public void go() throws ParserConfigurationException, SAXException,
			IOException, InvalidBundleException, ClassNotFoundException
	{
		Mapfile mapfile = MapFileAccess.open(fileMapfile.toFile());

		ConfigBundle configBundle = ConfigBundleReader
				.readConfigBundle(fileStyle.toFile());

		ClassFileHandler classes = ClassFileReader
				.read(configBundle.getClassesAsInputStream());

		LabelFileHandler labels = LabelFileReader
				.read(configBundle.getLabelsAsInputStream());

		MapRenderConfig renderConfig = new MapRenderConfig(
				classes.getBackground(), classes.getOverlayInner(),
				classes.getOverlayOuter(), classes.getOverlayGpsInner(),
				classes.getOverlayGpsOuter(), classes.getObjectClasses(),
				labels.getRules(), labels.getRuleToLabel(), configBundle);

		renderConfig
				.createSlimClasses(mapfile.getMetadata().getPoolForKeepKeys());

		MercatorImage mapImage = new MercatorImage(boundingBox, width, height);
		BBox box = mapImage.getVisibleBoundingBox();

		double scale = mapImage.getWorldSize();
		double base = Math.log(scale) / Math.log(2);
		double approxImageZoom = base - 8;
		int zoom = (int) Math.floor(approxImageZoom);

		MapImageCreator imageCreator = new MapImageCreator(mapfile,
				renderConfig);
		imageCreator.setZoom(zoom, approxImageZoom);

		final BufferedImage image = imageCreator.load(box, width, height);

		JFrame frame = new JFrame();
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g)
			{
				g.drawImage(image, 0, 0, null);
			}
		};
		frame.setContentPane(panel);
		frame.setSize(width + 20, height + 20);
		frame.setVisible(true);
	}

}
