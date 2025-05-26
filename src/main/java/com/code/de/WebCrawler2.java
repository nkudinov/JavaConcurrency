package com.code.de;



import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler2 implements WebCrawlerInterface {

    String getHome(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    private class Task extends RecursiveTask<Void> {

        private final String url;
        private final Set<String> seen;

        private final HtmlParser htmlParser;

        private Task(String url, Set<String> seen, HtmlParser htmlParser) {
            this.url = url;
            this.seen = seen;
            this.htmlParser = htmlParser;
        }

        @Override
        protected Void compute() {
            String home = getHome(url);
            List<Task> tasks = new ArrayList<>();
            for (String next : htmlParser.getUrls(url)) {
                if (home.equals(getHome(next)) && seen.add(next)) {
                    tasks.add(new Task(next, seen, htmlParser));
                }
            }
            invokeAll(tasks);
            return null;
        }
    }

    @Override
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        forkJoinPool.invoke(new Task(startUrl, seen, htmlParser));
        forkJoinPool.shutdown();
        return new ArrayList<>(seen);
    }
}
