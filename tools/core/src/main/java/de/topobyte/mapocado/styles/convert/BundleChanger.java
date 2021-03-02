package de.topobyte.mapocado.styles.convert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;

public class BundleChanger
{

	public static ConfigBundle modify(ConfigBundle input,
			ColorConverter converter) throws ParserConfigurationException,
			SAXException, IOException, TransformerException
	{
		// modify classes
		InputStream classesInput = input.getClassesAsInputStream();
		ByteArrayOutputStream classesOutput = new ByteArrayOutputStream();

		ClassFileChanger classFileChanger = new ClassFileChanger(converter);
		classFileChanger.execute(classesInput, classesOutput);
		byte[] classes = classesOutput.toByteArray();

		// modify labels
		InputStream labelsInput = input.getLabelsAsInputStream();
		ByteArrayOutputStream labelsOutput = new ByteArrayOutputStream();

		LabelFileChanger labelFileChanger = new LabelFileChanger(converter);
		labelFileChanger.execute(labelsInput, labelsOutput);
		byte[] labels = labelsOutput.toByteArray();

		// modify symbols and patterns
		Map<String, byte[]> symbols = new HashMap<>();
		Map<String, byte[]> patterns = new HashMap<>();

		for (String pattern : input.getPatternNames()) {
			InputStream is = input.getPatternAsInputStream(pattern);
			BufferedImage image = ImageIO.read(is);
			convertImage(image, converter);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ImageIO.write(image, "png", buffer);
			patterns.put(pattern, buffer.toByteArray());
		}

		// TODO: these are BVG symbols now
		// for (String symbol : input.getSymbolNames()) {
		// InputStream is = input.getSymbolAsInputStream(symbol);
		// BufferedImage image = ImageIO.read(is);
		// convertImage(image, converter);
		// ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		// ImageIO.write(image, "png", buffer);
		// symbols.put(symbol, buffer.toByteArray());
		// }
		for (String symbol : input.getSymbolNames()) {
			// TODO: not converting colors at the moment
			InputStream is = input.getSymbolAsInputStream(symbol);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			IOUtils.copy(is, buffer);
			symbols.put(symbol, buffer.toByteArray());
		}

		ConfigBundle output = new ConfigBundle(classes, labels, patterns,
				symbols);
		return output;
	}

	private static void convertImage(BufferedImage image,
			ColorConverter converter)
	{
		ConversionContext context = new ConversionContext();
		boolean hasAlpha = image.getColorModel().hasAlpha();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				ColorCode code = new ColorCode(rgb, hasAlpha);
				ColorCode newCode = converter.convert(context, code);
				image.setRGB(x, y, newCode.getValue());
			}
		}
	}
}
