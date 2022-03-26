#### Info
Quartz dynamic scheduling
#### How To use
  1. Create a jobClass by extending QuartzJobBean
  2. Annotate each job class with @JobSchedule
  3. Add @EnableJobScheduling passing package for JobClass to Scanned and configure
  
<details><summary>How To pass job Data</summary>
<p>
  @JobSchedule(jobGroup = "group", jobName = "name", cornExpression = "*/5 * * * * ? *", jobData = "{\"YY\" : 1 , \"xxx\" : \"zzz\"}")
</p>
</details>
    
<details><summary>Configure Using Property</summary>
<p>
  1. Create a jobClass by extending QuartzJobBean
  2. Add below properties in application.properties
    schedule.job.groups=propGroup
    schedule.job.propGroup.scheduleinfo={"cronExpression": "*/5 * * * * ? *","jobName": "someJob","jobClass": "com.scheduling.job.SomeJob1"}
</p>
</details>
