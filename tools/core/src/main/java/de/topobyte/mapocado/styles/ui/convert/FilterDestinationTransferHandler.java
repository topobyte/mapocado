// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

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
