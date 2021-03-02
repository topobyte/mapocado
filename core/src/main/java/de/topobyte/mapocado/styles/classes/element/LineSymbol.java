package de.topobyte.mapocado.styles.classes.element;

public class LineSymbol extends AbstractElement
{

	private static final long serialVersionUID = -8269968777911908695L;

	private final String source;
	private float offset;
	private boolean repeat;
	private float repeatDistance;

	public LineSymbol(int level, String source, float offset, boolean repeat,
			float repeatDistance)
	{
		super(level);
		this.source = source;
		this.offset = offset;
		this.repeat = repeat;
		this.repeatDistance = repeatDistance;
	}

	public String getSource()
	{
		return source;
	}

	public float getOffset()
	{
		return offset;
	}

	public boolean isRepeat()
	{
		return repeat;
	}

	public float getRepeatDistance()
	{
		return repeatDistance;
	}

}