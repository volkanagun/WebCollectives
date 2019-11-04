package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Volkan Agun
 */
public class TwitterSearch implements Serializable {

    private String language;
    private String query;
    private String includeAccount;
    private String includeTag;
    private Calendar since, until;
    private int scrollCount;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");
    public static String TURKISH = "tr";
    public static String RNGLISH = "en";

    public TwitterSearch(String query) {
        this.query = query.replaceAll("\\s+","%20");
    }

    public String getLanguage() {
        return language;
    }

    public TwitterSearch setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public TwitterSearch setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getIncludeAccount() {
        return includeAccount;
    }

    public TwitterSearch setIncludeAccount(String includeAccount) {
        this.includeAccount = includeAccount;
        return this;
    }

    public String getIncludeTag() {
        return includeTag;
    }

    public TwitterSearch setIncludeTag(String includeTag) {
        this.includeTag = includeTag;
        return this;
    }

    public Calendar getSince() {
        return since;
    }

    public void setSince(Calendar since) {
        this.since = since;
    }

    public Calendar getUntil() {
        return until;
    }

    public TwitterSearch setUntil(Calendar until) {
        this.until = until;
        return this;
    }

    public int getScrollCount() {
        return scrollCount;
    }

    public TwitterSearch setScrollCount(int scrollCount) {
        this.scrollCount = scrollCount;
        return this;
    }

    public String generateSeed() {
        String lanStr = language == null ? "" : language;
        String dateSpan = generateDate();
        String queryStr= query;

        queryStr += "%20" + includeAccount!=null? "%40"+includeAccount: "";
        queryStr += "%20" + includeTag!=null ? "%23" + includeTag: "";
        String url = "https://twitter.com/search?l="+lanStr+"&q="+ queryStr +dateSpan+"&src=typd";

        return url;
    }

    private String generateDate(){

        if(since !=null && until!=null)
            return "%20since=%3A"+formatter.format(since.getTime().toInstant())+"%20until=%3A"+formatter.format(until.getTime().toInstant());

        else return "";
    }

    public WebFlow build(){

        int pageCount = 10;
        String url = generateSeed();
        WebButtonClickCall clickCall = new WebButtonClickCall(1, "button.btn.btn-load-more");

        WebFunctionScrollHeight scrollCall = new WebFunctionScrollHeight(5);
        WebFunctionCall sequenceCall = new WebFunctionSequence(pageCount, scrollCall, clickCall).initialize();

        LookupPattern tweetPattern = new LookupPattern(LookupOptions.ARTICLE, LookupOptions.CONTAINER, "<div class=\"content\">", "</div>")
                .setStartEndMarker("<div", "</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.URL, url))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, "<span class=\"FullNameGroup\">", "</span>")
                        .setStartEndMarker("<span","</span>")
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, "<span class=\"_timestamp(.*?)data-time-ms=\"","\"")
                        .setNth(0)
                        .setStartEndMarker("<span","</span>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<div class=\"js-tweet-text-container\">","</div>")
                        .setNth(0)
                        .setStartEndMarker("<div","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH,"<p(.*?)>","</p>")
                                .setRemoveTags(true)));


        WebTemplate template = new WebTemplate(LookupOptions.TWEETDIRECTORY, "tweet-text",LookupOptions.EMPTYDOMAIN)
                .setMainPattern(tweetPattern)
                .setThreadSize(1)
                .addSeed(url)
                .setFunctionCall(sequenceCall);

        return new WebFlow(template);


    }

    public static List<WebFlow> buildQueries(String[] queryTerms, String language)
    {
        List<WebFlow> list = new ArrayList<>();
        for(String queryTerm:queryTerms){
            list.add(new TwitterSearch(queryTerm).setLanguage(language).build());
        }

        return list;

    }

    public static void main(String[] args) {
        String[] queryTerms = new String[]{"ırak", "fetö", "iran","abd","suriye","pkk","deaş"};
        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.batchSubmit(service, buildQueries(queryTerms, TURKISH));
        service.shutdown();
    }
}
