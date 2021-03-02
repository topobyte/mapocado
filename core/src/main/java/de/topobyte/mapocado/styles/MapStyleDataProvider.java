package de.topobyte.mapocado.styles;

import java.io.IOException;
import java.io.InputStream;

public interface MapStyleDataProvider
{

	public InputStream getSymbol(String source) throws IOException;

	public InputStream getTexture(String source) throws IOException;

}
