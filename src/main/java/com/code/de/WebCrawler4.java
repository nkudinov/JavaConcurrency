package com.code.de;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class WebCrawler4 implements WebCrawlerInterface {

    String getHome(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private class Task extends RecursiveAction {
        String url;
        HtmlParser htmlParser;
        Set<String> seen;

        public Task(String url, Set<String> seen, HtmlParser htmlParser) {
            this.url = url;
            this.htmlParser = htmlParser;
            this.seen = seen;
        }

        @Override
        protected void compute() {
            String home = getHome(url);
            List<Task> tasks = new ArrayList<>();
            for (String next : htmlParser.getUrls(url)) {
                if (home.equals(getHome(next)) && seen.add(next)) {
                    tasks.add(new Task(next, seen, htmlParser));
                }
            }
            invokeAll(tasks);
        }
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        ForkJoinPool pool = new ForkJoinPool(10);
        pool.invoke(new Task(startUrl, seen, htmlParser));
        pool.shutdown();
        return new ArrayList<>(seen);
    }
}
