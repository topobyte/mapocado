package de.topobyte.mapocado.styles.labels.elements;

public class IconLabel extends PlainLabel
{

	private String image = null;
	private float height = 14;

	public IconLabel()
	{
		super();
	}

	public IconLabel(PlainLabel label)
	{
		super(label);
	}

	public IconLabel(IconLabel label)
	{
		this((PlainLabel) label);
		image = label.image;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public float getIconHeight()
	{
		return height;
	}

}
