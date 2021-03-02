package de.topobyte.mapocado.styles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.labels.LabelFileHandler;
import de.topobyte.mapocado.styles.labels.LabelFileReader;
import de.topobyte.mapocado.styles.labels.elements.DotLabel;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.PlainLabel;
import de.topobyte.mapocado.styles.labels.elements.Rule;

public class TestLabelParser
{
	public static void main(String[] args) throws FileNotFoundException,
			ParserConfigurationException, SAXException, IOException
	{
		if (args.length != 1) {
			System.out.println("usage: " + TestLabelParser.class.getSimpleName()
					+ " <label.xml>");
			System.exit(1);
		}
		String path = args[0];

		LabelFileHandler h = LabelFileReader.read(new FileInputStream(path));

		for (Rule rule : h.getRules()) {
			System.out.println(rule.getMinZoom() + " " + rule.getMaxZoom() + " "
					+ rule.getKey());
			LabelContainer lc = h.getRuleToLabel().get(rule);
			switch (lc.getType()) {
			case PLAIN:
				PlainLabel p = (PlainLabel) lc.getLabel();
				print(p);
				break;
			case DOT:
				DotLabel d = (DotLabel) lc.getLabel();
				print(d);
				break;
			case ICON:
				IconLabel i = (IconLabel) lc.getLabel();
				print(i);
				break;
			}
		}
	}

	private static void print(PlainLabel p)
	{
		System.out.println("  " + p.getFamily() + " " + p.getStyle() + " "
				+ p.getFontSize() + " " + p.getStrokeWidth() + " " + p.getFg()
				+ " " + p.getBg());
	}

	private static void print(IconLabel p)
	{
		print((PlainLabel) p);
		System.out.println("  " + p.getImage());
	}

	private static void print(DotLabel p)
	{
		print((PlainLabel) p);
		System.out.println("  " + p.getDotFg() + " " + p.getRadius());
	}
}
