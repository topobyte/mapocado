package de.topobyte.mapocado.mapformat.rtree;

public interface ITreeElement
{

	public BoundingBox getBoundingBox();

	public boolean intersects(BoundingBox queryBox);

	public boolean isInner();

	public boolean isLeaf();
}
