package de.topobyte.mapocado.styles.ui.convert;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class ConversionPanel extends JPanel
{

	private static final long serialVersionUID = 2073422982923223246L;

	private FilterList filterList;

	public ConversionPanel()
	{
		super(new GridBagLayout());
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		// available filters
		AvailableFilterList availableFilterList = new AvailableFilterList();
		JScrollPane scrolledAvailableFilterList = new JScrollPane(
				availableFilterList);
		scrolledAvailableFilterList
				.setBorder(new TitledBorder("available filters"));

		filterList = new FilterList();
		JScrollPane scrolledFilterList = new JScrollPane(filterList);
		scrolledFilterList.setBorder(new TitledBorder("used filters"));

		// layout
		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(0.3, 1.0);
		add(scrolledAvailableFilterList, c.getConstraints());
		c.gridPos(1, 0).weight(1.0, 1.0);
		add(scrolledFilterList, c.getConstraints());
	}

	public FilterList getFilterList()
	{
		return filterList;
	}
}
