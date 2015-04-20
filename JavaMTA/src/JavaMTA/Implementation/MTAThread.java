/*
	MTAThread.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

// References
// http://www.javacodegeeks.com/2013/01/java-thread-pool-example-using-executors-and-threadpoolexecutor.html

package JavaMTA.Implementation;

import java.lang.reflect.Method;

// Class MTAThread
// ------------------------------------------------------------------
public class MTAThread implements Runnable 
{
    private final Method method;
    private final Object object;
	private final Object[] args;
    private Object result;

	// MTAThread
	// ------------------------------------------------------------------
    public MTAThread(Method m, Object o, Object[] a)
	{
        this.method = m;
		this.object = o;
		this.args = a;
    }

	// run
	// ------------------------------------------------------------------
    @Override
    public void run() 
	{
        // System.out.println(Thread.currentThread().getName() + " Start Worker for " + method);
        
        try 
		{
			System.out.println("MTA Thread " + Thread.currentThread().getId() + " for " + method.getName());
			
			if(MTAProxy.hasSinchronizeAnnotation(method))
			{
				MTAProxy.acquireSynchronization(method);

				result = method.invoke(object, args);

				MTAProxy.releaseSynchronization(method);
			}
			else
			{
				result = method.invoke(object, args);
			}
			
			System.out.println("MTA Thread " + Thread.currentThread().getId() + " for " + method.getName() + " : " + result);
		}
		catch (Exception e) 
		{
            throw new RuntimeException("MTA Thread Exception: " + e.getMessage());
        } 
		
		// System.out.println(Thread.currentThread().getName() + " End Worker for " + method);
    }

 	// toString
	// ------------------------------------------------------------------
   @Override
    public String toString()
	{
        return "MTA Thread for " + method;
    }
}