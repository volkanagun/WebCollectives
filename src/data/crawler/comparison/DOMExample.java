package data.crawler.comparison;

import data.crawler.web.LookupOptions;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class DOMExample {
    private DOMModel domModel = new DOMModel();
    private DOMHierarchy domHierarchy = domModel.getDomHierarchy();

    public DOMExample build() {
        DOMHierarchy root = domHierarchy;

        DOMHierarchy article = new DOMHierarchy(LookupOptions.ARTICLE);
        article.add(DOMLabel.create(LookupOptions.ARTICLE, "div", "class", "container"));
        root.addChild(article);

        DOMHierarchy author = new DOMHierarchy(LookupOptions.AUTHOR);
        author.add(DOMLabel.create(LookupOptions.AUTHOR, "div", "class", "news-profile"));
        article.addChild(author);
        domModel.structure(DOMModel.ROOT, LookupOptions.AUTHOR);

        DOMHierarchy title = new DOMHierarchy(LookupOptions.ARTICLETITLE);
        title.add(DOMLabel.create(LookupOptions.AUTHOR, "h1", ".*?", ".*?"));
        article.addChild(title);
        domModel.structure(DOMModel.ROOT, LookupOptions.ARTICLETITLE);

        DOMHierarchy text = new DOMHierarchy(LookupOptions.ARTICLETEXT);
        text.add(DOMLabel.create(LookupOptions.ARTICLETEXT, "div", "class", "news-content(.*?)"));
        article.addChild(text);
        domModel.structure(DOMModel.ROOT, LookupOptions.ARTICLETEXT);

        DOMHierarchy paragraph = new DOMHierarchy(LookupOptions.ARTICLEPARAGRAPH);
        paragraph.add(DOMLabel.create(LookupOptions.ARTICLEPARAGRAPH, "((p|h).*?)", "(.*?)", "(.*?)"));
        text.addChild(paragraph);
        domModel.structure(LookupOptions.ARTICLETEXT, LookupOptions.ARTICLEPARAGRAPH);

        DOMHierarchy genreContainer = new DOMHierarchy(LookupOptions.CONTAINER);
        genreContainer.add(DOMLabel.create(LookupOptions.CONTAINER, "div", "class", "news-tags"));
        article.addChild(genreContainer);
        domModel.structure(DOMModel.ROOT, LookupOptions.CONTAINER);

        DOMHierarchy genre = new DOMHierarchy(LookupOptions.GENRE);
        genre.add(DOMLabel.create(LookupOptions.GENRE, "a", "(.*?)", "(.*?)"));
        genreContainer.addChild(genre);
        domModel.structure(LookupOptions.CONTAINER, LookupOptions.GENRE);


        return this;
    }

    public void extract(String htmlFolder) throws IOException {
        File file = new File(htmlFolder);
        File[] files = file.listFiles();

        for (File f : files) {
            String fname = LookupOptions.COMPAREDIRECTORY + f.getName();
            Document document = domModel.parseTime(f);
            String content = domModel.extractTime(document);
            if (!content.isEmpty()) {
                domModel.write(fname, content);
            } else {
                f.delete();
            }
        }

        domModel.printAverageTime();
    }

    public static void main(String[] args) throws IOException {
        DOMExample example = new DOMExample().build();
        example.extract(LookupOptions.COMPAREHTMLDIRECTORY);
    }

}
