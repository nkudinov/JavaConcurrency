package com.code.de;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler1 {

    interface HtmlParser {

        List<String> getUrls(String url);
    }

    String getHome(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    class Task extends RecursiveTask<Void> {

        private final Set<String> seen;
        private final HtmlParser htmlParser;
        private final String url;

        public Task(Set<String> seen, HtmlParser htmlParser, String url) {
            this.seen = seen;
            this.htmlParser = htmlParser;
            this.url = url;
        }

        @Override
        protected Void compute() {
            List<Task> subtasks = new ArrayList<>();
            for (String next : htmlParser.getUrls(url)) {
                if (getHome(url).equals(getHome(next)) && seen.add(next)) {
                    Task task = new Task(seen, htmlParser, next);
                    subtasks.add(task);
                }
            }
            invokeAll(subtasks);
            return null;
        }
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        Task rootTask = new Task(seen, htmlParser, startUrl);
        forkJoinPool.invoke(rootTask);
        return new ArrayList<>(seen);
    }
}
