//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jsoup.nodes;

import java.io.IOException;
import java.io.Serializable;

import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class DocumentType extends LeafNode implements Serializable {
    public static final String PUBLIC_KEY = "PUBLIC";
    public static final String SYSTEM_KEY = "SYSTEM";
    private static final String NAME = "name";
    private static final String PUB_SYS_KEY = "pubSysKey";
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";

    public DocumentType(String name, String publicId, String systemId) {
        Validate.notNull(name);
        Validate.notNull(publicId);
        Validate.notNull(systemId);
        this.attr("name", name);
        this.attr("publicId", publicId);
        if (this.has("publicId")) {
            this.attr("pubSysKey", "PUBLIC");
        }

        this.attr("systemId", systemId);
    }

    /** @deprecated */
    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        this.attr("name", name);
        this.attr("publicId", publicId);
        if (this.has("publicId")) {
            this.attr("pubSysKey", "PUBLIC");
        }

        this.attr("systemId", systemId);
    }

    /** @deprecated */
    public DocumentType(String name, String pubSysKey, String publicId, String systemId, String baseUri) {
        this.attr("name", name);
        if (pubSysKey != null) {
            this.attr("pubSysKey", pubSysKey);
        }

        this.attr("publicId", publicId);
        this.attr("systemId", systemId);
    }

    public void setPubSysKey(String value) {
        if (value != null) {
            this.attr("pubSysKey", value);
        }

    }

    public String nodeName() {
        return "#doctype";
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.syntax() == Syntax.html && !this.has("publicId") && !this.has("systemId")) {
            accum.append("<!doctype");
        } else {
            accum.append("<!DOCTYPE");
        }

        if (this.has("name")) {
            accum.append(" ").append(this.attr("name"));
        }

        if (this.has("pubSysKey")) {
            accum.append(" ").append(this.attr("pubSysKey"));
        }

        if (this.has("publicId")) {
            accum.append(" \"").append(this.attr("publicId")).append('"');
        }

        if (this.has("systemId")) {
            accum.append(" \"").append(this.attr("systemId")).append('"');
        }

        accum.append('>');
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    private boolean has(String attribute) {
        return !StringUtil.isBlank(this.attr(attribute));
    }
}
