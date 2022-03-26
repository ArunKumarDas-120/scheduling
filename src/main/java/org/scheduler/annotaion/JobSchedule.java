package org.scheduler.annotaion;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobSchedule {

    String jobGroup();

    String jobName();

    String cornExpression() default "" ;

    String simpleJob() default "";

    String startOnDateAndTime() default "";

    String timeZone() default "";

    int repeat() default 0 ;

    String jobData() default "";
}
