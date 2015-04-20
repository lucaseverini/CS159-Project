/*
	TestProgram.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package JavaMTA.Testing;

import JavaMTA.Implementation.MTAAnnotations;
import JavaMTA.Implementation.MTAHelpers;

// Class TestProgram
// ------------------------------------------------------------------
public class TestProgram implements TestProgramInterface
{
	static private TestProgramInterface progInterface;
	private int counter;
	
	// TestProgram
	// ------------------------------------------------------------------
	public TestProgram()
	{
		counter = 0;
	}
	
	// main
	// ------------------------------------------------------------------
	public static int main(TestProgramInterface prog)
	{		
		if(prog != null)
		{
			progInterface = prog;
		}
		else
		{
			progInterface = new TestProgram();
		}
		
		progInterface.test();

		return 0;
	}
	
	// test
	// ------------------------------------------------------------------
	@MTAAnnotations(parallelize=false)
	@Override
	public void test()
	{
		for(int idx = 1; idx <= 100; idx++)
		{
			int result = progInterface.func(idx);
		}
	}
	
	// func
	// ------------------------------------------------------------------
	@MTAAnnotations(parallelize=true)
	@Override
	public int func(int param)
	{
		progInterface.section1Enter();
		
		// This piece of code needs a critical section
		int result = progInterface.getCounter() + 100;		
		MTAHelpers.sleep(300);		
		progInterface.setCounter(result);
		
		progInterface.section1Exit();
		
		// This code doesn't need a critical section
		// int result = t.addToCounter(100);
								
		printLog("Thread: " + Thread.currentThread().getId());
		
		MTAHelpers.sleep(1000);
		
		return result;
	}
	
	// addToCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=true) // Makes this method Synchronized (true) or not (false)
	@Override
	public int addToCounter(int val)
	{
		counter += val;
		return counter;
	}

	// setCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=false) // Makes this method Synchronized (true) or not (false)
	@Override
	public void setCounter(int val)
	{
		counter = val;
	}

	// getCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=false) // Makes this method Synchronized (true) or not (false)
	@Override
	public int getCounter()
	{
		return counter;
	}
	
	// section1Enter
	// ------------------------------------------------------------------
	@MTAAnnotations(mutex="section1", lock=true) // Makes this method a Critical Section entry point (true) or not (false)
	@Override
	public void section1Enter()
	{
		System.out.println("Entered section1...");
	}

	// section1Exit
	// ------------------------------------------------------------------
	@MTAAnnotations(mutex="section1", release=true) // Makes this method a Critical Section exit point (true) or not (false)
	@Override
	public void section1Exit()
	{
		System.out.println("Exited section1...");
	}
	
	// printLog
	// ------------------------------------------------------------------
	public void printLog(String text)
	{
		progInterface.section1Enter();		// Enter Critical Section

		System.out.println("Just print something: ***********");
		System.out.println("Just print something: ***********");

		System.out.println(text);

		System.out.println("Just print something: ***********");
		System.out.println("Just print something: ***********");
			
		progInterface.section1Exit();		// Exit Critical Section
	}
}
