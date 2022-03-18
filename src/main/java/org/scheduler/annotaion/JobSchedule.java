package org.scheduler.annotaion;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JobSchedule {

    String jobGroup();

    String jobName();

    String cornExpression() default "";

    String simpleJob() default "";

    String startOnDateAndTime() default "";

    String timeZone() default "";

    int repeat() default 0;
}
