/**
 * 
 */
package cryptic.network.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 598Johnn897
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo
{

	String id();

	String name();

	String version();

	String author() default "";

	String[] authors() default "";

	String description() default "";

	String[] depend() default "";

}
