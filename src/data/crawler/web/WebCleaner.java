package data.crawler.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolf on 05.07.2015.
 */
public class WebCleaner implements Serializable {

    private static String[] tags = new String[]{"<.*?>", ""};

    private static String[] c = new String[]{"&ccedil;", "ç"};
    private static String[] C = new String[]{"&Ccedil;", "Ç"};

    private static String[] u = new String[]{"&uuml;", "ü"};
    private static String[] U = new String[]{"&Uuml;", "Ü"};
    private static String[] ui = new String[]{"&ucirc;", "ü"};
    private static String[] UI = new String[]{"&Ucirc;", "Ü"};



    private static String[] o = new String[]{"&ouml;", "ö"};
    private static String[] O = new String[]{"&Ouml;", "ö"};

    private static String[] a = new String[]{"&acirc;", "a"};
    private static String[] A = new String[]{"&Acirc;", "A"};
    private static String[] i = new String[]{"&icirc;", "i"};
    private static String[] I = new String[]{"&Icirc;", "İ"};

    private static String[] dot = new String[]{"&hellip;", "..."};
    private static String[] space = new String[]{"&nbsp;", " "};
    private static String[] quo1 = new String[]{"&rsquo;", "'"};
    private static String[] quo2 = new String[]{"&lsquo;", "'"};

    private static String[] quo3 = new String[]{"&#8217;", "'"};
    private static String[] quo4 = new String[]{"&#8216;", "'"};
    private static String[] quo5 = new String[]{"&#8220;", "\""};
    private static String[] quo6 = new String[]{"&#8221;", "\""};


    private static String[] quoto1 = new String[]{"&ldquo;", "\""};
    private static String[] quoto2 = new String[]{"&rdquo;", "\""};
    private static String[] quoto3 = new String[]{"&quot;", "\""};
    private static String[] quoto4 = new String[]{"&#39;", "\""};
    private static String[] hypen = new String[]{"&shy;", "-"};
    private static String[] euro = new String[]{"&euro;", "€"};
    private static String[] and = new String[]{"&amp;", "ve"};
    private static String[] mdash = new String[]{"&mdash;", "—"};
    private static String[] ndash = new String[]{"&ndash;", "-"};

    private static String[] lsaquo = new String[]{"&lsaquo;", "‹"};
    private static String[] rsaquo = new String[]{"&rsaquo;", "›"};
    private static String[] rsquo = new String[]{"&rsquo;", "'"};
    private static String[] frasl = new String[]{"&frasl;", "/"};
    private static String[] hellip = new String[]{"&#8230;", "..."};


    //Foreign

    private static String[] eacute = new String[]{"&eacute;", "é"};
    private static String[] Eacute = new String[]{"&Eacute;", "É"};
    private static String[] Oslash = new String[]{"&Oslash;", "Ø"};
    private static String[] oslash = new String[]{"&oslash;", "ø"};
    private static String[] ealing = new String[]{"&AElig;", "Æ"};
    private static String[] Ealing = new String[]{"&aelig;", "æ"};

    private static String[] Iacute = new String[]{"&Iacute;", "Í"};
    private static String[] iacute = new String[]{"&iacute", "í"};

    private static String[] ordm = new String[]{"&ordm;", "º"};
    private static String[] reg = new String[]{"&reg;", "®"};
    private static String[] copy = new String[]{"&copy;", "©"};
    private static String[] trade = new String[]{"&trade;", "™"};


    //Reuters
    private static String[] three = new String[]{"&#3;"," "};
    private static String[] unk1 = new String[]{"�&#5;&#30;","I"};
    private static String[] lt = new String[]{"&lt;"," lt "};
    private static String[] gt = new String[]{"&gt;"," gt "};








    private static List<String[]> replaceList = new ArrayList<>();

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
