package data.crawler.web;

import data.util.TextFile;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Created by wolf on 02.07.2015.
 */
public class WebDocument implements Serializable {
    private String folder;
    private String name;
    private String url;
    private String text;
    private String type;
    private Map<String, String> properties;
    private String domain;
    private int index = 1;
    private List<LookupResult> lookupResultList;
    private final List<WebDocument> webFlowResultList;
    private Date fetchDate;
    private Boolean lookComplete = false;
    private String multiDocumentIdentifier;

    public WebDocument(String folder) {
        this.folder = folder;
        this.lookupResultList = new ArrayList<>();
        this.webFlowResultList = new ArrayList<>();
        this.properties = new HashMap<>();
    }

    public WebDocument(String folder, String name, String url) {
        this(folder);
        this.name = name;
        this.url = url;
    }

    public WebDocument(String folder, String name) {
        this(folder);
        this.name = name;
    }

    public List<WebDocument> getWebFlowResultList() {
        return webFlowResultList;
    }

    public WebDocument putProperty(String label, String value) {
        properties.put(label, value);
        return this;
    }


    public WebDocument addWebFlowResult(WebDocument webDocument) {
        //webDocument.setFolder(folder);
        this.webFlowResultList.add(webDocument);
        return this;
    }

    public WebDocument addWebFlowResult(List<WebDocument> webDocumentList) {
        for (WebDocument webDocument : webDocumentList) {
            addWebFlowResult(webDocument);
        }
        return this;
    }

    public WebDocument addLookupResult(LookupResult lookupResult) {
        this.lookupResultList.add(lookupResult);
        return this;
    }

    public List<LookupResult> getLookupFinalList(String labelLook) {
        List<LookupResult> lookupResults = new ArrayList<>();
        for (LookupResult aresult : lookupResultList) {
            List<LookupResult> allSubList = aresult.getSubResults(labelLook);
            lookupResults.addAll(allSubList);
        }
        return lookupResults;

    }

    public String getMultiDocumentIdentifier() {
        return multiDocumentIdentifier;
    }

    public WebDocument setMultiDocumentIdentifier(String multiDocumentIdentifier) {
        this.multiDocumentIdentifier = multiDocumentIdentifier;
        return this;
    }

    public Boolean getLookComplete() {
        return lookComplete;
    }

    public WebDocument setLookComplete(Boolean lookComplete) {
        this.lookComplete = lookComplete;
        return this;
    }

    private String nameCode() {
        Integer hashcode = url.hashCode();
        if (hashcode > 0)
            return "-p-" + hashcode;
        else
            return "-n-" + -hashcode;
    }

    private String nameCode(String title) {
        Integer hashcode = url.hashCode() + 7 * title.hashCode();
        if (hashcode > 0)
            return "-p-" + hashcode;
        else
            return "-n-" + -hashcode;
    }

    public String filename() {
        return folder + "/" + name + nameCode();
    }

    public String htmlFilename(String htmlFolder) {
        return htmlFolder + "/" + name + nameCode();
    }

    public String filename(String title) {
        return folder + "/" + name + nameCode(title);
    }

    public Boolean filenameExists() {
        return (new File(filename())).exists();
    }

    public void deleteOnExit() {
        new File(filename()).deleteOnExit();
    }

    public Boolean exists(String identifier) {
        for (LookupResult resultFile : lookupResultList) {
            String code = resultFile.getSubResults(identifier).get(0).getText();
            TextFile textFile = new TextFile(filename(code));
            if (textFile.exists()) continue;
            else return false;
        }

        return true;
    }

    public Boolean exists() {

        TextFile textFile = new TextFile(filename());
        return textFile.exists();

    }

    public Boolean saveHTML(String htmlFolder) {
        if (htmlFolder != null) {
            (new File(htmlFolder)).mkdirs();
            if (!name.isEmpty()) {
                TextFile textFile = new TextFile(htmlFilename(htmlFolder));
                if (!textFile.exists()) {
                    textFile.openBufferWrite();
                    textFile.writeFullText(text);
                    textFile.closeBufferWrite();
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean loadHTML(String htmlFolder) {
        if (htmlFolder != null) {
            (new File(htmlFolder)).mkdirs();
            String htmlFilename = htmlFilename(htmlFolder);
            if (!name.isEmpty() && new File(htmlFilename).exists()) {
                TextFile textFile = new TextFile(htmlFilename);
                if (!textFile.exists()) {
                    textFile.openBufferRead();
                    setText(textFile.readFullText());
                    textFile.closeBufferRead();

                    return true;
                }
            }
        }

        return false;
    }

    public List<LookupResult> getIndexDocument() {
        List<LookupResult> lookupFinals = new ArrayList<>();
        if (multiDocumentIdentifier != null && !name.isEmpty() && !lookupResultList.isEmpty()) {
            for (LookupResult resultFile : lookupResultList) {
                lookupFinals.add(getIndexDocument(resultFile));
            }
        }
        else if(!name.isEmpty() && !lookupResultList.isEmpty()){
            lookupFinals.add(getIndexDocument(lookupResultList.get(0)));
        }

        return lookupFinals;
    }

    private LookupResult getIndexDocument(LookupResult resultFile) {
        String icode = multiDocumentIdentifier != null ?
                filename(resultFile.getSubResults(multiDocumentIdentifier).get(0).getText()) : filename();
        LookupResult documentResult = new LookupResult(LookupResult.DOC, LookupResult.DOC);

        LookupResult iResult = new LookupResult(LookupResult.ID, icode);
        LookupResult tResult = new LookupResult(LookupResult.TYPE, resultFile.getType());
        LookupResult urlResult = new LookupResult(LookupResult.URL, url);
        LookupResult htmlResult = new LookupResult(LookupResult.HTML, text);
        List<LookupResult> contentResult = resultFile.getAllSubLinear();

        documentResult.addSubList(iResult);
        documentResult.addSubList(urlResult);
        documentResult.addSubList(htmlResult);
        documentResult.addSubList(contentResult);
        return documentResult;
    }

    public int saveAsMultiXML(String identifier) {
        (new File(folder)).mkdirs();
        int count = 0;
        if (!name.isEmpty() && !lookupResultList.isEmpty()) {
            for (LookupResult resultFile : lookupResultList) {
                String code = resultFile.getSubResults(identifier).get(0).toString();
                TextFile textFile = new TextFile(filename(code));
                if (!textFile.exists()) {
                    textFile.openBufferWrite();
                    textFile.writeNextLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    if (type == null)
                        textFile.writeNextLine("<ROOT>");
                    else
                        textFile.writeNextLine("<ROOT LABEL=\"" + type + "\">");

                    textFile.writeNextLine(resultFile.tagOpen());

                    for (LookupResult subResult : resultFile.getSubList()) {
                        String main = subResult.toString();
                        textFile.writeNextLine(main);
                    }
                    textFile.writeNextLine(resultFile.tagClose());
                    textFile.writeNextLine("</ROOT>");
                    textFile.closeBufferWrite();
                    count++;
                }
            }
        }

        return count;

    }

    public boolean isComplete(LookupPattern mainPattern) {
        if (lookComplete) {
            List<String> listLabels = mainPattern.getNonSkipSubpatternLabels();
            for (LookupResult lookupResult : lookupResultList) {
                Set<String> labelSet = lookupResult.getSubResultLabels(listLabels);
                if (labelSet.containsAll(listLabels)) continue;
                else return false;
            }
        }
        //System.out.println("Save ok...");
        return true;
    }


    public boolean isEmpty(){
        return lookupResultList.isEmpty();
    }

    public int saveAsFlatXML() {
        (new File(folder)).mkdirs();
        if (!name.isEmpty() && !lookupResultList.isEmpty()) {
            TextFile textFile = new TextFile(filename());
            if (!textFile.exists()) {
                textFile.openBufferWrite();
                textFile.writeNextLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                if (type == null)
                    textFile.writeNextLine("<ROOT>");
                else
                    textFile.writeNextLine("<ROOT LABEL=\"" + type + "\">");


                for (LookupResult lookupResult : lookupResultList) {
                    String main = lookupResult.toString();
                    textFile.writeNextLine(main);
                }
                textFile.writeNextLine("</ROOT>");
                textFile.closeBufferWrite();
                return 1;
            }
        }

        return 0;

    }

    public String extractLabel(String label) {
        String authorName = "";
        for (LookupResult lookupResult : lookupResultList) {
            List<LookupResult> subList = lookupResult.getSubResults(label);
            if (subList != null && !subList.isEmpty()) {
                authorName = subList.get(0).getText();
                break;
            }
        }

        return authorName;
    }

    public Date getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate(Date fetchDate) {
        this.fetchDate = fetchDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public WebDocument setText(String text) {
        this.text = text;
        return this;
    }

    public String getType() {
        return type;
    }

    public WebDocument setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<LookupResult> getLookupResultList() {
        return lookupResultList;
    }


    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setLookupResultList(List<LookupResult> lookupResultList) {
        this.lookupResultList = lookupResultList;
    }

    public void addLookupResultList(List<LookupResult> lookupResultList) {
        this.lookupResultList.addAll(lookupResultList);
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getIndex() {
        return index;
    }

    public WebDocument setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public String toString() {
        String resultText = "";
        if (webFlowResultList.isEmpty() && !lookupResultList.isEmpty()) {
            for (LookupResult result : lookupResultList) {
                resultText += result.toString() + "\n";
            }
        } else if (!webFlowResultList.isEmpty()) {
            for (WebDocument subDocument : webFlowResultList) {
                resultText += subDocument.toString() + "\n";
            }
        } else {
            resultText += text + "\n";
        }
        return resultText;
    }
}
