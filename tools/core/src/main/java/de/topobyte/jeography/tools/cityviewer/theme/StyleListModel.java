package de.topobyte.jeography.tools.cityviewer.theme;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

public class StyleListModel extends DefaultComboBoxModel<Style>
{

	private static final long serialVersionUID = 3892577607792883341L;

	private List<Style> styles = new ArrayList<>();

	public StyleListModel(List<Style> styles)
	{
		this.styles.addAll(styles);
	}

	@Override
	public int getSize()
	{
		return styles.size();
	}

	@Override
	public Style getElementAt(int index)
	{
		return styles.get(index);
	}

}
