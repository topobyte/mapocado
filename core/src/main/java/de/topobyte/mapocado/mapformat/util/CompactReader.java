package de.topobyte.mapocado.mapformat.util;

import java.io.IOException;

public interface CompactReader
{

	public int readByte() throws IOException;

	public int readVariableLengthUnsignedInteger() throws IOException;

	public int readVariableLengthSignedInteger() throws IOException;

	public int readInt32() throws IOException;

	public String readString() throws IOException;

}