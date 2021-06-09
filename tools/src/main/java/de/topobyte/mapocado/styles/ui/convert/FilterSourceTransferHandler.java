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
