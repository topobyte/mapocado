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

package de.topobyte.mapocado.color.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TestLab extends JPanel
{
	private static final long serialVersionUID = -8040240236897751333L;

	private double lightness = 1.0;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("XYZ");

		JPanel content = new JPanel(new GridBagLayout());
		frame.setContentPane(content);

		GridBagConstraints c = new GridBagConstraints();

		final TestLab panel = new TestLab();

		final int range = 100;
		final JSlider slider = new JSlider(0, range);
		slider.setValue(100);
		slider.setMajorTickSpacing(10);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				int value = slider.getValue();
				double v = value / (double) range;
				panel.lightness = v;
				panel.repaint();
			}
		});

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		content.add(slider, c);

		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 1;
		content.add(panel, c);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		CieLabColorSpace labSpace = CieLabColorSpace.getInstance();
		int vals = 512;
		for (int y = 0; y < vals; y++) {
			double dy = (vals - y) / (double) vals;
			for (int x = 0; x < vals; x++) {
				double dx = x / (double) vals;

				// CieLab cieLab = new CieLab(dx * Math.PI * 2, dy);
				// cieLab = CieLab.fromAB(dx, dy, lightness);
				// Color color = new Color(cieLab.toXYZ().toRGB().getValue());

				// CieLab cieLab = HCL.createLab(dx * 360, 1.0, dy * 100);
				// Color color = new Color(cieLab.toXYZ().toRGB().getValue());

				// double a = Math.sin(dx * Math.PI * 2);
				// double b = Math.cos(dx * Math.PI * 2);
				// Color color = labSpace.toColor((float) (lightness * 100),
				// (float) a * 127, (float) b * 127, 1);

				float[] values = new float[] { (float) (lightness * 100),
						(float) (dx * 255 - 128), (float) (dy * 255 - 128) };

				float[] rgb = labSpace.toRGB(values);
				Color color = new Color(rgb[0], rgb[1], rgb[2]);

				g.setColor(color);
				g.fillRect(x, y, 1, 1);
			}
		}
	}
}
