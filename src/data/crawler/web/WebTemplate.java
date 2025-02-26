package data.crawler.web;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import data.util.TextFile;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Created by wolf on 02.07.2015.
 */
public class WebTemplate implements Serializable {

    public class FailCount {
        int count = 0;
        int threadSize;

        public FailCount(int count, int threadSize) {
            this.count = count;
            this.threadSize = threadSize;
        }

        public synchronized Boolean increment() {
            this.count++;
            return check();
        }

        public Boolean check() {
            return count > threadSize;
        }
    }

    public abstract class DownloadCallable implements Callable<Boolean> {
        WebSeed seed;
        int index;
        FailCount sharedCount;
        WebDocumentList documentList;

        public DownloadCallable(WebDocumentList documentList, FailCount sharedCount, WebSeed seed, int index) {
            super();
            this.index = index;
            this.seed = seed;
            this.documentList = documentList;
            this.sharedCount = sharedCount;
        }

    }

    public abstract class DownloadCallableFast implements Callable<WebDocument[]> {
        WebSeed seed;
        int index;
        FailCount sharedCount;

        public DownloadCallableFast(FailCount sharedCount, WebSeed seed, int index) {
            super();
            this.index = index;
            this.seed = seed;
            this.sharedCount = sharedCount;
        }

    }


    //Extraction Template
    public static String FILEPREFIX = "file://";
    public static String TEXTPREFIX = "text://";
    private Boolean multipleDocuments = false;
    private String multipleIdentifier = null;
    private final List<WebSeed> seedList;
    private final Map<WebSeed, String> seedMap;


    private LookupPattern mainPattern;


    private String name;
    private String domain;
    private String folder;
    private String type;


    private WebLuceneSink webSink;
    private WebFunctionCall functionCall;
    private WebSuffixGenerator suffixGenerator;
    private String nextPageSuffix;
    private String nextPageSuffixAddition = "";
    private Integer nextPageSize;
    private Integer nextPageStart;
    private Integer nextPageJump = 1;
    private Integer threadSize = 2;
    private Long sleepTime = 200L;
    private Long waitTimeAfter = 60 * 60 * 10000L;
    private Long lastWaitTime = 0L;
    private Long waitTime = 1000L;
    private Long seedSizeLimit = Long.MAX_VALUE;

    private Boolean forceWrite = false;
    private Boolean doFast = false;
    private Boolean doDeleteStart = false;

    private Boolean doRandomSeed = false;
    private Boolean doFinal = false;
    private Integer randomSeedSize = 1;

    private Boolean lookComplete = false;
    private Boolean domainSame = false;
    private String linkPattern = null;
    private String linkRejectPattern = null;
    private String htmlSaveFolder = null;

    private String charset = "UTF-8";
    private final Map<String, WebTemplate> nextMap;
    private final Map<String, WebTemplate> extraRecursiveMap;
    private WebTemplate parent;

    private double totalParseTime = 0d;
    private double totalCount = 1d;

    private Boolean isMainContent = false;


    public WebTemplate(String folder, String name, String domain) {
        this.name = name;
        this.folder = folder;
        this.domain = domain;
        this.seedList = new ArrayList<>();
        this.seedMap = new HashMap<>();
        this.nextMap = new HashMap<>();
        this.extraRecursiveMap = new HashMap<>();
        this.nextPageSize = 2;
        this.nextPageStart = 2;
    }

    public WebTemplate(String folder, String name, String domain, String nextPageSuffix) {
        this(folder, name, domain);
        this.nextPageSuffix = nextPageSuffix;
    }

    public WebTemplate initialize() {
        if (webSink != null) webSink.openWriter();
        if (functionCall != null) functionCall.initialize();
        return this;
    }

    public WebTemplate destroy() {

        if (functionCall != null) {
            System.out.println("Destroying Selenium browser.");
            functionCall.destroy();
            functionCall = null;
            for(String key:nextMap.keySet()){
                nextMap.get(key).destroy();
            }

            for(String key:extraRecursiveMap.keySet()){
                extraRecursiveMap.get(key).destroy();
            }
        }
        return this;
    }

    public Boolean getMainContent() {
        return isMainContent;
    }

    public WebTemplate setMainContent(Boolean mainContent) {
        isMainContent = mainContent;
        return this;
    }

    public WebTemplate getParent() {
        return parent;
    }

    public void setParent(WebTemplate parent) {
        this.parent = parent;
    }

    public WebLuceneSink getWebSink() {
        return webSink;
    }

    public WebTemplate setWebSink(WebLuceneSink webSink) {
        this.webSink = webSink;
        return this;
    }

    public WebFunctionCall getFunctionCall() {
        return functionCall;
    }

    public WebTemplate setFunctionCall(WebFunctionCall functionCall) {
        this.functionCall = functionCall;
        return this;
    }

    public Long getWaitTime() {
        return waitTime;
    }

    public WebTemplate setWaitTime(Long waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public Long getWaitTimeAfter() {
        return waitTimeAfter;
    }

    public WebTemplate setWaitTimeAfter(Long waitTimeAfter) {
        this.waitTimeAfter = waitTimeAfter;
        return this;
    }

    public Long getLastWaitTime() {
        return lastWaitTime;
    }

    public WebTemplate setLastWaitTime(Long lastWaitTime) {
        this.lastWaitTime = lastWaitTime;
        return this;
    }

    public String getHtmlSaveFolder() {
        return htmlSaveFolder;
    }

    public WebTemplate setHtmlSaveFolder(String htmlSaveFolder) {
        this.htmlSaveFolder = htmlSaveFolder;
        return this;
    }

    public Boolean getDoFast() {
        return doFast;
    }

    public WebTemplate setDoFast(Boolean doFast) {
        this.doFast = doFast;
        return this;
    }

    public WebTemplate setDoRandomSeed(int doRandomCount) {
        this.randomSeedSize = doRandomCount;
        this.doRandomSeed = true;
        return this;
    }

    public Long getSeedSizeLimit() {
        return seedSizeLimit;
    }

    public WebTemplate setSeedSizeLimit(Long seedSizeLimit) {
        this.seedSizeLimit = seedSizeLimit;

        return this;
    }

    public Boolean getLookComplete() {
        return lookComplete;
    }

    public WebTemplate setLookComplete(Boolean lookComplete) {
        this.lookComplete = lookComplete;
        return this;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public WebTemplate setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public String getLinkPattern() {
        return linkPattern;
    }

    public WebDocument createDocument(String url) {
        return new WebDocument(folder, name, url);
    }

    public WebTemplate setLinkPattern(String linkAcceptPattern, String linkRejectPattern) {
        this.linkPattern = linkAcceptPattern;
        this.linkRejectPattern = linkRejectPattern;
        if (linkPattern != null) Pattern.compile(linkAcceptPattern);
        if (linkRejectPattern != null) Pattern.compile(linkRejectPattern);
        return this;
    }



    public WebSuffixGenerator getSuffixGenerator() {
        return suffixGenerator;
    }



    public WebTemplate setSuffixGenerator(WebSuffixGenerator suffixGenerator) {
        this.suffixGenerator = suffixGenerator;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public WebTemplate setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getMultipleIdentifier() {
        return multipleIdentifier;
    }

    public WebTemplate setMultipleIdentifier(String multipleIdentifier) {
        this.multipleIdentifier = multipleIdentifier;
        return this;
    }

    public Boolean getMultipleDocuments() {
        return multipleDocuments;
    }

    public WebTemplate setMultipleDocuments(Boolean multipleDocuments) {
        this.multipleDocuments = multipleDocuments;
        return this;
    }

    public Integer getThreadSize() {
        return threadSize;
    }

    public WebTemplate setThreadSize(Integer threadSize) {
        this.threadSize = threadSize;
        return this;
    }

    public Integer getNextPageStart() {
        return nextPageStart;
    }

    public WebTemplate setNextPageStart(Integer nextPageStart) {
        this.nextPageStart = nextPageStart;
        return this;
    }

    public Integer getNextPageSize() {
        return nextPageSize;
    }

    public WebTemplate setNextPageSize(Integer nextPageSize) {
        this.nextPageSize = nextPageSize;
        return this;
    }

    public Integer getNextPageJump() {
        return nextPageJump;
    }

    public WebTemplate setNextPageJump(Integer nextPageJump) {
        this.nextPageJump = nextPageJump;
        return this;
    }

    public WebTemplate setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public WebTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public String getNextPageSuffix() {
        return nextPageSuffix;
    }

    public WebTemplate setNextPageSuffix(String nextPageSuffix) {
        this.nextPageSuffix = nextPageSuffix;
        return this;
    }

    public String getNextPageSuffixAddition() {
        return nextPageSuffixAddition;
    }

    public WebTemplate setNextPageSuffixAddition(String nextPageSuffixAddition) {
        this.nextPageSuffixAddition = nextPageSuffixAddition;
        return this;
    }

    public LookupPattern getMainPattern() {
        return mainPattern;
    }

    public WebTemplate setMainPattern(LookupPattern mainPattern) {
        this.mainPattern = mainPattern;
        return this;
    }

    public WebTemplate addNextPattern(LookupPattern nextPattern, String label, Boolean doDelete) {
        WebTemplate nextTemplate = new WebTemplate(folder, label, domain)
                .setMainPattern(nextPattern)
                .setDoDeleteStart(doDelete);

        this.extraRecursiveMap.put(label,
                nextTemplate);

        nextTemplate.setParent(this);
        return this;
    }

    public WebTemplate addExtraTemplate(WebTemplate nextTemplate, String label) {

        this.extraRecursiveMap.put(label, nextTemplate);
        this.nextMap.put(label, nextTemplate);

        return this;
    }

    public String getDomain() {
        return domain;
    }

    public String getType() {
        return type;
    }

    public WebTemplate setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean getDomainSame() {
        return domainSame;
    }

    public WebTemplate setDomainSame(Boolean domainSame) {
        this.domainSame = domainSame;
        return this;
    }

    public WebTemplate addNext(WebTemplate nextTemplate, String label) {
        this.nextMap.put(label, nextTemplate);
        nextTemplate.setParent(this);
        return this;
    }


    public boolean nextEmpty() {
        return this.nextMap.isEmpty();
    }

    public boolean nextExtraEmpty() {
        return this.extraRecursiveMap.isEmpty();
    }

    public WebTemplate addSeed(WebSeed seed) {
        if (!seedList.contains(seed))
            seedList.add(seed);

        return this;
    }

    public WebTemplate addSeed(String mainURL) {
        WebSeed seed = new WebSeed(mainURL, mainURL, seedList.size());
        if (!seedList.contains(seed))
            seedList.add(seed);

        return this;
    }
    public WebTemplate addSeedDuplicate(String mainURL) {
        WebSeed seed = new WebSeed(mainURL, mainURL, seedList.size());
        seedList.add(seed);
        return this;
    }

    public WebTemplate addSeeds(Set<WebSeed> seeds) {
        for (WebSeed seed : seeds) {

            if (!seedList.contains(seed))
                seedList.add(seed);

        }

        return this;
    }

    public WebTemplate addSeed(String genre, WebSeed seed) {

        seedMap.put(seed, genre);
        return addSeed(seed);
    }

    public WebTemplate addSeed(String topic, String mainURL) {
        WebSeed seed = new WebSeed(mainURL, mainURL, seedList.size());
        seedMap.put(seed, topic);
        return addSeed(seed);
    }


    public WebTemplate addSeeds(String genre, Set<WebSeed> seeds) {
        for (WebSeed seed : seeds) {
            seedMap.put(seed, genre);
            addSeed(seed);
        }

        return this;
    }

    public WebTemplate addFolder(String folder) {
        File[] files = (new File(folder)).listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            WebSeed webSeed = new WebSeed(FILEPREFIX, FILEPREFIX + file.getPath(), i);
            addSeed(webSeed);
        }

        return this;
    }

    public List<WebSeed> getSeedList() {
        return seedList;
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public WebTemplate getNextMap(String label) {
        WebTemplate webTemplate = nextMap.get(label);
        webTemplate.getSeedList().clear();
        return webTemplate;
    }


    public Iterator<String> getNextIterator() {
        return nextMap.keySet().iterator();
    }

    public Boolean getForceWrite() {
        return forceWrite;
    }

    public Boolean getDoFinal() {
        return doFinal;
    }

    public void setDoFinal(Boolean doFinal) {
        this.doFinal = doFinal;
    }

    public WebTemplate setForceWrite(Boolean forceWrite) {
        this.forceWrite = forceWrite;
        return this;
    }

    public WebTemplate setDoDeleteStart(Boolean doDeleteStart) {
        this.doDeleteStart = doDeleteStart;
        return this;
    }

    public Boolean getDoDeleteStart() {
        return doDeleteStart;
    }

    public List<WebDocument> checkXML(List<WebDocument> documentList) {
        List<WebDocument> checkedList = new ArrayList<>();
        for (WebDocument document : documentList) {
            if (multipleIdentifier == null) {
                if (!document.exists()) checkedList.add(document);
            } else {
                if (!document.exists(multipleIdentifier)) checkedList.add(document);
            }
        }

        return checkedList;
    }

    public void zipXMLs(List<WebDocument> documentList) {
        saveXML(documentList);

        File[] files = new File(folder).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String filename = pathname.getName();
                String extention = filename.substring(filename.lastIndexOf(".") + 1);
                return extention.equals("xml") || extention.equals(filename);
            }
        });

        String hashcode = String.valueOf(Arrays.asList(files).hashCode());

        try (ArchiveOutputStream o = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP,
                new FileOutputStream(folder + hashcode + ".zip"))) {
            for (File f : files) {
                // maybe skip directories for formats like AR that don't store directories
                ArchiveEntry entry = o.createArchiveEntry(f, f.getName());
                // potentially add more flags to entry
                o.putArchiveEntry(entry);
                if (f.isFile()) {
                    try (InputStream i = Files.newInputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
                o.closeArchiveEntry();

                f.delete();
            }
            o.finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArchiveException e) {
            e.printStackTrace();
        }
    }

    public void saveXML(List<WebDocument> documentList) {
        double sucessRate = 0d;

        if (multipleIdentifier == null) {
            if (webSink != null) {
                webSink.writeDocuments(documentList);
            }

            for (WebDocument document : documentList) {
                if (!lookComplete || document.isComplete(mainPattern)) {
                    sucessRate += document.saveAsFlatXML();

                }
            }
        } else {
            if (webSink != null) {

                webSink.writeDocuments(documentList);
            }

            for (WebDocument document : documentList) {
                if (!lookComplete || document.isComplete(mainPattern)) {
                    sucessRate += document.saveAsMultiXML(multipleIdentifier);

                }
            }
        }


        System.out.println("Success rate: " + sucessRate / (documentList.size() == 0 ? 1.0 : documentList.size()));
    }

    public void saveXMLMultiDocs(List<WebDocument> documentList) {

    }

    private void addGenreSeed(Map<String, Map<String, Set<String>>> genreMap, String key, String genre, String seed) {
        if (genreMap.containsKey(key)) {
            if (genreMap.get(key).containsKey(genre)) genreMap.get(key).get(genre).add(seed);
            else {
                Set<String> seeds = new HashSet<>();
                seeds.add(seed);
                genreMap.get(key).put(genre, seeds);
            }
        } else {
            Map<String, Set<String>> mmap = new HashMap<>();
            Set<String> seeds = new HashSet<>();
            seeds.add(seed);
            mmap.put(genre, seeds);
            genreMap.put(key, mmap);
        }
    }

    private void addSeed(Map<String, Set<String>> seedMap, String key, String seed) {
        if (seedMap.containsKey(key)) {
            seedMap.get(key).add(seed);
        } else {
            Set<String> mmap = new HashSet<>();
            mmap.add(seed);
            seedMap.put(key, mmap);
        }
    }


    public void goSleep() {

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void goSleep(Long waitTime) {

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void goWait(Object lock, Long waitTime) {

        try {
            lock.wait(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doWait(Object lock) {
        Long crrTime = new Date().getTime();
        if (waitTimeAfter != null && crrTime >= (lastWaitTime + waitTimeAfter)) {
            synchronized (lock) {
                lastWaitTime = crrTime;
                goSleep(waitTime);
            }
        } else {
            /*synchronized (lock) {
                lock.notify();
            }*/
        }

    }

    public Boolean linkControl(WebSeed seed, WebTemplate nextTemplate) {
        if (new WebDocument(folder, nextTemplate.name, seed.getRequestURL()).filenameExists()) return false;

        String templateDomain = nextTemplate.domain;
        boolean containsDomain = seed.getRequestURL().contains(templateDomain.replaceAll("http(s?)", ""));

        if (linkPattern != null && linkRejectPattern != null) {
            return seed.getRequestURL().matches(linkPattern) && !seed.getRequestURL().matches(linkRejectPattern.replaceFirst(templateDomain, ""));
        } else if (linkPattern != null && linkRejectPattern != null && seed.getRequestURL().matches(linkPattern)) {
            return !seed.getRequestURL().matches(linkRejectPattern);
        } else if (containsDomain && linkPattern != null) {
            return seed.getRequestURL().matches(linkPattern);
        } else if (containsDomain && linkRejectPattern != null) {
            return !seed.getRequestURL().matches(linkRejectPattern);
        } else if (containsDomain && linkPattern != null)
            return seed.getRequestURL().matches(linkPattern.replaceFirst(templateDomain, ""));
        else if (!containsDomain && linkRejectPattern != null)
            return !seed.getRequestURL().matches(linkRejectPattern.replaceFirst(templateDomain, ""));
        else return containsDomain;

    }

    public WebDocument execute() {
        //Download template
        //Extract patterns
        //Pass seedlist to next template as urls if it is a url

        //Download current template
        System.out.println("Seed size: " + seedList.size());
        goSleep();


        WebDocumentList documentList = doFast ? downloadFast() : download();

        WebDocument mainDocument = new WebDocument(folder, name).setType(type);

        if (nextEmpty() && nextExtraEmpty()) {
            //Leaf template
            mainDocument.addWebFlowResult(documentList.getCurrentDocuments());
            saveXML(documentList.getCurrentDocuments());
            //zipXMLs(documentList);
        } else {
            //Non leaf template
            documentList.shuffle();
            Map<String, Set<String>> nextSeedList = new HashMap<>();
            Map<String, Map<String, Set<String>>> nextSeedMap = new HashMap<>();
            List<WebDocument> documents = documentList.getNextDocuments();
            saveXML(documentList.getCurrentDocuments());
            documentList.clearCurrentDocuments();

            //clear existing documents
            for (WebDocument document : documents) {
                Iterator<String> templateIter = getNextIterator();
                Map<String, String> properties = document.getProperties();
                String genre = properties.get(LookupOptions.GENRE);

                while (templateIter.hasNext()) {
                    String templateLink = templateIter.next();
                    WebTemplate template = getNextMap(templateLink);
                    String templateDomain = template.domain;
                    List<LookupResult> subResults = document.getLookupFinalList(templateLink);

                    for (int k = 0; k < Math.min(subResults.size(), seedSizeLimit); k++) {

                        LookupResult result = subResults.get(k);
                        WebSeed newSeed = new WebSeed(document.getUrl(), WebSeed.url(templateDomain, result.getText()), k);

                        if (linkControl(newSeed, template)) {

                            if (genre != null) {
                                template.addSeed(genre, newSeed);
                                //addGenreSeed(nextSeedMap, templateLink, genre, templateDomain + result.getText());
                            } else {
                                template.addSeed(newSeed);
                                //addSeed(nextSeedList, templateLink, templateDomain + result.getText());
                                //nextSeedList.add(domain + result.getText());
                            }
                        }
                    }


                    WebDocument webFlowResultList = template.execute();


                    mainDocument.addWebFlowResult(webFlowResultList);

                }
            }

            //mainDocument.addWebFlowResult(documentList.getCurrentDocuments());

            //zipXMLs(documentList);
        }


        return mainDocument;
    }

    public void pushGenerateSeeds(List<WebSeed> nextPageSeeds, Map<WebSeed, String> nextPageSeedGenre) {
        Collections.shuffle(seedList);

        if (nextPageSuffix != null) {

            for (WebSeed webSeed : seedList) {
                for (int i = nextPageStart; i <= nextPageStart + nextPageSize; i += nextPageJump) {
                    String newUrl = webSeed.getRequestURL() + nextPageSuffix + i + nextPageSuffixAddition;
                    WebSeed newSeed = new WebSeed(webSeed.getRequestURL(), newUrl, i);
                    if (!nextPageSeeds.contains(newSeed)) {
                        nextPageSeeds.add(newSeed);
                        nextPageSeedGenre.put(newSeed, seedMap.get(webSeed));
                    }
                }
            }

            seedList.clear();
            seedMap.clear();

            for (WebSeed url : nextPageSeeds) {

                if (!seedList.contains(url)) {
                    seedList.add(url);
                    seedMap.put(url, nextPageSeedGenre.get(url));
                }
            }
        }

        if (suffixGenerator != null) {

            List<WebSeed> generateSeedList = new ArrayList<>();
            generateSeedList.addAll(seedList);
            seedList.clear();
            seedMap.clear();

            int size = generateSeedList.size();

            for (int i = 0; i < size; i++) {
                WebSeed webSeed = generateSeedList.get(i);
                List<String> generatedSeeds = suffixGenerator.apply(webSeed.getRequestURL());
                for (int j = 0; j < generatedSeeds.size(); j++) {
                    String generatedUrl = generatedSeeds.get(j);
                    WebSeed generatedSeed = new WebSeed(webSeed.getRequestURL(), generatedUrl, j);
                    if (!seedList.contains(generatedSeed)) {
                        seedList.add(generatedSeed);
                        seedMap.put(generatedSeed, seedMap.get(webSeed));
                    }
                }
            }
        }
    }

    private Integer seedNumber(Map<String, Integer> map, String url) {
        synchronized (map) {
            return map.get(url);
        }
    }

    private List<WebSeed> randomSeedList() {
        if (doRandomSeed) {
            Collections.shuffle(seedList);
            return seedList.subList(0, Math.min(randomSeedSize, seedList.size()));
        } else {
            Collections.shuffle(seedList);
            return seedList;
        }
    }

    public WebDocumentList download() {
        WebDocumentList crrDocumentList = new WebDocumentList();

        List<WebSeed> nextPageSeeds = new ArrayList<>();
        Map<WebSeed, String> nextPageSeedGenre = new HashMap<>();
        Map<String, Integer> failMap = new HashMap<>();

        pushGenerateSeeds(nextPageSeeds, nextPageSeedGenre);

        List<DownloadCallable> threadList = new ArrayList<>();
        final FailCount failCount = new FailCount(0, threadSize);
        List<WebSeed> currentSeedList = randomSeedList();

        for (int i = 0; i < currentSeedList.size(); i++) {

            WebSeed webSeed = currentSeedList.get(i);
            DownloadCallable thread = new DownloadCallable(crrDocumentList, failCount, webSeed, i) {
                @Override
                public Boolean call() {
                    WebDocument document = new WebDocument(folder, name, webSeed.getRequestURL());
                    Integer seedNumber = seedNumber(failMap, webSeed.getMainURL());

                    Boolean returnResult = false;
                    if (!document.filenameExists() && webSeed.doRequest(seedNumber)) {
                        goSleep(2000L);
                        doWait(folder);
                        Integer returnValue = loadHTML(document, webSeed, index);
                        if (returnValue == 1 || returnValue == 0) {

                            synchronized (crrDocumentList) {
                                //Fill the list
                                if (nextEmpty() || isMainContent)
                                    crrDocumentList.add(document);
                                else
                                    crrDocumentList.addNext(document);
                            }

                            if (returnValue == 1) saveHTML(document);
                            returnResult = extract(document);

                            for (String label : extraRecursiveMap.keySet()) {
                                WebTemplate nextTemplate = extraRecursiveMap.get(label);
                                WebDocument nextDocument = extractNext(document, nextTemplate);
                                if (!nextDocument.isEmpty()) {
                                    synchronized (crrDocumentList) {
                                        if (nextTemplate.getDoFinal()) {
                                            crrDocumentList.add(nextDocument);
                                        } else {
                                            crrDocumentList.addNext(nextDocument);
                                        }
                                    }
                                }

                            }


                        } else {
                            synchronized (failMap) {
                                String seedUrl = webSeed.getMainURL();
                                if (failMap.containsKey(seedUrl)) {
                                    Integer number = Math.min(failMap.get(seedUrl), webSeed.getSeedNumber());
                                    failMap.put(seedUrl, webSeed.getSeedNumber());
                                } else {
                                    failMap.put(seedUrl, webSeed.getSeedNumber());
                                }
                            }
                        }

                    }

                    if (!forceWrite && document.filenameExists()) {
                        document.deleteOnExit();
                    }

                    return returnResult;
                }
            };

            threadList.add(thread);
        }

        for (DownloadCallable callable : threadList) {
            try {
                callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return crrDocumentList;
    }

    public WebDocumentList downloadFast() {
        WebDocumentList htmlDocumentList = new WebDocumentList();
        List<WebSeed> nextPageSeeds = new ArrayList<>();
        Map<WebSeed, String> nextPageSeedGenre = new HashMap<>();
        Map<String, Integer> failMap = new HashMap<>();


        pushGenerateSeeds(nextPageSeeds, nextPageSeedGenre);

        List<Callable<WebDocument[]>> threadList = new ArrayList<>();
        final FailCount failCount = new FailCount(0, threadSize);
        Collections.shuffle(seedList);
        for (int i = 0; i < seedList.size(); i++) {

            WebSeed webSeed = seedList.get(i);
            DownloadCallableFast thread = new DownloadCallableFast(failCount, webSeed, i) {
                @Override
                public WebDocument[] call() {


                    doWait(folder);
                    WebDocument document = new WebDocument(folder, name, webSeed.getRequestURL());
                    WebDocument nextDocument = null;
                    Integer seedNumber = seedNumber(failMap, webSeed.getMainURL());
                    Integer returnValue = loadHTML(document, webSeed, index);
                    Boolean returnResult = false;
                    if (returnValue == 0 || returnValue == 1) {

                        if (returnValue == 1) saveHTML(document);
                        returnResult = extract(document);

                        for (String label : extraRecursiveMap.keySet()) {
                            WebTemplate template = extraRecursiveMap.get(label);
                            WebDocument crrNext = extractNext(document, template);
                            if (!crrNext.isEmpty()) {
                                nextDocument = crrNext;
                            }
                        }

                    }

                    if (!forceWrite && document.filenameExists()) {
                        document.deleteOnExit();
                    }
                    if (nextDocument == null) return new WebDocument[]{document, document};
                    else return new WebDocument[]{document, document, nextDocument};
                }
            };

            threadList.add(thread);
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadSize);
        try {
            List<Future<WebDocument[]>> returnList = executor.invokeAll(threadList);
            for (Future<WebDocument[]> fdoc : returnList) {
                try {
                    WebDocument[] array = fdoc.get(500, TimeUnit.MILLISECONDS);
                    htmlDocumentList.add(array[0]);
                    if (array.length == 2){
                        htmlDocumentList.addNext(array[1]);
                    }
                    else if(array.length == 3) {
                        htmlDocumentList.addNext(array[1]);
                        htmlDocumentList.addNext(array[2]);
                    }
                } catch (TimeoutException e) {
                    System.out.println("Timeout....");
                }

            }

            executor.shutdown();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return htmlDocumentList;
    }

    private Boolean checkAnyUnfinished(List<Future<Boolean>> returnList) {
        for (Future<Boolean> task : returnList) {
            try {
                task.get(1000L, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                return true;
            } catch (ExecutionException e) {
                return true;
            } catch (TimeoutException e) {
                return true;
            }
        }

        return false;
    }

    private void recordTime(List<LookupResult> results, double time) {
        if (!results.isEmpty()) {
            totalParseTime += time;
            totalCount++;
        }
    }

    public synchronized void printTime(){
        System.out.println("Successful count: "+totalCount);
        System.out.println("Successful parse average: "+totalParseTime/totalCount);
    }

    public Boolean extract(WebDocument html) {
        double measuredNow = System.currentTimeMillis();
        String utf = new String(html.getText().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_16);
        List<LookupResult> results = mainPattern.getResult(html.getProperties(), mainPattern.lowercaseAllTags(html.getText()));
        double measuredNext = System.currentTimeMillis();
        recordTime(results, measuredNext-measuredNow);
        html.setLookupResultList(results);
        return !results.isEmpty();

    }

    public WebDocument extractNext(WebDocument html, WebTemplate nextTemplate) {

        boolean isNotEmpty = false;

        WebDocument nextDocument = nextTemplate.createDocument(html.getUrl());
        LookupPattern nextPattern = nextTemplate.mainPattern;

        List<LookupResult> results = nextPattern.getResult(html.getProperties(), nextPattern.lowercaseAllTags(html.getText()));
        nextDocument.addLookupResultList(results);

        return nextDocument;

    }

    public Boolean saveHTML(WebDocument html) {
        return html.saveHTML(htmlSaveFolder);
    }

    public Integer loadHTML(WebDocument document, WebSeed webSeed, Integer index) {
        if (document.loadHTML(htmlSaveFolder)) {
            document.setFetchDate(new Date());
            document.setDomain(domain);
            document.setType(type);
            document.setLookComplete(lookComplete);
            if (seedMap.containsKey(webSeed)) {
                document.putProperty(LookupOptions.GENRE, seedMap.get(webSeed));
            }
            document.setIndex(index);
            System.out.println("Loaded ..." + document.htmlFilename(htmlSaveFolder));
            return 0;
        } else {
            String downloadedHTML = doFast ? downloadFileFast(webSeed.getRequestURL(), charset) : downloadFile(webSeed.getRequestURL(), charset);
            if (downloadedHTML != null && !downloadedHTML.isEmpty()) {
                document.setFetchDate(new Date());
                document.setText(downloadedHTML);
                document.setDomain(domain);
                document.setType(type);
                document.setLookComplete(lookComplete);
                if (seedMap.containsKey(webSeed)) {
                    document.putProperty(LookupOptions.GENRE, seedMap.get(webSeed));
                }
                document.setIndex(index);
                return 1;
            }
        }
        return -1;
    }


    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Utilities">

    private String currentDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(format);
    }

    private String downloadFile(String address, String charset) {

        if (address.startsWith(TEXTPREFIX)) {
            String text = address.substring(address.indexOf(TEXTPREFIX) + TEXTPREFIX.length());
            return text;
        } else if (address.startsWith(FILEPREFIX)) {
            String filename = address.substring(address.indexOf(FILEPREFIX) + FILEPREFIX.length());
            System.out.println("Downloading... '" + address + "'" + " Date: " + currentDate());
            return new TextFile(filename, charset).readFullText();
        } else {
            System.out.println("Downloading... '" + address + "'" + " Date: " + currentDate());
            String text = "";
            try {

                if (functionCall == null) {
                    text = downloadPage(address, charset, 0);
                } else {
                    text = functionCall.returnHTML(address);
                }

            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }
    }

    private String downloadFileFast(String address, String charset) {

        if (address.startsWith(TEXTPREFIX)) {
            String text = address.substring(address.indexOf(TEXTPREFIX) + TEXTPREFIX.length());
            return text;
        } else if (address.startsWith(FILEPREFIX)) {
            String filename = address.substring(address.indexOf(FILEPREFIX) + FILEPREFIX.length());
            System.out.println("Downloading... '" + address + "'" + " Date: " + currentDate());
            return new TextFile(filename, charset).readFullText();
        } else {

            String text = "";
            try {
                if (functionCall == null) {
                    text = downloadPage(address, charset, 2);
                    System.out.println("Downloaded... '" + address + "'" + " Date: " + currentDate());
                } else {
                    text = functionCall.returnHTML(address);

                    System.out.println("Downloaded... '" + address + "'" + " Date: " + currentDate());
                }
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }
    }

    private String randomUserAgent() {

        String userAgent1 = "Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19";
        String userAgent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";
        String userAgent3 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_4) AppleWebKit/600.7.12 (KHTML, like Gecko) Version/8.0.7 Safari/600.7.12";
        String userAgent4 = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36 RuxitSynthetic/1.0 v6296917904 t38550 ath9b965f92 altpub";
        String userAgent5 = "Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev>(KHTML, like Gecko) Chrome/<Chrome Rev> Safari/<WebKit Rev>";
        String userAgent6 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1";
        String userAgent7 = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";
        String userAgent8 = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.183 Safari/537.36 RuxitSynthetic/1.0 v7506956899 t38550 ath9b965f92 altpub cvcv=2";
        String userAgent9 = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)";
        String userAgent10 = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)";
        String userAgent11 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1)";
        String userAgent12 = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36";

        Integer i = new Random().nextInt(8);
        String[] agents = new String[]{userAgent1, userAgent2, userAgent3, userAgent4, userAgent5, userAgent6, userAgent7, userAgent8, userAgent9, userAgent10, userAgent11, userAgent12};

        return agents[i];
    }

    private String fillRequest(HttpGet httpGet, String address) {

        String text = "curl 'https://www.fanfiction.net/book/Pegasus-Kate-O-Hearn/' " +
                "  -H 'authority: www.fanfiction.net' \\\n" +
                "  -H 'upgrade-insecure-requests: 1' \\\n" +
                "  -H 'dnt: 1' \\\n" +
                "  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36' \\\n" +
                "  -H 'accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9' \\\n" +
                "  -H 'sec-fetch-site: same-origin' \\\n" +
                "  -H 'sec-fetch-mode: navigate' \\\n" +
                "  -H 'sec-fetch-user: ?1' \\\n" +
                "  -H 'sec-fetch-dest: document' \\\n" +
                "  -H 'referer: https://www.fanfiction.net/book/' \\\n" +
                "  -H 'accept-language: en-US,en;q=0.9,tr-TR;q=0.8,tr;q=0.7' \\\n" +
                "  -H 'cookie: __cfduid=da9ba359db2b9d2a1bf578737bc8df1121609233924; __cf_bm=5a45cbadb20ecc4876b535e676ee7fc37fbffa80-1609233925-1800-ARZQsGJb9a107mXEd3+6AFibYErZoUCsynAYtuz+hpV1yGKTBhoQ9HDR4V+br5VeVkhjcST73I0mvAhq7Zoq0YVuwHyMlFtj5WmenVEP34h3KNibo1yPv429EOhHUf22og==; cookies=yes' \\\n";

        httpGet.addHeader("authority", address);
        httpGet.addHeader("upgrade-insecure-requests", "1");
        httpGet.addHeader("dnt", "1");
        httpGet.addHeader("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        httpGet.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.addHeader("sec-fetch-site", "same-origin");
        httpGet.addHeader("sec-fetch-mode", "navigate");
        httpGet.addHeader("sec-fetch-user", "?!");
        httpGet.addHeader("sec-fetch-dest", "document");
        httpGet.addHeader("referer", "https://www.fanfiction.net/book/");
        httpGet.addHeader("accept-language", "en-US,en;q=0.9,tr-TR;q=0.8,tr;q=0.7");
        httpGet.addHeader("cookie", "__cfduid=da9ba359db2b9d2a1bf578737bc8df1121609233924; __cf_bm=5a45cbadb20ecc4876b535e676ee7fc37fbffa80-1609233925-1800-ARZQsGJb9a107mXEd3+6AFibYErZoUCsynAYtuz+hpV1yGKTBhoQ9HDR4V+br5VeVkhjcST73I0mvAhq7Zoq0YVuwHyMlFtj5WmenVEP34h3KNibo1yPv429EOhHUf22og==; cookies=yes");
        httpGet.addHeader("accept", "image/gif, image/jpg, */*");
        httpGet.addHeader("connection", "keep-alive");
        httpGet.addHeader("accept-encoding", "gzip,deflate,sdch");
        return text;
    }


    private String downloadPage(String address, String charset, int tryCount) throws Exception {
        String text = "";
        if (tryCount > 2) return text;

        if (address != null && !address.isEmpty()) {
            address = address.replaceAll("\\s", "%20");
            address = address.replaceAll("\"", "");
            address = (address.startsWith("http://") || address.startsWith("https://")) ? address : "http://" + address;

            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });


            CookieStore cookieStore = new BasicCookieStore();

            SSLContext sslContext = builder.build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();


            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", plainsf)
                    .register("https", sslsf)
                    .build();

            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setConnectionTimeToLive(5, TimeUnit.SECONDS)
                    .setConnectionManager(cm)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setDefaultCookieStore(cookieStore)
                    .build();


            try {
                HttpGet request = new HttpGet(address);
                fillRequest(request, address);

                //Request request = Request.Get(address);
                if (request != null) {
                    try {


                        //request.addHeader("user-agent", randomUserAgent());


                        ResponseHandler<String> rh = new ResponseHandler<String>() {

                            @Override
                            public String handleResponse(
                                    final HttpResponse response) throws IOException {
                                StatusLine statusLine = response.getStatusLine();
                                HttpEntity entity = response.getEntity();
                                if (statusLine.getStatusCode() >= 300) {
                                    throw new HttpResponseException(
                                            statusLine.getStatusCode(),
                                            statusLine.getReasonPhrase());
                                }
                                if (entity == null) {
                                    throw new ClientProtocolException("Response contains no content");
                                }
                                ContentType contentType = ContentType.getOrDefault(entity);
                                Charset charset = contentType.getCharset();
                                charset = charset == null ? StandardCharsets.UTF_8 : charset;
                                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
                                String txt = "";
                                String line = null;
                                while ((line = reader.readLine()) != null) {
                                    txt += line + "\n";
                                }
                                return txt;
                            }
                        };

                        sleep(100);
                        text = httpClient.execute(request, rh); //request.execute();


                    } catch (Exception ex1) {

                        System.out.println("Error in url retrying... '" + address + "'");
                        sleep(200);

                        try {
                            text = downloadPage(address, charset, ++tryCount);
                        } catch (Exception ex2) {
                        }
                    }
                }
            } catch (Exception ex) {

            }
        }
        return text;
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
        }
    }


    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

}
