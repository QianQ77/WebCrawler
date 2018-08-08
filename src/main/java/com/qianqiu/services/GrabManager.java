package com.qianqiu;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiPredicate;

/**
 * Created by qiuqian on 8/6/18.
 */
public class GrabManager {

    public static final int THREAD_COUNT = 5;
    private static final int PAUSE_TIME = 1000;

    // contains urls have been visited
    private Set<URL> visitedURLSet = new HashSet<URL>();
    // Future objects are how we can check the status of a thread and get the results of the operation
    private List<Future<GrabPage>> futures = new ArrayList<Future<GrabPage>>();
    private ExecutorService excecutorService = Executors.newFixedThreadPool(THREAD_COUNT);

    private String urlBase;

    private final int maxDepth;
    private final int maxUrls;

    public GrabManager(int maxDepth, int maxUrls) {
        this.maxDepth = maxDepth;
        this.maxUrls = maxUrls;
    }

    public void go(URL start) throws InterruptedException{

        //Stay within the same site
        urlBase = start.toString().replaceAll("(.*//.*/).*", "$1");

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        submitNewURL(start, 0);

        while(checkPageGrabs());

        stopWatch.stop();

        System.out.println("Found" + visitedURLSet.size() + " urls");
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");

    }

    // Create the GrabPage object for a given url and drops it on the execution queue
    private void submitNewURL(URL url, int depth) {
        if (shouldVisit(url, depth)) {
            visitedURLSet.add(url);

            //visit this url
            GrabPage grabPage = new GrabPage(url, depth);
            Future future = excecutorService.submit(grabPage);
            futures.add(future);

        }
    }

    private boolean shouldVisit(URL url, int depth) {
        if (depth > maxDepth) {
            return false;
        }
        if (visitedURLSet.contains(url)) {
            return false;
        }
        if (!url.toString().startsWith(urlBase)) {
            return false;
        }
        if (url.toString().endsWith(".pdf")) {
            return false;
        }
        if (visitedURLSet.size() >= maxUrls) {
            return false;
        }
        return true;
    }

    /**
     * THis method checks the status of all the threads and collects their work results
     * @return
     * @throws InterruptedException
     */
    private boolean checkPageGrabs() throws InterruptedException{
        //inject some sleep to avoid using all the resources
        Thread.sleep(PAUSE_TIME);
        Set<GrabPage> pageSet = new HashSet<GrabPage>();
        Iterator<Future<GrabPage>> futureIterator = futures.iterator();
        while(futureIterator.hasNext()) {
            Future<GrabPage> future = futureIterator.next();
            if (future.isDone()) {
                futureIterator.remove();
                try {
                    pageSet.add(future.get());
                } catch (ExecutionException e) {

                }
            }
        }

        for (GrabPage grabPage : pageSet) {
            addNewURLs(grabPage);
        }

        return (futures.size() > 0);
    }

    /**
     * Get the URLs from the grabPage object;
     * Remove any anchor references;
     * Call submitNewURL() function to add this url to the work list
     * @param grabPage
     */
    private void addNewURLs(GrabPage grabPage) {
        for (URL url : grabPage.getUrlSet()) {
            if (url.toString().contains("#")) {
                try {
                    url = new URL(StringUtils.substringBefore(url.toString(), "#"));

                } catch (MalformedURLException e) {

                }
            }

            submitNewURL(url, grabPage.getDepth() + 1);
        }
    }

    public void write(String outputPath) throws IOException{
        FileUtils.writeLines(new File(outputPath), visitedURLSet);
    }

    public Set<URL> getVisitedURLSet() {
        return visitedURLSet;
    }

}
