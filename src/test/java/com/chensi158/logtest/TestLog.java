package com.chensi158.logtest;

import com.chensi158.logtest.controller.TestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

/**
 * @author chensi158
 * @date 2021/4/23 6:44 下午
 */
public class TestLog {

    @Test
    public void testSyncLog() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService pool = new ThreadPoolExecutor(64, 64,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        Logger log = LogManager.getLogger("com.chensi158.logtest.controller.TestController");
        int size = 1000000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for(int i = 0; i < size; i++) {
            LogTask task = new LogTask("index-" + i, log, countDownLatch);
            pool.submit(task);
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("1000w条日志通过64线程池同步打印耗时：" + (endTime - startTime) + "ms");
    }

    @Test
    public void testASyncLog() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ExecutorService pool = new ThreadPoolExecutor(64, 64,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        Logger log = LogManager.getLogger("RollingFile2");
        int size = 1000000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for(int i = 0; i < size; i++) {
            LogTask task = new LogTask("index-" + i, log, countDownLatch);
            pool.submit(task);
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("1000w条日志通过64线程池异步打印耗时：" + (endTime - startTime) + "ms");
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
