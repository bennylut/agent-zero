package bgu.dcr.az.api.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation that attach a metadata about a method that should be used as a message receiver
 * @author bennyl
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WhenReceived {
    /**
     * 
     * @return the name of the message the given method should handle
     */
    String value();
}
