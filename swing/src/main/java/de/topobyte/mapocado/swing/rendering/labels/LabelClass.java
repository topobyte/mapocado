package de.topobyte.mapocado.swing.rendering.labels;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import de.topobyte.mapocado.styles.labels.elements.DotLabel;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.Label;
import de.topobyte.mapocado.styles.labels.elements.LabelType;
import de.topobyte.mapocado.styles.labels.elements.PlainLabel;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class LabelClass
{

	private float density;
	private float magnification = 1;

	public LabelType type;

	public boolean hasDot;
	public int dotSize;
	public boolean hasIcon;
	public int iconSize;
	public LabelBoxConfig labelBoxConfig;
	public Label labelStyle;
	public int dy;

	public Font font;
	public FontMetrics fontMetrics;
	public Color dotColor;
	public int strokeWidth;
	public Color fg;
	public Color bg;

	public LabelClass(LabelType type, Label labelStyle, float magnification,
			float density)
	{
		this.type = type;
		this.labelStyle = labelStyle;
		this.density = density;
		this.hasDot = labelStyle instanceof DotLabel;

		PlainLabel plain = (PlainLabel) labelStyle;

		font = new Font(Conversion.getFontFamily(plain.getFamily()),
				Conversion.getFontStyle(plain.getStyle()),
				Math.round(plain.getFontSize()));
		Canvas c = new Canvas();
		fontMetrics = c.getFontMetrics(font);

		if (labelStyle instanceof DotLabel) {
			DotLabel dot = (DotLabel) labelStyle;
			dotColor = Conversion.getColor(dot.getDotFg());
		}

		update();
	}

	public void setMagnification(float magnification)
	{
		this.magnification = magnification;
		update();
	}

	private void update()
	{
		PlainLabel plain = (PlainLabel) labelStyle;

		fg = Conversion.getColor(plain.getFg());
		bg = Conversion.getColor(plain.getBg());

		int textSize = (int) Math.ceil(plain.getFontSize() * magnification);
		strokeWidth = (int) Math.ceil(plain.getStrokeWidth() * magnification);
		int border = (int) Math
				.ceil(plain.getStrokeWidth() * magnification / 2);

		LabelBoxConfig lbc = new LabelBoxConfig(textSize, border);
		labelBoxConfig = lbc;

		if (labelStyle instanceof DotLabel) {
			DotLabel dot = (DotLabel) labelStyle;
			dotSize = (int) Math.ceil(dot.getRadius() * magnification);
			dy = -lbc.height + lbc.lowExtra - dotSize;
		} else if (labelStyle instanceof IconLabel) {
			IconLabel icon = (IconLabel) labelStyle;
			iconSize = Math.round(icon.getIconHeight() * density);
			dy = -lbc.height + lbc.lowExtra - iconSize / 2;
		} else {
			dy = -lbc.height / 2;
		}
	}

	public int getBoxWidth(String name)
	{
		return fontMetrics.stringWidth(name) + 2 * labelBoxConfig.border;
	}

}
