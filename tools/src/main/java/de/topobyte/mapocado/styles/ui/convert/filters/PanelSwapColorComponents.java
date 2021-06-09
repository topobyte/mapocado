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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.mapocado.styles.convert.ColorComponentSwap;
import de.topobyte.mapocado.styles.convert.ConverterSwapColorComponents;

public class PanelSwapColorComponents extends JPanel
{
	private static final long serialVersionUID = 8138614991050084032L;

	private final ConverterSwapColorComponents converter;
	private JComboBox<ColorComponentSwap> inputSwapMode;

	public PanelSwapColorComponents(ConverterSwapColorComponents swapConverter)
	{
		super(new GridBagLayout());
		this.converter = swapConverter;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JLabel label = new JLabel("rotate hue");
		JLabel labelAngle = new JLabel("angle:");
		inputSwapMode = new JComboBox<>(ColorComponentSwap.values());

		inputSwapMode.setSelectedItem(converter.get());
		inputSwapMode.addActionListener(new ChangeListener());

		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(1, 0);
		c.gridWidth(2);
		add(label, c.getConstraints());
		c.gridWidth(1);
		c.gridPos(0, 1).weight(0, 0);
		add(labelAngle, c.getConstraints());
		c.gridPos(1, 1).weight(1, 0);
		add(inputSwapMode, c.getConstraints());
	}

	private class ChangeListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			Object item = inputSwapMode.getSelectedItem();
			converter.set((ColorComponentSwap) item);
		}

	}
}
