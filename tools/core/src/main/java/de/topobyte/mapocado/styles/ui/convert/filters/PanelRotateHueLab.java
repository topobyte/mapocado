package de.topobyte.mapocado.styles.ui.convert.filters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.mapocado.styles.convert.ConverterRotateHueLab;
import de.topobyte.swing.util.DocumentAdapter;

public class PanelRotateHueLab extends JPanel
{

	private static final long serialVersionUID = -1305947604259813954L;

	private final ConverterRotateHueLab converter;
	private JTextField inputAngle;

	public PanelRotateHueLab(ConverterRotateHueLab converter)
	{
		super(new GridBagLayout());
		this.converter = converter;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JLabel label = new JLabel("rotate hue with lab luminance correction");
		JLabel labelAngle = new JLabel("angle:");
		inputAngle = new JTextField();

		inputAngle.setText(String.format("%.1f", converter.getAngle()));
		inputAngle.getDocument().addDocumentListener(new TextListener());

		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(1, 0);
		c.gridWidth(2);
		add(label, c.getConstraints());
		c.gridWidth(1);
		c.gridPos(0, 1).weight(0, 0);
		add(labelAngle, c.getConstraints());
		c.gridPos(1, 1).weight(1, 0);
		add(inputAngle, c.getConstraints());
	}

	private class TextListener extends DocumentAdapter
	{

		@Override
		public void update(DocumentEvent event)
		{
			String text = inputAngle.getText();
			try {
				float value = (float) Double.parseDouble(text);
				converter.setAngle(value);
			} catch (NumberFormatException e) {

			}
		}

	}
}
