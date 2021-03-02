package de.topobyte.mapocado.styles.ui.convert.filters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.mapocado.styles.convert.ConverterStoreLuminanceLab;
import de.topobyte.swing.util.DocumentAdapter;

public class PanelStoreLuminanceLab extends JPanel
{
	private static final long serialVersionUID = -3346843782923457372L;

	private final ConverterStoreLuminanceLab converter;
	private JTextField inputKey;

	public PanelStoreLuminanceLab(ConverterStoreLuminanceLab converter)
	{
		super(new GridBagLayout());
		this.converter = converter;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraintsEditor c = new GridBagConstraintsEditor();

		JLabel label = new JLabel("store lab luminance");
		JLabel labelKey = new JLabel("key:");
		inputKey = new JTextField();

		inputKey.setText(converter.getStorageKey());
		inputKey.getDocument().addDocumentListener(new TextListener());

		c.fill(GridBagConstraints.BOTH);
		c.gridPos(0, 0).weight(1, 0);
		c.gridWidth(2);
		add(label, c.getConstraints());
		c.gridWidth(1);
		c.gridPos(0, 1).weight(0, 0);
		add(labelKey, c.getConstraints());
		c.gridPos(1, 1).weight(1, 0);
		add(inputKey, c.getConstraints());
	}

	private class TextListener extends DocumentAdapter
	{

		@Override
		public void update(DocumentEvent event)
		{
			String text = inputKey.getText();
			converter.setStorageKey(text);
		}

	}
}
