package data.crawler.web;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.bytedeco.javacv.FrameFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebTokenSink extends WebSink {


    private Set<String> tokens = new HashSet<>();

    public WebTokenSink() {
    }

    public WebTokenSink(Set<String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public WebSink initialize() {
        return this;
    }

    public List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("TEXT", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    @Override
    public void addDocuments(List<WebDocument> documents) {

        for(WebDocument document:documents){
            List<LookupResult> subresults = document.getLookupFinalList(LookupOptions.ARTICLEPARAGRAPH);
            for(LookupResult subResult:subresults){
                try {
                    tokens.addAll(analyze(subResult.getText(), new StandardAnalyzer()));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> getText() {
        return new ArrayList<>(tokens);
    }
}
