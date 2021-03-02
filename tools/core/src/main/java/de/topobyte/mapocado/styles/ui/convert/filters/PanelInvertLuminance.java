package de.topobyte.mapocado.styles.ui.convert.filters;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelInvertLuminance extends JPanel
{
	private static final long serialVersionUID = 8138614991050084032L;

	public PanelInvertLuminance()
	{
		JLabel label = new JLabel("invert Luminance");
		add(label);
	}
}
