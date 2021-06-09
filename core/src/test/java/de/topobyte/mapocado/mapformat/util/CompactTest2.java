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

package de.topobyte.mapocado.mapformat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompactTest2
{

	public static void main(String[] args) throws IOException
	{
		int nNumbers = 10000;
		// int nNumbers = 2;
		int maxVal = 0x7FFFFFFF;
		// int add = 0;
		int add = +0x7FFFFFFF;
		// int maxVal = 63;
		// int add = -128;

		Random random = new Random();

		List<Integer> numbers = new ArrayList<>();

		for (int i = 0; i < nNumbers; i++) {
			int number = random.nextInt(maxVal) + add;
			numbers.add(number);
			System.out.println(number);
		}

		System.out.println("writing...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		ObjectOutputOutputStream os = new ObjectOutputOutputStream(oos);
		CompactWriter writer = new CompactWriter(os);
		for (int number : numbers) {
			writer.writeVariableLengthSignedInteger(number);
		}
		os.flush();

		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = new ObjectInputStream(bais);
		ObjectInputInputStream is = new ObjectInputInputStream(ois);
		CompactReaderInputStream reader = new CompactReaderInputStream(is);

		System.out.println("reading...");
		List<Integer> readNumbers = new ArrayList<>();
		for (int i = 0; i < nNumbers; i++) {
			int readNumber = reader.readVariableLengthSignedInteger();
			readNumbers.add(readNumber);
		}

		boolean passed = true;
		for (int i = 0; i < nNumbers; i++) {
			int original = numbers.get(i);
			int testing = readNumbers.get(i);
			if (original != testing) {
				passed = false;
				System.out.println(String.format("FAILED #%d: %d -> %d", i,
						original, testing));
			}
		}

		if (passed) {
			System.out.println("ALL TESTS PASSED");
		} else {
			System.out.println("SOME TESTS FAILED");
		}
	}

}
