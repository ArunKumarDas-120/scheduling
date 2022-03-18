package org.scheduler.service;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.scheduler.model.JobScheduleInfo;

public interface ScheduleService {

    boolean createJob(JobScheduleInfo info);

    SchedulerMetaData getMetaData() throws SchedulerException;
}
