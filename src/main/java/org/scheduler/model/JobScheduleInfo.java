package org.scheduler.model;

import lombok.Data;

import java.util.Map;

@Data
public class JobScheduleInfo {
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String desc;
    private Long repeatTime;
    private Map<String,Object> jobDataMap;
}
