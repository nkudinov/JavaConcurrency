package com.code.de;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler {

    String getHome(String url) {
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for (char ch : url.toCharArray()) {
            if (ch == '/') {
                cnt++;
            } else if (cnt == 2) {
                sb.append(ch);
            } else if (cnt == 3) {
                break;

            }
        }
        return sb.toString();
    }

    public List<String> crawl1(String startUrl, HtmlParser htmlParser) {
        Queue<CompletableFuture<List<String>>> queue = new LinkedList<>();
        String home = getHome(startUrl);
        Set<String> seen = new HashSet<>();
        seen.add(startUrl);
        queue.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(startUrl)));
        while (!queue.isEmpty()) {
            CompletableFuture<List<String>> cur = queue.poll();
            try {
                List<String> urls = cur.get();
                for (String next : urls) {
                    if (home.equals(getHome(next)) && seen.add(next)) {
                        queue.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(next)));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("There was an issue");
            }
        }
        return new ArrayList<>(seen);
    }

    public List<String> crawl3(String startUrl, HtmlParser htmlParser) {
        String home = getHome(startUrl);
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Set<String> seen = ConcurrentHashMap.newKeySet();
        queue.add(startUrl);
        seen.add(startUrl);
        AtomicInteger counter = new AtomicInteger(1);
        List<Thread> workers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Thread worker = new Thread() {
                @Override
                public void run() {
                    while (counter.get() != 0) {
                        String url = queue.poll();
                        if (url != null) {
                            for (String nextUrl : htmlParser.getUrls(url)) {
                                if (home.equals(getHome(nextUrl)) && seen.add(nextUrl)) {
                                    counter.incrementAndGet();
                                    queue.add(nextUrl);
                                }
                            }
                            counter.decrementAndGet();
                        }
                    }
                }
            };
            worker.start();
            workers.add(worker);
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(seen);
    }



}

interface HtmlParser {

    public List<String> getUrls(String url);
}



