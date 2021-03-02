package de.topobyte.mapocado.swing.viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import de.topobyte.jeography.core.mapwindow.TileMapWindow;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.mapocado.swing.rendering.Conversion;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;

public class GpsSimulatorOverlay implements PaintListener
{

	private final MapocadoViewer viewer;

	private boolean enabled = true;

	public GpsSimulatorOverlay(MapocadoViewer viewer)
	{
		this.viewer = viewer;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	@Override
	public void onPaint(TileMapWindow mapWindow, Graphics graphics)
	{
		if (!enabled) {
			return;
		}
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		MapRenderConfig renderConfig = viewer.getRenderConfig();

		Color inner = Conversion.getColor(renderConfig.getOverlayGpsInner());
		Color outer = Conversion.getColor(renderConfig.getOverlayGpsOuter());

		g.setColor(inner);
		g.fillArc(100, 100, 50, 50, 0, 360);
		g.setColor(outer);
		g.drawArc(100, 100, 50, 50, 0, 360);
	}

}
