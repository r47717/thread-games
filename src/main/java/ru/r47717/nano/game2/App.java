package ru.r47717.nano.game2;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class App
{
    public static void main(String[] args) throws InterruptedException {
        String[] urls = new String[] {
          "http://google.com",
          "http://yandex.ru",
          "http://yahoo.com",
          "http://microsoft.com",
          "http://amazone.com"
        };

        List<String> results = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newCachedThreadPool();
        CyclicBarrier start = new CyclicBarrier(urls.length);
        CountDownLatch done = new CountDownLatch(urls.length);

        Arrays.stream(urls).forEach(url -> {
            executor.submit(new Crawler(url, start, done, results));
        });

        done.await();

        results.forEach(System.out::println);
    }
}

class Crawler implements Runnable
{
    private String url;
    private CyclicBarrier start;
    private CountDownLatch done;
    private List<String> results;

    public Crawler(String url, CyclicBarrier start, CountDownLatch done, List<String> results) {
        this.url = url;
        this.start = start;
        this.done = done;
        this.results = results;
    }

    @Override
    public void run() {
        try {
            start.await();
            long result = task();
            results.add(String.format("%s: %d", url, result));
        } catch (InterruptedException | BrokenBarrierException | IOException e) {
            e.printStackTrace();
        } finally {
            done.countDown();
        }
    }

    private long task() throws IOException {
        OkHttpClient http = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        String resp = http.newCall(request).execute().body().string();

        return resp.length();
    }
}