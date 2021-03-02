package de.topobyte.mapocado.mapformat.io;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilePartition implements Serializable, Externalizable
{

	private static final long serialVersionUID = -7520941230911448833L;

	private List<Integer> positions = new ArrayList<>();

	public FilePartition()
	{
		// for externalizability
	}

	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		for (int position : positions) {
			buffer.append(position);
			buffer.append(" ");
		}
		return buffer.toString();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(positions.size());
		for (int i = 0; i < positions.size(); i++) {
			out.writeInt(positions.get(i));
		}
	}

	@Override
	public void readExternal(ObjectInput in)
			throws IOException, ClassNotFoundException
	{

		int num = in.readInt();
		for (int i = 0; i < num; i++) {
			int position = in.readInt();
			positions.add(position);
		}
	}

	public void add(int i)
	{
		positions.add(i);
	}

	public List<Integer> getPositions()
	{
		return positions;
	}
}
