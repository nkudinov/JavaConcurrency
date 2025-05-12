package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler1 {

    interface HtmlParser {

        public List<String> getUrls(String url);
    }

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

    private class Task extends RecursiveTask<Void> {

        private String url;
        private HtmlParser htmlParser;

        private Set<String> seen;

        public Task(String url, HtmlParser htmlParser, Set<String> seen) {
            this.url = url;
            this.htmlParser = htmlParser;
            this.seen = seen;
        }

        @Override
        protected Void compute() {
            List<Task> tasks = new ArrayList<>();
            for (String next : htmlParser.getUrls(url)) {
                if (getHome(url).equals(getHome(next)) && seen.add(next)) {
                    tasks.add(new Task(next, htmlParser, seen));
                }
            }
            invokeAll(tasks);

            return  null;
        }
    }

    public List<String> crawl(String startUrl,  HtmlParser htmlParser) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        forkJoinPool.execute(new Task(startUrl, htmlParser, seen));
        forkJoinPool.shutdown();
        return new ArrayList<>(seen);
    }
}
