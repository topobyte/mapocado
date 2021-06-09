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

import de.topobyte.mapocado.mapformat.MapfileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;

public class FileMapfileOpener implements MapfileOpener
{

	private File file;

	public FileMapfileOpener(File file)
	{
		this.file = file;
	}

	@Override
	public Mapfile open() throws IOException, ClassNotFoundException
	{
		return MapfileAccess.open(file);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof FileMapfileOpener)) {
			return false;
		}
		FileMapfileOpener other = (FileMapfileOpener) o;
		return other.file.equals(file);
	}

	@Override
	public int hashCode()
	{
		return file.hashCode();
	}

}
