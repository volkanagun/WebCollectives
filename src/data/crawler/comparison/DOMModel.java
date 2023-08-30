package data.crawler.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.util.TextFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

public class DOMModel {

    private Map<String, Double> timeMap = new HashMap<>();
    private double count = 0d;


    private XMLMap contentMap = new XMLMap();

    private DOMHierarchy domHierarchy = new DOMHierarchy("ROOT");

    public static String ROOT = "ROOT";

    public Map<String, Double> getTimeMap() {
        return timeMap;
    }

    public void setTimeMap(Map<String, Double> timeMap) {
        this.timeMap = timeMap;
    }


    public void write(String fname, String content) {
        increment();
        new TextFile(fname).writeFullText(content);
    }

    public DOMHierarchy getDomHierarchy() {

        timeMap.put("PARSE", 0d);
        timeMap.put("EXTRACT", 0d);
        count = 1;
        return domHierarchy;
    }

    public void setDomHierarchy(DOMHierarchy domHierarchy) {
        this.domHierarchy = domHierarchy;
    }

    public DOMModel structure(String parent, String current) {
        if (parent != null) contentMap.structure(parent, current);
        else contentMap.structure(XMLMap.ROOT, current);
        return this;
    }

    public DOMModel content(String tagName, List<String> tagContent) {
        contentMap.content(tagName, tagContent);
        return this;
    }


    public void printAverageTime() {
        double totalParse = timeMap.get("PARSE");
        double totalExtract = timeMap.get("EXTRACT");
        System.out.println("Total count: " + count);
        System.out.println("Parse time: " + totalParse / count);
        System.out.println("Extract time: " + totalExtract / count);
        System.out.println("Extract total time: " + (totalExtract+totalParse) / count);

    }


    public Document parseTime(File f) throws IOException {
        long crrTime = System.currentTimeMillis();
        Document document = parse(f);
        long newTime = System.currentTimeMillis();
        double measured = newTime - crrTime;
        timeMap.put("PARSE", timeMap.get("PARSE") + measured);
        return document;
    }

    //Parse html create DOM
    public Document parse(File f) throws IOException {

        Document document = Jsoup.parse(f, "UTF-8");
        return document;
    }

    public String extractTime(Document document) {
        long crrTime = System.currentTimeMillis();
        String content = extract(document);
        long newTime = System.currentTimeMillis();
        if (!content.isEmpty()) {
            double measured = newTime - crrTime;
            timeMap.put("EXTRACT", timeMap.get("EXTRACT") + measured);
        }

        return content;
    }

    public void increment() {
        count++;
    }

    //Extract tag content
    public String extract(Document document) {
        Map<DOMHierarchy, List<String>> map = domHierarchy.extract(document.body());
        map.put(domHierarchy, new ArrayList<>());
        extract(map, domHierarchy);
        String xml = contentMap.extract();
        return xml;
    }




    public void extract(Map<DOMHierarchy, List<String>> map, DOMHierarchy hierarchy) {

        if (map.containsKey(hierarchy) && map.get(hierarchy).isEmpty()) {
            List<DOMHierarchy> children = hierarchy.getChildren();
            for (int i = 0; i < children.size(); i++) {
                DOMHierarchy child = children.get(i);
                extract(map, child);
            }
        } else if (map.containsKey(hierarchy)) {
            contentMap.content(hierarchy.getLabel(), map.get(hierarchy));
        }
    }


}
