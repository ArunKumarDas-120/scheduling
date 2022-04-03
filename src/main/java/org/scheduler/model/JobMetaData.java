package org.scheduler.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobMetaData {

    private Map<String, List<JobScheduleInfo>> jobConfig = new HashMap<>();

    public Map<String, List<JobScheduleInfo>> getJobConfig() {
        return jobConfig;
    }
}
