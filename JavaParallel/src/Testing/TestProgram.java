/*
	TestProgram.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package Testing;

import JavaMTA.MTAAnnotations;
import JavaMTA.MTAHelpers;

// Class TestProgram
// ------------------------------------------------------------------
public class TestProgram implements TestProgramInterface
{
	static private TestProgramInterface t;
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
			t = prog;
		}
		else
		{
			t = new TestProgram();
		}
		
		t.test();

		return 0;
	}
	
	// test
	// ------------------------------------------------------------------
	@MTAAnnotations(parallelize=false)
	public void test()
	{
		for(int idx = 1; idx <= 100; idx++)
		{
			int result = t.func(idx);
		}
	}
	
	// func
	// ------------------------------------------------------------------
	@MTAAnnotations(parallelize=true)
	public int func(int param)
	{
		t.section1Enter();
		
		// This piece of code needs a critical section
		int result = t.getCounter() + 100;		
		MTAHelpers.sleep(300);		
		t.setCounter(result);
		
		t.section1Exit();
		
		// This code doesn't need a critical section
		// int result = t.addToCounter(100);
								
		printLog("Thread: " + Thread.currentThread().getId());
		
		return result;
	}
	
	// addToCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=true)
	public int addToCounter(int val)
	{
		counter += val;
		return counter;
	}

	// setCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=false)
	public void setCounter(int val)
	{
		counter = val;
	}

	// getCounter
	// ------------------------------------------------------------------
	@MTAAnnotations(synchronize=false)
	public int getCounter()
	{
		return counter;
	}
	
	// section1Enter
	// ------------------------------------------------------------------
	@MTAAnnotations(mutex="section1", lock=true)
	public void section1Enter()
	{
		System.out.println("Entered section1...");
	}

	// section1Exit
	// ------------------------------------------------------------------
	@MTAAnnotations(mutex="section1", release=true)
	public void section1Exit()
	{
		System.out.println("Exited section1...");
	}
	
	// printLog
	// ------------------------------------------------------------------
	public void printLog(String text)
	{
		t.section1Enter();

		System.out.println("1111111111111111111");
		System.out.println("2222222222222222222");

		System.out.println(text);

		System.out.println("3333333333333333333");
		System.out.println("4444444444444444444");
			
		t.section1Exit();
	}
}
