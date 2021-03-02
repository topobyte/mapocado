package de.topobyte.mapocado.mapformat.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		int nNumbers = 100;

		Random random = new Random();

		List<Integer> numbers = new ArrayList<>();

		for (int i = 0; i < nNumbers; i++) {
			int number = random.nextInt(1000);
			numbers.add(number);
			System.out.println(number);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (int number : numbers) {
			WriterHelper.writeVarUINT(baos, number);
		}

		byte[] buffer = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		List<Integer> readNumbers = new ArrayList<>();
		for (int i = 0; i < nNumbers; i++) {
			int readNumber = ReaderHelper.readVarUINT(bais);
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
