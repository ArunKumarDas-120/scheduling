package org.scheduler.config;


import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.scheduler.model.JobScheduleInfo;
import org.scheduler.service.JobScheduleCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class StartSchedule {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired(required = false)
    Map<String, JobDataMapConfigurer> configurerMap;


    private void startSchedule() {
        Set<JobScheduleInfo> jobInfoSet = (Set<JobScheduleInfo>) applicationContext.getBean("jobRegistry");
        jobInfoSet.forEach(j -> {
            Map<String, Object> dataMap = null;
            JobDataMapConfigurer configurer = null;
            if (Objects.nonNull(configurerMap)) {
                configurer = configurerMap.get(j.getJobName());
                if (Objects.nonNull(configurer))
                    dataMap = configurer.configure(j.getJobName(), j.getJobGroup());
            }

            if (Objects.nonNull(dataMap)) {
                if (Objects.nonNull(j.getJobDataMap()))
                    j.getJobDataMap().putAll(dataMap);
                else
                    j.setJobDataMap(dataMap);
            }
            this.scheduleNewJob(j);
        });
    }

    private boolean scheduleNewJob(JobScheduleInfo jobInfo) {
        boolean result = Boolean.FALSE;
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {

                jobDetail = JobScheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, applicationContext,
                        jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo.getJobDataMap());

                Trigger trigger;
                if (StringUtils.hasLength(jobInfo.getCronExpression())) {
                    trigger = JobScheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = JobScheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                result = Boolean.TRUE;
            } else {
            }

        } catch (ClassNotFoundException | SchedulerException e) {
            e.printStackTrace();
        }

        return result;
    }

}
