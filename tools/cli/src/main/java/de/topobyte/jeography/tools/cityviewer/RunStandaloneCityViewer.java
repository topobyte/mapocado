package de.topobyte.jeography.tools.cityviewer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.jeography.tools.cityviewer.theme.Style;
import de.topobyte.jeography.tools.cityviewer.theme.StyleConfig;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;

public class RunStandaloneCityViewer
{

	private static final int STARTUP_ZOOM = 14;

	public static void main(String name, String[] args)
	{
		Charset defaultCharset = Charset.defaultCharset();
		System.out.println("default charset: " + defaultCharset);

		System.out.println("CityViewer started");
		URL resMapfile = Thread.currentThread().getContextClassLoader()
				.getResource(CityViewer.RES_MAPFILE);
		URL resDatabase = Thread.currentThread().getContextClassLoader()
				.getResource(CityViewer.RES_DATABASE);
		URL resStyles = Thread.currentThread().getContextClassLoader()
				.getResource(CityViewer.RES_STYLES);

		System.out.println("Mapfile: " + resMapfile);
		System.out.println("Database: " + resDatabase);
		System.out.println("Styles: " + resStyles);

		if (resMapfile == null) {
			System.out.println("Unable to open mapfile");
			System.exit(1);
		}
		if (resDatabase == null) {
			System.out.println("Unable to open database");
			System.exit(1);
		}
		if (resStyles == null) {
			System.out.println("Unable to open styles");
			System.exit(1);
		}

		StyleConfig styleConfig = null;
		try {
			styleConfig = StyleConfig.parse(resStyles);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			System.out.println("unable to parse styles.xml");
			e.printStackTrace();
			System.exit(1);
		}
		List<Style> styles = styleConfig.getStyles();

		File fileMapfile = null;
		File fileDatabase = null;
		try {
			fileMapfile = File.createTempFile("cityviewer", ".xmap");
			fileMapfile.deleteOnExit();
			System.out.println("temporary mapfile: " + fileMapfile);
		} catch (IOException e) {
			System.out.println("unable to create temp file for mapfile");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			fileDatabase = File.createTempFile("cityviewer", ".sqlite");
			fileDatabase.deleteOnExit();
			System.out.println("temporary database: " + fileDatabase);
		} catch (IOException e) {
			System.out.println("unable to create temp file for database");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			InputStream isMapfile = resMapfile.openStream();
			InputStream isDatabase = resDatabase.openStream();
			System.out.println("copying mapfile...");
			copyFile(isMapfile, fileMapfile);
			System.out.println("copying database...");
			copyFile(isDatabase, fileDatabase);
		} catch (IOException e) {
			System.out.println("error while copying file");
			e.printStackTrace();
			System.exit(1);
		}

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

	public static void copyFile(InputStream is, File out) throws IOException
	{
		BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(out));
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		os.close();
	}

}
