//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jsoup.nodes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.select.NodeFilter;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public abstract class Node implements Cloneable, Serializable {
    static final String EmptyString = "";
    Node parentNode;
    int siblingIndex;

    protected Node() {
    }

    public abstract String nodeName();

    protected abstract boolean hasAttributes();

    public boolean hasParent() {
        return this.parentNode != null;
    }

    public String attr(String attributeKey) {
        Validate.notNull(attributeKey);
        if (!this.hasAttributes()) {
            return "";
        } else {
            String val = this.attributes().getIgnoreCase(attributeKey);
            if (val.length() > 0) {
                return val;
            } else {
                return attributeKey.startsWith("abs:") ? this.absUrl(attributeKey.substring("abs:".length())) : "";
            }
        }
    }

    public abstract Attributes attributes();

    public Node attr(String attributeKey, String attributeValue) {
        attributeKey = NodeUtils.parser(this).settings().normalizeAttribute(attributeKey);
        this.attributes().putIgnoreCase(attributeKey, attributeValue);
        return this;
    }

    public boolean hasAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        if (attributeKey.startsWith("abs:")) {
            String key = attributeKey.substring("abs:".length());
            if (this.attributes().hasKeyIgnoreCase(key) && !this.absUrl(key).equals("")) {
                return true;
            }
        }

        return this.attributes().hasKeyIgnoreCase(attributeKey);
    }

    public Node removeAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        this.attributes().removeIgnoreCase(attributeKey);
        return this;
    }

    public Node clearAttributes() {
        Iterator it = this.attributes().iterator();

        while(it.hasNext()) {
            it.next();
            it.remove();
        }

        return this;
    }

    public abstract String baseUri();

    protected abstract void doSetBaseUri(String var1);

    public void setBaseUri(final String baseUri) {
        Validate.notNull(baseUri);
        this.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                node.doSetBaseUri(baseUri);
            }

            public void tail(Node node, int depth) {
            }
        });
    }

    public String absUrl(String attributeKey) {
        Validate.notEmpty(attributeKey);
        return !this.hasAttr(attributeKey) ? "" : StringUtil.resolve(this.baseUri(), this.attr(attributeKey));
    }

    protected abstract List<Node> ensureChildNodes();

    public Node childNode(int index) {
        return this.ensureChildNodes().get(index);
    }

    public List<Node> childNodes() {
        return Collections.unmodifiableList(this.ensureChildNodes());
    }

    public List<Node> childNodesCopy() {
        List<Node> nodes = this.ensureChildNodes();
        ArrayList<Node> children = new ArrayList(nodes.size());
        Iterator var3 = nodes.iterator();

        while(var3.hasNext()) {
            Node node = (Node)var3.next();
            children.add(node.clone());
        }

        return children;
    }

    public abstract int childNodeSize();

    protected Node[] childNodesAsArray() {
        return this.ensureChildNodes().toArray(new Node[0]);
    }

    public Node parent() {
        return this.parentNode;
    }

    public final Node parentNode() {
        return this.parentNode;
    }

    public Node root() {
        Node node;
        for(node = this; node.parentNode != null; node = node.parentNode) {
        }

        return node;
    }

    public Document ownerDocument() {
        Node root = this.root();
        return root instanceof Document ? (Document)root : null;
    }

    public void remove() {
        Validate.notNull(this.parentNode);
        this.parentNode.removeChild(this);
    }

    public Node before(String html) {
        this.addSiblingHtml(this.siblingIndex, html);
        return this;
    }

    public Node before(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex, node);
        return this;
    }

    public Node after(String html) {
        this.addSiblingHtml(this.siblingIndex + 1, html);
        return this;
    }

    public Node after(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex + 1, node);
        return this;
    }

    private void addSiblingHtml(int index, String html) {
        Validate.notNull(html);
        Validate.notNull(this.parentNode);
        Element context = this.parent() instanceof Element ? (Element)this.parent() : null;
        List<Node> nodes = NodeUtils.parser(this).parseFragmentInput(html, context, this.baseUri());
        this.parentNode.addChildren(index, nodes.toArray(new Node[0]));
    }

    public Node wrap(String html) {
        Validate.notEmpty(html);
        Element context = this.parent() instanceof Element ? (Element)this.parent() : null;
        List<Node> wrapChildren = NodeUtils.parser(this).parseFragmentInput(html, context, this.baseUri());
        Node wrapNode = wrapChildren.get(0);
        if (!(wrapNode instanceof Element)) {
            return null;
        } else {
            Element wrap = (Element)wrapNode;
            Element deepest = this.getDeepChild(wrap);
            this.parentNode.replaceChild(this, wrap);
            deepest.addChildren(this);
            if (wrapChildren.size() > 0) {
                for(int i = 0; i < wrapChildren.size(); ++i) {
                    Node remainder = wrapChildren.get(i);
                    remainder.parentNode.removeChild(remainder);
                    wrap.appendChild(remainder);
                }
            }

            return this;
        }
    }

    public Node unwrap() {
        Validate.notNull(this.parentNode);
        List<Node> childNodes = this.ensureChildNodes();
        Node firstChild = childNodes.size() > 0 ? childNodes.get(0) : null;
        this.parentNode.addChildren(this.siblingIndex, this.childNodesAsArray());
        this.remove();
        return firstChild;
    }

    private Element getDeepChild(Element el) {
        List<Element> children = el.children();
        return children.size() > 0 ? this.getDeepChild(children.get(0)) : el;
    }

    void nodelistChanged() {
    }

    public void replaceWith(Node in) {
        Validate.notNull(in);
        Validate.notNull(this.parentNode);
        this.parentNode.replaceChild(this, in);
    }

    protected void setParentNode(Node parentNode) {
        Validate.notNull(parentNode);
        if (this.parentNode != null) {
            this.parentNode.removeChild(this);
        }

        this.parentNode = parentNode;
    }

    protected void replaceChild(Node out, Node in) {
        Validate.isTrue(out.parentNode == this);
        Validate.notNull(in);
        if (in.parentNode != null) {
            in.parentNode.removeChild(in);
        }

        int index = out.siblingIndex;
        this.ensureChildNodes().set(index, in);
        in.parentNode = this;
        in.setSiblingIndex(index);
        out.parentNode = null;
    }

    protected void removeChild(Node out) {
        Validate.isTrue(out.parentNode == this);
        int index = out.siblingIndex;
        this.ensureChildNodes().remove(index);
        this.reindexChildren(index);
        out.parentNode = null;
    }

    protected void addChildren(Node... children) {
        List<Node> nodes = this.ensureChildNodes();
        Node[] var3 = children;
        int var4 = children.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Node child = var3[var5];
            this.reparentChild(child);
            nodes.add(child);
            child.setSiblingIndex(nodes.size() - 1);
        }

    }

    protected void addChildren(int index, Node... children) {
        Validate.noNullElements(children);
        List<Node> nodes = this.ensureChildNodes();
        Node[] var4 = children;
        int var5 = children.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Node child = var4[var6];
            this.reparentChild(child);
        }

        nodes.addAll(index, Arrays.asList(children));
        this.reindexChildren(index);
    }

    protected void reparentChild(Node child) {
        child.setParentNode(this);
    }

    private void reindexChildren(int start) {
        List<Node> childNodes = this.ensureChildNodes();

        for(int i = start; i < childNodes.size(); ++i) {
            childNodes.get(i).setSiblingIndex(i);
        }

    }

    public List<Node> siblingNodes() {
        if (this.parentNode == null) {
            return Collections.emptyList();
        } else {
            List<Node> nodes = this.parentNode.ensureChildNodes();
            List<Node> siblings = new ArrayList(nodes.size() - 1);
            Iterator var3 = nodes.iterator();

            while(var3.hasNext()) {
                Node node = (Node)var3.next();
                if (node != this) {
                    siblings.add(node);
                }
            }

            return siblings;
        }
    }

    public Node nextSibling() {
        if (this.parentNode == null) {
            return null;
        } else {
            List<Node> siblings = this.parentNode.ensureChildNodes();
            int index = this.siblingIndex + 1;
            return siblings.size() > index ? siblings.get(index) : null;
        }
    }

    public Node previousSibling() {
        if (this.parentNode == null) {
            return null;
        } else {
            return this.siblingIndex > 0 ? this.parentNode.ensureChildNodes().get(this.siblingIndex - 1) : null;
        }
    }

    public int siblingIndex() {
        return this.siblingIndex;
    }

    protected void setSiblingIndex(int siblingIndex) {
        this.siblingIndex = siblingIndex;
    }

    public Node traverse(NodeVisitor nodeVisitor) {
        Validate.notNull(nodeVisitor);
        NodeTraversor.traverse(nodeVisitor, this);
        return this;
    }

    public Node filter(NodeFilter nodeFilter) {
        Validate.notNull(nodeFilter);
        NodeTraversor.filter(nodeFilter, this);
        return this;
    }

    public String outerHtml() {
        StringBuilder accum = StringUtil.borrowBuilder();
        this.outerHtml(accum);
        return StringUtil.releaseBuilder(accum);
    }

    protected void outerHtml(Appendable accum) {
        NodeTraversor.traverse(new Node.OuterHtmlVisitor(accum, NodeUtils.outputSettings(this)), this);
    }

    abstract void outerHtmlHead(Appendable var1, int var2, OutputSettings var3) throws IOException;

    abstract void outerHtmlTail(Appendable var1, int var2, OutputSettings var3) throws IOException;

    public <T extends Appendable> T html(T appendable) {
        this.outerHtml(appendable);
        return appendable;
    }

    public String toString() {
        return this.outerHtml();
    }

    protected void indent(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append('\n').append(StringUtil.padding(depth * out.indentAmount()));
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public boolean hasSameValue(Object o) {
        if (this == o) {
            return true;
        } else {
            return o != null && this.getClass() == o.getClass() && this.outerHtml().equals(((Node) o).outerHtml());
        }
    }

    public Node clone() {
        Node thisClone = this.doClone(null);
        LinkedList<Node> nodesToProcess = new LinkedList();
        nodesToProcess.add(thisClone);

        while(!nodesToProcess.isEmpty()) {
            Node currParent = nodesToProcess.remove();
            int size = currParent.childNodeSize();

            for(int i = 0; i < size; ++i) {
                List<Node> childNodes = currParent.ensureChildNodes();
                Node childClone = childNodes.get(i).doClone(currParent);
                childNodes.set(i, childClone);
                nodesToProcess.add(childClone);
            }
        }

        return thisClone;
    }

    public Node shallowClone() {
        return this.doClone(null);
    }

    protected Node doClone(Node parent) {
        Node clone;
        try {
            clone = (Node)super.clone();
        } catch (CloneNotSupportedException var4) {
            throw new RuntimeException(var4);
        }

        clone.parentNode = parent;
        clone.siblingIndex = parent == null ? 0 : this.siblingIndex;
        return clone;
    }

    private static class OuterHtmlVisitor implements NodeVisitor {
        private final Appendable accum;
        private final OutputSettings out;

        OuterHtmlVisitor(Appendable accum, OutputSettings out) {
            this.accum = accum;
            this.out = out;
            out.prepareEncoder();
        }

        public void head(Node node, int depth) {
            try {
                node.outerHtmlHead(this.accum, depth, this.out);
            } catch (IOException var4) {
                throw new SerializationException(var4);
            }
        }

        public void tail(Node node, int depth) {
            if (!node.nodeName().equals("#text")) {
                try {
                    node.outerHtmlTail(this.accum, depth, this.out);
                } catch (IOException var4) {
                    throw new SerializationException(var4);
                }
            }

        }
    }
}
