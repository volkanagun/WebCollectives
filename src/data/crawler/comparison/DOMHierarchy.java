package data.crawler.comparison;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOMHierarchy {
    private DOMHierarchy parent;
    private String label;
    private List<DOMHierarchy> children = new ArrayList<>();
    private List<DOMLabel> matchList = new ArrayList<>();

    public DOMHierarchy(String label) {
        this.label = label;
    }

    public DOMHierarchy add(DOMLabel domLabel) {
        matchList.add(domLabel);
        return this;
    }

    public DOMHierarchy addChild(DOMHierarchy domHierarchy) {
        children.add(domHierarchy);
        domHierarchy.parent = this;
        return this;
    }

    public List<DOMHierarchy> getChildren() {
        return children;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DOMHierarchy getParent() {
        return parent;
    }

    public void setParent(DOMHierarchy parent) {
        this.parent = parent;
    }

    public boolean check(Element element) {
        boolean checked = true;
        for (int i = 0; i < matchList.size(); i++) {
            if (!matchList.get(i).check(element)) {
                checked = false;
                break;
            }
        }
        return checked;
    }

    public Map<DOMHierarchy, List<String>> extract(Elements nodes) {
        Map<DOMHierarchy, List<String>> valueMap = new HashMap<>();

        for (int j = 0; j < children.size(); j++) {
            DOMHierarchy crrHierarchy = children.get(j);
            for (int i = 0; i < nodes.size(); i++) {
                Element node = nodes.get(i);
                boolean checked = crrHierarchy.check(node);
                if (checked && crrHierarchy.children.isEmpty()) {
                    String text = node.text();
                    if (valueMap.containsKey(crrHierarchy)) {
                        valueMap.get(crrHierarchy).add(text);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(text);
                        valueMap.put(crrHierarchy, list);
                    }
                } else if (checked) {
                    valueMap.putAll(crrHierarchy.extract(node));
                }
            }
        }

        return valueMap;
    }

    public Map<DOMHierarchy, List<String>> extract(Element root) {
        Map<DOMHierarchy, List<String>> valueMap = new HashMap<>();
        if (check(root)) {
            if (children.isEmpty()) {
                List<String> items = new ArrayList<>();
                items.add(root.text());
                valueMap.put(this, items);
            } else {
                valueMap.put(this, new ArrayList<>());
                valueMap.putAll(extract(root.getAllElements()));

            }
        }
        return valueMap;

    }
}
