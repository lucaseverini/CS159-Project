package javaParallel;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface Parallelization
{
	public boolean parallelize() default false;
	public boolean synchronize() default false;
	public String mutex() default "";
	public boolean lock() default false;
	public boolean release() default false;
	public int semaphore() default -1;
	public int value() default -1;
}