package de.topobyte.mapocado.styles.classes.element;

public class Symbol extends AbstractElement
{

	private static final long serialVersionUID = 441707670733220749L;

	private final String source;

	public Symbol(int level, String source)
	{
		super(level);
		this.source = source;
	}

	public String getSource()
	{
		return source;
	}

}