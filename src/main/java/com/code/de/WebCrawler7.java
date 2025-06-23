package com.code.de;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler7 {
    private String getHostName(String url) {
        // Assumes URL starts with "http://" or "https://"
        int idx = url.indexOf("/", 8); // skip "http://" (7) or "https://" (8)
        if (idx == -1) return url;
        return url.substring(0, idx);
    }

    private class Task extends RecursiveTask<Void> {
        private final String url;
        private final Set<String> seen;
        private final HtmlParser htmlParser;

        Task(String url, Set<String> seen, HtmlParser htmlParser) {
            this.url = url;
            this.seen = seen;
            this.htmlParser = htmlParser;
        }

        @Override
        protected Void compute() {
            String host = getHostName(url);
            List<Task> tasks = new ArrayList<>();

            for (String nextUrl : htmlParser.getUrls(url)) {
                if (getHostName(nextUrl).equals(host) && seen.add(nextUrl)) {
                    Task task = new Task(nextUrl, seen, htmlParser);
                    task.fork(); // fork instead of collect in a list
                    tasks.add(task);
                }
            }

            for (Task task : tasks) {
                task.join(); // wait for completion
            }

            return null;
        }
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);

        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new Task(startUrl, seen, htmlParser));

        return new ArrayList<>(seen);
    }
}
