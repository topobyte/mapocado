package de.topobyte.mapocado.styles.ui.convert.filters;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelInvertLuminanceLab extends JPanel
{
	private static final long serialVersionUID = 8138614991050084032L;

	public PanelInvertLuminanceLab()
	{
		JLabel label = new JLabel("invert L*a*b* Luminance");
		add(label);
	}
}
