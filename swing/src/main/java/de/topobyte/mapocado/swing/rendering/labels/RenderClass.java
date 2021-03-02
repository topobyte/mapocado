package de.topobyte.mapocado.swing.rendering.labels;

public class RenderClass
{

	// Identifier used by the LabelDrawer internally
	int classId;
	// Identifier for database queries to the sqlite database, -1 for mapfile
	// classes
	int typeId;

	int minZoom;
	int maxZoom;
	LabelClass labelClass;

	public RenderClass(int classId, int typeId, int minZoom, int maxZoom,
			LabelClass labelClass)
	{
		this.classId = classId;
		this.typeId = typeId;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.labelClass = labelClass;
	}

}
