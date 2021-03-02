package de.topobyte.mapocado.styles.ui.convert;

import javax.swing.JList;

public class AvailableFilterList extends JList<FilterType>
		implements FilterProvider
{

	private static final long serialVersionUID = -7874385015113662160L;

	private AvailableFilterListModel model;

	public AvailableFilterList()
	{
		model = new AvailableFilterListModel();
		setModel(model);

		FilterSourceTransferHandler sourceTransferHandler = new FilterSourceTransferHandler(
				this);

		setDragEnabled(true);
		setTransferHandler(sourceTransferHandler);
	}

	@Override
	public FilterType getFilter()
	{
		int selectedIndex = getSelectedIndex();
		if (selectedIndex == -1) {
			return null;
		}
		FilterType filter = model.getElementAt(selectedIndex);
		return filter;
	}

}
