package data.crawler.web;

import data.util.TextPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LookupAuto extends LookupPattern {
    //find most text intensive

    private final Map<String, String> propertyRegex = new HashMap<>();

    public LookupAuto(String type, String label, String startRegex, String endRegex) {
        super(type, label, startRegex, endRegex);
    }

    public String removeTags(String text){
        return text.replaceAll("\\<(.*?)\\>","");
    }

    public LookupAuto addPropertyRegex(String label, String regex){
        propertyRegex.put(label, regex);
        return this;
    }

    private Boolean matchProperty(String label, String content){
        if(propertyRegex.containsKey(label)){
            if(propertyRegex.get(label).isEmpty()) return true;
            else {
                return Pattern.compile(propertyRegex.get(label)).matcher(content).find();
            }
        }
        else{
            return true;
        }
    }

    public Boolean matchDensity(String content){
       String withoutTags = content.replaceAll("\\<(.*?)\\>", "");
       return withoutTags.length() / content.length() > 0.8;
    }

    public LookupPattern decidePattern(String[] item){
        if(item[0].equals("p") && matchDensity(item[2]) && matchProperty(LookupOptions.ARTICLEPARAGRAPH, item[1])){
            return new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLEPARAGRAPH, removeTags(item[2]));
        }
        else if(item[0].equals("h") && matchProperty(LookupOptions.ARTICLETITLE, item[1])){
            return new LookupPattern(LookupOptions.TEXT, LookupOptions.ARTICLETITLE, removeTags(item[2]));
        }
        else if(item[0].equals("time") && matchProperty(LookupOptions.DATE, item[1])){
            return new LookupPattern(LookupOptions.TEXT, LookupOptions.DATE, removeTags(item[2]));
        }
        else if(item[0].matches("(span|p)") && matchProperty(LookupOptions.AUTHOR, item[1])){
            return new LookupPattern(LookupOptions.TEXT, LookupOptions.AUTHOR, removeTags(item[2]));
        }
        else return null;
    }

    protected List<String> getResults(Map<String, String> propertyMap, String partial) {
        Pattern regexStart = Pattern.compile("\\<(p|h1|h2|span|time)(.*?)\\>(.*?)</");
        List<String[]> items = TextPattern.obtainPatterns(regexStart, partial, new int[]{1, 2, 3});
        List<String> resultList = new ArrayList<>();

        for(String[] item:items){
            LookupPattern crrPattern =  decidePattern(item);
            if(crrPattern.label.equals(label)){
                resultList.add(crrPattern.value);
            }
        }

        if (isNth() && nth < resultList.size()) {
            List<String> subList = new ArrayList<>();
            for (int i = nth; i < Math.min(mth, resultList.size()); i++) {
                String result = getReplaces(resultList.get(i));
                subList.add(result);
            }
            resultList.clear();
            resultList.addAll(subList);
        }

        if (isRegex()) {

            List<String> subList = new ArrayList<>();
            for (int i = 0; i < resultList.size(); i++) {
                String result = getReplaces(resultList.get(i));
                subList.add(result);
            }
            resultList.clear();
            resultList.addAll(subList);
        }

        return resultList;
    }
}
