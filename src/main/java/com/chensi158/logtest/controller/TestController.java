package com.chensi158.logtest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;


/**
 * @author chensi158
 * @date 2021/4/22 10:39 上午
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        System.out.println("hello world");
        log.info("hello world!");
        return "hello, world";
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService pool = new ThreadPoolExecutor(64, 64,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        Logger log = LogManager.getLogger(TestController.class.getName());
        int size = 10000000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for(int i = 0; i < size; i++) {
            LogTask task = new LogTask("index-" + i, log, countDownLatch);
            pool.submit(task);
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("1000w条日志通过64线程池同步打印耗时：" + (endTime - startTime) + "ms");
    }

    static class LogTask implements Runnable {
        private String msg;
        private Logger log;
        private CountDownLatch countDownLatch;

        public LogTask(String msg, Logger log, CountDownLatch countDownLatch) {
            this.msg = msg;
            this.log = log;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            log.info("threadId:" + Thread.currentThread().getId() + ", msg: " + msg);
            countDownLatch.countDown();
        }
    }
}
