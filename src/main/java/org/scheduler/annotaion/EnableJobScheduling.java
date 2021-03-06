package org.scheduler.annotaion;


import org.scheduler.config.AnnotationScanConfig;
import org.scheduler.config.SchedulingRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({AnnotationScanConfig.class,SchedulingRegistrar.class})
public @interface EnableJobScheduling {

    String packageToScan() default "";

}
