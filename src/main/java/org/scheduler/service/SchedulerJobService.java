package org.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.scheduler.model.JobScheduleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class SchedulerJobService implements ScheduleService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    @Override
    public boolean createJob(JobScheduleInfo info) {
        Objects.requireNonNull(info, "JobScheduleInfo must not be null");
        return this.scheduleNewJob(info);
    }

    @Override
    public SchedulerMetaData getMetaData() throws SchedulerException {
        return scheduler.getMetaData();
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
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
                        jobInfo.getJobName(), jobInfo.getJobGroup());

                Trigger trigger;
                if (StringUtils.hasLength(jobInfo.getCronExpression())) {
                    trigger = JobScheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = JobScheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                log.info(">>>>> jobName {}  scheduled.", jobInfo.getJobName());
                result = Boolean.TRUE;
            } else {
                log.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

}
