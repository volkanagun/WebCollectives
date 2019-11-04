package data.crawler.web;

import java.io.Serializable;
import java.util.*;

/**
 * Created by wolf on 02.07.2015.
 */
public class LookupResult implements Serializable {
    private String label;
    private String text;
    private String type;
    private List<LookupResult> subList;

    public static String LABEL = "LABEL";
    public static String TYPE = "TYPE";
    public static String TEXT = "TEXT";
    public static String DOC = "DOC";
    public static String ID = "ID";
    public static String URL = "URL";
    public static String HTML = "HTML";

    public LookupResult(String type, String label, String text) {
        this.type = type;
        this.label = label;
        this.text = text;
        this.subList = new ArrayList<>();
    }

    public LookupResult(String type, String label) {
        this(type, label, "");
    }

    public void addSubList(LookupResult lookupResult) {
        //if (!subList.contains(lookupResult))
            subList.add(lookupResult);

    }

    public void addSubList(List<LookupResult> lookupResults) {
        for (LookupResult lookupResult : lookupResults) {
            addSubList(lookupResult);
        }
    }

    public List<LookupResult> getSubResults(String labelLook) {
        List<LookupResult> subResults = new ArrayList<>();
        if (label.equals(labelLook)) {
            subResults.add(this);
        } else {
            for (LookupResult subResult : subList) {
                List<LookupResult> items = subResult.getSubResults(labelLook);
                subResults.addAll(items);
            }
        }

        return subResults;
    }

    public List<LookupResult> getAllSubLinear() {
        List<LookupResult> subLinear = new ArrayList<>();
        if (!subList.isEmpty()) {
            if (type.equals(LookupOptions.SKIP)) {
               for(LookupResult subResult:subList){
                   subLinear.addAll(subResult.getAllSubLinear());
               }
            }
        }
        else if (!type.equals(LookupOptions.SKIP) && text != null && !text.isEmpty()){
            subLinear.add(new LookupResult(LookupResult.TEXT, label, WebCleaner.clean(text)));
        }

        return subLinear;
    }

    public List<LookupResult> getSubResults(List<String> labelLook){
        List<LookupResult> subResults = new ArrayList<>();
        for(String look:labelLook) subResults.addAll(getSubResults(look));
        return subResults;
    }

    public Set<String> getSubResultLabels(List<String> labelLook){
        Set<String> set = new HashSet<>();
        List<LookupResult> allSubResults = getSubResults(labelLook);
        for(LookupResult subResult:allSubResults){
            set.add(subResult.getLabel());
        }

        return set;
    }

    public List<LookupResult> getSubList() {
        return subList;
    }



    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LookupResult that = (LookupResult) o;

        if (!label.equals(that.label)) return false;
        if (!text.equals(that.text)) return false;
        return type.equals(that.type);
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookupResult that = (LookupResult) o;
        return label.equals(that.label) &&
                text.equals(that.text) &&
                type.equals(that.type) &&
                subList.equals(that.subList);
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public boolean isEmpty() {
        return subList.isEmpty();
    }

    public boolean isNull() {
        return text == null || text.isEmpty();
    }

    public String tagOpen(){
        return "<RESULT TYPE=\"" + type + "\" LABEL=\"" + label + "\">";
    }

    public String tagClose(){
        return "</RESULT>";
    }



    public String toString() {


        if (!subList.isEmpty()) {
            String main = null;
            if (type.equals(LookupOptions.SKIP)) {
                main = "";
                for (LookupResult subResult : subList) {
                    main += "\n" + subResult.toString();
                }

            } else {
                main = "<RESULT TYPE=\"" + type + "\" LABEL=\"" + label + "\">";
                for (LookupResult subResult : subList) {
                    main += "\n" + subResult.toString();
                }
                main += "\n" + "</RESULT>";
            }

            return main;
        } else if (!type.equals(LookupOptions.SKIP) && text != null && !text.isEmpty()) {
            String main = "<RESULT TYPE=\"" + type + "\" LABEL=\"" + label + "\">";
            main += "\n" + WebCleaner.clean(text);

            main += "\n" + "</RESULT>";
            return main;
        } else return "";


    }

    public String toString(Map<String,String> lookupMap) {


        if (!subList.isEmpty()) {
            String main = null;
            if (type.equals(LookupOptions.SKIP)) {
                main = "";
                for (LookupResult subResult : subList) {
                    main += "\n" + subResult.toString();
                }

            } else {
                main = "<RESULT TYPE=\"" + type + "\" LABEL=\"" + label + "\">";
                for (LookupResult subResult : subList) {
                    main += "\n" + subResult.toString();
                }
                main += "\n" + "</RESULT>";
            }

            return main;
        } else if (!type.equals(LookupOptions.SKIP) && text != null && !text.isEmpty()) {

            String main = "<RESULT TYPE=\"" + type + "\" LABEL=\"" + label + "\">";
            main += "\n" + WebCleaner.clean(text);

            main += "\n" + "</RESULT>";
            return main;
        } else return "";


    }
}
