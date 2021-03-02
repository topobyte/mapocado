package de.topobyte.mapocado.styles.ui.convert;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.collections.util.ListUtil;
import de.topobyte.mapocado.styles.convert.ChainedConverter;
import de.topobyte.mapocado.styles.convert.ColorComponentSwap;
import de.topobyte.mapocado.styles.convert.ColorConverter;
import de.topobyte.mapocado.styles.convert.ConverterAdjustLuminance;
import de.topobyte.mapocado.styles.convert.ConverterAdjustLuminanceLab;
import de.topobyte.mapocado.styles.convert.ConverterAdjustSaturation;
import de.topobyte.mapocado.styles.convert.ConverterInvertLuminance;
import de.topobyte.mapocado.styles.convert.ConverterInvertLuminanceLab;
import de.topobyte.mapocado.styles.convert.ConverterInvertRGB;
import de.topobyte.mapocado.styles.convert.ConverterLoadLuminanceLab;
import de.topobyte.mapocado.styles.convert.ConverterRotateHue;
import de.topobyte.mapocado.styles.convert.ConverterRotateHueLab;
import de.topobyte.mapocado.styles.convert.ConverterStoreLuminanceLab;
import de.topobyte.mapocado.styles.convert.ConverterSwapColorComponents;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelAdjustLuminance;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelAdjustLuminanceLab;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelAdjustSaturation;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelInvertLuminance;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelInvertLuminanceLab;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelInvertRGB;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelLoadLuminanceLab;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelRotateHue;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelRotateHueLab;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelStoreLuminanceLab;
import de.topobyte.mapocado.styles.ui.convert.filters.PanelSwapColorComponents;

public class FilterList extends JPanel
{

	private static final long serialVersionUID = -7874385015113662160L;

	// list of items
	private List<FilterItem> items = new ArrayList<>();
	// space component at the end of the panel
	private Component glue = Box.createGlue();
	// current layout insertion position
	int gridy = 0;

	public FilterList()
	{
		FilterListDestinationTransferHandler destinationTransferHandler = new FilterListDestinationTransferHandler();
		setTransferHandler(destinationTransferHandler);

		setLayout(new GridBagLayout());

		// add(FilterType.ROTATE_HUE);
		// add(FilterType.INVERT_RGB);
		// add(FilterType.INVERT_LUMINANCE);
		// add(FilterType.SWAP_COLOR_COMPONENTS);
		add(FilterType.ROTATE_HUE_LAB);
		add(FilterType.INVERT_LUMINANCE_LAB);

		// for (int i = 0; i < 10; i++) {
		// add(FilterType.ROTATE_HUE);
		// }
	}

	public ColorConverter getChainedColorConverter()
	{
		return new ChainedConverter(getConverterList());
	}

	public List<ColorConverter> getConverterList()
	{
		List<ColorConverter> converters = new ArrayList<>();
		for (FilterItem item : items) {
			converters.add(item.converter);
		}
		return converters;
	}

	// parallely maintain the list of converters as well as the components
	// within this layout.
	protected void add(FilterType filterType)
	{
		ColorConverter converter;
		JComponent component;
		switch (filterType) {
		default:
		case INVERT_RGB:
			converter = new ConverterInvertRGB();
			PanelInvertRGB invertRGB = new PanelInvertRGB();
			component = invertRGB;
			break;
		case INVERT_LUMINANCE:
			converter = new ConverterInvertLuminance();
			PanelInvertLuminance invertLuminance = new PanelInvertLuminance();
			component = invertLuminance;
			break;
		case INVERT_LUMINANCE_LAB:
			converter = new ConverterInvertLuminanceLab();
			PanelInvertLuminanceLab invertLuminanceLab = new PanelInvertLuminanceLab();
			component = invertLuminanceLab;
			break;
		case ROTATE_HUE:
			ConverterRotateHue hueConverter = new ConverterRotateHue(0.0f);
			converter = hueConverter;
			PanelRotateHue rotateColor = new PanelRotateHue(hueConverter);
			component = rotateColor;
			break;
		case ROTATE_HUE_LAB:
			ConverterRotateHueLab hueLabConverter = new ConverterRotateHueLab(
					0.0f);
			converter = hueLabConverter;
			PanelRotateHueLab rotateLabColor = new PanelRotateHueLab(
					hueLabConverter);
			component = rotateLabColor;
			break;
		case ADJUST_SATURATION:
			ConverterAdjustSaturation saturationConverter = new ConverterAdjustSaturation(
					1.0f);
			converter = saturationConverter;
			PanelAdjustSaturation adjustSaturation = new PanelAdjustSaturation(
					saturationConverter);
			component = adjustSaturation;
			break;
		case ADJUST_LUMINANCE:
			ConverterAdjustLuminance luminanceConverter = new ConverterAdjustLuminance(
					1.0f);
			converter = luminanceConverter;
			PanelAdjustLuminance adjustLuminance = new PanelAdjustLuminance(
					luminanceConverter);
			component = adjustLuminance;
			break;
		case ADJUST_LUMINANCE_LAB:
			ConverterAdjustLuminanceLab luminanceLabConverter = new ConverterAdjustLuminanceLab(
					1.0f);
			converter = luminanceLabConverter;
			PanelAdjustLuminanceLab adjustLuminanceLab = new PanelAdjustLuminanceLab(
					luminanceLabConverter);
			component = adjustLuminanceLab;
			break;
		case SWAP_COLOR_COMPONENTS:
			ConverterSwapColorComponents swapConverter = new ConverterSwapColorComponents(
					ColorComponentSwap.SwapGB);
			converter = swapConverter;
			PanelSwapColorComponents swapColor = new PanelSwapColorComponents(
					swapConverter);
			component = swapColor;
			break;
		case STORE_LUMINANCE_LAB:
			ConverterStoreLuminanceLab storeLuminanceLabConverter = new ConverterStoreLuminanceLab(
					"lab luminance");
			converter = storeLuminanceLabConverter;
			PanelStoreLuminanceLab store = new PanelStoreLuminanceLab(
					storeLuminanceLabConverter);
			component = store;
			break;
		case LOAD_LUMINANCE_LAB:
			ConverterLoadLuminanceLab loadLuminanceLabConverter = new ConverterLoadLuminanceLab(
					"lab luminance");
			converter = loadLuminanceLabConverter;
			PanelLoadLuminanceLab load = new PanelLoadLuminanceLab(
					loadLuminanceLabConverter);
			component = load;
			break;
		}
		addComponent(component, converter);
	}

	private void addComponent(JComponent component, ContextPanel context,
			int gridy)
	{
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		c.gridPos(0, gridy).weight(0.0, 0.0);
		c.fill(GridBagConstraints.BOTH);
		add(context, c.getConstraints());
		c.gridPos(1, gridy).weight(1.0, 0.0);
		c.fill(GridBagConstraints.BOTH);
		add(component, c.getConstraints());
	}

	private void addComponent(JComponent component, ColorConverter converter)
	{
		remove(glue);
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();
		ContextPanel context = new ContextPanel();

		addComponent(component, context, gridy);

		FilterItem item = new FilterItem(converter, context, component, gridy);
		items.add(item);
		context.setItem(item);

		gridy++;

		Border oldBorder = component.getBorder();
		Border newBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		CompoundBorder compound = BorderFactory.createCompoundBorder(newBorder,
				oldBorder);
		component.setBorder(compound);

		c.gridPos(1, gridy).weight(1.0, 1.0);
		c.fill(GridBagConstraints.BOTH);
		add(glue, c.getConstraints());
		validate();
	}

	private void remove(FilterItem item)
	{
		int index = items.indexOf(item);
		if (index < 0) {
			return;
		}
		items.remove(index);
		remove(item.component);
		remove(item.context);
		validate();
		getParent().validate();
		getParent().repaint();
	}

	private void bubbleDown(FilterItem item)
	{
		int index = items.indexOf(item);
		if (index < 0 || index > items.size() - 2) {
			return;
		}
		ListUtil.swap(items, index, index + 1);
		swap(items.get(index), items.get(index + 1));
	}

	private void bubbleUp(FilterItem item)
	{
		int index = items.indexOf(item);
		if (index < 1) {
			return;
		}
		ListUtil.swap(items, index, index - 1);
		swap(items.get(index), items.get(index - 1));
	}

	private void swap(FilterItem item, FilterItem item2)
	{
		remove(item.context);
		remove(item.component);
		remove(item2.context);
		remove(item2.component);
		int tmp = item.gridy;
		item.gridy = item2.gridy;
		item2.gridy = tmp;
		addComponent(item.component, item.context, item.gridy);
		addComponent(item2.component, item2.context, item2.gridy);
		validate();
		repaint();
	}

	private class FilterItem
	{
		private ColorConverter converter;
		private ContextPanel context;
		private JComponent component;
		private int gridy;

		public FilterItem(ColorConverter converter, ContextPanel context,
				JComponent component, int gridy)
		{
			this.converter = converter;
			this.context = context;
			this.component = component;
			this.gridy = gridy;
		}
	}

	private class FilterListDestinationTransferHandler
			extends FilterDestinationTransferHandler
	{

		private static final long serialVersionUID = 3742175690579037820L;

		@Override
		protected boolean importData(FilterType filterType)
		{
			add(filterType);
			return true;
		}

	};

	private class ContextPanel extends JPanel
	{

		private static final long serialVersionUID = 1480018840672485094L;

		private FilterItem item;

		public void setItem(FilterItem item)
		{
			this.item = item;
		}

		public ContextPanel()
		{
			super(new GridBagLayout());
			GridBagConstraintsEditor c = new GridBagConstraintsEditor();

			ImageIcon iconDelete = null;
			ImageIcon iconUp = null;
			ImageIcon iconDown = null;
			try {
				iconDelete = getIcon("delete.png");
				iconUp = getIcon("up.png");
				iconDown = getIcon("down.png");
			} catch (IOException e) {
				// ignore
			}

			JButton remove = iconDelete == null ? new JButton("remove")
					: new JButton(iconDelete);
			JButton up = iconDelete == null ? new JButton("up")
					: new JButton(iconUp);
			JButton down = iconDelete == null ? new JButton("down")
					: new JButton(iconDown);

			remove.setMargin(new Insets(0, 0, 0, 0));
			up.setMargin(new Insets(0, 0, 0, 0));
			down.setMargin(new Insets(0, 0, 0, 0));

			c.weight(0, 0);
			c.gridPos(0, 0);
			add(remove, c.getConstraints());
			c.gridPos(1, 0);
			add(up, c.getConstraints());
			c.gridPos(2, 0);
			add(down, c.getConstraints());

			remove.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					FilterList.this.remove(item);
				}
			});

			down.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					FilterList.this.bubbleDown(item);
				}
			});

			up.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					FilterList.this.bubbleUp(item);
				}
			});
		}

		private ImageIcon getIcon(String name) throws IOException
		{
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("icons/" + name);
			BufferedImage image = ImageIO.read(is);
			return new ImageIcon(image);
		}
	}

}
