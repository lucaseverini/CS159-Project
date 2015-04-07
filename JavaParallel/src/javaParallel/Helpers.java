
package javaParallel;

// Helpers -----------------------------------------------------
public class Helpers 
{
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
}
