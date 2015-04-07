
package javaParallel;

import java.util.concurrent.Semaphore;

/**
 * @author Luca
 */

public class JavaParallel
{
	public static void main(String[] args)
	{
		String[] params = {"Cheese", "Pepperoni", "Black Olives"};
		
		TestProgramInterface myTest = (TestProgramInterface)MyProxy.newInstance(new TestProgram());
				
        long start = System.nanoTime();

		System.out.println("START");
		
		TestProgram.main(myTest);	
		
		if(myTest != null)
		{
			MyProxy.proxy.waitForTermination();
			System.out.println("END: " + myTest.getCounter());		
		}	
		
		long end = System.nanoTime();
		
		System.out.println(String.format("Program executed in %d secs", (end - start) / 1000000000));
	}
}
