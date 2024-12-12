package data.crawler.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WebDocumentList {

    private List<WebDocument> currentDocuments = new ArrayList<>();
    private List<WebDocument> nextDocuments = new ArrayList<>();

    public WebDocumentList() {
    }

    public WebDocumentList(List<WebDocument> documents){
        this.currentDocuments = documents;

    }

    public WebDocumentList(List<WebDocument> crrDocuments,List<WebDocument> nextDocuments){
        this.currentDocuments = crrDocuments;
        this.nextDocuments = nextDocuments;
    }

    public WebDocumentList add(WebDocument document){
        this.currentDocuments.add(document);
        return this;
    }

    public WebDocumentList addNext(WebDocument nextDocument){

        this.nextDocuments.add(nextDocument);
        return this;
    }

    public WebDocumentList shuffle(){
        Collections.shuffle(currentDocuments);
        Collections.shuffle(nextDocuments);
        return this;
    }

    public List<WebDocument> getCurrentDocuments() {
        return currentDocuments;
    }

    public WebDocumentList clearCurrentDocuments(){
        this.currentDocuments.clear();
        return this;
    }

    public List<WebDocument> getNextDocuments() {
        List<WebDocument> webDocs = new ArrayList<>();
        for(WebDocument next:nextDocuments){
            if(next!=null && next.getProperties() !=null){
                webDocs.add(next);
            }
        }
        return webDocs;
    }

    public List<WebDocument> getAllDocuments(){
        List<WebDocument> crr = new ArrayList<>(getNextDocuments());
        crr.addAll(currentDocuments);
        return crr;
    }
}
