package de.topobyte.mapocado.styles.ui.convert;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

public class AvailableFilterListModel extends DefaultListModel<FilterType>
{

	private static final long serialVersionUID = 6965471895106323066L;

	private List<FilterType> filters = new ArrayList<>();

	public AvailableFilterListModel()
	{
		filters.add(FilterType.INVERT_RGB);
		filters.add(FilterType.INVERT_LUMINANCE);
		filters.add(FilterType.INVERT_LUMINANCE_LAB);
		filters.add(FilterType.ROTATE_HUE);
		filters.add(FilterType.ROTATE_HUE_LAB);
		filters.add(FilterType.SWAP_COLOR_COMPONENTS);
		filters.add(FilterType.ADJUST_SATURATION);
		filters.add(FilterType.ADJUST_LUMINANCE);
		filters.add(FilterType.ADJUST_LUMINANCE_LAB);
		filters.add(FilterType.STORE_LUMINANCE_LAB);
		filters.add(FilterType.LOAD_LUMINANCE_LAB);
	}

	@Override
	public int getSize()
	{
		return filters.size();
	}

	@Override
	public FilterType getElementAt(int index)
	{
		return filters.get(index);
	}

}
