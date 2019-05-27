package data.crawler.web;


import data.util.TextPattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wolf on 02.07.2015.
 */
public class LookupPattern implements Serializable {
    protected String label, type;
    protected String startRegex, endRegex;
    protected String singleRegex;
    protected Integer singleGroup;

    protected String startMarker, endMarker;
    protected String value;
    protected String[] regex, replaces;
    protected Integer nth, mth;
    protected boolean removeTags = true;
    protected List<LookupPattern> subpatterns;
    protected LookupFilter lookupFilter;

    public LookupPattern(String type, String label, String startRegex, String endRegex) {
        this.label = label;
        this.type = type;
        this.startRegex = regexifier(startRegex);
        this.endRegex = regexifier(endRegex);
        this.subpatterns = new ArrayList<>();
    }

    public LookupPattern(String type, String label, String value) {
        this.label = label;
        this.type = type;
        this.value = value;
        this.subpatterns = new ArrayList<>();
    }

    private String regexifier(String regex) {
        if (regex != null) {
            regex = regex.replaceAll("\\s", "\\\\s");
            regex = regex.replaceAll("\\-", "\\\\-");
            regex = regex.replaceAll("\\:", "\\\\:");
            regex = regex.replaceAll("\\_", "\\\\_");
        }

        return regex;
    }

    public LookupFilter getLookupFilter() {
        return lookupFilter;
    }

    public LookupPattern setLookupFilter(LookupFilter lookupFilter) {
        this.lookupFilter = lookupFilter;
        return this;
    }

    public LookupPattern addPattern(LookupPattern pattern) {
        this.subpatterns.add(pattern);
        return this;
    }

    public String getValue() {
        return value;
    }

    public LookupPattern setValue(String value) {
        this.value = value;
        return this;
    }

    public String[] getRegex() {
        return regex;
    }

    public LookupPattern setRegex(String[] regex) {
        this.regex = regex;
        return this;
    }

    public Integer getSingleGroup() {
        return singleGroup;
    }

    public LookupPattern setSingleGroup(Integer singleGroup) {
        this.singleGroup = singleGroup;
        return this;
    }

    public String getSingleRegex() {
        return singleRegex;
    }

    public LookupPattern setSingleRegex(String singleRegex) {
        this.singleRegex = singleRegex;
        return this;
    }

    public String[] getReplaces() {
        return replaces;
    }

    public LookupPattern setReplaces(String[] replaces) {
        this.replaces = replaces;
        return this;
    }

    public boolean isRemoveTags() {
        return removeTags;
    }

    public LookupPattern setRemoveTags(boolean removeTags) {
        this.removeTags = removeTags;
        return this;
    }

    public Integer getNth() {
        return nth;
    }

    public LookupPattern setNth(Integer nth) {
        this.nth = nth;
        this.mth = nth + 1;
        return this;
    }

    public Integer getMth() {
        return mth;
    }

    public LookupPattern setMth(Integer mth) {
        this.mth = mth;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public LookupPattern setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getType() {
        return type;
    }

    public LookupPattern setType(String type) {
        this.type = type;
        return this;
    }

    public String getStartRegex() {
        return startRegex;
    }

    public LookupPattern setStartRegex(String startRegex) {
        this.startRegex = startRegex;
        return this;
    }

    public String getEndRegex() {
        return endRegex;
    }

    public LookupPattern setEndRegex(String endRegex) {
        this.endRegex = endRegex;
        return this;
    }

    public LookupPattern setStartEndMarker(String startMarker, String endMarker) {

        this.startMarker = startMarker;
        this.endMarker = endMarker;
        return this;
    }

    public String getStartMarker() {
        return startMarker;
    }

    public LookupPattern setStartMarker(String startMarker) {
        this.startMarker = startMarker;
        return this;
    }

    public String getEndMarker() {
        return endMarker;
    }

    public LookupPattern setEndMarker(String endMarker) {
        this.endMarker = endMarker;
        return this;
    }

    public List<LookupPattern> getSubpatterns() {
        return subpatterns;
    }

    public List<String> getNonSkipSubpatternLabels() {
        List<String> labels = new ArrayList<>();
        for (LookupPattern subPattern : subpatterns) {
            if (subPattern.getSubpatterns().isEmpty()) {
                labels.add(subPattern.label);
            } else if (!subPattern.getSubpatterns().isEmpty()) if (!subPattern.getLabel().equals(LookupOptions.SKIP)) {
                labels.add(subPattern.label);
                labels.addAll(subPattern.getNonSkipSubpatternLabels());
            } else {
                labels.addAll(subPattern.getNonSkipSubpatternLabels());
            }

        }

        return labels;
    }

    public boolean isValue() {
        return value != null;
    }

    public boolean isRegex() {
        return replaces != null && regex != null;
    }

    public boolean isNth() {
        return nth != null;
    }


    @Override
    public String toString() {
        return "LookupPattern{" +
                "startRegex='" + startRegex + '\'' +
                ", endRegex='" + endRegex + '\'' +
                '}';
    }

    public List<LookupResult> getResult(Map<String, String> propertyMap, String partial) {
        List<LookupResult> lookupResults = new ArrayList<>();
        List<String> partialResults = getResults(propertyMap, partial);

        if (subpatterns.isEmpty()) {
            for (String partialResult : partialResults) {
                String finalResult = lookupFilter==null?partialResult : lookupFilter.accept(partialResult);
                if(finalResult!=null) {
                    LookupResult subResult = new LookupResult(type, label, finalResult);
                    if (!lookupResults.contains(subResult)) {
                        lookupResults.add(subResult);
                    }
                }
            }
        } else {

            for (String partialResult : partialResults) {
                //Adding buggy for parsing reuters
                String finalResult = lookupFilter==null?partialResult : lookupFilter.accept(partialResult);
                if(finalResult!=null) {
                    LookupResult lookupResult = new LookupResult(type, label);
                    for (LookupPattern partialPattern : subpatterns) {
                        List<LookupResult> subLookupResults = partialPattern.getResult(propertyMap, finalResult);
                        lookupResult.addSubList(subLookupResults);

                    }
                    lookupResults.add(lookupResult);
                }
            }


        }


        return lookupResults;
    }

    protected String getReplaces(String result) {
        if (isRegex()) {
            for (int i = 0; i < regex.length; i++) {
                result = result.replaceAll(regex[i], replaces[i]);
            }
        }
        return result;
    }

    protected List<String> getResults(Map<String, String> propertyMap, String partial) {
        ArrayList<String> resultList = new ArrayList<>();
        if (isValue()) {
            if (type.equals(LookupOptions.TEXT)) {
                resultList.add(value);
            } else if (type.equals(LookupOptions.LOOKUP)) {
                if (value != null && propertyMap.containsKey(value))
                    resultList.add(propertyMap.get(value));
            } else if (type.equals(LookupOptions.CONTAINER) && value.isEmpty()) {
                resultList.add(partial);
            }
        } else if (!type.equals(LookupOptions.TEXT) && startMarker != null && endMarker != null) {
            TextPattern.obtainPatterns(startRegex, endRegex, startMarker, endMarker, partial, resultList);
        } else if (type.equals(LookupOptions.TEXT) && singleRegex != null) {
            TextPattern.obtainPatterns(singleRegex, singleGroup, false, partial, resultList);
        } else if (!type.equals(LookupOptions.TEXT)) {
            TextPattern.obtainPatterns(startRegex, endRegex, false, partial, resultList);
        } else if (type.equals(LookupOptions.TEXT) && regex != null) {
            TextPattern.obtainPatterns(startRegex, endRegex, false, partial, resultList);
        } else if (type.equals(LookupOptions.TEXT)) {
            TextPattern.obtainPatterns(startRegex, endRegex, removeTags, partial, resultList);
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
