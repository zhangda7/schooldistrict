package com.pt.schooldistrict.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Created by da.zhang on 16/2/20.
 */
public class HouseCronJob implements Job {
    @Override
    public void execute(JobExecutionContext jobCtx) throws JobExecutionException {
        System.out.println(jobCtx.getTrigger().getJobKey() + "time is " + new Date());
    }
}
