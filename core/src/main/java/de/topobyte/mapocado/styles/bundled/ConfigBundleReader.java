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

package de.topobyte.mapocado.styles.bundled;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConfigBundleReader
{
	public static ConfigBundle readConfigBundle(File file)
			throws IOException, InvalidBundleException
	{
		return readConfigBundle(new FileInputStream(file));
	}

	public static ConfigBundle readConfigBundle(InputStream input)
			throws IOException, InvalidBundleException
	{
		ZipInputStream zip = new ZipInputStream(input);

		byte[] classes = null;
		byte[] labels = null;
		Map<String, byte[]> patterns = new HashMap<>();
		Map<String, byte[]> symbols = new HashMap<>();

		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				zip.closeEntry();
				continue;
			}
			String path = entry.getName();
			if (path.equals(ConfigBundle.NAME_CLASSES)) {
				// System.out.println(NAME_CLASSES);
				classes = read(zip);
			} else if (path.equals(ConfigBundle.NAME_LABELS)) {
				labels = read(zip);
			} else if (path.startsWith(ConfigBundle.PREFIX_PATTERNS)) {
				String name = path
						.substring(ConfigBundle.LENGTH_PREFIX_PATTERNS);
				if (name.contains("/")) {
					zip.closeEntry();
					continue;
				}
				// System.out.println("PATTERN: " + name);
				byte[] bytes = read(zip);
				patterns.put(name, bytes);
			} else if (path.startsWith(ConfigBundle.PREFIX_SYMBOLS)) {
				String name = path
						.substring(ConfigBundle.LENGTH_PREFIX_SYMBOLS);
				if (name.contains("/")) {
					zip.closeEntry();
					continue;
				}
				// System.out.println("SYMBOL: " + name);
				byte[] bytes = read(zip);
				symbols.put(name, bytes);
			}
			zip.closeEntry();
		}
		zip.close();

		if (classes == null) {
			throw new InvalidBundleException(
					"no '" + ConfigBundle.NAME_CLASSES + "' file in package");
		}

		return new ConfigBundle(classes, labels, patterns, symbols);
	}

	private static byte[] read(ZipInputStream zip) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		for (int c = zip.read(buf); c != -1; c = zip.read(buf)) {
			baos.write(buf, 0, c);
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
}
