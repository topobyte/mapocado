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
