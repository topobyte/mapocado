package de.topobyte.mapocado.styles.ui.convert.filters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.mapocado.styles.convert.ConverterAdjustSaturation;
import de.topobyte.swing.util.DocumentAdapter;

public class PanelAdjustSaturation extends JPanel
{
	private static final long serialVersionUID = -5941924336143215265L;

	private final ConverterAdjustSaturation converter;
	private JTextField inputSaturation;

	public PanelAdjustSaturation(ConverterAdjustSaturation converter)
	{
		super(new GridBagLayout());
		this.converter = converter;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JLabel label = new JLabel("adjust saturation");
		JLabel labelSaturation = new JLabel("saturation:");
		inputSaturation = new JTextField();

		inputSaturation.setText(String.format("%.1f", converter.getScale()));
		inputSaturation.getDocument().addDocumentListener(new TextListener());

		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(1, 0);
		c.gridWidth(2);
		add(label, c.getConstraints());
		c.gridWidth(1);
		c.gridPos(0, 1).weight(0, 0);
		add(labelSaturation, c.getConstraints());
		c.gridPos(1, 1).weight(0, 0);
		add(inputSaturation, c.getConstraints());
	}

	private class TextListener extends DocumentAdapter
	{

		@Override
		public void update(DocumentEvent event)
		{
			String text = inputSaturation.getText();
			try {
				float value = (float) Double.parseDouble(text);
				converter.setScale(value);
			} catch (NumberFormatException e) {

			}
		}

	}
}
