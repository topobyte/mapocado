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
