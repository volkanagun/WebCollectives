package data.crawler.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLMap {

    //Parent children
    private Map<String, List<String>> hierarchyMap = new HashMap<>();
    private Map<String, List<String>> contentMap = new HashMap<>();

    public static String ROOT = "ROOT";


    public void structure(String parent, String current){
        if(hierarchyMap.containsKey(parent)) hierarchyMap.get(parent).add(current);
        else{
            List<String> newList = new ArrayList<>();
            newList.add(current);
            hierarchyMap.put(parent, newList);
        }
    }

    public void content(String label, List<String> content){
        contentMap.put(label, content);
    }

    private String extract(String tagName){
        if(contentMap.containsKey(tagName)) {
            List<String> list = contentMap.get(tagName);
            String text = "";
            for(int i=0; i< list.size(); i++){
                String item = "<"+tagName+">\n" +  list.get(i) + "\n</"+tagName+">\n";
                text += item;
            }
            return text;
        }
        else if(hierarchyMap.containsKey(tagName)){
            String content = "";
            List<String> children = hierarchyMap.get(tagName);
            for(int i=0; i<children.size(); i++){
                String tag = children.get(i);
                String value = extract(tag);
                if(!value.isEmpty()) {
                    content += "<" + tag + ">\n";
                    content += value;
                    content += "\n</" + tag + ">\n";
                }
            }
            return content;
        }
        else{
            return "";
        }
    }

    public String extract(){
        return extract(ROOT);
    }
}
