/*
	MTAHelpers.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package JavaMTA;

import java.util.ArrayList;

// Class MTAHelpers
// ------------------------------------------------------------------
public class MTAHelpers 
{
	// sleep
	// ------------------------------------------------------------------
	public static void sleep(int microsecs)
	{
		try
		{
			Thread.sleep(microsecs);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// createNamedMutex
	// ------------------------------------------------------------------
	public static MTANamedMutex createNamedMutex(String name)
	{
		ArrayList<MTANamedMutex> namedMutexes = MTAProxy.getNamedMutexes();
		for(MTANamedMutex mutex : namedMutexes)
		{
			if(mutex.getName().equals(name))
			{
				return mutex;
			}
		}

		MTANamedMutex mutex = new MTANamedMutex(name);
		namedMutexes.add(mutex);

		return mutex;
	}
}
