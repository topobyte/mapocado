package de.topobyte.mapocado.styles.ui.convert;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class FilterSourceTransferHandler extends TransferHandler
{

	private static final long serialVersionUID = -2276962003501504913L;

	private final FilterProvider provider;

	public FilterSourceTransferHandler(FilterProvider provider)
	{
		this.provider = provider;
	}

	@Override
	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent c)
	{
		return new FilterTransferable(provider);
	}
}
