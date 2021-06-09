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
