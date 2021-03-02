package de.topobyte.mapocado.mapformat.rtree.compat;

import java.io.IOException;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.jsi.GenericRTree;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ram.Converter;

public class JsiIRTreeCompatible<T> extends GenericRTree<T>
		implements IRTreeCompatible<T>
{

	public JsiIRTreeCompatible(int min, int max)
	{
		super(min, max);
	}

	@Override
	public IRTree<T> createIRTree() throws IOException
	{
		try {
			return Converter.create(this);
		} catch (SecurityException e) {
			throw new IOException(e);
		} catch (IllegalArgumentException e) {
			throw new IOException(e);
		} catch (NoSuchFieldException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void add(BoundingBox rect, T object)
	{
		Rectangle r = new Rectangle(rect.getMinX(), rect.getMinY(),
				rect.getMaxX(), rect.getMaxY());
		add(r, object);
	}

}
