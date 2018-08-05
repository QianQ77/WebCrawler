package com.qianqiu;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by qiuqian on 8/5/18.
 */
public class TestGrabPage {

    @Test
    public void testIfWork() throws MalformedURLException, InterruptedException, ExecutionException{
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<GrabPage> future = executorService.submit(new GrabPage(new URL("https://www.google.com/")));
        GrabPage done = future.get();
        done.printAllLinks();
    }
}
