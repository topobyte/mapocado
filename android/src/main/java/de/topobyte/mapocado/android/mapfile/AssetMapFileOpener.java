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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;

public class AssetMapFileOpener implements MapFileOpener
{

	private String assetFileName;
	private Context context;

	public AssetMapFileOpener(Context context, String assetFileName)
	{
		this.context = context;
		this.assetFileName = assetFileName;
	}

	@Override
	public Mapfile open() throws IOException, ClassNotFoundException
	{
		AssetManager assets = context.getAssets();
		InputStream input = assets.open(assetFileName,
				AssetManager.ACCESS_RANDOM);
		return MapFileAccess.open(input);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof AssetMapFileOpener)) {
			return false;
		}
		AssetMapFileOpener other = (AssetMapFileOpener) o;
		return other.assetFileName.equals(assetFileName);
	}

	@Override
	public int hashCode()
	{
		return assetFileName.hashCode();
	}

}
