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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import de.topobyte.mapocado.mapformat.interval.IntervalArray;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.randomaccess.FileAccess;

public class Metadata implements Serializable
{

	private static final long serialVersionUID = -6061845838180251988L;

	// initial coordinate to use to present the file
	private Point start;

	private IntervalArray intervalsNodes;
	private IntervalArray intervalsWays;
	private IntervalArray intervalsRelations;
	private FilePartition filePartition;
	private StringPool poolForRefs;
	private StringPool poolForKeepKeys;

	public Metadata()
	{
		// default constructor for externalizability
	}

	public Metadata(IntervalArray intervalsNodes, IntervalArray intervalsWays,
			IntervalArray intervalsRelations, FilePartition filePartition,
			StringPool poolForRefs, StringPool poolForKeepKeys)
	{
		this.intervalsNodes = intervalsNodes;
		this.intervalsWays = intervalsWays;
		this.intervalsRelations = intervalsRelations;
		this.filePartition = filePartition;
		this.poolForRefs = poolForRefs;
		this.poolForKeepKeys = poolForKeepKeys;
	}

	public int read(FileAccess file, int position)
			throws IOException, ClassNotFoundException
	{
		file.seek(position);

		intervalsNodes = new IntervalArray();
		intervalsNodes.read(file);
		intervalsWays = new IntervalArray();
		intervalsWays.read(file);
		intervalsRelations = new IntervalArray();
		intervalsRelations.read(file);

		double startX = file.readDouble();
		double startY = file.readDouble();
		start = new GeometryFactory()
				.createPoint(new Coordinate(startX, startY));

		filePartition = new FilePartition();
		readObject(file, filePartition);

		poolForRefs = new StringPool();
		readObject(file, poolForRefs);

		poolForKeepKeys = new StringPool();
		readObject(file, poolForKeepKeys);

		return (int) file.getFilePointer() - position;
	}

	public int write(RandomAccessFile file, int position) throws IOException
	{
		file.seek(position);
		int size = 0;

		size += intervalsNodes.write(file);
		size += intervalsWays.write(file);
		size += intervalsRelations.write(file);

		file.writeDouble(start.getX());
		file.writeDouble(start.getY());
		size += 16;

		size += writeObject(file, filePartition);
		size += writeObject(file, poolForRefs);
		size += writeObject(file, poolForKeepKeys);

		return size;
	}

	private int writeObject(RandomAccessFile file, Externalizable object)
			throws IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(buffer);
		object.writeExternal(oos);
		oos.close();
		byte[] bytes = buffer.toByteArray();
		file.writeInt(bytes.length);
		file.write(bytes);
		return 4 + bytes.length;
	}

	private void readObject(FileAccess file, Externalizable object)
			throws IOException, ClassNotFoundException
	{
		int size = file.readInt();
		// System.out.println("metadata: reading object of size " + size);
		byte[] bytes = new byte[size];
		file.readFully(bytes);
		ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
		object.readExternal(new ObjectInputStream(buffer));
	}

	public IntervalArray getIntervalsNodes()
	{
		return intervalsNodes;
	}

	public IntervalArray getIntervalsWays()
	{
		return intervalsWays;
	}

	public IntervalArray getIntervalsRelations()
	{
		return intervalsRelations;
	}

	public void setFilePartition(FilePartition filePartition)
	{
		this.filePartition = filePartition;
	}

	public FilePartition getFilePartition()
	{
		return filePartition;
	}

	public StringPool getPoolForRefs()
	{
		return poolForRefs;
	}

	public StringPool getPoolForKeepKeys()
	{
		return poolForKeepKeys;
	}

	public static StringPool buildRefClassPool(List<ObjectClassRef> list)
	{
		StringPool pool = new StringPool();

		for (ObjectClassRef classRef : list) {
			String ref = classRef.getRef();
			pool.add(ref);
		}

		return pool;
	}

	public static StringPool buildKeepKeyPool(List<ObjectClassRef> list)
	{
		StringPool pool = new StringPool();

		Set<String> keys = new HashSet<>();

		for (ObjectClassRef classRef : list) {
			keys.addAll(classRef.getMandatoryKeepKeys());
			keys.addAll(classRef.getOptionalKeepKeys());
		}

		for (String key : keys) {
			pool.add(key);
		}

		return pool;
	}

	public void setStart(Point point)
	{
		this.start = point;
	}

	public Point getStart()
	{
		return start;
	}

}
