package org.scheduler.config;


import org.scheduler.model.JobScheduleInfo;
import org.scheduler.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Set;


public class StartSchedule {

    @Autowired
    private ScheduleService service;
    @Autowired
    private ApplicationContext applicationContext;


    private void startSchedule(){
        Set<JobScheduleInfo> jobInfoSet = (Set<JobScheduleInfo>) applicationContext.getBean("jobRegistry");
        jobInfoSet.forEach(j->service.createJob(j));
    }


}
