package com.pt.schooldistrict.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created by da.zhang on 16/2/20.
 */
public class SchoolScheduler {

    public static void main(String[] args) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(HouseCronJob.class).withIdentity("job1", "group1").build();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").
                    startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(5)).build();

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            sched.scheduleJob(jobDetail, trigger);
            sched.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }


    }

}
