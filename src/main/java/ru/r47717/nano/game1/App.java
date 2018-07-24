package ru.r47717.nano.game1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


/**
 * 1. start random (up to 30) number of threads at the same time
 * 2. each thread is calculating its own task with number of
 */
public class App
{
    public static void main( String[] args ) throws InterruptedException
    {
        final int THREADS_NO = new Random().nextInt(30);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(THREADS_NO);
        List<Future<Long>> futures = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(THREADS_NO);

        for (int i = 0; i < THREADS_NO; i++) {
            futures.add(executor.submit(new Worker(i, start, done)));
        }

        start.countDown();
        done.await();

        futures.forEach(item -> {
            try {
                System.out.println(item.isDone() ? item.get() : "NOT DONE");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}

class Worker implements Callable<Long> {
    private int id;
    private CountDownLatch start;
    private CountDownLatch done;
    private long data = 0;

    Worker(int id, CountDownLatch start, CountDownLatch done) {
        this.id =  id;
        this.start = start;
        this.done = done;
    }

    @Override
    public Long call() throws Exception {
        start.await();

        int reps = new Random().nextInt(100);

        for (int i = 0; i < reps; i++) {
            calc();
            Thread.sleep(100);
        }

        done.countDown();

        return data;
    }

    private void calc() {
        data += id;
    }
}
