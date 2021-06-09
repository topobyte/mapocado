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

package de.topobyte.mapocado.mapformat.io;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;

import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.mapocado.mapformat.util.CompactWriter;
import de.topobyte.mapocado.mapformat.util.ObjectInputInputStream;
import de.topobyte.mapocado.mapformat.util.ObjectOutputOutputStream;

public class StringPool implements Serializable, Externalizable
{

	private static final long serialVersionUID = -3467495089942316773L;

	private int size = 0;
	// private Map<String, Integer> stringToId = new HashMap<String, Integer>();
	// private Map<Integer, String> idToString = new HashMap<Integer, String>();

	private TIntObjectHashMap<String> idToString = new TIntObjectHashMap<>();
	private TObjectIntHashMap<String> stringToId = new TObjectIntHashMap<>();

	public void add(String string)
	{
		int id = size++;
		stringToId.put(string, id);
		idToString.put(id, string);
	}

	public boolean containsString(String string)
	{
		return stringToId.contains(string);
	}

	public int getId(String string)
	{
		return stringToId.get(string);
	}

	public String getString(int id)
	{
		return idToString.get(id);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		CompactWriter writer = new CompactWriter(
				new ObjectOutputOutputStream(out));
		writer.writeVariableLengthSignedInteger(size);
		for (int i = 0; i < size; i++) {
			String string = idToString.get(i);
			out.writeUTF(string);
		}
	}

	public int getNumberOfEntries()
	{
		return idToString.size();
	}

	@Override
	public void readExternal(ObjectInput in)
			throws IOException, ClassNotFoundException
	{
		CompactReaderInputStream reader = new CompactReaderInputStream(
				new ObjectInputInputStream(in));
		size = reader.readVariableLengthSignedInteger();
		for (int id = 0; id < size; id++) {
			String string = in.readUTF();
			stringToId.put(string, id);
			idToString.put(id, string);
		}
	}

}
