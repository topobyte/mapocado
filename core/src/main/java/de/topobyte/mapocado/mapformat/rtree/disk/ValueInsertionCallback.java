package de.topobyte.mapocado.mapformat.rtree.disk;

public interface ValueInsertionCallback<T>
{

	/**
	 * Notify a listener about the insertion of an element into the tree.
	 * 
	 * @param entryAddress
	 *            the address of the tree leaf that contains the value
	 * @param childNumber
	 *            the index of the value within the array of values the leaf
	 *            stores
	 * @param thing
	 *            the actual thing that has been inserted
	 */
	public void valueInserted(int entryAddress, int childNumber, T thing);

}
