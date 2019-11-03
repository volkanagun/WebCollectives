package data.crawler.web;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

    public WebLuceneSink(String indexFilename) {
        indexpath = Paths.get(new File(indexFilename).getAbsolutePath());

    }

    public WebLuceneSink openWriter() {

        try (FSDirectory dir = FSDirectory.open(indexpath)) {
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            indexWriter = new IndexWriter(dir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public WebLuceneSink closeWriter(){
        try {
            if(indexWriter!=null) {
                indexWriter.close();
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
            for (LookupResult fieldResult : fields) {
                if (LookupResult.ID.equals(fieldResult.getType())) {
                    document.add(new StringField(LookupResult.ID, fieldResult.getLabel(), Field.Store.YES));
                } else {
                    document.add(new TextField(fieldResult.getType(), fieldResult.getLabel(), Field.Store.YES));
                }
            }
            documents.add(document);
        }

        return documents;

    }

    public WebLuceneSink writeDocuments(List<WebDocument> documents) {
        openWriter();
        for(WebDocument webDocument:documents){
            writeDocument(webDocument);
        }

        closeWriter();
        return this;
    }

    private WebLuceneSink writeDocument(WebDocument webDocument){

        try {
            indexWriter.addDocuments(createIndexDocument(webDocument));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return this;
    }

}
