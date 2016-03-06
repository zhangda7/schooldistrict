package com.pt.schooldistrict.scheduler;

import com.pt.schooldistrict.MyConsolePipeline;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import us.codecraft.webmagic.Spider;

import java.util.TimeZone;

/**
 * Created by da.zhang on 16/2/20.
 */
public class SchoolScheduler {

    public static void startQuartz() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(HouseCronJob.class).withIdentity("job1", "group1").build();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").
                    startNow().withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(11,10).inTimeZone(TimeZone.getDefault())).build();
            //SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(5)).build();

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            sched.scheduleJob(jobDetail, trigger);
            sched.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void testOneRound() {
        Spider.create(new HouseCronJob()).
                addUrl("https://www.baidu.com").
                thread(5).run();
    }

    public static void main(String[] args) {
        testOneRound();
        //startQuartz();
    }

}
