package com.qianqiu;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;


import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by qiuqian on 8/5/18.
 * Create a 'callable' task that will visit a web page using its url
 * and find all the links on that page;
 * This 'callable' task can be invoked from a Java ExecutorService;
 * By implementing Callable interface, the object can run in a separate thread.
 * In addition, it can return itself so we can retrieve the information it contains.
 */
public class GrabPage implements Callable<GrabPage> {
    static final int TIMEOUT = 60000;
    private URL url;
    private Set<URL> urllist = new HashSet<URL>();

    public GrabPage(URL url) {
        this.url = url;
    }

    public GrabPage call() throws Exception {
        Document document = null;
        document = Jsoup.parse(url, TIMEOUT);

        // Jsoup selector finds all the "a" tags with a "href" attribute
        Elements links = document.select("a[href]");

        for (Element link : links) {
            String href = link.attr("href");
            if (StringUtil.isBlank(href) || href.startsWith("#")) {
                continue;
            }

            try {
                URL nextUrl = new URL(url, href);
                urllist.add(nextUrl);
            } catch (MalformedURLException e) {
                System.out.println("MalformedURL: " + href);
            }
        }
        return this;
    }

    public void printAllLinks() {
        for (URL url : urllist) {
            System.out.println("Links to " + url.toString());
        }
    }
}
