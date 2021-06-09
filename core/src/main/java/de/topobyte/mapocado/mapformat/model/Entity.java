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

package de.topobyte.mapocado.mapformat.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.mapocado.mapformat.util.CompactReaderBuffer;
import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.mapocado.mapformat.util.CompactWriter;
import de.topobyte.mapocado.mapformat.util.ioparam.IntResult;
import de.topobyte.mapocado.mapformat.util.ioparam.StringResult;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public abstract class Entity implements Serializable, Byteable
{

	// private static THashSet<LightObjectClassRef> classesBlueprint = new
	// THashSet<LightObjectClassRef>();
	// private static THashMap<String, String> tagsBlueprint = new
	// THashMap<String, String>();

	private static final long serialVersionUID = -2150537781221624780L;

	// private Set<LightObjectClassRef> classes = new
	// HashSet<LightObjectClassRef>();
	// private Map<String, String> tags = new HashMap<String, String>();

	// private THashSet<LightObjectClassRef> classes = new
	// THashSet<LightObjectClassRef>();
	// private THashMap<String, String> tags = new THashMap<String, String>();
	// private THashSet<LightObjectClassRef> classes = classesBlueprint.clone();
	// private THashMap<String, String> tags = tagsBlueprint.clone();

	// private Set<LightObjectClassRef> classes = new
	// TreeSet<LightObjectClassRef>();
	// private Map<Integer, String> tags = new TreeMap<Integer, String>();

	private TIntArrayList classes = new TIntArrayList();
	// private THashSet<LightObjectClassRef> classes = new
	// THashSet<LightObjectClassRef>();
	private TIntObjectHashMap<String> tags = new TIntObjectHashMap<>();

	public Entity()
	{
		// default constructor needed for externalizability
	}

	public Entity(Map<Integer, String> tags)
	{
		for (int key : tags.keySet()) {
			String value = tags.get(key);
			this.tags.put(key, value);
		}
	}

	public TIntArrayList getClasses()
	{
		return classes;
	}

	// public Map<Integer, String> getTags()
	public TIntObjectHashMap<String> getTags()
	{
		return tags;
	}

	public void eraseUnnecessaryTags(Collection<ObjectClassRef> refs,
			StringPool keepPool)
	{
		Set<String> keys = new HashSet<>();
		for (ObjectClassRef classRef : refs) {
			for (String key : classRef.getMandatoryKeepKeys()) {
				keys.add(key);
			}
			for (String key : classRef.getOptionalKeepKeys()) {
				keys.add(key);
			}
		}
		// THashMap<Integer, String> newTags = new THashMap<Integer, String>();
		// for (int keyId : tags.keySet()) {
		TIntObjectHashMap<String> newTags = new TIntObjectHashMap<>();
		for (int keyId : tags.keys()) {
			String key = keepPool.getString(keyId);
			if (keys.contains(key)) {
				newTags.put(keyId, tags.get(keyId));
			}
		}
		tags = newTags;
	}

	/*
	 * specialBits is a bitfield that stores multiple parts of data.
	 * 
	 * * the last two bits (pos. 0,1) are: number of classes - 1
	 * 
	 * * the next two bits (pos. 2,3) are: number of tags
	 */

	private static int MASK_CLASSES = 0x03;
	private static int MASK_TAGS = 0x0C;
	private static int SHIFT_TAGS = 2;

	@Override
	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException
	{
		CompactWriter writer = new CompactWriter(os);
		boolean needExtraClasses = false;
		boolean needExtraTags = false;
		int specialBits = 0;
		// classes
		int nClasses = classes.size();
		if (nClasses >= 1 && nClasses <= 3) {
			specialBits |= (nClasses - 1);
		} else {
			specialBits |= MASK_CLASSES;
			needExtraClasses = true;
		}
		// tags
		int nTags = tags.size();
		if (nTags <= 2) {
			specialBits |= (nTags << SHIFT_TAGS);
		} else {
			specialBits |= MASK_TAGS;
			needExtraTags = true;
		}
		writer.writeByte(specialBits);
		// classes
		if (needExtraClasses) {
			writer.writeVariableLengthUnsignedInteger(nClasses);
		}
		for (int ref : classes.toArray()) {
			writer.writeVariableLengthUnsignedInteger(ref);
		}
		// tags
		if (needExtraTags) {
			writer.writeVariableLengthUnsignedInteger(nTags);
		}
		for (int keyId : tags.keys()) {
			String value = tags.get(keyId);
			writer.writeVariableLengthUnsignedInteger(keyId);
			writer.writeString(value);
		}
	}

	@Override
	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException
	{
		CompactReaderInputStream reader = new CompactReaderInputStream(is);

		int specialBits = reader.readByte();
		boolean hasExtraClasses = (specialBits & MASK_CLASSES) == MASK_CLASSES;
		boolean hasExtraTags = (specialBits & MASK_TAGS) == MASK_TAGS;

		int nClasses = (specialBits & MASK_CLASSES) + 1;
		int nTags = (specialBits & MASK_TAGS) >> SHIFT_TAGS;

		if (hasExtraClasses) {
			nClasses = reader.readVariableLengthUnsignedInteger();
		}
		for (int i = 0; i < nClasses; i++) {
			int refId = reader.readVariableLengthUnsignedInteger();
			classes.add(refId);
		}
		if (hasExtraTags) {
			nTags = reader.readVariableLengthUnsignedInteger();
		}
		for (int i = 0; i < nTags; i++) {
			int keyId = reader.readVariableLengthUnsignedInteger();
			String value = reader.readString();
			tags.put(keyId, value);
		}
	}

	@Override
	public void clear()
	{
		classes.clear();
		tags.clear();
	}

	// result object used in deserialization
	private IntResult ir = new IntResult();
	private StringResult sr = new StringResult();

	@Override
	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		int offset = 0;

		offset = CompactReaderBuffer.readByte(buffer, offset, ir);
		int specialBits = ir.value;
		boolean hasExtraClasses = (specialBits & MASK_CLASSES) == MASK_CLASSES;
		boolean hasExtraTags = (specialBits & MASK_TAGS) == MASK_TAGS;

		int nClasses = (specialBits & MASK_CLASSES) + 1;
		int nTags = (specialBits & MASK_TAGS) >> SHIFT_TAGS;

		if (hasExtraClasses) {
			offset = CompactReaderBuffer
					.readVariableLengthUnsignedInteger(buffer, offset, ir);
			nClasses = ir.value;
		}
		for (int i = 0; i < nClasses; i++) {
			offset = CompactReaderBuffer
					.readVariableLengthUnsignedInteger(buffer, offset, ir);
			int refId = ir.value;
			classes.add(refId);
		}
		if (hasExtraTags) {
			offset = CompactReaderBuffer
					.readVariableLengthUnsignedInteger(buffer, offset, ir);
			nTags = ir.value;
		}
		for (int i = 0; i < nTags; i++) {
			offset = CompactReaderBuffer
					.readVariableLengthUnsignedInteger(buffer, offset, ir);
			int keyId = ir.value;
			offset = CompactReaderBuffer.readString(buffer, offset, ir, sr);
			String value = sr.value;
			tags.put(keyId, value);
		}
		return offset;
	}
}
