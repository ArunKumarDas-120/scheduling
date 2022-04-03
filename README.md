#### Info
Quartz dynamic scheduling
***
<details><summary>How To use</summary>
  
  * Create a jobClass by extending QuartzJobBean
  * Annotate each job class with @JobSchedule or configure using property
  * Add @EnableJobScheduling passing package for JobClass to Scanned and configure
  
</details>

 ***
<details><summary>How To pass job Data</summary>
  
  * @JobSchedule(jobGroup = "group", jobName = "name", cornExpression = "*/5 * * * * ? *", jobData = "{\"YY\" : 1 , \"xxx\" : \"zzz\"}")
  * schedule.job.propGroup.scheduleinfo={"cronExpression": "exp","jobName": "someJob","jobClass": "jobClass", "jobDataMap" : {"key": "value"}}
  
</details>

***
<details><summary>Configure Using Property</summary>
  
  * Create a jobClass by extending QuartzJobBean
  * One job group can contain multiple job
  * Add below properties in application.properties or any other property
    * job.scheduler.jobConfig.[group][0].cronExpression=*/5 * * * * ? *
    * job.scheduler.jobConfig.[group][0].jobName=someJob1
    * job.scheduler.jobConfig.[group][0].jobClass=com.scheduling.job.SomeJob1
    * job.scheduler.jobConfig.[group][0].jobDataMap[key]=value
    
    * job.scheduler.jobConfig.[group][1].cronExpression=*/5 * * * * ? *
    * job.scheduler.jobConfig.[group][1].jobName=someJob
    * job.scheduler.jobConfig.[group][1].jobClass=com.scheduling.job.SomeJob
    * job.scheduler.jobConfig.[group][1].jobDataMap[key]=value
    
    * job.scheduler.jobConfig.[group1][1].cronExpression=*/5 * * * * ? *
    * job.scheduler.jobConfig.[group1][1].jobName=someJob
    * job.scheduler.jobConfig.[group1][1].jobClass=com.scheduling.job.SomeJob
    * job.scheduler.jobConfig.[group1][1].jobDataMap[key]=value
    
  * above properties indicate two jobgroup **group** and **group1**. fist group contains two job , and second group contains one 
  
</details>


<details><summary>How to modify job data map programmatically</summary>
  
  * Create a bean by implementing JobDataMapConfigurer.
  * Each JobDataMapConfigurer must qualify with job name.@Component(value="jobName")  
  
</details>
