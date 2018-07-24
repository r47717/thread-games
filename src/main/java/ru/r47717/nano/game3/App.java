package ru.r47717.nano.game3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    public static void main(String[] args) throws InterruptedException {
        int THREAD_NUM = 10000;
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < THREAD_NUM; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        start.countDown();

        while (counter.get() != THREAD_NUM) {
            Thread.sleep(10);
            System.out.println(counter.get());
        }

        System.out.println("ALL DONE");
    }
}
