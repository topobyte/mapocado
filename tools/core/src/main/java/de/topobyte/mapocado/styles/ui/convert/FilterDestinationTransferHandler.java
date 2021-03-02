package de.topobyte.mapocado.styles.ui.convert;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.TransferHandler;

public abstract class FilterDestinationTransferHandler extends TransferHandler
{

	private static final long serialVersionUID = -2276962003501504913L;

	@Override
	public boolean canImport(TransferSupport support)
	{
		DataFlavor[] flavors = support.getDataFlavors();
		for (DataFlavor flavor : flavors) {
			if (flavor.equals(FilterTransferable.FLAVOR_FILTERTYPE)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		Transferable transferable = support.getTransferable();
		try {
			Object transferData = transferable
					.getTransferData(FilterTransferable.FLAVOR_FILTERTYPE);
			if (!(transferData instanceof FilterType)) {
				return false;
			}
			FilterType filterType = (FilterType) transferData;
			return importData(filterType);
		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	protected abstract boolean importData(FilterType filterType);

}
