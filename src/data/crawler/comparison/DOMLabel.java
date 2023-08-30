package data.crawler.comparison;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Objects;

public class DOMLabel {
    private String tagLabel;
    private String tagRegex;
    private String attributeRegex;
    private String attributeValueRegex;

    public DOMLabel(String tagLabel, String tagRegex, String attributeRegex, String attributeValueRegex) {
        this.tagLabel = tagLabel;
        this.tagRegex = tagRegex;
        this.attributeRegex = attributeRegex;
        this.attributeValueRegex = attributeValueRegex;
    }

    public static DOMLabel create(String tagLabel, String tagRegex, String attributeRegex, String attributeValueRegex) {
        return new DOMLabel(tagLabel, tagRegex, attributeRegex, attributeValueRegex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DOMLabel)) return false;
        DOMLabel domLabel = (DOMLabel) o;
        return Objects.equals(tagLabel, domLabel.tagLabel) && Objects.equals(tagRegex, domLabel.tagRegex) && Objects.equals(attributeRegex, domLabel.attributeRegex) && Objects.equals(attributeValueRegex, domLabel.attributeValueRegex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagLabel, tagRegex, attributeRegex, attributeValueRegex);
    }

    public boolean check(Element element) {
        if (element.nodeName().matches(tagRegex)) {
            List<Attribute> attributes = element.attributes().asList();
            for (int i = 0; i < attributes.size(); i++) {
                Attribute node = attributes.get(i);
                if (node.getKey().matches(attributeRegex) && node.getValue().matches(attributeValueRegex)) {
                    return true;
                }
            }

            if (attributeRegex.equals("(.*?)")) {
                return true;
            }


        }
        return false;
    }
}
