package data.crawler.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolf on 05.07.2015.
 */
public class WebCleaner implements Serializable {

    private static final String[] tags = new String[]{"<.*?>", ""};

    private static final String[] c = new String[]{"&ccedil;", "ç"};
    private static final String[] C = new String[]{"&Ccedil;", "Ç"};

    private static final String[] u = new String[]{"&uuml;", "ü"};
    private static final String[] U = new String[]{"&Uuml;", "Ü"};
    private static final String[] ui = new String[]{"&ucirc;", "ü"};
    private static final String[] UI = new String[]{"&Ucirc;", "Ü"};



    private static final String[] o = new String[]{"&ouml;", "ö"};
    private static final String[] O = new String[]{"&Ouml;", "ö"};

    private static final String[] a = new String[]{"&acirc;", "a"};
    private static final String[] A = new String[]{"&Acirc;", "A"};
    private static final String[] i = new String[]{"&icirc;", "i"};
    private static final String[] I = new String[]{"&Icirc;", "İ"};

    private static final String[] dot = new String[]{"&hellip;", "..."};
    private static final String[] space = new String[]{"&nbsp;", " "};
    private static final String[] quo1 = new String[]{"&rsquo;", "'"};
    private static final String[] quo2 = new String[]{"&lsquo;", "'"};

    private static final String[] quo3 = new String[]{"&#8217;", "'"};
    private static final String[] quo4 = new String[]{"&#8216;", "'"};
    private static final String[] quo5 = new String[]{"&#8220;", "\""};
    private static final String[] quo6 = new String[]{"&#8221;", "\""};


    private static final String[] quoto1 = new String[]{"&ldquo;", "\""};
    private static final String[] quoto2 = new String[]{"&rdquo;", "\""};
    private static final String[] quoto3 = new String[]{"&quot;", "\""};
    private static final String[] quoto4 = new String[]{"&#39;", "\""};
    private static final String[] hypen = new String[]{"&shy;", "-"};
    private static final String[] euro = new String[]{"&euro;", "€"};
    private static final String[] and = new String[]{"&amp;", "ve"};
    private static final String[] mdash = new String[]{"&mdash;", "—"};
    private static final String[] ndash = new String[]{"&ndash;", "-"};

    private static final String[] lsaquo = new String[]{"&lsaquo;", "‹"};
    private static final String[] rsaquo = new String[]{"&rsaquo;", "›"};
    private static final String[] rsquo = new String[]{"&rsquo;", "'"};
    private static final String[] frasl = new String[]{"&frasl;", "/"};
    private static final String[] hellip = new String[]{"&#8230;", "..."};


    //Foreign

    private static final String[] eacute = new String[]{"&eacute;", "é"};
    private static final String[] Eacute = new String[]{"&Eacute;", "É"};
    private static final String[] Oslash = new String[]{"&Oslash;", "Ø"};
    private static final String[] oslash = new String[]{"&oslash;", "ø"};
    private static final String[] ealing = new String[]{"&AElig;", "Æ"};
    private static final String[] Ealing = new String[]{"&aelig;", "æ"};

    private static final String[] Iacute = new String[]{"&Iacute;", "Í"};
    private static final String[] iacute = new String[]{"&iacute", "í"};

    private static final String[] ordm = new String[]{"&ordm;", "º"};
    private static final String[] reg = new String[]{"&reg;", "®"};
    private static final String[] copy = new String[]{"&copy;", "©"};
    private static final String[] trade = new String[]{"&trade;", "™"};


    //Reuters
    private static final String[] three = new String[]{"&#3;"," "};
    private static final String[] unk1 = new String[]{"�&#5;&#30;","I"};
    private static final String[] lt = new String[]{"&lt;"," lt "};
    private static final String[] gt = new String[]{"&gt;"," gt "};








    private static final List<String[]> replaceList = new ArrayList<>();

    static {
        replaceList.add(tags);

        replaceList.add(c);
        replaceList.add(C);
        replaceList.add(u);
        replaceList.add(U);
        replaceList.add(ui);
        replaceList.add(UI);

        replaceList.add(o);
        replaceList.add(O);
        replaceList.add(a);
        replaceList.add(A);
        replaceList.add(i);
        replaceList.add(I);

        replaceList.add(dot);

        replaceList.add(space);
        replaceList.add(rsquo);
        replaceList.add(quo1);
        replaceList.add(quo2);
        replaceList.add(quo3);
        replaceList.add(quo4);
        replaceList.add(quo5);
        replaceList.add(quo6);
        replaceList.add(quoto1);
        replaceList.add(quoto2);
        replaceList.add(quoto3);
        replaceList.add(quoto4);

        replaceList.add(hypen);
        replaceList.add(euro);


        replaceList.add(and);
        replaceList.add(mdash);
        replaceList.add(ndash);
        replaceList.add(rsaquo);
        replaceList.add(lsaquo);
        replaceList.add(frasl);
        replaceList.add(hellip);

        //Foreign
        replaceList.add(eacute);
        replaceList.add(Eacute);
        replaceList.add(Oslash);
        replaceList.add(oslash);
        replaceList.add(ealing);
        replaceList.add(Ealing);

        replaceList.add(ordm);
        replaceList.add(iacute);
        replaceList.add(Iacute);

        //Reuters
        replaceList.add(lt);
        replaceList.add(gt);
        replaceList.add(three);
        replaceList.add(unk1);



    }

    public WebCleaner() {


    }

    public static String clean(String text) {
        for (String[] replacement : replaceList) {
            text = text.replaceAll(replacement[0], replacement[1]);
        }

        return text;
    }

}
