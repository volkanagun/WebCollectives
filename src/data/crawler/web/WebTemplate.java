package data.crawler.web;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import data.util.TextFile;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String url;
        int index;
        FailCount sharedCount;
        List<WebDocument> documentList;

        public DownloadCallable(List<WebDocument> documentList, FailCount sharedCount, String url, int index) {
            super();
            this.index = index;
            this.url = url;
            this.documentList = documentList;
            this.sharedCount = sharedCount;
        }

    }


    //Extraction Template
    public static String FILEPREFIX = "file://";
    public static String TEXTPREFIX = "text://";
    private Boolean multipleDocuments = false;
    private String multipleIdentifier = null;
    private List<String> seedList;
    private Map<String, String> seedMap;
    private LookupPattern mainPattern;
    private String name;
    private String domain;
    private String folder;
    private String type;
    private Boolean lookComplete;

    private WebSuffixGenerator suffixGenerator;
    private String nextPageSuffix;
    private String nextPageSuffixAddition = "";
    private Integer nextPageSize;
    private Integer nextPageStart;
    private Integer threadSize = 2;
    private Long sleepTime = 200L;

    private Boolean forceWrite = false;

    private String charset = "UTF-8";
    private Map<String, WebTemplate> nextMap;


    public WebTemplate(String folder, String name, String domain) {
        this.name = name;
        this.folder = folder;
        this.domain = domain;
        this.seedList = new ArrayList<>();
        this.seedMap = new HashMap<>();
        this.nextMap = new HashMap<>();
        this.nextPageSize = 2;
        this.nextPageStart = 2;

    }

    public WebTemplate(String folder, String name, String domain, String nextPageSuffix) {
        this(folder, name, domain);
        this.nextPageSuffix = nextPageSuffix;
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

    public WebTemplate addNext(WebTemplate nextTemplate, String label) {
        this.nextMap.put(label, nextTemplate);

        return this;
    }


    public boolean nextEmpty() {
        return this.nextMap.isEmpty();
    }

    public WebTemplate addSeed(String seed) {
        if (!seedList.contains(seed))
            seedList.add(seed);

        return this;
    }

    public WebTemplate addSeeds(Set<String> seeds) {
        for (String seed : seeds) {

            if (!seedList.contains(seed))
                seedList.add(seed);

        }

        return this;
    }

    public WebTemplate addSeed(String genre, String seed) {

        seedMap.put(seed, genre);
        return addSeed(seed);
    }


    public WebTemplate addSeeds(String genre, Set<String> seeds) {
        for (String seed : seeds) {
            seedMap.put(seed, genre);
            addSeed(seed);
        }

        return this;
    }

    public WebTemplate addFolder(String folder) {
        File[] files = (new File(folder)).listFiles();
        for (File file : files) {
            addSeed(FILEPREFIX + file.getPath());
        }

        return this;
    }

    public List<String> getSeedList() {
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

    public WebTemplate setForceWrite(Boolean forceWrite) {
        this.forceWrite = forceWrite;
        return this;
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

    public void saveXML(List<WebDocument> documentList) {
        double sucessRate = 0d;
        if (multipleIdentifier == null) {
            for (WebDocument document : documentList) {
                if (document.isComplete(mainPattern)) {
                    document.saveAsFlatXML();
                    sucessRate++;
                }
            }
        } else {
            for (WebDocument document : documentList) {
                if (document.isComplete(mainPattern)) {
                    document.saveAsMultiXML(multipleIdentifier);
                    sucessRate++;
                }
            }
        }

        System.out.println("Success rate: "+sucessRate/documentList.size());
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

    public String url(String domain, String mainUrl) {
        if (mainUrl.startsWith(domain)) return mainUrl;
        else return domain + mainUrl;
    }

    public void goSleep(){

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void goSleep(Long waitTime){

        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public WebDocument execute() {
        //Download template
        //Extract patterns
        //Pass seedlist to next template as urls if it is a url

        //Download current template
        System.out.println("Seed size: " + seedList.size());
        goSleep();

        List<WebDocument> documentList = download();
        WebDocument mainDocument = new WebDocument(folder, name).setType(type);


        if (nextEmpty()) {
            //Leaf template
            mainDocument.addWebFlowResult(documentList);
            saveXML(documentList);
        } else {
            //Non leaf template
            Collections.shuffle(documentList);
            Map<String, Set<String>> nextSeedList = new HashMap<>();
            Map<String, Map<String, Set<String>>> nextSeedMap = new HashMap<>();

            //clear existing documents
            for (WebDocument document : documentList) {
                Iterator<String> templateIter = getNextIterator();
                Map<String, String> properties = document.getProperties();
                String genre = properties.get(LookupOptions.GENRE);

                while (templateIter.hasNext()) {
                    String templateLink = templateIter.next();
                    WebTemplate template = getNextMap(templateLink);
                    String templateDomain = template.domain;
                    for (LookupResult result : document.getLookupFinalList(templateLink)) {

                        if (genre != null) {
                            template.addSeed(genre, url(templateDomain, result.getText()));
                            //addGenreSeed(nextSeedMap, templateLink, genre, templateDomain + result.getText());
                        } else {
                            template.addSeed(url(templateDomain, result.getText()));
                            //addSeed(nextSeedList, templateLink, templateDomain + result.getText());
                            //nextSeedList.add(domain + result.getText());
                        }


                    }

                    WebDocument webFlowResultList = template.execute();
                    mainDocument.addWebFlowResult(webFlowResultList);
                }
            }

            /*Iterator<String> templateIter = getNextIterator();
            while (templateIter.hasNext()) {
                String templateLink = templateIter.next();
                WebTemplate template = getNextMap(templateLink);

                if (nextSeedMap.containsKey(templateLink)) {
                    Set<String> genres = nextSeedMap.get(templateLink).keySet();
                    for (String genre : genres) {
                        template.addSeeds(genre, nextSeedMap.get(templateLink).get(genre));
                    }
                }

                if (nextSeedList.containsKey(templateLink)) template.addSeeds(nextSeedList.get(templateLink));


                WebDocument webFlowResultList = template.execute();
                mainDocument.addWebFlowResult(webFlowResultList);
            }*/

            saveXML(documentList);
        }


        return mainDocument;
    }

    public void pushGenerateSeeds(List<String> nextPageSeeds, Map<String, String> nextPageSeedGenre) {
        Collections.shuffle(seedList);

        if (nextPageSuffix != null && !nextPageSuffix.isEmpty()) {

            for (String url : seedList) {
                for (int i = nextPageStart; i <= nextPageSize; i++) {
                    String newUrl = url + nextPageSuffix + i + nextPageSuffixAddition;
                    if (!nextPageSeeds.contains(newUrl)) {
                        nextPageSeeds.add(newUrl);
                        nextPageSeedGenre.put(newUrl, seedMap.get(url));
                    }
                }
            }

            for (String url : nextPageSeeds) {

                if (!seedList.contains(url)) {
                    seedList.add(url);
                    seedMap.put(url, nextPageSeedGenre.get(url));
                }
            }
        }

        if (suffixGenerator != null) {
            int size = seedList.size();
            for (int i = 0; i < size; i++) {
                String url = seedList.get(i);
                List<String> generatedSeeds = suffixGenerator.apply(url);
                for (String generatedUrl : generatedSeeds) {
                    if (!seedList.contains(generatedUrl)) {
                        seedList.add(generatedUrl);
                        seedMap.put(generatedUrl, seedMap.get(url));
                    }
                }
            }
        }
    }

    public List<WebDocument> download() {
        List<WebDocument> htmlDocumentList = new ArrayList<>();
        List<String> nextPageSeeds = new ArrayList<>();
        Map<String, String> nextPageSeedGenre = new HashMap<>();


        pushGenerateSeeds(nextPageSeeds, nextPageSeedGenre);

        List<Callable<Boolean>> threadList = new ArrayList<>();
        final FailCount failCount = new FailCount(0, threadSize);
        Collections.shuffle(seedList);
        for (int i = 0; i < seedList.size(); i++) {

            String url = seedList.get(i);
            DownloadCallable thread = new DownloadCallable(htmlDocumentList, failCount, url, i) {
                @Override
                public Boolean call() {

                    WebDocument document = new WebDocument(folder, name, url);
                    Boolean returnResult = false;
                    if (!document.filenameExists()) {
                        goSleep(100L);
                        String downloadedHTML = downloadFile(url, charset);
                        if (downloadedHTML != null && !downloadedHTML.isEmpty()) {
                            document.setFetchDate(new Date());
                            document.setText(downloadedHTML);
                            document.setDomain(domain);
                            document.setType(type);
                            document.setLookComplete(lookComplete);
                            if (seedMap.containsKey(url)) {
                                document.putProperty(LookupOptions.GENRE, seedMap.get(url));
                            }
                            document.setIndex(index);

                            synchronized (documentList) {
                                documentList.add(document);
                            }

                            returnResult = extract(document);
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

        ExecutorService executor = Executors.newFixedThreadPool(threadSize);
        try {
            List<Future<Boolean>> returnList = executor.invokeAll(threadList);

            while (checkAnyUnfinished(returnList)) {
                Thread.sleep(20000);
            }
            executor.shutdown();
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        return htmlDocumentList;
    }

    private Boolean checkAnyUnfinished(List<Future<Boolean>> returnList) {
        for (Future<Boolean> task : returnList) {
            try {
                task.get(1000L, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {

            } catch (ExecutionException e) {

            } catch (TimeoutException e) {

            }
        }

        return false;
    }

    public Boolean extract(WebDocument html) {

        List<LookupResult> results = mainPattern.getResult(html.getProperties(), html.getText());
        html.setLookupResultList(results);
        return !results.isEmpty();

    }

    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Utilities">

    private String downloadFile(String address, String charset) {

        if (address.startsWith(TEXTPREFIX)) {
            String text = address.substring(address.indexOf(TEXTPREFIX) + TEXTPREFIX.length());
            return text;
        } else if (address.startsWith(FILEPREFIX)) {
            String filename = address.substring(address.indexOf(FILEPREFIX) + FILEPREFIX.length());
            System.out.println("Downloading... '" + address + "'");
            return new TextFile(filename, charset).readFullText();
        } else {
            System.out.println("Downloading... '" + address + "'");
            String text = "";
            try {
                text = downloadPage(address, charset, 0);

            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            return text;
        }
    }

    private String downloadPage(String address, String charset, int tryCount) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        String text = "";
        if(tryCount > 2) return text;

        if (address != null && !address.isEmpty()) {
            address = address.replaceAll("\\s", "%20");
            address = (address.startsWith("http://") || address.startsWith("https://")) ? address : "http://" + address;
            HttpGet request = new HttpGet(address);
            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });

            SSLContext sslContext = builder.build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
            Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", plainsf)
                    .register("https", sslsf)
                    .build();

            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();


            //Request request = Request.Get(address);
            if (request != null) {
                try {
                    //request.socketTimeout(160).connectTimeout(1500);
                    request.addHeader("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
                    request.addHeader("accept", "image/gif, image/jpg, */*");
                    request.addHeader("connection", "keep-alive");
                    request.addHeader("accept-encoding", "gzip,deflate,sdch");
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
                            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
                            String txt = "";
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                txt += line + "\n";
                            }
                            return txt;
                        }
                    };

                    Thread.sleep(1000);
                    text = httpClient.execute(request, rh); //request.execute();

                } catch (IOException ex) {
                    System.out.println("Error in url retrying... '" + address + "'");

                    text = downloadPage(address,charset, ++tryCount);

                } catch (InterruptedException ex) {
                    Logger.getLogger(WebTemplate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return text;
    }


    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

}
