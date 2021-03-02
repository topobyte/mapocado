package de.topobyte.mapocado.styles.labels.elements;

public class LabelContainer
{

	private LabelType type;
	private Label label;

	public LabelContainer(LabelType type, Label label)
	{
		this.type = type;
		this.label = label;
	}

	public LabelType getType()
	{
		return type;
	}

	public void setType(LabelType type)
	{
		this.type = type;
	}

	public Label getLabel()
	{
		return label;
	}

	public void setLabel(Label label)
	{
		this.label = label;
	}

}
