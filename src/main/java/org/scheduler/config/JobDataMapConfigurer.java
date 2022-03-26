package org.scheduler.config;

import org.scheduler.model.JobScheduleInfo;

import java.util.Map;

public interface JobDataMapConfigurer {

    Map<String,Object> configure(String jobName,String jobGroup );
}
