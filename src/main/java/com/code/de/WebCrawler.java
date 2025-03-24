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
    String getHome(String url) {
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        for(char ch:url.toCharArray()) {
            if (ch=='/') {
                cnt++;
                if (cnt == 3) {
                    break;
                }
            } else if (cnt == 2) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        String home = getHome(startUrl);
        Queue<CompletableFuture<List<String>>> q = new LinkedList<>();
        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(startUrl)));
        Set<String> seen = new HashSet<>();
        seen.add(startUrl);
        while(!q.isEmpty()) {
            var cur = q.poll();
            try {
                List<String> nextUrls = cur.get();
                for(String nextUrl:nextUrls) {
                    if (home.equals(getHome(nextUrl)) && seen.add(nextUrl)) {
                        q.add(CompletableFuture.supplyAsync(() -> htmlParser.getUrls(nextUrl)));
                    }
                }
            } catch(InterruptedException |ExecutionException e) {

            }
        }
        return new ArrayList<>(seen);
    }
}

    interface HtmlParser {

        public List<String> getUrls(String url);
    }



