/*
	MTAAnnotations.java

    CS159 - Class Project
	April-1-2015

	By Luca Severini (lucaseverini@mac.com)
*/

// References
// http://www.mkyong.com/java/java-custom-annotations-example/

package JavaMTA.Implementation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

// Annotation Interface MTAAnnotations
// ------------------------------------------------------------------
public @interface MTAAnnotations
{
	public boolean parallelize() default false;
	public boolean synchronize() default false;
	public String mutex() default "";
	public boolean lock() default false;
	public boolean release() default false;
}