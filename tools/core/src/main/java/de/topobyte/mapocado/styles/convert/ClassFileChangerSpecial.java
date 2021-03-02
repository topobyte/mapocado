package de.topobyte.mapocado.styles.convert;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class ClassFileChangerSpecial extends ClassFileChanger
{
	private List<String> nodeNames = new ArrayList<>();
	private List<String> attributeNames = new ArrayList<>();

	private ColorConverter converterAlt;

	public ClassFileChangerSpecial(ColorConverter converter,
			ColorConverter converterAlt, String... args)
	{
		super(converter);
		this.converterAlt = converterAlt;
		for (int i = 0; i < args.length; i += 2) {
			nodeNames.add(args[i]);
			attributeNames.add(args[i + 1]);
		}
	}

	@Override
	protected ColorConverter getConverter(Node node, String attribute)
	{
		for (int i = 0; i < nodeNames.size(); i++) {
			if (node.getNodeName().equals(nodeNames.get(i))
					&& attribute.equals(attributeNames.get(i))) {
				return converterAlt;
			}
		}
		return super.getConverter(node, attribute);
	}
}
