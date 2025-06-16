package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawler6 implements WebCrawlerInterface {

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

    @Override
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Set<String> seenUrls = ConcurrentHashMap.newKeySet();
        seenUrls.add(startUrl);
        AtomicInteger count = new AtomicInteger(1);
        Semaphore semaphore = new Semaphore(0);
        crawl(startUrl, htmlParser, seenUrls, executorService, count, semaphore);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        return new ArrayList<>(seenUrls);
    }

    private void crawl(String url, HtmlParser htmlParser, Set<String> seenUrls,
        ExecutorService executorService, AtomicInteger count, Semaphore semaphore) {
        String home = getHome(url);

        CompletableFuture.runAsync(() -> {
            for (String next : htmlParser.getUrls(url)) {
                if (home.equals(getHome(next)) && seenUrls.add(next)) {
                    count.incrementAndGet();
                    crawl(next, htmlParser, seenUrls, executorService, count, semaphore);
                }
            }
        }, executorService).whenComplete((result, error) -> {
            if (count.decrementAndGet() == 0) {
                semaphore.release();
            }
        });
    }
}
