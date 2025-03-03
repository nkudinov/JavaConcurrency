package com.code.de;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class WebCrawler {

    interface HtmlParser {

        public List<String> getUrls(String url);
    }


    static String getHome(String url) {
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (char ch : url.toCharArray()) {
            if (ch == '/') {
                cnt++;
            } else if (cnt == 3) {
                break;

            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public List<String> crawl1(String startUrl, HtmlParser htmlParser) {
        Queue<CompletableFuture<List<String>>> q = new LinkedList<>();
        Set<String> seen = new HashSet<>();
        seen.add(startUrl);
        String home = getHome(startUrl);
        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(startUrl)));
        while (!q.isEmpty()) {
            CompletableFuture<List<String>> cur = q.poll();
            try {
                List<String> urls = cur.get();
                for (String nextUrl : urls) {
                    if (!seen.contains(nextUrl) && home.equals(getHome(nextUrl))) {
                        seen.add(nextUrl);
                        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(nextUrl)));
                    }
                }
            } catch (Exception e) {

            }
        }
        return new ArrayList<>(seen);
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        // each worker run code to get list of urls and put next batch in a queue
        BlockingQueue<CompletableFuture<Void>> q = new LinkedBlockingQueue<>();
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        String home = getHome(startUrl);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        submit(home, startUrl, htmlParser, q, seen, executorService);
        CompletableFuture<Void> cur;
        while ((cur = q.poll()) != null) {
            cur.join();
        }
        executorService.shutdown();
        return new ArrayList<>(seen);
    }

    private static void submit(String home, String startUrl, HtmlParser htmlParser,
        BlockingQueue<CompletableFuture<Void>> q,
        Set<String> seen, ExecutorService executorService) {
        q.add(CompletableFuture.runAsync(() -> {
            for (String nextUrl : htmlParser.getUrls(startUrl)) {
                if (home.equals(getHome(nextUrl))) {
                    if (seen.add(nextUrl)) {
                        submit(home, nextUrl, htmlParser, q, seen, executorService);
                    }
                }
            },executorService));
        };
    }
}

