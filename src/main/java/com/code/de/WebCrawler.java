package com.code.de;

import java.util.ArrayList;
import java.util.List;
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
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
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

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        BlockingQueue<CompletableFuture<Void>> tasks = new LinkedBlockingQueue<>();
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        submit(startUrl, seen, tasks, executorService, htmlParser);

        CompletableFuture<Void> cur;
        while ((cur = tasks.poll()) != null) {
            cur.join();
        }

        executorService.shutdown();
        return new ArrayList<>(seen);
    }

    private static void submit(String startUrl, Set<String> seen, BlockingQueue<CompletableFuture<Void>> tasks,
        ExecutorService executorService, HtmlParser htmlParser) {
        tasks.add(CompletableFuture.runAsync(() -> {
            for (String nextUrl : htmlParser.getUrls(startUrl)) {
                if (getHome(startUrl).equals(getHome(nextUrl)) && seen.add(nextUrl) ) {
                    submit(nextUrl, seen, tasks, executorService, htmlParser);
                }
            }
        }, executorService));
    }


}

