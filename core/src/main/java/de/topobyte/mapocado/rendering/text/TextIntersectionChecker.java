package de.topobyte.mapocado.rendering.text;

/**
 * An interface defining the methods necessary to implement a intersection based
 * rendering of text.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public interface TextIntersectionChecker
{

	/**
	 * Add the specified boxes to the set of boxes maintained as occupied by
	 * text.
	 * 
	 * @param boxes
	 *            the boxes to mark as occupied.
	 */
	public void add(int[][] boxes);

	/**
	 * Test whether is would be valid to paint something that covers the
	 * specified boxes.
	 * 
	 * @param boxes
	 *            the boxes an object would occupy.
	 * @return whether the checker would allow to paint this.
	 */
	public boolean isValid(int[][] boxes);

}
