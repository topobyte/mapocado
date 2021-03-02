package de.topobyte.mapocado.swing.rendering.pathtext;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class AwtTextUtil
{
	private static final float FLATNESS = 1;

	public static float measurePathLength(Shape shape)
	{
		PathIterator it = new FlatteningPathIterator(
				shape.getPathIterator(null), FLATNESS);
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;
		float total = 0;

		while (!it.isDone()) {
			type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;

			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX - lastX;
				float dy = thisY - lastY;
				total += (float) Math.sqrt(dx * dx + dy * dy);
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return total;
	}

	public static Area createBox(Shape shape, float height, float length,
			float skip)
	{
		PathIterator it = new FlatteningPathIterator(
				shape.getPathIterator(null), FLATNESS);
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;

		// done is the distance on the path already walked
		float done = 0;
		// doneBox is the length of the box until now
		float doneBox = 0;

		Area area = new Area();

		while (!it.isDone()) {
			type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;
			case PathIterator.SEG_CLOSE:
				// System.out.println("close");
				points[0] = moveX;
				points[1] = moveY;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				// System.out.println("lineto");
				thisX = points[0];
				thisY = points[1];
				float dx = thisX - lastX;
				float dy = thisY - lastY;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				// System.out.println("piece of length " + distance);
				float angle = (float) Math.atan2(dy, dx);

				if (done + distance <= skip) {
					done += distance;
					// System.out.println("skipping");
					lastX = thisX;
					lastY = thisY;
					break;
				}
				float skipNow = 0;
				if (done < skip) {
					skipNow = skip - done;
				}
				// System.out.println("skip now: " + skipNow);
				done += distance;

				float leftBox = length - doneBox;
				float availableOnPath = distance - skipNow;
				float doNow = availableOnPath >= leftBox ? leftBox
						: availableOnPath;
				doneBox += doNow;

				float r = skipNow / distance;
				float startX = lastX + dx * r;
				float startY = lastY + dy * r;

				float endX = startX + doNow / distance * dx;
				float endY = startY + doNow / distance * dy;

				// new
				float h = height / 2;
				float a = -angle;
				a -= Math.PI / 2;
				float mx = (float) Math.cos(a) * h;
				float my = (float) Math.sin(a) * h;
				// end

				GeneralPath path = new GeneralPath();

				path.moveTo(startX - mx, startY + my);
				path.lineTo(endX - mx, endY + my);
				path.lineTo(endX + mx, endY - my);
				path.lineTo(startX + mx, startY - my);
				path.closePath();

				Area nextArea = new Area(path);
				area.add(nextArea);

				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return area;
	}

	public static double getTextWidth(Font font, String text)
	{
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, text);
		return glyphVector.getLogicalBounds().getWidth();
	}

	public static Shape createStrokedShape(Shape shape, Font font, String text)
	{
		/* the result */
		GeneralPath result = new GeneralPath();

		/* create the glyph vector */
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, text);
		int length = glyphVector.getNumGlyphs();
		/* metrics of the text */
		double width = glyphVector.getLogicalBounds().getWidth();
		float pathLength = AwtTextUtil.measurePathLength(shape);
		// System.out.println("text: " + text);
		// System.out.println("glyph vector width: " + width);
		// System.out.println("path length: " + pathLength);

		/* ignore empty glyph vector */
		if (length == 0) {
			return result;
		}

		/* get some font information */
		LineMetrics lineMetrics = font.getLineMetrics(text, frc);
		float height = lineMetrics.getHeight();
		float descent = lineMetrics.getDescent();

		/* ensure to center the text on the path */
		float remaining = (float) (pathLength - width);
		float offset = remaining / 2;

		/* this transform is used to position individual glyphs */
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;

		int currentChar = 0;
		float nextAdvance = glyphVector.getGlyphMetrics(currentChar)
				.getAdvance() * 0.5f;
		float next = offset + nextAdvance;

		AffineTransform t = new AffineTransform();

		PathIterator it = new FlatteningPathIterator(
				shape.getPathIterator(null), FLATNESS);
		while (currentChar < length && !it.isDone()) {
			int type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;
			case PathIterator.SEG_CLOSE:
				thisX = moveX;
				thisY = moveY;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				thisX = points[0];
				thisY = points[1];
				float dx = thisX - lastX;
				float dy = thisY - lastY;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				if (distance >= next) {
					float r = 1.0f / distance;
					float angle = (float) Math.atan2(dy, dx);
					while (currentChar < length && distance >= next) {
						Shape glyph = glyphVector.getGlyphOutline(currentChar);
						Point2D p = glyphVector.getGlyphPosition(currentChar);
						float px = (float) p.getX();
						float py = (float) p.getY();
						float x = lastX + next * dx * r;
						float y = lastY + next * dy * r;

						float advance = nextAdvance;
						nextAdvance = currentChar >= length - 1 ? 0
								: glyphVector.getGlyphMetrics(currentChar + 1)
										.getAdvance() * 0.5f;
						t.setToTranslation(x, y);
						t.rotate(angle);
						t.translate(-px - advance, height / 2 - descent - py);
						result.moveTo(0, 0);
						result.append(t.createTransformedShape(glyph), false);
						next += advance + nextAdvance;
						currentChar++;
					}
				}
				next -= distance;
				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return result;
	}

	public static TextPath createLine(Shape shape, float length, float skip)
	{
		// System.out.println("length: " + length + " skip: " + skip);
		PathIterator it = new FlatteningPathIterator(
				shape.getPathIterator(null), FLATNESS);
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		float lastX = 0, lastY = 0;
		float thisX = 0, thisY = 0;
		int type = 0;

		// done is the distance on the path already walked
		float done = 0;
		// donePath is the length of the create path until now
		float donePath = 0;

		GeneralPath path = new GeneralPath();
		int segments = 0;

		float lineStartX = 0, lineStartY = 0, lineEndX = 0, lineEndY = 0;

		loop: while (!it.isDone()) {
			type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = lastX = points[0];
				moveY = lastY = points[1];
				break;
			case PathIterator.SEG_CLOSE:
				// System.out.println("close");
				points[0] = moveX;
				points[1] = moveY;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				// System.out.println("lineto");
				thisX = points[0];
				thisY = points[1];
				float dx = thisX - lastX;
				float dy = thisY - lastY;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				// System.out.println("piece of length " + distance);
				// float angle = (float) Math.atan2(dy, dx);

				if (done + distance <= skip) {
					done += distance;
					// System.out.println("skipping " + done);
					lastX = thisX;
					lastY = thisY;
					break;
				}
				float skipNow = 0;
				if (done < skip) {
					skipNow = skip - done;
				}
				// System.out.println("skip now: " + skipNow);
				done += distance;

				float leftPath = length - donePath;
				// System.out.println("left: " + leftPath);
				float availableOnPath = distance - skipNow;
				boolean canFinish = availableOnPath >= leftPath;
				float doNow = canFinish ? leftPath : availableOnPath;
				donePath += doNow;

				float r = skipNow / distance;
				float startX = lastX + dx * r;
				float startY = lastY + dy * r;

				float endX = startX + doNow / distance * dx;
				float endY = startY + doNow / distance * dy;

				if (segments == 0) {
					path.moveTo(startX, startY);
					lineStartX = startX;
					lineStartY = startY;
				}
				path.lineTo(endX, endY);
				lineEndX = endX;
				lineEndY = endY;
				segments += 1;
				if (canFinish) {
					break loop;
				}

				lastX = thisX;
				lastY = thisY;
				break;
			}
			it.next();
		}

		return new TextPath(path, lineStartX, lineStartY, lineEndX, lineEndY);
	}

	public static Path2D reverse(Path2D path)
	{
		Path2D reversed = new Path2D.Float();
		PathIterator it = path.getPathIterator(new AffineTransform());
		List<Float> xs = new ArrayList<>();
		List<Float> ys = new ArrayList<>();
		float points[] = new float[6];
		float moveX = 0, moveY = 0;
		while (!it.isDone()) {
			int type = it.currentSegment(points);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				moveX = points[0];
				moveY = points[1];
				xs.add(moveX);
				ys.add(moveY);
				break;
			case PathIterator.SEG_CLOSE:
				points[0] = moveX;
				points[1] = moveY;
				//$FALL-THROUGH$
			case PathIterator.SEG_LINETO:
				xs.add(points[0]);
				ys.add(points[1]);
			}
			it.next();
		}
		int len = xs.size();
		reversed.moveTo(xs.get(len - 1), ys.get(len - 1));
		for (int i = len - 2; i >= 0; i--) {
			reversed.lineTo(xs.get(i), ys.get(i));
		}
		return reversed;
	}
}
