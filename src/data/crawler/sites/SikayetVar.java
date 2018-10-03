package data.crawler.sites;

import data.crawler.web.*;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SikayetVar implements Serializable {

    public static WebFlow build(){
        String domain = "https://www.sikayetvar.com/";
        String seed = "https://www.sikayetvar.com/sikayetler";
        Integer start = 1;
        Integer end = 2;
        LookupPattern urlPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLELINKCONTAINER, "<div class=\"media-body\">","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.URL,"<a href=\"","\"\\s(title||class)"));

        WebTemplate linkTemplate = new WebTemplate(LookupOptions.SIKAYETVARDOC, "links", LookupOptions.EMPTYDOMAIN);

        linkTemplate = linkTemplate.setMainPattern(urlPattern)
                .setThreadSize(2)
                .setNextPageStart(start)
                .setNextPageSize(end)
                .setNextPageSuffix("?page=")
                .addSeed(seed)
                .setDomain(domain);

        LookupPattern mainPattern = new LookupPattern(LookupOptions.CONTAINER, LookupOptions.URL, "<div class=\"quickPreviewContainer\"","</div>")
                .setStartEndMarker("<div","</div>")
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE,"<h[12]\\sclass\\=\"title\"","</h[12]>"))
                .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE,"<span title=\"","\"")
                        .setNth(0)
                        .setRemoveTags(true))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.ARTICLETEXT,"<div class=\"description\">","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETEXT, "<p>","</p>")))
                .addPattern(new LookupPattern(LookupOptions.CONTAINER, LookupOptions.TAG, "<div class=\"hashtags\">","</div>")
                        .addPattern(new LookupPattern(LookupOptions.TEXT, LookupOptions.TAG, "<a(.*?)>","</a>")));



        WebTemplate mainTemplate = new WebTemplate(LookupOptions.SIKAYETVARDOC, "docs", LookupOptions.EMPTYDOMAIN);
        mainTemplate.setMainPattern(mainPattern)
                .setDomain(domain)
                .setLookComplete(false);

        WebDocument doc = new WebDocument("","deneme.xml","deneme").setText("<!DOCTYPE html>\n" +
                "<html lang=\"tr\">\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\"/>\n" +
                "<title>Türk Telekom TTNET'in Sorumsuz Tutumu! - Şikayetvar</title>\n" +
                "<meta name=\"description\" content=\"Türk Telekom için yazılan 'Türk Telekom TTNET'in Sorumsuz Tutumu!' şikayetini ve yorumlarını okumak ya da Türk Telekom hakkında şikayet yazmak için tıklayın!\" /><meta name=\"twitter:title\" content=\"► @Turk_Telekom TTNET'in Sorumsuz Tutumu!\" /><meta name=\"twitter:card\" content=\"summary\" /><meta name=\"twitter:site\" content=\"@SikayetvarCom\" /><meta name=\"twitter:image\" content=\"https://cdn.sikayetvar.com/assets/twitter-share-card-image.png\" /><meta name=\"twitter:description\" content=\"Türk Telekom için yazılan 'Türk Telekom TTNET'in Sorumsuz Tutumu!' şikayetini ve yorumlarını okumak ya da Türk Telekom hakkında şikayet yazmak için tıklayın!\" /><meta property=\"og:image\" content=\"https://cdn.sikayetvar.com/assets/facebook-share-card-image.png\" /><meta property=\"og:title\" content=\"Türk Telekom TTNET'in Sorumsuz Tutumu!\" /><meta property=\"og:description\" content=\"Türk Telekom için yazılan 'Türk Telekom TTNET'in Sorumsuz Tutumu!' şikayetini ve yorumlarını okumak ya da Türk Telekom hakkında şikayet yazmak için tıkl...\" /><meta name=\"yandex-verification\" content=\"62a99b502d70a7cf\" />\n" +
                "<link rel=\"canonical\" href=\"https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\" />                    <link rel=\"amphtml\" href=\"https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu/amp\" />                                                    \n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "<meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//cdn-desktop.sikayetvar.com\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//cdn.sikayetvar.com\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//www.google-analytics.com\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//pagead2.googlesyndication.com\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//tpc.googlesyndication.com\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//googleads.g.doubleclick.net\"/>\n" +
                "<link rel=\"dns-prefetch\" href=\"//securepubads.g.doubleclick.net\"/>\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn-desktop.sikayetvar.com/system/cache/css/2016.sikayetvar.com-style.1127.min.css\">\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/css/extra/owl.carousel.min.css?28\">\n" +
                "<link rel=\"icon\" type=\"image/png\" href=\"https://cdn-desktop.sikayetvar.com/images/favicon.ico\">\n" +
                "<script async src=\"https://www.googletagmanager.com/gtag/js?id=UA-2527738-4\"></script>\n" +
                "<script>\n" +
                "      window.dataLayer = window.dataLayer || [];\n" +
                "      function gtag(){dataLayer.push(arguments);}\n" +
                "      gtag('js', new Date());\n" +
                "      gtag('config', 'UA-2527738-4', {'custom_map': {'dimension1': 'Viewed Company ID'}});\n" +
                "</script>\n" +
                "<script async='async' src='https://www.googletagservices.com/tag/js/gpt.js'></script>\n" +
                "<script>\n" +
                "      var googletag = googletag || {};\n" +
                "      googletag.cmd = googletag.cmd || [];\n" +
                "</script>\n" +
                "<script async='async' type=\"text/javascript\" src=\"https://static.criteo.net/js/ld/publishertag.js\"></script>\n" +
                "<script>\n" +
                "        window.Criteo = window.Criteo || {};\n" +
                "        window.Criteo.events = window.Criteo.events || [];\n" +
                "</script>  \n" +
                "</head>\n" +
                "<body>\n" +
                "<header>\n" +
                "<div class=\"row\">\n" +
                "<div class=\"small-1 columns nopadding sv-logo\">\n" +
                "<a href=\"/\" title=\"Şikayetvar\" aria-haspopup=\"true\" class=\"has-tip\" data-disable-hover='false'>\n" +
                "<img src=\"https://cdn-desktop.sikayetvar.com/images/small-logo.svg\" alt=\"Şikayetvar Logo\" width=\"50\" height=\"58\" />\n" +
                "</a>\n" +
                "</div>\n" +
                "<div class=\"small-8 columns nopadding\">\n" +
                "<div id=\"main-search\" class=\"container\">\n" +
                "<div class=\"search-wrapper\">\n" +
                "<div class=\"cell\">\n" +
                "<div class=\"key-wrapper\">\n" +
                "</div>\n" +
                "<div class=\"scroll\">\n" +
                "<a class=\"prev\"></a>\n" +
                "<a class=\"next\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"input-wrapper\">\n" +
                "<input type=\"text\" id=\"search-keyword\" class=\"form-control\" placeholder=\"Şikayet veya marka arayın...\">\n" +
                "<input type=\"text\" id=\"search-keyword-hint\" disabled class=\"form-control\" value=\"\">\n" +
                "<div class=\"lastSearchResults\">\n" +
                "<div class=\"info\">Son arananlar <span>Temizle</span></div>\n" +
                "<div class=\"results\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "<div class=\"sub-search-button icon-cancel\" id=\"search-keyword-search-button\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-3 columns nopadding align-right text-right\">\n" +
                "<ul class=\"menu\">\n" +
                "<li class=\"loginButtonSubPages\">\n" +
                "<a data-fullmodal=\"true\" data-overlay=\"login-form-modal\" data-ajax-url=\"login.form\" class=\"login-link\">Giriş Yap</a>\n" +
                "<div class=\"divider\"></div>\n" +
                "<a data-fullmodal=\"true\" data-overlay=\"signup-form-modal\" data-ajax-url=\"signup.form\" class=\"login-link\">Üye Ol</a>\n" +
                "</li>\n" +
                "<li>\n" +
                "<a class=\"btn orange btn-large\" id=\"write-complaint\" href=\"/sikayetyaz\" onclick=\"setEventTrigger('Complaint Write (Desktop)','Start','Open from top Button');\" rel=\"nofollow\">Şikayet Yaz\n" +
                "</a>\n" +
                "</li>\n" +
                "</ul>\n" +
                "</div>\n" +
                "</div>\n" +
                "</header>\n" +
                "<script>\n" +
                "    var userLoged =  false ;\n" +
                "</script><main id=\"complaint-detail\">\n" +
                "<section class=\"breadcrumbs row\">\n" +
                "<ul>\n" +
                "<li><a href=\"/\" title=\"Anasayfa\">Anasayfa</a> <i class=\"icon-right-open-big\"></i></li>\n" +
                "<li><a href=\"/turk-telekom\" title=\"Türk Telekom\">Türk Telekom</a> <i class=\"icon-right-open-big\"></i></li>            <li>Türk Telekom TTNET'in Sorumsuz Tutumu!</li>        </ul>\n" +
                "</section>\n" +
                "<section>\n" +
                "<div class=\"row details\">\n" +
                "<div class=\"quickPreviewContainer\">\n" +
                "<span title=\"30 Mart 2017 10:16\" class=\"date date-tips\"><i class=\"icon-clock\"></i> 30 Mart 2017 </span>\n" +
                "<span class=\"complaint-id\">#8153428</span>\n" +
                "<div class=\"left\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\" class=\"company-logo\">\n" +
                "<img src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" alt=\"Türk Telekom\" width=\"90\" height=\"50\">\n" +
                "</a>\n" +
                "<div class=\"quick-share\">\n" +
                "<a target=\"_blank\" class=\"facebook share-tips-top\" title=\"Facebook'ta paylaş\" href=\"http://www.facebook.com/sharer/sharer.php?u=https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\" onclick=\"setEventTrigger('Share Buttons (Desktop)','Share','facebook');\">\n" +
                "<i class=\"block icon-facebook\"></i>\n" +
                "</a>\n" +
                "<a target=\"_blank\" class=\"twitter share-tips-top\" title=\"Twitter'da paylaş\" href=\"http://twitter.com/share?text=%E2%96%BA+%40Turk_Telekom+TTNET%27in+Sorumsuz+Tutumu%21+via+%40SikayetvarCom&url=https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\" onclick=\"setEventTrigger('Share Buttons (Desktop)','Share','twitter');\">\n" +
                "<i class=\"block icon-twitter\"></i>\n" +
                "</a>\n" +
                "<a class=\"linkedin share-tips-top\" href=\"https://www.linkedin.com/shareArticle?mini=true&url=https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu&source=sikayetvar.com\" target=\"_blank\" title=\"Linkedin'de Paylaş\" onclick=\"setEventTrigger('Share Buttons (Desktop)','Share','linkedin');\">\n" +
                "<i class=\"block icon-linkedin\"></i>\n" +
                "</a>\n" +
                "<a class=\"whatsapp share-tips-top\" title=\"Whatsapp'ta Paylaş\" target=\"_blank\" href=\"https://web.whatsapp.com/send?text=Türk Telekom TTNET'in Sorumsuz Tutumu!%20https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\" onclick=\"setEventTrigger('Share Buttons (Desktop)','Share','whatsapp');\">\n" +
                "<i class=\"block icon-whatsapp\"></i>\n" +
                "</a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"right\">\n" +
                "<h1 class=\"title\">\n" +
                "                                                                        Türk Telekom TTNET'in Sorumsuz Tutumu!\n" +
                "</h1>\n" +
                "<div class=\"description\">\n" +
                "<p>Türk Telekom TTNET'in 6 yıllık, faturasını zamanında ödeyen, taahhütlere sadık müşterisiyim. 75 GB AKN kotanın hızı düşürülmüş şekilde 200 GB'lik bir kullanıma ulaşıldığı olmuştur. Bugüne kadar memnuniyetsizliğim olmasına rağmen herhangi bir şekilde bu memnuniyetsizliği gündeme getirmedim. Ta ki, bu sene Taahhüt süresinin bitimine yakın, 27 Mart'ta Bayrampaşa'da ki müdürlüğe giderek tarifemin iyileştirilip bana uygun tarifeli bir teklifle gelinmesini beklerken bu konuda her hangi bir ikna çabasına girilmediğinden internet bağlantımın iptaline ilişkin talimatı imzaladım. </p><br /><p>28 Mart'ta genel müdürlükten aranıp internet iptalinin gerekçesi sorulup bana uygun bir teklifin (500 GB AKN, 8 Mbps sabit hızlı, 91 TL) iletilmesi akabinde internet bağlantım dondurulmak suretiyle dilersem 90 gün içinde şahsıma sunulan bu tekliften faydalanabileceğim belirtildi. 29 Mart'ta ise bana sunulan teklifi kabul edip internet bağlantımın tekrar aktif edilmesi için genel müdürlük personeliyle görüşmem neticesinde tarafıma bu tür bir teklifin sunulmadığı iletilmiştir. Bana yapılan bu ayıbın ardından geniş ailemde, çevremde ne kadar Türk Telekom abonesi varsa, Türk Telekom'un bana göstermiş olduğu bu tutumu anlatacak takdiri de onlara bırakacağım!</p>\n" +
                "</div>\n" +
                "<div class=\"hashtags\">\n" +
                "<ul>\n" +
                "<li><a title=\"Türk Telekom Abone\" href=\"/turk-telekom/abone\" >abone</a></li>\n" +
                "<li><a title=\"Türk Telekom Akn\" href=\"/turk-telekom/akn\" >akn</a></li>\n" +
                "<li><a title=\"Türk Telekom Fatura\" href=\"/turk-telekom/fatura\" >fatura</a></li>\n" +
                "<li><a title=\"Türk Telekom İnternet\" href=\"/turk-telekom/internet\" >internet</a></li>\n" +
                "<li><a title=\"Türk Telekom İnternet Bağlantisi\" href=\"/turk-telekom/internet-baglantisi\" >internet bağlantısı</a></li>\n" +
                "<li><a title=\"Türk Telekom Müdür\" href=\"/turk-telekom/mudur\" >müdür</a></li>\n" +
                "<li><a title=\"Türk Telekom Taahhüt Süresi\" href=\"/turk-telekom/taahhut-suresi\" >taahhüt süresi</a></li>\n" +
                "<li><a title=\"Türk Telekom Ttnet\" href=\"/turk-telekom/ttnet\" >ttnet</a></li>\n" +
                "</ul>\n" +
                "</div>\n" +
                "<div class=\"info-block\">\n" +
                "<span class=\"user\" data-memberid=\"1647343\">\n" +
                "<img src=\"https://cdn.sikayetvar.com/member_picture/no-avatar_50x50.jpg\" width=\"36\" height=\"36\">\n" +
                "<span>İbrahim</span>\n" +
                "</span>\n" +
                "<span class=\"view-count-detail\"><i class=\"icon-eye\"></i> <b>721</b> <span class=\"text\"> Okunma</span></span>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\" id=\"referanceElement\"></div>\n" +
                "<div class=\"previewAdsArea\">\n" +
                "<script>\n" +
                "                      googletag.cmd.push(function() {\n" +
                "                        googletag.defineSlot('/50947975/Desktop_Detail_Bottom', [728, 90], 'div-gpt-ad-1521019067332-0').addService(googletag.pubads())\n" +
                "                        .setTargeting(\"companyID\", company.id)\n" +
                "                        .setTargeting(\"tags\", selectedTags); \n" +
                "                                                       \n" +
                "                        googletag.pubads().enableSingleRequest();\n" +
                "                        googletag.pubads().collapseEmptyDivs();\n" +
                "                        googletag.pubads().disableInitialLoad();         Criteo.events.push(function() {              Criteo.RequestBidsOnGoogleTagSlots(8314, function() {                 Criteo.SetDFPKeyValueTargeting();                  googletag.pubads().refresh();              }, 2000);          });         googletag.enableServices();\n" +
                "                      });\n" +
                "</script>\n" +
                "<div id='div-gpt-ad-1521019067332-0' style='height:90px; width:728px;'>\n" +
                "<script>\n" +
                "                    googletag.cmd.push(function() { googletag.display('div-gpt-ad-1521019067332-0'); });\n" +
                "</script>\n" +
                "</div>                \n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"detailVerticalAdsContainer\">\n" +
                "<div class=\"complaint-detail-vertical-ads\" id=\"complaintDetailVerticalAds\"></div>\n" +
                "<script>\n" +
                "                  googletag.cmd.push(function() {\n" +
                "                    googletag.defineSlot('/50947975/Desktop_Detail_Left_Sticky', [160, 600], 'complaintDetailVerticalAds').addService(googletag.pubads())\n" +
                "                    .setTargeting(\"companyID\", company.id)\n" +
                "                    .setTargeting(\"tags\", selectedTags);  \n" +
                "                    googletag.pubads().enableSingleRequest();\n" +
                "                    googletag.pubads().collapseEmptyDivs();\n" +
                "                    googletag.pubads().disableInitialLoad();         Criteo.events.push(function() {              Criteo.RequestBidsOnGoogleTagSlots(8314, function() {                 Criteo.SetDFPKeyValueTargeting();                  googletag.pubads().refresh();              }, 2000);          });         googletag.enableServices();\n" +
                "                  });\n" +
                "</script> \n" +
                "<div class=\"complaint-detail-vertical-ads\" id=\"complaintDetailVerticalAds\">\n" +
                "<script>\n" +
                "                    googletag.cmd.push(function() { googletag.display('complaintDetailVerticalAds'); });\n" +
                "</script>                \n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</section>\n" +
                "<div id=\"complaint-list\" class=\"similarComplaints\">\n" +
                "<h2 class=\"boxTitle\">Benzer Şikayetler</h2>\n" +
                "<div class=\"row\">\n" +
                "<div class=\"owl-carousel\" id=\"similar-complaints-carousel\">\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"10818597\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-kampanya-aldatmacasi-1\" class=\"complaint-link-for-ads\" title=\"Türk Telekom Kampanya Aldatmacası!\" data-id=\"10818597\">Türk Telekom Kampanya Aldatmacası!</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    12.07.2018 tarihinde Telekom tarafından aranmış ve arayan kişi aboneliğinizin yenilenme tarihi yaklaşmakta 109 TL internet + Tivibu kullanıyorsunuz 89 TL yapalım 200 GB AKN olsun aboneliğinizi 2 yıl daha uzatal&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"27 Ağustos 2018 16:24\"><i class=\"icon-clock\"></i> 27 Ağustos </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"5419888\"><img alt=\"Emre\" src=\"https://cdn.sikayetvar.com/member_picture/3d/ed/3ded45dcad084a5263d7de81bde8f6d2_50x50.jpg?1535114327\" width=\"36\" height=\"36\">Emre</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 1.043 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom Kampanya Aldatmacası!\" data-url=\"/turk-telekom/turk-telekom-kampanya-aldatmacasi-1\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+Kampanya+Aldatmacas%C4%B1%21+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"10123725\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-hatali-yapilan-abonelik-islemleri\" class=\"complaint-link-for-ads\" title=\"Türk Telekom Hatalı Yapılan Abonelik İşlemleri\" data-id=\"10123725\">Türk Telekom Hatalı Yapılan Abonelik&hellip;</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    1.02.2018 günü yalın internet (24 Mbps-50GB) olarak Türk Telekom'a üyeliğimi yaptırdım. 12.2.2018 tarihinde 444 0 375'ten arandım ve gelen kampanya önerisi doğrultusunda, ödemekte olduğum faturaya taahhütsüz ol&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"13 Şubat 2018 13:16\"><i class=\"icon-clock\"></i> 13 Şubat </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"2035068\"><img alt=\"Bengi\" src=\"https://cdn.sikayetvar.com/member_picture/no-avatar_50x50.jpg\" width=\"36\" height=\"36\">Bengi</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 722 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom Hatalı Yapılan Abonelik İşlemleri\" data-url=\"/turk-telekom/turk-telekom-hatali-yapilan-abonelik-islemleri\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+Hatal%C4%B1+Yap%C4%B1lan+Abonelik+%C4%B0%C5%9Flemleri+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"10813613\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-hiz-akn-problemi\" class=\"complaint-link-for-ads\" title=\"Türk Telekom Hız / Akn Problemi\" data-id=\"10813613\">Türk Telekom Hız / Akn Problemi</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    Yalın ultranet limitsiz 50 GB paketi kullandığım halde ve kota aşımı gerçekleşme&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<div class=\"card-media-box\">\n" +
                "<a href=\"https://cdn.sikayetvar.com/complaint/1081/10813613/akn-1534859175.jpg\" rel=\"nofollow gallery10813613\" class=\"fancybox \" >\n" +
                "<img src=\"https://cdn.sikayetvar.com/complaint/1081/10813613/akn-1534859175_50x50.jpg\" width=\"72\" height=\"72\" alt=\"Şikayet resmi\">\n" +
                "<span><i class=\"icon-search\"></i></span>\n" +
                "</a>\n" +
                "<a href=\"https://cdn.sikayetvar.com/complaint/1081/10813613/online-islemler-1534859176.jpg\" rel=\"nofollow gallery10813613\" class=\"fancybox \" >\n" +
                "<img src=\"https://cdn.sikayetvar.com/complaint/1081/10813613/online-islemler-1534859176_50x50.jpg\" width=\"72\" height=\"72\" alt=\"Şikayet resmi\">\n" +
                "<span><i class=\"icon-search\"></i></span>\n" +
                "</a>\n" +
                "<a href=\"https://cdn.sikayetvar.com/complaint/1081/10813613/paketbilgisi-1534859177.jpg\" rel=\"nofollow gallery10813613\" class=\"fancybox \" >\n" +
                "<img src=\"https://cdn.sikayetvar.com/complaint/1081/10813613/paketbilgisi-1534859177_50x50.jpg\" width=\"72\" height=\"72\" alt=\"Şikayet resmi\">\n" +
                "<span><i class=\"icon-search\"></i></span>\n" +
                "</a>\n" +
                "<a href=\"https://cdn.sikayetvar.com/complaint/1081/10813613/ttnet32mbps-1534859177.jpg\" rel=\"nofollow gallery10813613\" class=\"fancybox \" >\n" +
                "<img src=\"https://cdn.sikayetvar.com/complaint/1081/10813613/ttnet32mbps-1534859177_50x50.jpg\" width=\"72\" height=\"72\" alt=\"Şikayet resmi\">\n" +
                "<span><i class=\"icon-search\"></i></span>\n" +
                "</a>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"25 Ağustos 2018 19:55\"><i class=\"icon-clock\"></i> 25 Ağustos </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"727853\"><img alt=\"Hamza\" src=\"https://cdn.sikayetvar.com/member_picture/no-avatar_50x50.jpg\" width=\"36\" height=\"36\">Hamza</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 920 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom Hız / Akn Problemi\" data-url=\"/turk-telekom/turk-telekom-hiz-akn-problemi\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+H%C4%B1z+%2F+Akn+Problemi+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"9714500\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-internetimde-kopma-ve-hiz-sorunlari\" class=\"complaint-link-for-ads\" title=\"Türk Telekom İnternetimde Kopma ve Hız Sorunları!\" data-id=\"9714500\">Türk Telekom İnternetimde Kopma ve H&hellip;</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    Yaklaşık 1 seneden beri kullanmakta olduğum (veya kullanamamakta olduğum denileb&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<div class=\"card-media-box\">\n" +
                "<a href=\"https://cdn.sikayetvar.com/complaint/9714/9714500/ekran-goruntusu-2-1508622050.jpg\" rel=\"nofollow gallery9714500\" class=\"fancybox \" >\n" +
                "<img src=\"https://cdn.sikayetvar.com/complaint/9714/9714500/ekran-goruntusu-2-1508622050_50x50.jpg\" width=\"72\" height=\"72\" alt=\"Şikayet resmi\">\n" +
                "<span><i class=\"icon-search\"></i></span>\n" +
                "</a>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"23 Ekim 2017 08:40\"><i class=\"icon-clock\"></i> 23 Ekim 2017 </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"3765449\"><img alt=\"Bahadır\" src=\"https://cdn.sikayetvar.com/member_picture/no-avatar_50x50.jpg\" width=\"36\" height=\"36\">Bahadır</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 169 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom İnternetimde Kopma ve Hız Sorunları!\" data-url=\"/turk-telekom/turk-telekom-internetimde-kopma-ve-hiz-sorunlari\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+%C4%B0nternetimde+Kopma+ve+H%C4%B1z+Sorunlar%C4%B1%21+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"10680703\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-ttnet-akn-asim-sorunu\" class=\"complaint-link-for-ads\" title=\"Türk Telekom TTNET AKN Aşım Sorunu!\" data-id=\"10680703\">Türk Telekom TTNET AKN Aşım Sorunu!</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    Taahhüdü biten aboneliğimi tekrar taahhüt vererek yeniledim yeni bir pakete geçtim ama hızı düşmüş olarak, çağrı merkezini aradığımda eski pakette kota aşımı yapmışım hızı yavaşlatmışlar. Eyvallah bir şey demiy&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"20 Temmuz 2018 08:28\"><i class=\"icon-clock\"></i> 20 Temmuz </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"5353627\"><img alt=\"Muhammed\" src=\"https://cdn.sikayetvar.com/member_picture/no-avatar_50x50.jpg\" width=\"36\" height=\"36\">Muhammed</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 347 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom TTNET AKN Aşım Sorunu!\" data-url=\"/turk-telekom/turk-telekom-ttnet-akn-asim-sorunu\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+TTNET+AKN+A%C5%9F%C4%B1m+Sorunu%21+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"small-6 grid-item w-100\" data-id=\"10327159\">\n" +
                "<div class=\"complaint-card box-shadow-20\">\n" +
                "<div class=\"media\">\n" +
                "<div class=\"media-left\">\n" +
                "<div class=\"kart-50\">\n" +
                "<div class=\"img-wrapper\">\n" +
                "<a href=\"/turk-telekom\" title=\"Türk Telekom\"><img alt=\"Türk Telekom\" src=\"https://cdn.sikayetvar.com/company_logo/10/10.jpg?1522650125\" width=\"90\" height=\"50\"></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"media-body\">\n" +
                "<h2><a href=\"/turk-telekom/turk-telekom-fatura-haksizligi-22\" class=\"complaint-link-for-ads\" title=\"Türk Telekom Fatura Haksızlığı!\" data-id=\"10327159\">Türk Telekom Fatura Haksızlığı!</a></h2>\n" +
                "</div>\n" +
                "<p class=\"complaint-summary\">\n" +
                "                                    Part 1. TTNET’e o kadar sinir oldum ki destanımı partlara döküyorum. 1) Uzun süredir iş yoğunluğumdan dolayı taahhüdüm geçmişti, geçen bir fark ettim ki maşallah 2 katı 3 katı göndermişler. İnsaf yani, bütün di&hellip;\n" +
                "</p>\n" +
                "</div>\n" +
                "<span class=\"tips-top card-time-box\" title=\"12 Nisan 2018 11:02\"><i class=\"icon-clock\"></i> 12 Nisan </span>\n" +
                "<div class=\"item-footer\">\n" +
                "<span class=\"name\" data-memberid=\"5203004\"><img alt=\"Murat\" src=\"https://cdn.sikayetvar.com/member_picture/d7/b7/d7b7c2edb6064fa2393e5df3381acdac_50x50.jpg?1523423908\" width=\"36\" height=\"36\">Murat</span>\n" +
                "<span class=\"view-count\"><i class=\"icon-eye\"></i> 283 <span class=\"text\"> Okunma</span></span>\n" +
                "<div class=\"complaint-card-sharebox\">\n" +
                "<div class=\"share-icon\"> <i class=\"icon-share\"></i>\n" +
                "<div class=\"sharebox\" data-title=\"Türk Telekom Fatura Haksızlığı!\" data-url=\"/turk-telekom/turk-telekom-fatura-haksizligi-22\" data-twittext=\"%E2%96%BA+%40Turk_Telekom+Fatura+Haks%C4%B1zl%C4%B1%C4%9F%C4%B1%21+via+%40SikayetvarCom\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"clearfix\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</main>\n" +
                "<script type=\"text/javascript\">\n" +
                "    var complaintID = 8153428;\n" +
                "    var memberID = 1647343;\n" +
                "    var companyUrlForShare = '/turk-telekom';\n" +
                "    var complaintHashtags = [{\"id\":337498,\"hashtag\":\"abone\",\"score\":175836000,\"url\":\"\\/turk-telekom\\/abone\",\"link\":\"abone\",\"title\":\"Abone\"},{\"id\":341861,\"hashtag\":\"akn\",\"score\":11747900,\"url\":\"\\/turk-telekom\\/akn\",\"link\":\"akn\",\"title\":\"Akn\"},{\"id\":427709,\"hashtag\":\"fatura\",\"score\":355935000,\"url\":\"\\/turk-telekom\\/fatura\",\"link\":\"fatura\",\"title\":\"Fatura\"},{\"id\":467481,\"hashtag\":\"internet\",\"score\":289768000,\"url\":\"\\/turk-telekom\\/internet\",\"link\":\"internet\",\"title\":\"\\u0130nternet\"},{\"id\":467484,\"hashtag\":\"internet ba\\u011flant\\u0131s\\u0131\",\"score\":103875000,\"url\":\"\\/turk-telekom\\/internet-baglantisi\",\"link\":\"internet-baglantisi\",\"title\":\"\\u0130nternet Ba\\u011flantisi\"},{\"id\":525473,\"hashtag\":\"m\\u00fcd\\u00fcr\",\"score\":27304800,\"url\":\"\\/turk-telekom\\/mudur\",\"link\":\"mudur\",\"title\":\"M\\u00fcd\\u00fcr\"},{\"id\":712099,\"hashtag\":\"taahh\\u00fct s\\u00fcresi\",\"score\":false,\"url\":\"\\/turk-telekom\\/taahhut-suresi\",\"link\":\"taahhut-suresi\",\"title\":\"Taahh\\u00fct S\\u00fcresi\"},{\"id\":606607,\"hashtag\":\"ttnet\",\"score\":851743000,\"url\":\"\\/turk-telekom\\/ttnet\",\"link\":\"ttnet\",\"title\":\"Ttnet\"}];\n" +
                "    var company = {\"id\":\"10\",\"name\":\"T\\u00fcrk Telekom\",\"url\":\"\\/turk-telekom\",\"logo\":\"https:\\/\\/cdn.sikayetvar.com\\/company_logo\\/10\\/10.jpg?1522650125\",\"twitter\":\"Turk_Telekom\",\"fixedCategory\":[\"455\",\"688\",\"593\",\"736\",\"222\"],\"membershipType\":\"\\u00dcye De\\u011fil\"};\n" +
                "    var category = {\"name\":\"Servis Sa\\u011flay\\u0131c\\u0131lar\",\"name_en\":\"Service Providers\",\"parent_name\":\"\\u0130nternet\",\"parent_name_en\":\"Internet\"};\n" +
                "    \n" +
                "    \n" +
                "    gtag('event', 'Viewed Company', {'Viewed Company ID': '10'});\n" +
                "       \n" +
                "    \n" +
                "    var selectedTags = [];\n" +
                "           var selectedTags = [\"abone\",\"akn\",\"fatura\",\"internet\",\"internet-baglantisi\",\"mudur\",\"taahhut-suresi\",\"ttnet\"]; \n" +
                "        console.log({\"id\":\"8153428\",\"url\":\"\\/turk-telekom\\/turk-telekom-ttnetin-sorumsuz-tutumu\",\"view_count_formatted\":\"721\",\"view_count\":721,\"category_id\":\"222\",\"company_id\":\"10\",\"member_id\":\"1647343\",\"answer_status\":false,\"title\":\"T\\u00fcrk Telekom TTNET'in Sorumsuz Tutumu!\",\"content\":\"T\\u00fcrk Telekom TTNET'in 6 y\\u0131ll\\u0131k, faturas\\u0131n\\u0131 zaman\\u0131nda \\u00f6deyen, taahh\\u00fctlere sad\\u0131k m\\u00fc\\u015fterisiyim. 75 GB AKN kotan\\u0131n h\\u0131z\\u0131 d\\u00fc\\u015f\\u00fcr\\u00fclm\\u00fc\\u015f \\u015fekilde 200 GB'lik bir kullan\\u0131ma ula\\u015f\\u0131ld\\u0131\\u011f\\u0131 olmu\\u015ftur. Bug\\u00fcne kadar memnuniyetsizli\\u011fim olmas\\u0131na ra\\u011fmen herhangi bir \\u015fekilde bu memnuniyetsizli\\u011fi g\\u00fcndeme getirmedim. Ta ki, bu sene Taahh\\u00fct s\\u00fcresinin bitimine yak\\u0131n, 27 Mart'ta Bayrampa\\u015fa'da ki m\\u00fcd\\u00fcrl\\u00fc\\u011fe giderek tarifemin iyile\\u015ftirilip bana uygun tarifeli bir teklifle gelinmesini beklerken bu konuda her hangi bir ikna \\u00e7abas\\u0131na girilmedi\\u011finden internet ba\\u011flant\\u0131m\\u0131n iptaline ili\\u015fkin talimat\\u0131 imzalad\\u0131m. \\n\\n28 Mart'ta genel m\\u00fcd\\u00fcrl\\u00fckten aran\\u0131p internet iptalinin gerek\\u00e7esi sorulup bana uygun bir teklifin (500 GB AKN, 8 Mbps sabit h\\u0131zl\\u0131, 91 TL) iletilmesi akabinde internet ba\\u011flant\\u0131m dondurulmak suretiyle dilersem 90 g\\u00fcn i\\u00e7inde \\u015fahs\\u0131ma sunulan bu tekliften faydalanabilece\\u011fim belirtildi. 29 Mart'ta ise bana sunulan teklifi kabul edip internet ba\\u011flant\\u0131m\\u0131n tekrar aktif edilmesi i\\u00e7in genel m\\u00fcd\\u00fcrl\\u00fck personeliyle g\\u00f6r\\u00fc\\u015fmem neticesinde taraf\\u0131ma bu t\\u00fcr bir teklifin sunulmad\\u0131\\u011f\\u0131 iletilmi\\u015ftir. Bana yap\\u0131lan bu ay\\u0131b\\u0131n ard\\u0131ndan geni\\u015f ailemde, \\u00e7evremde ne kadar T\\u00fcrk Telekom abonesi varsa, T\\u00fcrk Telekom'un bana g\\u00f6stermi\\u015f oldu\\u011fu bu tutumu anlatacak takdiri de onlara b\\u0131rakaca\\u011f\\u0131m!\",\"delete_status\":\"\",\"view_time\":\"2017-03-30 10:16:08\",\"view_time_formatted\":\"30 Mart 2017 \",\"view_time_formatted_full\":\"30 Mart 2017 10:16\",\"write_time\":\"2017-03-29 21:51:58\",\"write_time_formatted\":\"29 Mart 2017 21:51\",\"process_time\":\"2018-04-09 18:49:33\",\"process_time_formatted\":\"09 Nisan \",\"process_time_formatted_full\":\"09 Nisan 2018 18:49\",\"publish_status\":\"Yay\\u0131nda\",\"publish_status_id\":5,\"noindex\":\"\",\"poll_solution_point\":\"\",\"member\":{\"id\":\"1647343\",\"name\":\"\\u0130brahim\",\"picture\":{\"original\":\"https:\\/\\/cdn.sikayetvar.com\\/member_picture\\/no-avatar.jpg\",\"50x50\":\"https:\\/\\/cdn.sikayetvar.com\\/member_picture\\/no-avatar_50x50.jpg\",\"90x90\":\"https:\\/\\/cdn.sikayetvar.com\\/member_picture\\/no-avatar_90x90.jpg\",\"130x130\":\"https:\\/\\/cdn.sikayetvar.com\\/member_picture\\/no-avatar_130x130.jpg\",\"150x150\":\"https:\\/\\/cdn.sikayetvar.com\\/member_picture\\/no-avatar_150x150.jpg\"},\"prefix_name\":\"\\u0130brahim'in\"},\"summary\":\"T\\u00fcrk Telekom TTNET'in 6 y\\u0131ll\\u0131k, faturas\\u0131n\\u0131 zaman\\u0131nda \\u00f6deyen, taahh\\u00fctlere sad\\u0131k m\\u00fc\\u015fterisiyim. 75 GB AKN kotan\\u0131n h\\u0131z\\u0131 d\\u00fc\\u015f\\u00fcr\\u00fclm\\u00fc\\u015f \\u015fekilde 200 GB'lik bir kullan\\u0131ma ula\\u015f\\u0131ld\\u0131\\u011f\\u0131 olmu\\u015ftur. Bug\\u00fcne kadar memnuniyetsizli\\u011fim olmas\\u0131na ra\\u011fmen herhangi bir \\u015fekilde bu memnuniyetsizli\\u011fi g\\u00fcndeme getirmedim. Ta ki, bu...\",\"category\":{\"name\":\"Servis Sa\\u011flay\\u0131c\\u0131lar\",\"name_en\":\"Service Providers\",\"parent_name\":\"\\u0130nternet\",\"parent_name_en\":\"Internet\"},\"company\":{\"id\":\"10\",\"name\":\"T\\u00fcrk Telekom\",\"url\":\"\\/turk-telekom\",\"logo\":\"https:\\/\\/cdn.sikayetvar.com\\/company_logo\\/10\\/10.jpg?1522650125\",\"twitter\":\"Turk_Telekom\",\"fixedCategory\":[\"455\",\"688\",\"593\",\"736\",\"222\"],\"membershipType\":\"\\u00dcye De\\u011fil\"},\"hashtags\":[{\"id\":337498,\"hashtag\":\"abone\",\"score\":175836000,\"url\":\"\\/turk-telekom\\/abone\",\"link\":\"abone\",\"title\":\"Abone\"},{\"id\":341861,\"hashtag\":\"akn\",\"score\":11747900,\"url\":\"\\/turk-telekom\\/akn\",\"link\":\"akn\",\"title\":\"Akn\"},{\"id\":427709,\"hashtag\":\"fatura\",\"score\":355935000,\"url\":\"\\/turk-telekom\\/fatura\",\"link\":\"fatura\",\"title\":\"Fatura\"},{\"id\":467481,\"hashtag\":\"internet\",\"score\":289768000,\"url\":\"\\/turk-telekom\\/internet\",\"link\":\"internet\",\"title\":\"\\u0130nternet\"},{\"id\":467484,\"hashtag\":\"internet ba\\u011flant\\u0131s\\u0131\",\"score\":103875000,\"url\":\"\\/turk-telekom\\/internet-baglantisi\",\"link\":\"internet-baglantisi\",\"title\":\"\\u0130nternet Ba\\u011flantisi\"},{\"id\":525473,\"hashtag\":\"m\\u00fcd\\u00fcr\",\"score\":27304800,\"url\":\"\\/turk-telekom\\/mudur\",\"link\":\"mudur\",\"title\":\"M\\u00fcd\\u00fcr\"},{\"id\":712099,\"hashtag\":\"taahh\\u00fct s\\u00fcresi\",\"score\":false,\"url\":\"\\/turk-telekom\\/taahhut-suresi\",\"link\":\"taahhut-suresi\",\"title\":\"Taahh\\u00fct S\\u00fcresi\"},{\"id\":606607,\"hashtag\":\"ttnet\",\"score\":851743000,\"url\":\"\\/turk-telekom\\/ttnet\",\"link\":\"ttnet\",\"title\":\"Ttnet\"}],\"breadcrumbHashtags\":[{\"id\":606607,\"hashtag\":\"Ttnet\",\"score\":851743000,\"url\":\"\\/turk-telekom\\/ttnet\",\"link\":\"ttnet\",\"title\":\"Ttnet\"},{\"id\":427709,\"hashtag\":\"Fatura\",\"score\":355935000,\"url\":\"\\/turk-telekom\\/ttnet\\/fatura\",\"link\":\"fatura\",\"title\":\"Fatura\"},{\"id\":467481,\"hashtag\":\"\\u0130nternet\",\"score\":289768000,\"url\":\"\\/turk-telekom\\/ttnet\\/fatura\\/internet\",\"link\":\"internet\",\"title\":\"\\u0130nternet\"}],\"twitText\":\"\\u25ba @Turk_Telekom TTNET'in Sorumsuz Tutumu! via @SikayetvarCom\",\"show\":true,\"owner\":false,\"answer_form\":false,\"delete_form\":false,\"poll_form\":false,\"headerTags\":{\"title\":\"T\\u00fcrk Telekom TTNET'in Sorumsuz Tutumu!\",\"metaTags\":{\"description\":\"T\\u00fcrk Telekom i\\u00e7in yaz\\u0131lan 'T\\u00fcrk Telekom TTNET'in Sorumsuz Tutumu!' \\u015fikayetini ve yorumlar\\u0131n\\u0131 okumak ya da T\\u00fcrk Telekom hakk\\u0131nda \\u015fikayet yazmak i\\u00e7in t\\u0131klay\\u0131n!\"}}});\n" +
                "</script>\n" +
                "<script type=\"application/ld+json\">\n" +
                "{\n" +
                "\t\"@context\": \"http://schema.org\",\n" +
                "\t\"@type\": \"WebPage\",\n" +
                "\t\"inLanguage\": \"tr-TR\",\n" +
                "\t\"isFamilyFriendly \": \"true\",\n" +
                "    \"url\":\"https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\",\n" +
                "\t\"significantLink\": \"https://www.sikayetvar.com/turk-telekom\",\n" +
                "    \"description\": \"Türk Telekom için yazılan 'Türk Telekom TTNET'in Sorumsuz Tutumu!' şikayetini ve yorumlarını okumak ya da Türk Telekom hakkında şikayet yazmak için tıklayın!\",\n" +
                "    \t\"interactionStatistic\": {\n" +
                "\t\t\"@type\": \"InteractionCounter\",\n" +
                "\t\t\"interactionType\": \"https://schema.org/UserPageVisits\",\n" +
                "\t\t\"userInteractionCount\": \"721\"\n" +
                "\t},\n" +
                "\t\"breadcrumb\": {\n" +
                "\t\t\"@type\": \"BreadcrumbList\",\n" +
                "\t\t\"numberOfItems\": 3,\n" +
                "\t\t\"itemListElement\": [\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"@type\": \"ListItem\",\n" +
                "\t\t\t\t\"position\": 1,\n" +
                "\t\t\t\t\"item\": {\n" +
                "\t\t\t\t\t\"@id\": \"https://www.sikayetvar.com\",\n" +
                "\t\t\t\t\t\"name\": \"Ana Sayfa\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"@type\": \"ListItem\",\n" +
                "\t\t\t\t\"position\": 2,\n" +
                "\t\t\t\t\"item\": {\n" +
                "\t\t\t\t\t\"@id\": \"https://www.sikayetvar.com/turk-telekom\",\n" +
                "\t\t\t\t\t\"name\": \"Türk Telekom\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"@type\": \"ListItem\",\n" +
                "\t\t\t\t\"position\": 3,\n" +
                "\t\t\t\t\"item\": {\n" +
                "\t\t\t\t\t\"@id\": \"https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\",\n" +
                "\t\t\t\t\t\"name\": \"Türk Telekom TTNET'in Sorumsuz Tutumu!\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"mainEntity\": {\n" +
                "\t\t\"@type\": \"Article\",\n" +
                "\t\t\"mainEntityOfPage\": \"https://www.sikayetvar.com/turk-telekom/turk-telekom-ttnetin-sorumsuz-tutumu\",\n" +
                "\t\t\"image\": \"https://cdn-desktop.sikayetvar.com/images/sikayetvar-200x50.png\",\n" +
                "\t\t\"headline\": \"Türk Telekom TTNET'in Sorumsuz Tutumu!\",\n" +
                "\t\t\"alternativeHeadline\": \"Türk Telekom TTNET'in Sorumsuz Tutumu! - Şikayetvar\",\n" +
                "\t\t\"datePublished\": \"2017-03-30\",\n" +
                "\t\t\"dateCreated\": \"2017-03-29\",\n" +
                "\t\t\"dateModified\": \"2018-04-09\",\n" +
                "\t\t\"author\": {\n" +
                "                \"@type\": \"Person\",\n" +
                "                \"name\": \"İbrahim\"\n" +
                "        },\n" +
                "\t\t\"publisher\": {\n" +
                "\t\t\t\"@type\": \"Organization\",\n" +
                "\t\t\t\"name\": \"Şikayetvar\",\n" +
                "\t\t\t\"logo\": {\n" +
                "\t\t\t\t\"@type\": \"ImageObject\",\n" +
                "\t\t\t\t\"url\": \"https://cdn-desktop.sikayetvar.com/images/sikayetvar-200x50.png\",\n" +
                "\t\t\t\t\"width\": 200,\n" +
                "\t\t\t\t\"height\": 50\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t\"articleBody\": \"Türk Telekom TTNET'in 6 yıllık, faturasını zamanında ödeyen, taahhütlere sadık müşterisiyim. 75 GB AKN kotanın hızı düşürülmüş şekilde 200 GB'lik bir kullanıma ulaşıldığı olmuştur. Bugüne kadar memnuniyetsizliğim olmasına rağmen herhangi bir şekilde bu memnuniyetsizliği gündeme getirmedim. Ta ki, bu sene Taahhüt süresinin bitimine yakın, 27 Mart'ta Bayrampaşa'da ki müdürlüğe giderek tarifemin iyileştirilip bana uygun tarifeli bir teklifle gelinmesini beklerken bu konuda her hangi bir ikna çabasına girilmediğinden internet bağlantımın iptaline ilişkin talimatı imzaladım. \n" +
                "\n" +
                "28 Mart'ta genel müdürlükten aranıp internet iptalinin gerekçesi sorulup bana uygun bir teklifin (500 GB AKN, 8 Mbps sabit hızlı, 91 TL) iletilmesi akabinde internet bağlantım dondurulmak suretiyle dilersem 90 gün içinde şahsıma sunulan bu tekliften faydalanabileceğim belirtildi. 29 Mart'ta ise bana sunulan teklifi kabul edip internet bağlantımın tekrar aktif edilmesi için genel müdürlük personeliyle görüşmem neticesinde tarafıma bu tür bir teklifin sunulmadığı iletilmiştir. Bana yapılan bu ayıbın ardından geniş ailemde, çevremde ne kadar Türk Telekom abonesi varsa, Türk Telekom'un bana göstermiş olduğu bu tutumu anlatacak takdiri de onlara bırakacağım!\",\n" +
                "\t\t\"interactionStatistic\": {\n" +
                "\t\t\t\"@type\": \"InteractionCounter\",\n" +
                "\t\t\t\"interactionType\": \"http://schema.org/CommentAction\",\n" +
                "\t\t\t\"userInteractionCount\": \"0\"\n" +
                "\t\t}\n" +
                "\t\t\t}\n" +
                "}\n" +
                "</script><footer>\n" +
                "<section class=\"first-footer\">\n" +
                "<div class=\"row align-justify\">\n" +
                "<div class=\"small-4 nopl columns text-center\">\n" +
                "<span class=\"title\">Markalar</span>\n" +
                "<a href=\"/netspeed\" title=\"Netspeed şikayetleri\">Netspeed, </a>\n" +
                "<a href=\"/millenicom\" title=\"Millenicom şikayetleri\">Millenicom, </a>\n" +
                "<a href=\"/d-smart-net\" title=\"D-Smart Net şikayetleri\">D-Smart Net, </a>\n" +
                "<a href=\"/kablo-net\" title=\"Kablo Net şikayetleri\">Kablo Net, </a>\n" +
                "<a href=\"/goknet\" title=\"Göknet şikayetleri\">Göknet, </a>\n" +
                "<a href=\"/superonline\" title=\"Superonline şikayetleri\">Superonline, </a>\n" +
                "<a href=\"/vodafone-net\" title=\"Vodafone Net şikayetleri\">Vodafone Net, </a>\n" +
                "<a href=\"/turknet\" title=\"TurkNet şikayetleri\">TurkNet, </a>\n" +
                "<a href=\"/poyrazwifi\" title=\"Poyrazwifi şikayetleri\">Poyrazwifi, </a>\n" +
                "<a href=\"/extranet\" title=\"Extranet şikayetleri\">Extranet, </a>\n" +
                "<a href=\"/final-okullari\" title=\"Final Okulları şikayetleri\">Final Okulları, </a>\n" +
                "<a href=\"/evidea\" title=\"Evidea şikayetleri\">Evidea, </a>\n" +
                "<a href=\"/sporcu-besinleri\" title=\"Sporcu Besinleri şikayetleri\">Sporcu Besinleri, </a>\n" +
                "<a href=\"/mng-kargo\" title=\"MNG Kargo şikayetleri\">MNG Kargo, </a>\n" +
                "<a href=\"/volkswagen\" title=\"Volkswagen şikayetleri\">Volkswagen, </a>\n" +
                "<a href=\"/sok-marketler\" title=\"Şok Marketler şikayetleri\">Şok Marketler, </a>\n" +
                "<a href=\"/bets10\" title=\"Bets10 şikayetleri\">Bets10</a>\n" +
                "</div>\n" +
                "<div class=\"small-4 columns text-center footer-complaints\">\n" +
                "<span class=\"title\">Şikayetler</span>\n" +
                "<a href=\"/turk-telekom/turk-telekom-ttnet-internet-hatti-25-gundur-arizali\" title=\"Türk Telekom TTNET İnternet Hattı 25 Gündür Arızalı!\">Türk Telekom TTNET İnternet Hattı 25 Gündür Arızalı!</a><br>\n" +
                "<a href=\"/jerybet10com/jerybet10com-odeme-yapmiyor-1\" title=\"Jerybet10.com  Ödeme Yapmıyor!\">Jerybet10.com  Ödeme Yapmıyor!</a><br>\n" +
                "<a href=\"/pegasus/pegasus-online-bilet-alirken-hata-ve-zam\" title=\"Pegasus Online Bilet Alırken Hata Ve Zam!\">Pegasus Online Bilet Alırken Hata Ve Zam!</a><br>\n" +
                "<a href=\"/sikayetler\" title=\"Tüm Şikayetler\">Tüm Şikayetler</a>\n" +
                "</div>\n" +
                "<div class=\"small-4 columns text-center nopr\">\n" +
                "<span class=\"title\">Çok Arananlar</span>\n" +
                "<a href=\"https://www.sikayetvar.com/turk-telekom\" title=\"Türk Telekom\">Türk Telekom, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/hepsiburada\" title=\"Hepsiburada\">Hepsiburada, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/btcturk\" title=\"Btcturk\">Btcturk, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/trendyol\" title=\"Trendyol\">Trendyol, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/herbalife\" title=\"Herbalife\">Herbalife, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/gittigidiyor\" title=\"gittigidiyor\">gittigidiyor, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/vodafone\" title=\"Vodafone\">Vodafone, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/n11\" title=\"N11\">N11, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/paribu\" title=\"Paribu\">Paribu, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/tatilbudur\" title=\"Tatilbudur\">Tatilbudur, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/ets-tur\" title=\"ETS Tur\">ETS Tur, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/getir\" title=\"Getir\">Getir, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/modanisa\" title=\"Modanisa\">Modanisa, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/lcw\" title=\"LCW\">LCW, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/adidas\" title=\"Adidas\">Adidas, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/media-markt\" title=\"Media Markt\">Media Markt, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/thy\" title=\"THY\">THY, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/opel\" title=\"Opel\">Opel, </a>\n" +
                "<a href=\"https://www.sikayetvar.com/pegasus\" title=\"Pegasus\">Pegasus</a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"footer-info text-center\">\n" +
                "                © 2018 Şikayetvar. Her hakkı saklıdır.\n" +
                "</div>\n" +
                "</section>\n" +
                "<section class=\"bottom-footer\" data-first=\"ok\">\n" +
                "<div class=\"row\">\n" +
                "<div class=\"small-5 columns\" style=\"width: 33%; padding-right: 0;\">\n" +
                "<span id=\"moreFooter\" class=\"more-footer share-tips-top\" title=\"Daha fazla göster\"><b>...</b></span>\n" +
                "<a href=\"/hakkimizda\" title=\"Hakkımızda\">Hakkımızda</a>\n" +
                "<a href=\"/sss\" title=\"Yardım\" >Yardım</a>\n" +
                "<div class=\"social\">\n" +
                "<a rel=\"nofollow\" href=\"https://facebook.com/sikayetvar\" class=\"fb\" target=\"_blank\"><i class=\"icon-facebook\"></i></a>\n" +
                "<a rel=\"nofollow\" href=\"https://twitter.com/sikayetvarcom\" class=\"tw\" target=\"_blank\"><i class=\"icon-twitter\"></i></a>\n" +
                "<a rel=\"nofollow\" href=\"https://www.linkedin.com/company/sikayetvar/\" class=\"linkedin\" target=\"_blank\"><i class=\"icon-linkedin\"></i></a>\n" +
                "<a rel=\"nofollow\" href=\"https://www.youtube.com/user/sikayetvarcom\" class=\"youtube\" target=\"_blank\"><i class=\"icon-youtube-play\"></i></a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<nav class=\"small-7 columns text-right\" style=\"width: 67%;\">\n" +
                "<a href=\"https://www.netsecop.com/trynetsecop.html\" target=\"_blank\" rel=\"nofollow\">\n" +
                "<img src=\"https://cdn-desktop.sikayetvar.com/images/netsecop.svg\" alt=\"netsecop badge\" width=\"100\" height=\"26\">\n" +
                "</a>\n" +
                "<a href=\"//www.dmca.com/Protection/Status.aspx?ID=fb3d0077-2042-4464-b65f-47d4e9191362\" title=\"DMCA.com Protection Status\" class=\"dmca-badge\" rel=\"nofollow\">\n" +
                "<img src=\"https://cdn-desktop.sikayetvar.com/images/dmca-badge-w100-5x1-05.png\" alt=\"DMCA.com Protection Status\">\n" +
                "</a>\n" +
                "<script src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/DMCABadgeHelper.min.js\"></script>\n" +
                "<a href=\"/kurumsal/urunler\" title=\"Neden Üye Olmalıyım?\">Markam için neden üye olmalıyım?</a>\n" +
                "<a rel=\"nofollow\" href=\"https://www.sikayetplus.com\" class=\"brand\" target=\"_blank\">Marka Girişi <i class=\"icon-right\"></i> </a>\n" +
                "</nav>\n" +
                "</div>\n" +
                "</section>\n" +
                "</footer>\n" +
                "<div id=\"notifyBox\">\n" +
                "<div class=\"modal-header\">\n" +
                "<span class=\"big-icon\"><i class=\"icon-ok\"></i></span>\n" +
                "<h2></h2>\n" +
                "<p></p>\n" +
                "</div>\n" +
                "<div class=\"modal-footer\">\n" +
                "<button class=\"btn white notifyBox_close\">Tamam</button>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"login-form-modal\">\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"signup-form-modal\" style=\"overflow-y:scroll;\">\n" +
                "</div>\n" +
                "<a style=\"display: none\" data-fullmodal=\"true\" data-overlay=\"social-media-form-modal\" data-ajax-url=\"social.media.form\" class=\"login-link\"></a>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"social-media-form-modal\" style=\"overflow-y:scroll;\">\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"forgot-form-modal\">\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"survey-form-modal\">\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"delete-complaint-modal\">\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"phone-phonechange-form-modal\">\n" +
                "<div class=\"row new-form-design\">\n" +
                "<div id=\"phonechange-form\" class=\"small-12 new-form-container\">\n" +
                "<button type=\"button\" class=\"overlay-close\"><i class=\"icon-times\"></i></button>\n" +
                "<h2 class=\"new-form-title\"><span>GSM Numarası Güncelleme</span></h2>\n" +
                "<form class=\"tf-form-wrapper\">\n" +
                "<p id=\"verificationComment\">Mevcut GSM Numaranız:</p>\n" +
                "<p id=\"currentPhoneNumber\"><strong></strong></p>\n" +
                "<div class=\"form-group countryselectinput\">\n" +
                "<label id=\"gsmnolabel\">Yeni GSM Numaranız:</label>\n" +
                "<div class=\"country-codes-box selectCountry\">\n" +
                "<div class=\"cc-select\">\n" +
                "<span class=\"cc-selected flag flag-tr\"></span>\n" +
                "<i class=\"cc-arrow\"></i>\n" +
                "<div class=\"cc-select-data\">\n" +
                "<div class=\"cc-flag-container\">\n" +
                "<ul>\n" +
                "</ul>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<input type=\"tel\" class=\"cc-input tf-form\" id=\"newphone\" name=\"newphone\" value=\"\">\n" +
                "<input type=\"hidden\" name=\"country-code\">\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"form-group\">\n" +
                "<button type=\"submit\" class=\"orange btn block\" data-load-text=\"Lütfen bekleyiniz...\" data-original-text=\"Devam\">Devam</button>\n" +
                "</div>\n" +
                "</form>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"overlay overlay-hugeinc\" id=\"phone-verifycheck-form-modal\">\n" +
                "<div class=\"row new-form-design\">\n" +
                "<div id=\"verifycheck-form\" class=\"small-12 new-form-container\">\n" +
                "<button type=\"button\" class=\"overlay-close\"><i class=\"icon-times\"></i></button>\n" +
                "<h2 class=\"new-form-title\"><span>GSM Numarası Güncelleme</span></h2>\n" +
                "<p><b id=\"sendedcodethisphone\"></b> numaralı telefonunuza gönderdiğimiz doğrulama kodunu aşağıdaki kutuya yazınız.<br><a href=\"#\" id=\"backtoverifyphonepopup\"><i class=\"icon-left\"></i> Geri Dön</a></p>\n" +
                "<form class=\"tf-form-wrapper\">\n" +
                "<div class=\"form-group\">\n" +
                "<label for=\"activationcode\">Doğrulama Kodu</label>\n" +
                "<input id=\"activationcode\" type=\"text\" name=\"activationcode\" class=\"tf-form\" />\n" +
                "<span class=\"tf-form-clearbox icon-delete\"></span>\n" +
                "</div>\n" +
                "<div class=\"form-group\">\n" +
                "<button type=\"submit\" class=\"orange btn block\" data-load-text=\"Lütfen bekleyiniz...\" data-original-text=\"Gönder\">Gönder</button>\n" +
                "</div>\n" +
                "</form>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"page-loader\">\n" +
                "<div class=\"page-loader-spinner\">\n" +
                "<div class=\"bounce1\"></div>\n" +
                "<div class=\"bounce2\"></div>\n" +
                "<div class=\"bounce3\"></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"gototop-icon\"><i class=\"icon-up-open-big\"></i></div>\n" +
                "<script src=\"https://www.google.com/recaptcha/api.js?hl=tr\"></script>\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>\n" +
                "<script>\n" +
                "    var jspath = '//www.sikayetvar.com//application/2016.sikayetvar.com/', jsroot = '', jsbase = '//www.sikayetvar.com/', img = '', controller = 'complaint', action = 'detail', ajaxDomain = '//ajax.sikayetvar.com';\n" +
                "    var code =  null ;\n" +
                "    var googleRecaptchaSiteKey = '6Lc2PQwUAAAAAM2yTirb4gDoHoOInAeB2pvQnS6a';\n" +
                "\n" +
                "    var selectionInfos =  null ;\n" +
                "</script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/all.libraries.compressed.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/functions/global-methods.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/countrycodes.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/spellcheck.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/morphing-modal.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/tooltipster.bundle.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/html2canvas.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/sv-notification.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/jquery.fancybox.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/jquery.fancybox-thumbs.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/sweetalert2.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/turkish.chars.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/deasciifier.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/deasciifier.patterns.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/turkishEncoder.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/html_decoder.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/deasciifier/html_deasciifier.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/complaint-delete.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/owl.carousel.min.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/application/2016.sikayetvar.com/js/extra/jquery.sticky-kit.js?28\"></script>\n" +
                "<script type=\"text/javascript\" src=\"https://cdn-desktop.sikayetvar.com/system/cache/js/2016.sikayetvar.com-javascript.1127.min.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "        var isLogin = false;\n" +
                "\t\tvar memberID = false;\n" +
                "        var alertDetail = false;\n" +
                "        loginRedirectPage = false;\n" +
                "        socialMediaFormRequired = false;\n" +
                "        var updateTokenDevice = false;\n" +
                "        var warningMessage = \"\";\n" +
                "</script>\n" +
                "<div style=\"position:absolute; left:-1000000px;\">\n" +
                "<img style=\"border:0; padding:0; margin:0; line-height:0;\" width=\"0\" height=\"0\" src=\"https://ps.eyeota.net/pixel?pid=r8h3b20&t=gif&sid=sikayetvar&brand=turk-telekom\" />\n" +
                "</div> \n" +
                "</body>\n" +
                "</html>");
        mainTemplate.extract(doc);
        doc.saveAsFlatXML();

        linkTemplate.addNext(mainTemplate,LookupOptions.URL);

        WebFlow mainFlow = new WebFlow(linkTemplate);
        return mainFlow;
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);
        WebFlow.submit(service, build());
        service.shutdown();
    }
}
