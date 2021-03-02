package de.topobyte.mapocado.styles.ui.convert;

import javax.swing.JFrame;

public class TestConversionPanel
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ConversionPanel conversionPanel = new ConversionPanel();
		frame.setContentPane(conversionPanel);

		frame.setSize(600, 500);
		frame.setVisible(true);
	}
}
