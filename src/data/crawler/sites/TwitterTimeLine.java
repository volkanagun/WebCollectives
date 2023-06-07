package data.crawler.sites;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * Created by wolf on 05.07.2015.
 */
public class TwitterTimeLine implements Serializable {

    private final String filename = "conf/twitter.properties";
    private final Properties properties = new Properties();
    private Twitter twitter;
    private Integer pageStart = 1, pageLength = 15, numPages = 100;
    private Integer sessionMax = 200;
    private final String folder;

    public TwitterTimeLine(String folder) {
        load();
        loginNormal();
        this.folder = folder;
    }

    public Integer getPageLength() {
        return pageLength;
    }

    public TwitterTimeLine setPageLength(Integer pageLength) {
        this.pageLength = pageLength;
        return this;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public TwitterTimeLine setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
        return this;
    }

    public Integer getSessionMax() {
        return sessionMax;
    }

    public TwitterTimeLine setSessionMax(Integer sessionMax)  {
        this.sessionMax = sessionMax;
        return this;
    }

    public Integer getNumPages() {
        return numPages;
    }

    public TwitterTimeLine setNumPages(Integer numPages) {
        this.numPages = numPages;
        return this;
    }

    public void load() {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filename);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginNormal() {
        String consumerKey = properties.getProperty("oauth.consumerKey");
        String consumerSecret = properties.getProperty("oauth.consumerSecret");
        String accessToken = properties.getProperty("oauth.accessToken");
        String accessTokenSecret = properties.getProperty("oauth.accessTokenSecret");

        if (consumerKey != null && consumerSecret != null && accessToken != null && accessTokenSecret != null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(consumerKey);
            builder.setOAuthConsumerSecret(consumerSecret);
            Configuration configuration = builder.build();
            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance(new AccessToken(accessToken, accessTokenSecret));
            //twitter.setOAuthConsumer(consumerKey, consumerSecret);
            //twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        }
    }


    public void sleep(Long milisecs) {
        try {
            Thread.sleep(milisecs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected String cleanMessage(String message){
        String result = message.replaceAll("<","&lt;");
        result = result.replaceAll(">","&gt;");
        result = result.replaceAll("&","&amp;");
        return result.trim();
    }

    public void download() {
        String mainDirectory = folder;
        String mainFilename = mainDirectory + "twitter-1.xml";
        (new File(mainDirectory)).mkdirs();
        int sessionCount = 0;
        while (sessionCount < sessionMax) {
            try {
                PrintWriter twitterFile = new PrintWriter(new FileWriter(mainFilename, true));
                twitterFile.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                twitterFile.println("<ROOT LABEL=\"TWEET-DOC\">");

                for (int i = pageStart; i < numPages; i++) {
                    try {

                        Paging paging = new Paging(i, pageLength);
                        List<Status> statusList = twitter.getHomeTimeline(paging);

                        for (int j = 0; j < statusList.size(); j++) {
                            Status status = statusList.get(j);
                            User twitterUser = status.getUser();
                            String user = twitterUser.getName();
                            String screenName = twitterUser.getScreenName();
                            String message = status.getText().replaceAll("\n", " ");
                            message = cleanMessage(message);
                            String line = "<RESULT TYPE=\"CONTAINER\" LABEL=\""+ "TWEET"+"\">\n" +
                                    "<RESULT TYPE=\"TEXT\" LABEL=\"AUTHORNAME\">\n" + user + "\n</RESULT>" +
                                    "\n<RESULT TYPE=\"TEXT\" LABEL=\"TWEETTEXT\">\n" + message.trim() + "\n</RESULT>\n</RESULT>";
                            System.out.println("Page: " + i + " Status: " + (j + 1));
                            twitterFile.println(line);
                        }

                        System.out.println("Waiting for next page ("+i+"/"+numPages+")...");
                        sleep(60000L);


                    } catch (TwitterException e) {
                        e.printStackTrace();
                        Long wait = (long) e.getRateLimitStatus().getResetTimeInSeconds() * 1000;
                        sleep(wait);
                    }
                }
                twitterFile.println("</ROOT>");
                twitterFile.close();
                System.out.println("Waiting for next ("+sessionCount+"/"+sessionMax+")session...");
                sleep(100000L);
                sessionCount++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        TwitterTimeLine twitterTimeLine = new TwitterTimeLine("resources/tweets-turkish/")
                .setPageStart(1).setNumPages(100)
                .setPageLength(10).setSessionMax(15);

        twitterTimeLine.download();

        //twitterTimeLine.eliminateDuplicates("twitter-main-1.xml");
    }


}
