package ru.job4j.quartz;

import org.quartz.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.*;

import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {


    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            try (Connection cn = getConnection()) {
                data.put("store", store);
                data.put("connection", cn);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(getInterval())
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
                System.out.println(store);
            }
        } catch (Exception se) {
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

    private static Connection getConnection() throws Exception {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("c:/projects/job4j_grabber/src/main/resources/rabbit.properties")) {
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Class.forName(properties.getProperty("jdbc.driver"));
        String url = properties.getProperty("jdbc.url");
        String login = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        return DriverManager.getConnection(url, login, password);

    }

    private static long insert(long date, Connection cn) {
        try (PreparedStatement statement =
                     cn.prepareStatement("insert into rabbit(create_date) values (?)")) {
            statement.setTimestamp(1, new Timestamp(date));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }


    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            long time = System.currentTimeMillis();
            store.add(time);
            insert(time, cn);
        }
    }
}