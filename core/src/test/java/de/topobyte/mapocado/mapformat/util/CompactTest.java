package de.topobyte.mapocado.mapformat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompactTest
{

	public static void main(String[] args) throws IOException
	{
		// int nNumbers = 100000;
		int nNumbers = 1000;
		int maxVal = 0x7FFFFFF;
		// int add = 0;
		int add = -0x0FFFFFFF;
		// int maxVal = 63;
		// int add = -128;

		Random random = new Random();

		List<Integer> numbers = new ArrayList<>();

		for (int i = 0; i < nNumbers; i++) {
			int number = random.nextInt(maxVal) + add;
			numbers.add(number);
			System.out.println(number);
		}
		// numbers.add(-86000000);

		System.out.println("writing...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompactWriter writer = new CompactWriter(baos);
		for (int number : numbers) {
			writer.writeVariableLengthSignedInteger(number);
		}

		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		CompactReaderInputStream reader = new CompactReaderInputStream(bais);

		System.out.println("reading...");
		List<Integer> readNumbers = new ArrayList<>();
		for (int i = 0; i < numbers.size(); i++) {
			int readNumber = reader.readVariableLengthSignedInteger();
			readNumbers.add(readNumber);
		}

		boolean passed = true;
		for (int i = 0; i < numbers.size(); i++) {
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
