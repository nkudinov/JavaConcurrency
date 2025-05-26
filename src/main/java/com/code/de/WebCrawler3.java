package com.code.de;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler3 implements WebCrawlerInterface {

    String getHome(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String home = getHome(startUrl);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        seen.add(startUrl);
        BlockingQueue<CompletableFuture<Void>> tasks = new ArrayBlockingQueue<>(10);
        try {
            submit(startUrl, seen, htmlParser, tasks, executorService);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        return new ArrayList<>(seen);
    }

    private void submit(String startUrl, Set<String> seen, HtmlParser htmlParser,
        BlockingQueue<CompletableFuture<Void>> tasks, ExecutorService executorService) throws InterruptedException {
        String home = getHome(startUrl);
        tasks.put(CompletableFuture.runAsync(() -> {
            for (String next : htmlParser.getUrls(startUrl)) {
                if (home.equals(getHome(next)) && seen.add(next)) {
                    try {
                        submit(next, seen, htmlParser, tasks, executorService);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }

    ;
}

