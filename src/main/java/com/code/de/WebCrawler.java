package com.code.de;

import java.util.List;

public class WebCrawler {

    interface HtmlParser {

        public List<String> getUrls(String url);
    }

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {

    }
}

