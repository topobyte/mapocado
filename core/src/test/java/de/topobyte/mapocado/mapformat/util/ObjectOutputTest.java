package de.topobyte.mapocado.mapformat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectOutputTest
{

	public static void main(String[] args) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		ObjectOutputOutputStream os = new ObjectOutputOutputStream(oos);
		CompactWriter writer = new CompactWriter(os);
		writer.writeVariableLengthSignedInteger(0);
		os.flush();

		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(bais);
		ObjectInputInputStream is = new ObjectInputInputStream(ois);
		CompactReaderInputStream reader = new CompactReaderInputStream(is);

		System.out.println(reader.readVariableLengthSignedInteger());

		// while(is.available() > 0) {
		// int read = bais.read();
		// System.out.println(String.format("GOT: 0x%X", read));
		// }
	}

}
