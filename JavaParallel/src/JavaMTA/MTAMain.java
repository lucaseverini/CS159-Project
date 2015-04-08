/*
	MTACore.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package JavaMTA;

import Testing.TestProgram;
import Testing.TestProgramInterface;

// Class MTAMain
// ------------------------------------------------------------------
public class MTAMain
{
	public static void main(String[] args)
	{
		TestProgramInterface myTest = (TestProgramInterface)MTAProxy.newInstance(new TestProgram());
				
        long start = System.nanoTime();

		System.out.println("START");
		
		TestProgram.main(myTest);	
		
		if(myTest != null)
		{
			MTAProxy.getProxy().waitForTermination();
			System.out.println("END: " + myTest.getCounter());		
		}	
		
		long end = System.nanoTime();
		
		System.out.println(String.format("Program executed in %d secs", (end - start) / 1000000000));
	}
}
