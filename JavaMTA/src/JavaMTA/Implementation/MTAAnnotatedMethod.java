/*
	MTAAnnotatedMethod.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

package JavaMTA.Implementation;

import java.util.concurrent.Semaphore;

// Class MTAAnnotatedMethod
// ------------------------------------------------------------------
public class MTAAnnotatedMethod
{
	private final String name;
	private final MTAAnnotations annotation;
	private Semaphore semaphore;
	private MTANamedMutex mutex;

	// MTAAnnotatedMethod
	// ------------------------------------------------------------------
	public MTAAnnotatedMethod(String name, MTAAnnotations annotation)
	{
		this.name = name;
		this.annotation = annotation;

		if(this.annotation.synchronize())
		{
			this.semaphore = new Semaphore(1);
		}

		if(this.annotation.lock() || this.annotation.release())
		{
			this.mutex = MTAHelpers.createNamedMutex(this.annotation.mutex());
		}
	}

	// getName
	// ------------------------------------------------------------------
	public String getName()
	{
		return name;
	}

	// getAnnotation
	// ------------------------------------------------------------------
	public MTAAnnotations getAnnotation()
	{
		return annotation;
	}

	// acquireSemaphore
	// ------------------------------------------------------------------
	public void acquireSemaphore()
	{
		System.out.println(name + " : Acquiring Semaphore " + semaphore + " ...");	

		try 
		{
			semaphore.acquire();
		}
		catch(InterruptedException ex) 
		{
			ex.printStackTrace();
		}
	}

	// releaseSemaphore
	// ------------------------------------------------------------------
	public void releaseSemaphore()
	{
		System.out.println(name + " : Releasing Semaphore " + semaphore + " ...");	

		semaphore.release();
	}

	// lockMutex
	// ------------------------------------------------------------------
	public void lockMutex()
	{
		System.out.println(name + " : Locking Mutex " + mutex.getName() + " ...");	

		try 
		{
			mutex.acquire();
		}
		catch(InterruptedException ex) 
		{
			ex.printStackTrace();
		}
	}

	// releaseMutex
	// ------------------------------------------------------------------
	public void releaseMutex()
	{
		System.out.println(name + " : Releasing Mutex " + mutex.getName() + " ...");	

		mutex.release();
	}
}
