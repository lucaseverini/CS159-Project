
import it.pcan.java.interceptor.MethodInterceptor;
import it.pcan.java.interceptor.annotations.InterceptedBy;

// Target class & method
public class Test 
{
    // fields....

    @InterceptedBy(MyInterceptor.class)
    public int methodToBeIntercepted(String argument1, Object argument2, float argument3)
	{        
        int value = 0;
                
        value = (int)argument3 * 2;
        
        return value;
    }

	public static void main(String[] args)
	{
		String[] params = {"Cheese", "Pepperoni", "Black Olives"};
		
		Test myTest = new Test();
		System.out.println(myTest);
		
		int result = myTest.methodToBeIntercepted("arg", null, 10.0f);
		System.out.println("result: " + result);
	}
}
