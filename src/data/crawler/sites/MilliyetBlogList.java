package data.crawler.sites;

import data.crawler.web.LookupOptions;
import data.crawler.web.LookupPattern;
import data.crawler.web.WebFlow;
import data.crawler.web.WebTemplate;

import java.io.Serializable;

public class MilliyetBlogList implements Serializable {
    public static WebFlow build() {
        WebTemplate yazarTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "yazar-links", LookupOptions.EMPTYDOMAIN);
        LookupPattern authorPattern = new LookupPattern(LookupOptions.URL, LookupOptions.AUTHORLINK, "<a(.*?)href\\=\"/(.*?)/\\?UyeNo\\=", "\"\\s?(target=\"_blank\"|class=\"flt_left\"?)>");

        yazarTemplate.setMainPattern(authorPattern);
        yazarTemplate.setDomain("http://blog.milliyet.com.tr/BloggerBloglar/?UyeNo=")
                .setForceWrite(false)
                .setNextPageSuffix("&Page=")
                .setNextPageStart(1)
                .setNextPageSize(500)
                .setThreadSize(1)
                .addSeed("http://blog.milliyet.com.tr/BlogListe/?Status=&Sort=&KategoriNo=");
        /*.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=2")*/
                /*.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=3")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=4")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=5")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=6")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=7")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=8")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=9")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=10")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=11")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=12")*/
        //.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=13")
        /*.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=19")*/
                /*.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=20")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=21")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=22")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=23")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=24")
                */
        //.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=25")
        //.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=281");
                /*.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=2")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=3")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=4")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=5")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=6")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=16")
                .addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=102");*/
        //.addSeed("http://blog.milliyet.com.tr/BlogListeKategori/?Status=&Sort=&KategoriNo=199");

        LookupPattern linkPattern = new LookupPattern(LookupOptions.SKIP, LookupOptions.CONTAINER, "<div\\sclass=\"details\">", "</div>")
                .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<h6>", "</h6>")
                        .setStartEndMarker("<h6", "</h6")
                        .addPattern(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a\\shref\\=\"", "\"\\starget\\=")));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-links", LookupOptions.EMPTYDOMAIN);
        linkTemplate.setMainPattern(linkPattern)
                .setForceWrite(false)
                .setDomain("http://blog.milliyet.com.tr/BloggerBloglar/?UyeNo=")
                .setNextPageSuffix("&Page=")
                .setNextPageStart(1)
                .setNextPageSize(50)
                .setThreadSize(2);


        LookupPattern articlePattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLE, "<div\\sid\\=\"_middle_content_bottom_child2\"\\sclass\\=\"colA\">", "</div>")
                .setStartEndMarker("<div", "</div")
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.AUTHOR, "<div\\sclass\\=\"blogerBaslik\\scurvedNoBottom\">", "</div>")
                        .setType(LookupOptions.SKIP)
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHORNAME, "<h4>", "</h4>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.GENRETEXT, "<dd>", "</dd>")
                        .setType(LookupOptions.SKIP)
                        .setStartEndMarker("<dd", "</dd>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.GENRE, "<a\\shref\\=\"", "\\/Kategori\\/\\?")))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, "<h1>", "</h1>"))
                .addPattern(new LookupPattern(LookupOptions.ARTICLE, LookupOptions.ARTICLETEXT, "<div\\sid\\=\"BlogDetail\"(.*?)>", "</div>")
                        .setStartEndMarker("<div", "</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, "<p(.*?)>", "</p>")));

        WebTemplate articleTemplate = new WebTemplate(LookupOptions.BLOGDIRECTORY, "blog-text", LookupOptions.EMPTYDOMAIN)
                .setDomain("http://blog.milliyet.com.tr");
        articleTemplate.setMainPattern(articlePattern).setForceWrite(true)
                .setType(LookupOptions.BLOGDOC);


        yazarTemplate.addNext(linkTemplate, LookupOptions.AUTHORLINK);
        linkTemplate.addNext(articleTemplate, LookupOptions.ARTICLELINK);
        WebFlow flow = new WebFlow(yazarTemplate);
        return flow;
    }
}
