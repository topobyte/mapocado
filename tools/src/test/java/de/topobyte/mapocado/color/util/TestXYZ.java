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

public class TestXYZ extends JPanel
{
	private static final long serialVersionUID = -8040240236897751333L;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("XYZ");

		TestXYZ panel = new TestXYZ();
		frame.setContentPane(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		double Y = 1.0;

		for (int y = 0; y < 255; y++) {
			double dy = (255 - y) / 255.0;
			for (int x = 0; x < 255; x++) {
				double dx = x / 255.0;

				double X = (Y / dy) * dx;
				double Z = (Y / dy) * (1 - dx - dy);

				CieXYZ cieXYZ = new CieXYZ(X, Y, Z);
				Color color = new Color(cieXYZ.toRGB().getValue());
				g.setColor(color);
				g.fillRect(x, y, 1, 1);
			}
		}
	}

}
