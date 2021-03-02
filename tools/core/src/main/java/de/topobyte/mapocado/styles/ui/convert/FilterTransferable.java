package de.topobyte.mapocado.styles.ui.convert;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class FilterTransferable implements Transferable
{

	public static DataFlavor FLAVOR_FILTERTYPE = new DataFlavor(
			FilterType.class, "FilterType");

	private DataFlavor[] flavors = new DataFlavor[] { FLAVOR_FILTERTYPE };

	private final FilterProvider provider;

	public FilterTransferable(FilterProvider provider)
	{
		this.provider = provider;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		for (DataFlavor available : flavors) {
			if (flavor.equals(available)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
	{
		return provider.getFilter();
	}

}
