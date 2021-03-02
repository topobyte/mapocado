package de.topobyte.mapocado.android.mapfile;

import java.io.IOException;

import de.topobyte.mapocado.mapformat.Mapfile;

public interface MapFileOpener
{

	public Mapfile open() throws IOException, ClassNotFoundException;

}
