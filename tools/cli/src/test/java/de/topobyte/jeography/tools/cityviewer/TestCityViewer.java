package de.topobyte.jeography.tools.cityviewer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.jeography.tools.cityviewer.theme.Style;
import de.topobyte.jeography.tools.cityviewer.theme.StyleConfig;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;

public class TestCityViewer
{

	private static final int STARTUP_ZOOM = 14;

	public static void main(String[] args)
	{
		String pathMapfile = "/data/oxygen/hafnium/mapfiles/planet-180813/germany/Berlin.xmap";
		String pathDatabase = "/data/oxygen/nomioc.v2/planet-180813/germany/Berlin.sqlite";

		File fileMapfile = new File(pathMapfile);
		File fileDatabase = new File(pathDatabase);

		Charset defaultCharset = Charset.defaultCharset();
		System.out.println("default charset: " + defaultCharset);

		System.out.println("CityViewer started");
		URL resStyles = Thread.currentThread().getContextClassLoader()
				.getResource(CityViewer.RES_STYLES);

		System.out.println("Mapfile: " + pathMapfile);
		System.out.println("Database: " + pathDatabase);
		System.out.println("Styles: " + resStyles);

		StyleConfig styleConfig = null;
		try {
			styleConfig = StyleConfig.parse(resStyles);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			System.out.println("unable to parse styles.xml");
			e.printStackTrace();
			System.exit(1);
		}
		List<Style> styles = styleConfig.getStyles();

		ConfigBundle configBundle = null;
		Style style = styles.get(0);
		try {
			configBundle = CityViewer.getConfigBundleForStyle(style);
		} catch (IOException | InvalidBundleException e) {
			System.out.println("unable to open style");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			new CityViewer(fileMapfile, configBundle, fileDatabase, false,
					false, false, 0, 0, true, STARTUP_ZOOM, styles);
		} catch (IOException | ClassNotFoundException
				| ParserConfigurationException | SAXException e) {
			System.out.println("unable to lauch viewer");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
