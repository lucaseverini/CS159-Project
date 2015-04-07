
// http://www.javacodegeeks.com/2013/01/java-thread-pool-example-using-executors-and-threadpoolexecutor.html

package javaParallel;

import java.lang.reflect.Method;

public class WorkerThread implements Runnable 
{
    private final Method method;
    private final Object object;
	private final Object[] args;
    private Object result;

    public WorkerThread(Method m, Object o, Object[] a)
	{
        this.method = m;
		this.object = o;
		this.args = a;
    }

    @Override
    public void run() 
	{
        // System.out.println(Thread.currentThread().getName() + " Start Worker for " + method);
        
        try 
		{
			System.out.println("Worker thread " + Thread.currentThread().getId() + " for " + method.getName());
			
			if(MyProxy.hasSinchronizeAnnotation(method))
			{
				MyProxy.acquireSynchronization(method);

				result = method.invoke(object, args);

				MyProxy.releaseSynchronization(method);
			}
			else
			{
				result = method.invoke(object, args);
			}
			
			System.out.println("Worker thread " + Thread.currentThread().getId() + " for " + method.getName() + " : " + result);
		}
		catch (Exception e) 
		{
            throw new RuntimeException("Worker Thread Exception: " + e.getMessage());
        } 
		
		// System.out.println(Thread.currentThread().getName() + " End Worker for " + method);
    }

    @Override
    public String toString()
	{
        return "Worker for " + method;
    }
}