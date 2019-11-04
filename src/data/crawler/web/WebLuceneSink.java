package data.crawler.web;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WebLuceneSink implements Serializable {
    private Path indexpath;
    private IndexWriter indexWriter;
    private IndexSearcher searcher;

    public WebLuceneSink(String indexFilename) {
        File f = new File(indexFilename);
        f.mkdir();
        indexpath = Paths.get(f.getAbsolutePath());

    }

    public synchronized WebLuceneSink openWriter() {

        if(indexWriter==null) {
            try (FSDirectory dir = FSDirectory.open(indexpath)) {
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                indexWriter = new IndexWriter(dir, config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public synchronized WebLuceneSink closeWriter(){
        try {
            if(indexWriter!=null && indexWriter.isOpen()) {
                indexWriter.close();
                indexWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public List<Document> createIndexDocument(WebDocument webDocument) {
        List<LookupResult> lookupResults = webDocument.getIndexDocument();
        List<Document> documents = new ArrayList<>();
        for (LookupResult lookupResult : lookupResults) {
            Document document = new Document();
            List<LookupResult> fields = lookupResult.getSubList();
            Boolean foundID = false;
            for (LookupResult fieldResult : fields) {
                if (LookupResult.ID.equals(fieldResult.getType())) {
                    foundID = true;
                    document.add(new StringField(LookupResult.ID, fieldResult.getLabel(), Field.Store.YES));
                } else {
                    document.add(new TextField(fieldResult.getType(), fieldResult.getLabel(), Field.Store.YES));
                }
            }

            if(foundID) documents.add(document);
        }

        return documents;

    }

    public int writeDocuments(List<WebDocument> documents) {

        int count = 0;
        for(WebDocument webDocument:documents){
            count += writeDocument(webDocument);
        }

        return count;
    }

    private int writeDocument(WebDocument webDocument){
        int count = 0;
        try {
            List<Document> luceneDocuments = createIndexDocument(webDocument);
            for(Document luceneDocument:luceneDocuments) {
                if(!lookupDocument(luceneDocument)) {
                    count++;
                    indexWriter.addDocument(luceneDocument);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in indexing");
        }
        return count;
    }

    private boolean lookupDocument(Document document){
        Boolean found = false;
        try {
            TopDocs docs = searcher.search(new TermQuery(new Term(LookupResult.ID, document.get(LookupResult.ID))),1);
            if(docs.totalHits.value > 0) found = true;
        } catch (IOException e) {

        }

        return found;
    }

}
