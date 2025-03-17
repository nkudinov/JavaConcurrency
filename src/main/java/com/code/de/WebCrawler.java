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
    static String getHome(String url) {
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for (char ch : url.toCharArray()) {
            if (ch == '/') {
                cnt++;
            } else if (cnt == 3) {
                break;
            } else if (cnt == 2) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Queue<CompletableFuture<List<String>>> q = new LinkedList<>();
        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(startUrl)));
        Set<String> seen = new HashSet<>();
        seen.add(startUrl);
        String home = getHome(startUrl);
        while (!q.isEmpty()) {
            var cur = q.poll();
            try {
                List<String> nextUrls = cur.get();
                for (String next : nextUrls) {
                    if (home.equals(getHome(next)) && seen.add(next)) {
                        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(next)));
                    }
                }
            } catch (Exception e) {

            }
        }
        return new ArrayList<>(seen);
    }
    public List<String> crawl1(String startUrl, HtmlParser htmlParser) {
        BlockingQueue<CompletableFuture<Void>> tasks = new LinkedBlockingQueue<>();
        Set<String> seen = ConcurrentHashMap.newKeySet();
        String home = getHome(startUrl);
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)){
            submit(startUrl, tasks, seen, executorService, htmlParser);
            CompletableFuture<Void> cur = null;
            while ((cur = tasks.poll())!= null) {
                cur.join();
            }
        }
        return new ArrayList<>(seen);
    }

    private static void submit(String startUrl, BlockingQueue<CompletableFuture<Void>> tasks, Set<String> seen,
        ExecutorService executorService, HtmlParser htmlParser) {
        tasks.add(CompletableFuture.runAsync(() -> {
            seen.add(startUrl);
            for(String next:htmlParser.getUrls(startUrl)) {
                if (getHome(startUrl).equals(getHome(next)) && seen.add(next)) {
                    submit(next, tasks, seen, executorService, htmlParser);
                }
            }
        }));
    }
}

    interface HtmlParser {

        public List<String> getUrls(String url);
    }



