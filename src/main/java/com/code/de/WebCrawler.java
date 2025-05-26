package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ConcurrentQueue<CompletableFuture<Void>> tasks = new ConcurrentQueue<>();
        Set<String> seen = ConcurrentHashMap.newKeySet();
        ExecutorService service = Executors.newFixedThreadPool(10);
        seen.add(startUrl);
        submit(tasks, startUrl, htmlParser, seen, service);
        while (!tasks.isEmpty()) {
            tasks.poll().join();
        }
        service.shutdown();
        return new ArrayList<>(seen);
    }

    private void submit(ConcurrentQueue<CompletableFuture<Void>> tasks, String startUrl, HtmlParser htmlParser,
        Set<String> seen, ExecutorService service) {
        String home = getHome(startUrl);
        tasks.add(CompletableFuture.runAsync(
            () -> {
                for (String next : htmlParser.getUrls(startUrl)) {
                    if (home.equals(getHome(next)) && seen.add(next)) {
                        submit(tasks, next, htmlParser, seen, service);
                    }
                }
            }
            , service));
    }
}

interface  HtmlParser {

    public List<String> getUrls(String url);
}



