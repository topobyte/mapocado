package de.topobyte.misc.util;

public class TimeCounter
{

	private long[] totals;
	private long[] starts;

	public TimeCounter(int n)
	{
		totals = new long[n];
		starts = new long[n];
	}

	public void start(int n)
	{
		starts[n] = System.currentTimeMillis();
	}

	public void stop(int n)
	{
		totals[n] += System.currentTimeMillis() - starts[n];
	}

	public long getTotal(int n)
	{
		return totals[n];
	}

}
