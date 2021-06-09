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

package de.topobyte.mapocado.android.mapfile;

import java.io.File;
import java.io.IOException;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;

public class FileMapFileOpener implements MapFileOpener
{

	private File file;

	public FileMapFileOpener(File file)
	{
		this.file = file;
	}

	@Override
	public Mapfile open() throws IOException, ClassNotFoundException
	{
		return MapFileAccess.open(file);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof FileMapFileOpener)) {
			return false;
		}
		FileMapFileOpener other = (FileMapFileOpener) o;
		return other.file.equals(file);
	}

	@Override
	public int hashCode()
	{
		return file.hashCode();
	}

}
