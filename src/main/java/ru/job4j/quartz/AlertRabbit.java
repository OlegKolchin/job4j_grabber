package ru.job4j.quartz;

import org.quartz.*;
import java.io.*;
import java.util.Properties;

import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static int getInterval() {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("c:/projects/job4j_grabber/src/main/resources/rabbit.properties")) {
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = properties.getProperty("rabbit.interval");
        return Integer.parseInt(s);
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}