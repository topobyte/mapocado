package de.topobyte.jeography.tools.cityviewer.theme;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public abstract class StyleItemListener implements ItemListener
{

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Style style = (Style) e.getItem();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			System.out.println(style);
			setStyle(style);
		}
	}

	public abstract void setStyle(Style style);
}
