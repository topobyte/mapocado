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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConfigBundleWriter
{

	public static void writeConfigBundle(File outputFile, ConfigBundle bundle)
			throws IOException
	{
		writeConfigBundle(new FileOutputStream(outputFile), bundle);
	}

	public static void writeConfigBundle(FileOutputStream fileOutputStream,
			ConfigBundle bundle) throws IOException
	{
		ZipOutputStream zipStream = new ZipOutputStream(fileOutputStream);
		ZipEntry entry;

		entry = new ZipEntry(ConfigBundle.NAME_CLASSES);
		zipStream.putNextEntry(entry);
		InputStream classesInput = bundle.getClassesAsInputStream();
		transfer(classesInput, zipStream);
		zipStream.closeEntry();

		for (String pattern : bundle.getPatternNames()) {
			String name = ConfigBundle.PREFIX_PATTERNS + pattern;
			entry = new ZipEntry(name);
			zipStream.putNextEntry(entry);
			InputStream input = bundle.getPatternAsInputStream(pattern);
			transfer(input, zipStream);
			zipStream.closeEntry();
		}

		for (String symbol : bundle.getSymbolNames()) {
			String name = ConfigBundle.PREFIX_SYMBOLS + symbol;
			entry = new ZipEntry(name);
			zipStream.putNextEntry(entry);
			InputStream input = bundle.getSymbolAsInputStream(symbol);
			transfer(input, zipStream);
			zipStream.closeEntry();
		}

		zipStream.close();
	}

	private static void transfer(InputStream input, OutputStream output)
			throws IOException
	{
		byte[] buffer = new byte[1024];
		while (true) {
			int b = input.read(buffer);
			if (b < 0) {
				return;
			}
			output.write(buffer, 0, b);
		}
	}

}
