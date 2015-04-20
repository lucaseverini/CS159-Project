/*
	MTAProxy.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

// References
// http://www.javacodegeeks.com/2012/08/creating-java-dynamic-proxy.html
// http://www.mkyong.com/java/java-custom-annotations-example/

package JavaMTA.Implementation;

import JavaMTA.Testing.TestProgram;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Class MTAProxy
// ------------------------------------------------------------------
public class MTAProxy implements java.lang.reflect.InvocationHandler 
{
	private static boolean enabled = true;
	private static MTAProxy proxy;
	private final Object obj;
	private final ExecutorService executor;
	private static ArrayList<MTAAnnotatedMethod> annotatedMethods;
	private static ArrayList<MTANamedMutex> namedMutexes;
	
	// newInstance
	// ------------------------------------------------------------------
    public static Object newInstance(Object obj) 
	{
        return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new MTAProxy(obj));
    }

	// MTAProxy
	// ------------------------------------------------------------------
    private MTAProxy(Object obj) 
	{
        this.obj = obj;		
		this.executor = Executors.newFixedThreadPool(15);		
		this.proxy = this;
		
		annotatedMethods = new ArrayList<>();
		namedMutexes = new ArrayList<>();

		collectAnnotatedMethods();
    }

	// invoke
	// ------------------------------------------------------------------
	@Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
	{
		Object result = new Integer(0);
		
		if(!enabled)
		{
			return m.invoke(obj, args);
		}

        try 
		{
            System.out.println("Before method " + m.getName());

            long start = System.nanoTime();

			if(hasParallelizeAnnotation(m))
			{
				System.out.println("Invoking " + m.getName() + " Concurrently...");
			
				Runnable worker = new MTAThread(m, obj, args);
				executor.execute(worker);
			}
			else
			{
				if(hasSinchronizeAnnotation(m))
				{
					acquireSynchronization(m);
					
					result = m.invoke(obj, args);
					
					releaseSynchronization(m);
				}
				else if(hasMutexLockAnnotation(m))
				{										
					lockMutex(m);
					
					result = m.invoke(obj, args);
				}
				else if(hasMutexReleaseAnnotation(m))
				{					
					releaseMutex(m);
					
					result = m.invoke(obj, args);
				}
				else
				{
					result = m.invoke(obj, args);
				}
			}
			
            long end = System.nanoTime();

            System.out.println(String.format("%s took %d ns", m.getName(), end - start));
        } 
		catch (Exception e) 
		{
            throw new RuntimeException("Proxy exception: " + e.getMessage());
        } 
		finally 
		{
			System.out.println("After method " + m.getName());
        }

        return result;
    }
	
	// waitForTermination
	// ------------------------------------------------------------------
	public void waitForTermination()
	{
		System.out.println("Waiting for termination...");
		
		MTAHelpers.sleep(1000);
		
		executor.shutdown();
		
		while (!executor.isTerminated()) 
		{
			Thread.yield();
		}
	}
	
	// collectAnnotatedMethods
	// ------------------------------------------------------------------
	private void collectAnnotatedMethods()
	{		
		Class<TestProgram> prog = TestProgram.class;
		for (Method m : prog.getDeclaredMethods()) 
		{
			if (m.isAnnotationPresent(MTAAnnotations.class)) 
			{
				Annotation annotation = m.getAnnotation(MTAAnnotations.class);
				MTAAnnotations parallelization = (MTAAnnotations) annotation;
		
				// Show possible value for all annotations
				System.out.println("Method " + m.getName() + " : parallelize = " + parallelization.parallelize());		
				System.out.println("Method " + m.getName() + " : synchronize = " + parallelization.synchronize());		
				System.out.println("Method " + m.getName() + " : mutex = " + parallelization.mutex());
				System.out.println("Method " + m.getName() + " : lock = " + parallelization.lock());		
				System.out.println("Method " + m.getName() + " : release = " + parallelization.release());
				
				annotatedMethods.add(new MTAAnnotatedMethod(m.getName(), parallelization));
			}
		}		
	}
	
	// hasParallelizeAnnotation
	// ------------------------------------------------------------------
	public static boolean hasParallelizeAnnotation(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
 				return annotM.getAnnotation().parallelize();
			}
		}
		
		return false;
	}

	// hasSinchronizeAnnotation
	// ------------------------------------------------------------------
	public static boolean hasSinchronizeAnnotation(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().synchronize();
			}
		}
		
		return false;
	}

	// acquireSynchronization
	// ------------------------------------------------------------------
	public static void acquireSynchronization(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{				
				annotM.acquireSemaphore();
				return;
			}
		}
	}

	// releaseSynchronization
	// ------------------------------------------------------------------
	public static void releaseSynchronization(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				annotM.releaseSemaphore();
				return;
			}
		}
	}
	
	// hasMutexLockAnnotation
	// ------------------------------------------------------------------
	public static boolean hasMutexLockAnnotation(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().lock();
			}
		}
		
		return false;
	}

	// hasMutexReleaseAnnotation
	// ------------------------------------------------------------------
	public static boolean hasMutexReleaseAnnotation(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().release();
			}
		}
		
		return false;
	}

	// lockMutex
	// ------------------------------------------------------------------
	public static void lockMutex(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{				
				annotM.lockMutex();
				return;
			}
		}
	}

	// releaseMutex
	// ------------------------------------------------------------------
	public static void releaseMutex(Method m)
	{
		for(MTAAnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				annotM.releaseMutex();
				return;
			}
		}
	}	
	
	// getNamedMutexes
	// ------------------------------------------------------------------
	public static ArrayList<MTANamedMutex> getNamedMutexes()
	{
		return namedMutexes;
	}

	// getProxy
	// ------------------------------------------------------------------
	public static MTAProxy getProxy()
	{
		return proxy;
	}

	// setEnabled
	// ------------------------------------------------------------------
	public static void setEnabled(boolean enable)
	{
		enabled = enable;
	}

	// getEnabled
	// ------------------------------------------------------------------
	public static boolean getEnabled()
	{
		return enabled;
	}
}
