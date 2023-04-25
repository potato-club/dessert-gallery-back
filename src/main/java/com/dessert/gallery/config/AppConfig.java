package com.dessert.gallery.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.support.CronTrigger;
//
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
// 자료 조사 중
//@Configuration
//@EnableScheduling
//public class AppConfig implements SchedulingConfigurer {
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.setScheduler(taskExecutor());
//        taskRegistrar.addTriggerTask(
//                () -> { /* 실행될 작업 내용 */ },
//                triggerContext -> new CronTrigger("*/5 * * * * ?").nextExecutionTime(triggerContext)
//        );
//    }
//
//    @Bean(destroyMethod = "shutdown")
//    public Executor taskExecutor() {
//        return Executors.newScheduledThreadPool(10);
//    }
//}
