package data.crawler.sites;

import data.crawler.web.WebFlow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SiteRunner {

    public static WebFlow build() {
        return null;
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
