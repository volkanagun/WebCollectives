package data.crawler.web;

import java.util.Objects;

public class WebSeed {
    private String mainURL, requestURL;
    private Integer seedNumber;

    public WebSeed(String mainURL, String requestURL, Integer seedNumber) {
        this.mainURL = mainURL;
        this.requestURL = requestURL;
        this.seedNumber = seedNumber;
    }

    public Boolean doRequest(Integer failSeedNumber){
        return failSeedNumber==null || seedNumber < failSeedNumber;
    }

    @Override
    public String toString() {
        return "WebSeed{" +
                "mainURL='" + mainURL + '\'' +
                ", requestURL='" + requestURL + '\'' +
                ", seedNumber=" + seedNumber +
                '}';
    }

    public String getMainURL() {
        return mainURL;
    }

    public void setMainURL(String mainURL) {
        this.mainURL = mainURL;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public Integer getSeedNumber() {
        return seedNumber;
    }

    public void setSeedNumber(Integer seedNumber) {
        this.seedNumber = seedNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSeed webSeed = (WebSeed) o;
        return requestURL.equals(webSeed.requestURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestURL);
    }
}
