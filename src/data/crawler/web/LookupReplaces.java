package data.crawler.web;

import java.util.ArrayList;
import java.util.List;

public class LookupReplaces {

    private final List<String[]> replaceList = new ArrayList<>();

    public static void initHTML(List<String[]> mainList){
        mainList.add(new String[]{"&#231;","ç"});
    }

}
