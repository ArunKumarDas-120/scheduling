package org.scheduler.model;

import lombok.Data;

@Data
public class JobScheduleInfo {
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String desc;
    private Long repeatTime;
}
