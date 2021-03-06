// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mapocado.styles.ui.convert.filters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.mapocado.styles.convert.ConverterAdjustLuminance;
import de.topobyte.swing.util.DocumentAdapter;

public class PanelAdjustLuminance extends JPanel
{
	private static final long serialVersionUID = -3346843782923457372L;

	private final ConverterAdjustLuminance converter;
	private JTextField inputLuminance;

	public PanelAdjustLuminance(ConverterAdjustLuminance converter)
	{
		super(new GridBagLayout());
		this.converter = converter;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JLabel label = new JLabel("adjust luminance");
		JLabel labelSaturation = new JLabel("luminance:");
		inputLuminance = new JTextField();

		inputLuminance.setText(String.format("%.1f", converter.getScale()));
		inputLuminance.getDocument().addDocumentListener(new TextListener());

		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(1, 0);
		c.gridWidth(2);
		add(label, c.getConstraints());
		c.gridWidth(1);
		c.gridPos(0, 1).weight(0, 0);
		add(labelSaturation, c.getConstraints());
		c.gridPos(1, 1).weight(0, 0);
		add(inputLuminance, c.getConstraints());
	}

	private class TextListener extends DocumentAdapter
	{

		@Override
		public void update(DocumentEvent event)
		{
			String text = inputLuminance.getText();
			try {
				float value = (float) Double.parseDouble(text);
				converter.setScale(value);
			} catch (NumberFormatException e) {

			}
		}

	}
}
