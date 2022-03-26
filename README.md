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
  * Add below properties in application.properties
    * schedule.job.groups=propGroup
    * schedule.job.propGroup.scheduleinfo={"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob1"}
  
</details>

***
<details><summary>Configure multiple jobs Using Property</summary>
  
  * One Group can have multiple jobs (delimter used **~**)
  * Add below properties in application.properties
    * schedule.job.groups=**propGroup~propGroup1**
    * schedule.job.**propGroup**.scheduleinfo={"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob"}**~**{"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob1"}
    * schedule.job.**propGroup1**.scheduleinfo={"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob"}**~**{"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob1"}
  
</details>

***
<details><summary>How to modify job data map programmatically</summary>
  
  * Create a bean by implementing JobDataMapConfigurer 
  
</details>
