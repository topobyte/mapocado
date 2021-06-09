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

package de.topobyte.mapocado.styles.convert;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.bvg.BvgMetadata;
import de.topobyte.bvg.Color;
import de.topobyte.bvg.Fill;
import de.topobyte.bvg.IColor;
import de.topobyte.bvg.LineStyle;
import de.topobyte.bvg.PaintElement;
import de.topobyte.bvg.Stroke;
import de.topobyte.bvg.path.CompactPath;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

/**
 * Manipulate a style configuration by applying some transformation to each
 * colour. This program operates on a style directory.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 */
public class RunStyleChanger
{
	static final Logger logger = LoggerFactory.getLogger(RunStyleChanger.class);

	private static final String OPTION_INPUT_CLASSES = "input_classes";
	private static final String OPTION_OUTPUT_CLASSES = "output_classes";

	private static final String OPTION_INPUT_LABELS = "input_labels";
	private static final String OPTION_OUTPUT_LABELS = "output_labels";

	private static final String OPTION_MODE = "mode";
	private static final String OPTION_ANGLE = "angle";

	private static final String MODE_INVERT_RGB = "invert_rgb";
	private static final String MODE_INVERT_LUMINANCE = "invert_luminance";
	private static final String MODE_INVERT_RGB_LUMINANCE = "invert_rgb_luminance";
	private static final String MODE_BW_LIGHT = "bw-light";
	private static final String MODE_BW_DARK = "bw-dark";
	private static final String MODE_ROTATE_HUE = "rotate_hue";

	private static Set<String> possibleModes = new HashSet<>();
	static {
		possibleModes.add(MODE_INVERT_RGB);
		possibleModes.add(MODE_INVERT_LUMINANCE);
		possibleModes.add(MODE_INVERT_RGB_LUMINANCE);
		possibleModes.add(MODE_ROTATE_HUE);
		possibleModes.add(MODE_BW_LIGHT);
		possibleModes.add(MODE_BW_DARK);
	}

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			// @formatter:off
			Options options = new Options();
			OptionHelper.addL(options, OPTION_INPUT_CLASSES, true, true, "an input style file");
			OptionHelper.addL(options, OPTION_OUTPUT_CLASSES, true, true, "an output style file");
			OptionHelper.addL(options, OPTION_INPUT_LABELS, true, true, "an input labels file");
			OptionHelper.addL(options, OPTION_OUTPUT_LABELS, true, true, "an output labels file");
			OptionHelper.addL(options, OPTION_MODE, true, true, "mode");
			OptionHelper.addL(options, OPTION_ANGLE, true, false, "roation angle of hue");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws SAXException, IOException, ParserConfigurationException,
			TransformerException
	{
		CommandLine line = arguments.getLine();

		String pathInputClasses = line.getOptionValue(OPTION_INPUT_CLASSES);
		String pathOutputClasses = line.getOptionValue(OPTION_OUTPUT_CLASSES);
		String pathInputLabels = line.getOptionValue(OPTION_INPUT_LABELS);
		String pathOutputLabels = line.getOptionValue(OPTION_OUTPUT_LABELS);

		String mode = line.getOptionValue(OPTION_MODE);
		if (!possibleModes.contains(mode)) {
			System.out.println("modification mode not supported: " + mode);
			System.out.println("supported modes:");
			for (String supportedMode : possibleModes) {
				System.out.println("	" + supportedMode);
			}
			System.exit(1);
		}

		float angle = 0;
		boolean needAngle = mode.equals(MODE_ROTATE_HUE);
		if (needAngle) {
			if (!line.hasOption(OPTION_ANGLE)) {
				System.out.println("please specify the angle");
				System.exit(1);
			}
			String value = line.getOptionValue(OPTION_ANGLE);
			try {
				angle = Float.parseFloat(value);
			} catch (NumberFormatException e) {
				System.out.println(
						"unable to parse angle. please specify a valid angle");
				System.exit(1);
			}
		}

		System.out.println("input classes: " + pathInputClasses);
		System.out.println("output classes: " + pathOutputClasses);
		System.out.println("input labels: " + pathInputLabels);
		System.out.println("output labels: " + pathOutputLabels);
		System.out.println("mode: " + mode);

		List<?> extra = line.getArgList();
		List<File> dirs = new ArrayList<>();
		if (extra.size() > 0) {
			System.out.println("additional image directories:");
			for (Object dirname : extra) {
				File dir = new File((String) dirname);
				if (!dir.exists()) {
					System.out.println("ignoring non-existant dir: " + dir);
				} else {
					System.out.println("dir: " + dir);
					dirs.add(dir);
				}
			}
		}

		ColorConverter converter = null;
		ColorConverter converterAlt = null;
		ClassFileChanger classFileChanger = null;
		LabelFileChanger labelFileChanger;
		Set<String> altFilenames = new HashSet<>();

		if (mode.equals(MODE_INVERT_RGB)) {
			converter = new ConverterInvertRGB();
		} else if (mode.equals(MODE_INVERT_LUMINANCE)) {
			converter = createInvertLuminanceConverter();
		} else if (mode.equals(MODE_INVERT_RGB_LUMINANCE)) {
			converter = new ConverterInvertRGBLuminance();
		} else if (mode.equals(MODE_BW_LIGHT)) {
			List<ColorConverter> converters = new ArrayList<>();
			converters.add(new ConverterStoreLuminanceLab("lab luminance"));
			converters.add(new ConverterAdjustSaturation(0.0f));
			converters.add(new ConverterLoadLuminanceLab("lab luminance"));
			converter = new ChainedConverter(converters);
			converterAlt = new ConverterIdentity();
			altFilenames = createAlternativeNames();
		} else if (mode.equals(MODE_BW_DARK)) {
			List<ColorConverter> converters = new ArrayList<>();
			converters.add(new ConverterStoreLuminanceLab("lab luminance"));
			converters.add(new ConverterAdjustSaturation(0.0f));
			converters.add(new ConverterLoadLuminanceLab("lab luminance"));
			converters.add(new ConverterInvertLuminanceLab());
			converter = new ChainedConverter(converters);
			converterAlt = createInvertLuminanceConverter();
			altFilenames = createAlternativeNames();
		} else if (mode.equals(MODE_ROTATE_HUE)) {
			List<ColorConverter> converters = new ArrayList<>();
			converters.add(new ConverterStoreLuminanceLab("lab luminance"));
			converters.add(new ConverterRotateHue(angle));
			converters.add(new ConverterLoadLuminanceLab("lab luminance"));
			converter = new ChainedConverter(converters);
		}

		if (converterAlt == null) {
			classFileChanger = new ClassFileChanger(converter);
			labelFileChanger = new LabelFileChanger(converter);
		} else {
			classFileChanger = createPreservingClassFileChanger(converter,
					converterAlt);
			labelFileChanger = new LabelFileChanger(converterAlt);
		}

		System.out.println("converting classes xml file");
		classFileChanger.execute(pathInputClasses, pathOutputClasses);

		System.out.println("converting labels xml file");
		labelFileChanger.execute(pathInputLabels, pathOutputLabels);

		if (dirs.size() > 0) {
			System.out.println("converting images");
			for (File dir : dirs) {
				File[] files = dir.listFiles();
				for (File file : files) {
					String filename = file.getName();
					boolean alt = altFilenames.contains(filename);
					ColorConverter c = alt ? converterAlt : converter;
					try {
						if (filename.endsWith(".png")) {
							convertPngImage(file, c);
						} else if (filename.endsWith(".bvg")) {
							convertBvgImage(file, c);
						}
					} catch (IOException e) {
						System.out.println("error while converting image ("
								+ file + "): " + e.getMessage());
					}
				}
			}
		}
	}

	private static ColorConverter createInvertLuminanceConverter()
	{
		ConverterAdjustLuminanceLab adjustL = new ConverterAdjustLuminanceLab(
				0.9f);
		ConverterInvertLuminanceLab invertL = new ConverterInvertLuminanceLab();
		return new ChainedConverter(
				Arrays.asList(new ColorConverter[] { adjustL, invertL }));
	}

	private static ClassFileChanger createPreservingClassFileChanger(
			ColorConverter converter, ColorConverter converterAlt)
	{
		return new ClassFileChangerSpecial(converter, converterAlt, "classes",
				"map-overlay-gps-inner", "classes", "map-overlay-gps-outer");
	}

	private static Set<String> createAlternativeNames()
	{
		Set<String> set = new HashSet<>();
		set.add("marker.bvg");
		return set;
	}

	private static void convertPngImage(File file, ColorConverter converter)
			throws IOException
	{
		BufferedImage image = ImageIO.read(file);
		convertBufferedImageImage(image, converter);
		ImageIO.write(image, "png", file);
	}

	private static void convertBufferedImageImage(BufferedImage image,
			ColorConverter converter)
	{
		boolean hasAlpha = image.getColorModel().hasAlpha();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				ColorCode code = new ColorCode(rgb, hasAlpha);
				ConversionContext context = new ConversionContext();
				ColorCode newCode = converter.convert(context, code);
				image.setRGB(x, y, newCode.getValue());
			}
		}
	}

	private static void convertBvgImage(File file, ColorConverter converter)
			throws IOException
	{
		BvgMetadata metadata = new BvgMetadata();
		BvgImage image = BvgIO.read(file, metadata);

		BvgImage changed = new BvgImage(image.getWidth(), image.getHeight());

		List<CompactPath> paths = image.getPaths();
		List<PaintElement> paintElements = image.getPaintElements();
		for (int i = 0; i < paintElements.size(); i++) {
			PaintElement element = paintElements.get(i);
			CompactPath path = paths.get(i);
			if (element instanceof Stroke) {
				Stroke stroke = (Stroke) element;
				LineStyle lineStyle = stroke.getLineStyle();
				IColor color = stroke.getColor();
				IColor c = changeColor(color, converter);
				changed.addStroke(new Stroke(c, lineStyle), path);
			} else if (element instanceof Fill) {
				Fill fill = (Fill) element;
				IColor color = fill.getColor();
				IColor c = changeColor(color, converter);
				changed.addFill(new Fill(c), path);
			}
		}

		BvgIO.write(changed, file, metadata.getEncodingMethod(),
				metadata.getEncodingStrategy());
	}

	private static IColor changeColor(IColor color, ColorConverter converter)
	{
		int rgb = color.getColorCode();
		ColorCode code = new ColorCode(rgb, true);
		ColorCode c = converter.convert(new ConversionContext(), code);
		int value = c.getValue();
		return new Color(value, true);
	}
}
