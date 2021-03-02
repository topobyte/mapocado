package de.topobyte.mapocado.rendering.pathtext;

import java.util.List;

import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.util.ioparam.BoolResult;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.RenderingLogic;
import de.topobyte.mapocado.rendering.text.TextIntersectionChecker;
import de.topobyte.mapocado.rendering.text.TextUtil;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;

public abstract class PathLabeller
{

	protected static final int PATHTEXT_CHUNK_SPACE = 500;

	private float padding = 4.0f;
	private double acceptableCurvature = 0.05;

	protected boolean debugLabelPlacement = false;

	private int pathTextCount = 0;

	private TextIntersectionChecker checker;
	private Clipping hitTest;

	protected int zoom;
	protected LengthTransformer mercator;
	protected float combinedScaleFactor;
	protected float scaledPadding;

	protected float fontSize = 0;
	private double fontHeightStorage;

	// out parameter for call of createTextBoxes method call
	private BoolResult isReverse = new BoolResult();

	public PathLabeller(TextIntersectionChecker checker, Clipping hitTest,
			int zoom, LengthTransformer mercator, float combinedScaleFactor)
	{
		this.checker = checker;
		this.hitTest = hitTest;
		this.zoom = zoom;
		this.mercator = mercator;
		this.combinedScaleFactor = combinedScaleFactor;

		scaledPadding = this.padding * combinedScaleFactor;
	}

	public int getNumberOfPathTexts()
	{
		return pathTextCount;
	}

	public void setStyle(PathTextSlim pathText)
	{
		fontSize = pathText.getFontSize();
		fontSize = RenderingLogic.scaleFont(fontSize, zoom);
		fontSize = RenderingLogic.scale(fontSize, combinedScaleFactor);

		fontHeightStorage = mercator.getLengthStorageUnits(fontSize, zoom);
	}

	protected abstract void render(Linestring string, String labelText,
			float pathLengthStorage, float offset, float paddedTextLength,
			boolean reverse, int[][] boxes, boolean chunked, LabelType type);

	protected abstract double getTextLength(String labelText);

	protected abstract void drawStringForDebugging(Linestring string);

	public void renderPathText(Linestring string, String labelText)
	{
		// measure length of label text
		double textLength = getTextLength(labelText);
		double paddedTextLength = textLength + 2 * scaledPadding;
		double textLenStorage = mercator.getLengthStorageUnits(paddedTextLength,
				zoom);
		renderPathText(string, labelText, paddedTextLength, textLenStorage,
				false);
	}

	private void renderPathText(Linestring string, String labelText,
			double paddedTextLength, double textLenStorage, boolean chunked)
	{
		LinestringLabeller labeller = new LinestringLabeller(string);

		// measure path length
		double pathLenStorage = labeller.getTotalLength();

		// check available length on path
		if (textLenStorage > pathLenStorage) {
			return;
		}

		/* check whether chunking makes sense with this linestring */
		double chunkSpaceStorage = mercator
				.getLengthStorageUnits(PATHTEXT_CHUNK_SPACE, zoom);
		boolean chunkingMakesSense = pathLenStorage >= textLenStorage * 2
				+ chunkSpaceStorage * 2;

		if (!chunkingMakesSense) {
			renderPathText(labelText, string, labeller, paddedTextLength,
					textLenStorage, pathLenStorage, chunked);
		} else if (!chunked) {
			List<Linestring> chunks = LinestringUtil.createChunks(string,
					textLenStorage + chunkSpaceStorage);
			for (Linestring chunk : chunks) {
				double chunkLenStorage = LinestringUtil
						.measurePathLength(chunk);
				if (chunkLenStorage < textLenStorage) {
					continue;
				}
				if (!LinestringUtil.isRelevant(hitTest, chunk)) {
					continue;
				}

				renderPathText(chunk, labelText, paddedTextLength,
						textLenStorage, true);
			}
		}
	}

	private void renderPathText(String labelText, Linestring string,
			LinestringLabeller labeller, double paddedTextLength,
			double textLenStorage, double pathLenStorage, boolean chunked)
	{
		/*
		 * Overlap-calculations are done in storage mercator units to save on
		 * conversion related calculation time.
		 */

		double offset = (pathLenStorage - textLenStorage) / 2.0;
		// No curvature problems at all
		if (labeller.getTotalCurvature() < acceptableCurvature) {
			int[][] boxes = TextUtil.createTextBoxes(string, offset,
					textLenStorage, fontHeightStorage, isReverse);
			mercator.getLengthTileUnits(offset, zoom);
			renderPathTextSimple(string, pathLenStorage, offset, boxes,
					isReverse.value, labelText, paddedTextLength,
					LabelType.SIMPLE1, chunked);
			return;
		}
		// Let's have a close look at the curvature in the relevant part of
		// the path
		if (debugLabelPlacement) {
			drawStringForDebugging(string);
		}
		double curvature = labeller.getCurvature(offset, textLenStorage);
		if (curvature < acceptableCurvature) {
			int[][] boxes = TextUtil.createTextBoxes(string, offset,
					textLenStorage, fontHeightStorage, isReverse);

			renderPathTextSimple(string, pathLenStorage, offset, boxes,
					isReverse.value, labelText, paddedTextLength,
					LabelType.SIMPLE2, chunked);
			return;
		}

		// Try to find a different position on the path with valid curvature
		double optimizedOffsetStorage = labeller.optimize(textLenStorage,
				acceptableCurvature);

		if (optimizedOffsetStorage >= 0) {
			int[][] boxes = TextUtil.createTextBoxes(string,
					optimizedOffsetStorage, textLenStorage, fontHeightStorage,
					isReverse);

			renderPathTextSimple(string, pathLenStorage, optimizedOffsetStorage,
					boxes, isReverse.value, labelText, paddedTextLength,
					LabelType.OPTIMIZED, chunked);

			return;
		}
		// Unable to label
		if (debugLabelPlacement) {
			System.out.println("DISMISS: '" + labelText + "' curvature: "
					+ labeller.getTotalCurvature() + " string length: "
					+ pathLenStorage + " text length: " + textLenStorage
					+ " relevant curvature: " + curvature);
		}
	}

	private void renderPathTextSimple(Linestring string,
			double pathLengthStorage, double offsetStorage, int[][] boxes,
			boolean reverse, String labelText, double paddedTextLength,
			LabelType type, boolean chunked)
	{
		if (!checker.isValid(boxes)) {
			return;
		}
		checker.add(boxes);

		pathTextCount += 1;

		double offset = mercator.getLengthTileUnits(offsetStorage, zoom);

		render(string, labelText, (float) pathLengthStorage, (float) offset,
				(float) paddedTextLength, reverse, boxes, chunked, type);
	}

}
