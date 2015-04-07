
// http://www.javacodegeeks.com/2012/08/creating-java-dynamic-proxy.html
// http://www.mkyong.com/java/java-custom-annotations-example/

package javaParallel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class MyProxy implements java.lang.reflect.InvocationHandler 
{
	public static MyProxy proxy;
	private final Object obj;
	private final ExecutorService executor;
	private static ArrayList<AnnotatedMethod> annotatedMethods;
	private static ArrayList<NamedMutex> namedMutexes;

	private class NamedMutex extends Semaphore
	{
		private static final long serialVersionUID = 1L;
		
		private final String name;
		
		public NamedMutex(String name)
		{
			super(1);	// Creates a semaphore with 1 access only
			
			this.name = name;
		}

		public String getName()
		{
			return name;
		}		
	}

	private class AnnotatedMethod
	{
		private final String name;
		private final Parallelization annotation;
		private Semaphore semaphore;
		private NamedMutex mutex;
		
		public AnnotatedMethod(String name, Parallelization annotation)
		{
			this.name = name;
			this.annotation = annotation;
			
			if(this.annotation.synchronize())
			{
				this.semaphore = new Semaphore(1);
			}

			if(this.annotation.lock() || this.annotation.release())
			{
				this.mutex = CreateNamedMutex(this.annotation.mutex());
			}
		}
		
		public String getName()
		{
			return name;
		}
		
		public Parallelization getAnnotation()
		{
			return annotation;
		}
		
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

		public void releaseSemaphore()
		{
			System.out.println(name + " : Releasing Semaphore " + semaphore + " ...");	

			semaphore.release();
		}

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

		public void releaseMutex()
		{
			System.out.println(name + " : Releasing Mutex " + mutex.getName() + " ...");	

			mutex.release();
		}
	}
	
    public static Object newInstance(Object obj) 
	{
        return java.lang.reflect.Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new MyProxy(obj));
    }

    private MyProxy(Object obj) 
	{
        this.obj = obj;		
		this.executor = Executors.newFixedThreadPool(15);		
		this.proxy = this;
		
		annotatedMethods = new ArrayList<>();
		namedMutexes = new ArrayList<>();

		collectAnnotatedMethods();
    }

	@Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
	{
		Object result = new Integer(0);

        try 
		{
            System.out.println("before method " + m.getName());

            long start = System.nanoTime();

			if(hasParallelizeAnnotation(m))
			{
				System.out.println("Invoking " + m.getName() + " from thread...");
			
				Runnable worker = new WorkerThread(m, obj, args);
				executor.execute(worker);
			}
			else
			{
				System.out.println("Invoking " + m.getName() + " directly...");
				
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
			System.out.println("after method " + m.getName());
        }

        return result;
    }
	
	public void waitForTermination()
	{
		System.out.println("Waiting...");
		
		Helpers.sleep(1000);
		
		executor.shutdown();
		
		while (!executor.isTerminated()) 
		{
			Thread.yield();
		}
	}
	
	private void collectAnnotatedMethods()
	{		
		Class<TestProgram> o = TestProgram.class;
		for (Method m : o.getDeclaredMethods()) 
		{
			if (m.isAnnotationPresent(Parallelization.class)) 
			{
				Annotation annotation = m.getAnnotation(Parallelization.class);
				Parallelization parallelization = (Parallelization) annotation;
		
				System.out.println(m.getName() + " : parallelize = " + parallelization.parallelize());		
				System.out.println(m.getName() + " : semaphore = " + parallelization.semaphore());		
				System.out.println(m.getName() + " : lock = " + parallelization.lock());		
				System.out.println(m.getName() + " : release = " + parallelization.release());	
				
				annotatedMethods.add(new AnnotatedMethod(m.getName(), parallelization));
			}
		}		
	}
	
	public static boolean hasParallelizeAnnotation(Method m)
	{
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
 				return annotM.getAnnotation().parallelize();
			}
		}
		
		return false;
	}

	public static boolean hasSinchronizeAnnotation(Method m)
	{
		System.out.println(m.getName());
		
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().synchronize();
			}
		}
		
		return false;
	}

	public static void acquireSynchronization(Method m)
	{
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{				
				annotM.acquireSemaphore();
				return;
			}
		}
	}

	public static void releaseSynchronization(Method m)
	{
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				annotM.releaseSemaphore();
				return;
			}
		}
	}
	
	private NamedMutex CreateNamedMutex(String name)
	{
		for(NamedMutex mutex : namedMutexes)
		{
			if(mutex.getName().equals(name))
			{
				return mutex;
			}
		}
		
		NamedMutex mutex = new NamedMutex(name);
		namedMutexes.add(mutex);
		
		return mutex;
	}

	public static boolean hasMutexLockAnnotation(Method m)
	{
		System.out.println(m.getName());
		
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().lock();
			}
		}
		
		return false;
	}

	public static boolean hasMutexReleaseAnnotation(Method m)
	{
		System.out.println(m.getName());
		
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				return annotM.getAnnotation().release();
			}
		}
		
		return false;
	}

	public static void lockMutex(Method m)
	{
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{				
				annotM.lockMutex();
				return;
			}
		}
	}

	public static void releaseMutex(Method m)
	{
		for(AnnotatedMethod annotM : annotatedMethods)
		{
			if(annotM.getName().equals(m.getName()))
			{
				annotM.releaseMutex();
				return;
			}
		}
	}	
}
