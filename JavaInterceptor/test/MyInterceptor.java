
// https://code.google.com/p/java-interceptor/

import it.pcan.java.interceptor.MethodInterceptor;
import it.pcan.java.interceptor.excpetions.InvocationAbortedException;

// Interceptor class
public class MyInterceptor implements MethodInterceptor 
{
	private static final long serialVersionUID = 1L;
	
    public void methodInvoked(Object object, String className, String methodName, Object[] params) throws InvocationAbortedException 
	{
        System.out.println("Invoked " + methodName + " on object " + object + " of class " + className + " with " + params.length + " parameters.");
    }
}