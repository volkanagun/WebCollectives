package data.crawler.web;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LookupSplit extends LookupPattern {
    String splitRegex;

    public LookupSplit(String type, String label, String splitRegex) {
        super(type, label, null);

        this.splitRegex = splitRegex;
    }

    public List<String> getResults(Map<String, String> propertyMap, String partial) {

        String[] array = partial.split(splitRegex);
        if (isNth() && nth < array.length) {
            List<String> resultList = new ArrayList<String>();
            int i = nth;
            while (i < mth) {
                String result = getReplaces(array[i]);
                resultList.add(result);
                i += 1;
            }

            return resultList;
        } else {
            return Arrays.asList(array);
        }

    }
}
