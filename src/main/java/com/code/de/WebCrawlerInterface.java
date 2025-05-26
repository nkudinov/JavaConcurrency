package com.code.de;

import java.util.List;

public interface WebCrawlerInterface {

    List<String> crawl(String startUrl, HtmlParser htmlParser);

    public interface HtmlParser {

        List<String> getUrls(String url);
    }
}
