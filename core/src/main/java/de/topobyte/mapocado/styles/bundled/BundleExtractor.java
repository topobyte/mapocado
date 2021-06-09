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
import java.util.Set;

public class BundleExtractor
{

	private final File outputDirectory;
	private final ConfigBundle configBundle;

	public BundleExtractor(File outputDirectory, ConfigBundle configBundle)
	{
		this.outputDirectory = outputDirectory;
		this.configBundle = configBundle;
	}

	public void extract() throws IOException
	{
		copyPatterns();
		copySymbols();
	}

	private void copyPatterns() throws IOException
	{
		File patternDir = new File(outputDirectory, Paths.DIR_PATTENRS);
		patternDir.mkdir();
		Set<String> patternNames = configBundle.getPatternNames();
		for (String pattern : patternNames) {
			File patternFile = new File(patternDir, pattern);
			InputStream is = configBundle.getPatternAsInputStream(pattern);
			copy(is, patternFile);
		}
	}

	private void copySymbols() throws IOException
	{
		File symbolsDir = new File(outputDirectory, Paths.DIR_SYMBOLS);
		symbolsDir.mkdir();
		Set<String> symbolNames = configBundle.getSymbolNames();
		for (String symbol : symbolNames) {
			File symbolFile = new File(symbolsDir, symbol);
			InputStream is = configBundle.getSymbolAsInputStream(symbol);
			copy(is, symbolFile);
		}
	}

	private void copy(InputStream is, File file) throws IOException
	{
		FileOutputStream os = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		while (true) {
			int b = is.read(buffer);
			if (b < 0) {
				break;
			}
			os.write(buffer, 0, b);
		}
		os.close();
	}

}
