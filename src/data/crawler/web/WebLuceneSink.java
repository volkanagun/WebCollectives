package data.crawler.web;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.bytedeco.javacv.FrameFilter;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WebLuceneSink extends WebSink {
    private Path indexpath;
    private IndexWriter indexWriter;
    private IndexSearcher searcher;

    public WebLuceneSink(String indexFilename) {
        File f = new File(indexFilename);
        f.mkdir();
        indexpath = Paths.get(f.getAbsolutePath());

    }

    @Override
    public WebSink initialize() {
        openWriter();
        return this;
    }

    public synchronized WebLuceneSink openWriter() {

        if (indexWriter == null) {
            try {
                FSDirectory dir = FSDirectory.open(indexpath);
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);


                indexWriter = new IndexWriter(dir, config);
                //searcher = new IndexSearcher(DirectoryReader.open(indexWriter));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public synchronized WebLuceneSink closeWriter() {

        if (indexWriter != null && indexWriter.isOpen()) {

            indexWriter = null;
            searcher = null;
        }

        return this;
    }

    @Override
    public void addDocuments(List<WebDocument> documents) {
        writeDocuments(documents);
    }

    @Override
    public List<String> getText() {
        return new ArrayList<>();
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

            if (foundID) documents.add(document);
        }

        return documents;

    }

    public int writeDocuments(List<WebDocument> documents) {

        int count = 0;
        for (WebDocument webDocument : documents) {
            count += writeDocument(webDocument);
        }

        return count;
    }

    private int writeDocument(WebDocument webDocument) {
        int count = 0;

        List<Document> luceneDocuments = createIndexDocument(webDocument);
           /* for (Document luceneDocument : luceneDocuments) {
                if (!lookupDocument(luceneDocument)) {
                    count++;*/
        synchronized (indexWriter) {
            try {
                //openWriter();
                indexWriter.addDocuments(luceneDocuments);
                indexWriter.commit();
                count+= luceneDocuments.size();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
             /*   }
            }*/


        return count;
    }

    private boolean lookupDocument(Document document) {
        Boolean found = false;
        try {
            TopDocs docs = searcher.search(new TermQuery(new Term(LookupResult.ID, document.get(LookupResult.ID))), 1);
            if (docs.totalHits.value > 0) found = true;
        } catch (IOException e) {

        }

        return found;
    }

}
