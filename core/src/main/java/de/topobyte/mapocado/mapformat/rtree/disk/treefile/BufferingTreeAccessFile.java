package de.topobyte.mapocado.mapformat.rtree.disk.treefile;

import java.io.IOException;
import java.io.InputStream;

import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.randomaccess.FileAccess;

public class BufferingTreeAccessFile implements ITreeAccessFile
{
	private static int BUFFER_SIZE = 256;

	private final FileAccess file;

	public BufferingTreeAccessFile(FileAccess file)
	{
		this.file = file;
	}

	/*
	 * buffer layer
	 */

	// we maintain our own filePointer
	long pointer = 0;

	// buffer
	byte[] buffer = new byte[BUFFER_SIZE];
	// buffer status
	int offset = 0;
	int validBytes = 0;

	private void fillBuffer() throws IOException
	{
		// System.out.println("filling buffer");
		file.seek(pointer);
		long oldPointer = pointer;
		validBytes = readBytes(buffer);
		file.seek(oldPointer);
		offset = 0;
	}

	// private static int invalidationCount = 0;

	private void invalidateBuffer()
	{
		validBytes = 0;
		// invalidationCount += 1;
		// System.out.println("invalidations: " + invalidationCount);
	}

	// this is just for internal usage, thus do not use, this operates directly
	// on the file
	private int readBytes(byte[] buffer) throws IOException
	{
		int read = 0;
		int size = buffer.length;
		do {
			int count = file.read(buffer, read, size - read);
			if (count < 0) {
				break;
			}
			read += count;
		} while (read < size);
		// System.out.println("read: " + read);
		return read;
	}

	private byte[] getBytesFromBuffer(int numBytes) throws IOException
	{
		byte[] result = new byte[numBytes];
		int read = 0;
		do {
			if (validBytes == 0) {
				fillBuffer();
			}
			int copy = numBytes - read;
			if (copy > validBytes) {
				copy = validBytes;
			}
			System.arraycopy(buffer, offset, result, read, copy);
			offset += copy;
			validBytes -= copy;
			read += copy;
			pointer += copy;
		} while (read < numBytes);
		return result;
	}

	private void getBytesFromBuffer(byte[] result) throws IOException
	{
		int numBytes = result.length;
		int read = 0;
		do {
			if (validBytes == 0) {
				fillBuffer();
			}
			int copy = numBytes - read;
			if (copy > validBytes) {
				copy = validBytes;
			}
			System.arraycopy(buffer, offset, result, read, copy);
			offset += copy;
			validBytes -= copy;
			read += copy;
			pointer += copy;
		} while (read < numBytes);
	}

	private int bufferReadByte() throws IOException
	{
		if (validBytes == 0) {
			fillBuffer();
		}
		// System.out.println("reading byte at offset: " + offset);
		byte b = buffer[offset];
		validBytes -= 1;
		offset += 1;

		pointer += 1;
		return b;
	}

	private int bufferReadInt() throws IOException
	{
		byte[] bytes = getBytesFromBuffer(4);
		int result = 0;
		result |= (bytes[0] & 0xff) << 24;
		result |= (bytes[1] & 0xff) << 16;
		result |= (bytes[2] & 0xff) << 8;
		result |= (bytes[3] & 0xff) << 0;
		// System.out.println("int: " + result);
		return result;
	}

	/*
	 * positioning methods
	 */

	@Override
	public void seek(long i) throws IOException
	{
		if (i != pointer) {
			long diff = i - pointer;

			// seek
			file.seek(i);
			pointer = i;

			// if (diff < 0) {
			// System.out.println("negative seek: " + diff);
			// }

			// try to reuse the existing buffer by manipulating offset
			if (diff > 0 && diff < validBytes) {
				offset += diff;
				validBytes -= diff;
			} else {
				invalidateBuffer();
			}
		}
	}

	@Override
	public long getFilePointer() throws IOException
	{
		return pointer;
		// return file.getFilePointer();
	}

	/*
	 * primitive data access
	 */

	@Override
	public int read() throws IOException
	{
		// return file.read();
		return bufferReadByte();
	}

	@Override
	public float readFloat() throws IOException
	{
		// invalidateBuffer();
		// file.seek(pointer);
		// pointer += 4;
		// return file.readFloat();

		int intBits = bufferReadInt();
		return Float.intBitsToFloat(intBits);
	}

	@Override
	public int readInt() throws IOException
	{
		// invalidateBuffer();
		// file.seek(pointer);
		// pointer += 4;
		// int num = file.readInt();
		// System.out.println("int: " + num);
		// return num;

		return bufferReadInt();
	}

	@Override
	public int readUnsignedByte() throws IOException
	{
		// invalidateBuffer();
		// file.seek(pointer);
		// // System.out.println("seek to: " + pointer);
		// pointer += 1;
		// return file.readUnsignedByte();

		int number = bufferReadByte();
		return number & 0xFF;
	}

	@Override
	public void readFully(byte[] buffer) throws IOException
	{
		// invalidateBuffer();
		// file.seek(pointer);
		// pointer += buffer.length;
		// file.readFully(buffer);

		getBytesFromBuffer(buffer);
	}

	/*
	 * CompactReader style access
	 */

	// an InputStream wrapper at the current position of the file
	private InputStream currentInputStream = new InputStream() {

		@Override
		public int read() throws IOException
		{
			return BufferingTreeAccessFile.this.read();
		}
	};

	// a CompactReader reading from the current position of the file via the
	// InputStream wrapper
	private CompactReaderInputStream currentCompactReader = new CompactReaderInputStream(
			currentInputStream);

	@Override
	public int readVariableLengthUnsignedInteger() throws IOException
	{
		return currentCompactReader.readVariableLengthUnsignedInteger();
	}
}
