package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebCrawler8 implements WebCrawlerInterface {

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
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
        ExecutorService executor = new ThreadPoolExecutor(10, 10, 1000, TimeUnit.MICROSECONDS, queue);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        dfs(startUrl, htmlParser, seen);
        return new ArrayList<>(seen);
    }

    private void dfs(String startUrl, HtmlParser htmlParser, Set<String> seen) {
        String home = getHome(startUrl);
        for (String url : htmlParser.getUrls(startUrl)) {
            if (home.equals(getHome(url)) && seen.add(url)) {
                dfs(url, htmlParser, seen);
            }
        }
    }

}
