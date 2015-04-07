
package javaParallel;

// TestProgram -----------------------------------------------------
public class TestProgram implements TestProgramInterface
{
	static private TestProgramInterface t;
	private int counter;
	
	TestProgram()
	{
		counter = 0;
	}
	
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
		
		int result = t.test();

		return result;
	}
	
	@Parallelization(parallelize=false)
	public int test()
	{
		int total = 0;
		
		for(int idx = 1; idx <= 100; idx++)
		{
			total += t.func(idx);
			System.out.println("test: " + total);
		}
		
		return total;
	}
	
	@Parallelization(parallelize=true)
	public int func(int param)
	{
		t.section1Enter();
		
		// This one doesn't need critical sections
		int result = t.getCounter() + 100;		
		Helpers.sleep(300);		
		t.setCounter(result);
		
		t.section1Exit();
		
		// This one doesn't need critical sections
		// int result = t.addToCounter(100);
								
		t.section1Enter();
		
		System.out.println("===================");
		System.out.println("Thread: " + Thread.currentThread().getId());
		System.out.println("===================");
		
		t.section1Exit();
		
		return result;
	}
	
	@Parallelization(synchronize=true)
	public int addToCounter(int val)
	{
		counter += val;
		return counter;
	}

	@Parallelization(synchronize=true)
	public void setCounter(int val)
	{
		counter = val;
	}

	@Parallelization(synchronize=true)
	public int getCounter()
	{
		return counter;
	}
	
	@Parallelization(mutex="section1", lock=true)
	public void section1Enter()
	{
		System.out.println("Entered section1...");
	}

	@Parallelization(mutex="section1", release=true)
	public void section1Exit()
	{
		System.out.println("Exited section1...");
	}
}
