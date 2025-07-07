package com.code.de;

import com.code.de.WebCrawler1.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class WebCrawler9 implements WebCrawlerInterface{
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
        private final Set<String> seen;
        private final String startUrl;
        private final HtmlParser htmlParser;

        public Task(HtmlParser htmlParser, Set<String> seen, String startUrl) {
            this.htmlParser = htmlParser;
            this.seen = seen;
            this.startUrl = startUrl;
        }
        @Override
        protected Void compute() {
            List<Task> tasks = new LinkedList<>();
            String home = getHome(startUrl);
            for(String next:htmlParser.getUrls(startUrl)){
                if (home.equals(getHome(next)) && seen.add(next)) {
                    tasks.add(new Task(htmlParser,seen,next));
                }
            }
            invokeAll(tasks);
            return null;
        }
    }
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10 );
        Set<String> seen = ConcurrentHashMap.newKeySet();
        forkJoinPool.invoke( new Task( htmlParser, seen,startUrl) );
        return new ArrayList<>(seen);
    }
}
