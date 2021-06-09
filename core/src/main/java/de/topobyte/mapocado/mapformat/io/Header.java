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

package de.topobyte.mapocado.mapformat.io;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.randomaccess.FileAccess;

public class Header
{
	final static Logger logger = LoggerFactory.getLogger(Header.class);

	private int LEN_CHECKSUM = 16;
	private int LEN_RESERVED = 32;

	// file type number
	private int fileTypeNumber = FileTypeInfo.FILE_TYPE_NUMBER;
	// file version number
	private int fileVersion = FileTypeInfo.FILE_VERSION;
	// length of the file
	private int fileLength = 0;
	// MD5 checksum of the data part
	private byte[] checksum = new byte[LEN_CHECKSUM];

	public void setFileLength(int length)
	{
		fileLength = length;
	}

	public void setChecksum(byte[] checksum) throws IllegalArgumentException
	{
		if (checksum.length != LEN_CHECKSUM) {
			throw new IllegalArgumentException(String.format(
					"the supplied checksum's length is %d, should be %d",
					checksum.length, LEN_CHECKSUM));
		}
		this.checksum = checksum;
	}

	public int getFileVersion()
	{
		return fileVersion;
	}

	public int getFileTypeNumber()
	{
		return fileTypeNumber;
	}

	public int getFileLength()
	{
		return fileLength;
	}

	public byte[] getChecksum()
	{
		return checksum;
	}

	public String getReadableChecksum()
	{
		StringBuffer buffer = new StringBuffer();
		for (byte val : checksum) {
			buffer.append(String.format("%x", val));
		}
		return buffer.toString();
	}

	public int read(FileAccess file, int position) throws IOException
	{
		file.seek(position);
		int offset = 0;

		byte[] magic = new byte[8];
		file.readFully(magic);
		fileTypeNumber = file.readShort();
		fileVersion = file.readInt();
		fileLength = file.readInt();
		for (int i = 0; i < LEN_CHECKSUM; i++) {
			checksum[i] = (byte) file.read();
		}

		logger.info("magic: " + new String(magic));
		logger.info("file type number: " + fileTypeNumber);
		logger.info("file version: " + fileVersion);
		logger.info("file size: " + fileLength);
		logger.info("file checksum: " + getReadableChecksum());

		offset += FileTypeInfo.MAGIC_CODE.length;
		offset += 2 + 4; // file type + version
		offset += 4; // file size
		offset += LEN_CHECKSUM;
		offset += LEN_RESERVED;
		return offset;
	}

	public int write(RandomAccessFile file, int position) throws IOException
	{
		file.seek(position);
		int offset = 0;

		file.write(FileTypeInfo.MAGIC_CODE);
		file.writeShort(fileTypeNumber);
		file.writeInt(fileVersion);
		file.writeInt(fileLength);
		for (int i = 0; i < LEN_CHECKSUM; i++) {
			file.write(checksum[i]);
		}
		// reserved bytes
		for (int i = 0; i < LEN_RESERVED; i++) {
			file.write(0);
		}

		offset += FileTypeInfo.MAGIC_CODE.length;
		offset += 2 + 4; // file type + version
		offset += 4; // file size
		offset += LEN_CHECKSUM;
		offset += LEN_RESERVED;
		return offset;
	}
}
