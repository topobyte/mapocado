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

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.mapocado.swing.rendering.Conversion;

public class TestHSL extends JPanel
{
	private static final long serialVersionUID = -8040240236897751333L;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("HSL");

		TestHSL panel = new TestHSL();
		frame.setContentPane(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		for (int hue = 0; hue < 360; hue++) {
			for (int sat = 0; sat < 255; sat++) {
				HSLColor hsl = new HSLColor(hue, sat / 255f * 100, 50);
				Color color = hsl.getRGB();
				color = check(color);
				g.setColor(color);
				g.fillRect(hue, sat, 1, 1);
			}
		}
	}

	private Color check(Color color)
	{
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		CieXYZ xyz = new CieXYZ(r, g, b);
		return Conversion.getColor(xyz.toRGB());
	}

}
