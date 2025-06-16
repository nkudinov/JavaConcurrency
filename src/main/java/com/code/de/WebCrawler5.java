package com.code.de;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler5 {

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

        Task(String url, HtmlParser htmlParser, Set<String> seen) {
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
                    tasks.add(new Task(next, htmlParser, seen));
                }
            }
            invokeAll(tasks);
            return null;
        }

    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ForkJoinPool pool = new ForkJoinPool(10);
        Set<String> seen = ConcurrentHashMap.newKeySet();
        seen.add(startUrl);
        return new ArrayList<>(seen);
    }
}
