package com.lft.netty.test_2018_12_5;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * jdk 的任务调度
 * @author Ryze
 * @date 2018-12-05 17:28
 */
public class ScheduledThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        executorService.schedule(() -> System.out.println("60 秒执行一次"), 60, TimeUnit.SECONDS);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
