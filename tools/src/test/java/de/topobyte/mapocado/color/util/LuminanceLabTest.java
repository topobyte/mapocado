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

public class LuminanceLabTest extends JPanel
{

	private static final long serialVersionUID = -7832560277835382907L;

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Luminance L*a*b* test");

		LuminanceLabTest panel = new LuminanceLabTest();
		frame.setContentPane(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		frame.setVisible(true);
	}

	// size of individually filled squares
	private int width = 10;
	private int height = 10;
	// horizontal / vertical space between squares
	private int vspace = 0;
	private int hspace = 0;
	// number of degrees to increase the hue per horizontal step
	private int steppingDegrees = 5;
	// how many vertical squares should be created
	private int ysteps = 70;

	private boolean borderTop = true;
	private Color colorBorderTop = Color.BLACK;
	private boolean borderLow = false;
	private Color colorBorderLow = Color.GRAY;

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		int x;
		int i = -1;
		for (int a = 0; a <= 360; a += steppingDegrees) {
			i++;
			x = i * (width + hspace);

			HSLColor hsl = new HSLColor(a, 100f, 50f);
			g.setColor(hsl.getRGB());
			g.fillRect(x, 0, width, height);

			if (borderTop) {
				g.setColor(colorBorderTop);
				g.drawRect(x, 0, width, height);
			}

			for (int k = 0; k < ysteps; k++) {
				Color source = hsl.getRGB();

				Color dest = CieLabUtil.fixedLuminance(source,
						(ysteps - k) / (float) ysteps);

				int y = (k + 1) * (height + vspace);
				g.setColor(dest);
				g.fillRect(x, y, width, height);

				if (borderLow) {
					g.setColor(colorBorderLow);
					g.drawRect(x, y, width, height);
				}
			}
		}
	}

}
