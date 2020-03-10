//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jsoup.nodes;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jsoup.SerializationException;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;

public class Attributes implements Iterable<Attribute>, Cloneable, Serializable {
    protected static final String dataPrefix = "data-";
    private static final int InitialCapacity = 4;
    private static final int GrowthFactor = 2;
    private static final String[] Empty = new String[0];
    static final int NotFound = -1;
    private static final String EmptyString = "";
    private int size = 0;
    String[] keys;
    String[] vals;

    public Attributes() {
        this.keys = Empty;
        this.vals = Empty;
    }

    private void checkCapacity(int minNewSize) {
        Validate.isTrue(minNewSize >= this.size);
        int curSize = this.keys.length;
        if (curSize < minNewSize) {
            int newSize = curSize >= 4 ? this.size * 2 : 4;
            if (minNewSize > newSize) {
                newSize = minNewSize;
            }

            this.keys = copyOf(this.keys, newSize);
            this.vals = copyOf(this.vals, newSize);
        }
    }

    private static String[] copyOf(String[] orig, int size) {
        String[] copy = new String[size];
        System.arraycopy(orig, 0, copy, 0, Math.min(orig.length, size));
        return copy;
    }

    int indexOfKey(String key) {
        Validate.notNull(key);

        for(int i = 0; i < this.size; ++i) {
            if (key.equals(this.keys[i])) {
                return i;
            }
        }

        return -1;
    }

    private int indexOfKeyIgnoreCase(String key) {
        Validate.notNull(key);

        for(int i = 0; i < this.size; ++i) {
            if (key.equalsIgnoreCase(this.keys[i])) {
                return i;
            }
        }

        return -1;
    }

    static String checkNotNull(String val) {
        return val == null ? "" : val;
    }

    public String get(String key) {
        int i = this.indexOfKey(key);
        return i == -1 ? "" : checkNotNull(this.vals[i]);
    }

    public String getIgnoreCase(String key) {
        int i = this.indexOfKeyIgnoreCase(key);
        return i == -1 ? "" : checkNotNull(this.vals[i]);
    }

    private void add(String key, String value) {
        this.checkCapacity(this.size + 1);
        this.keys[this.size] = key;
        this.vals[this.size] = value;
        ++this.size;
    }

    public Attributes put(String key, String value) {
        int i = this.indexOfKey(key);
        if (i != -1) {
            this.vals[i] = value;
        } else {
            this.add(key, value);
        }

        return this;
    }

    void putIgnoreCase(String key, String value) {
        int i = this.indexOfKeyIgnoreCase(key);
        if (i != -1) {
            this.vals[i] = value;
            if (!this.keys[i].equals(key)) {
                this.keys[i] = key;
            }
        } else {
            this.add(key, value);
        }

    }

    public Attributes put(String key, boolean value) {
        if (value) {
            this.putIgnoreCase(key, (String)null);
        } else {
            this.remove(key);
        }

        return this;
    }

    public Attributes put(Attribute attribute) {
        Validate.notNull(attribute);
        this.put(attribute.getKey(), attribute.getValue());
        attribute.parent = this;
        return this;
    }

    private void remove(int index) {
        Validate.isFalse(index >= this.size);
        int shifted = this.size - index - 1;
        if (shifted > 0) {
            System.arraycopy(this.keys, index + 1, this.keys, index, shifted);
            System.arraycopy(this.vals, index + 1, this.vals, index, shifted);
        }

        --this.size;
        this.keys[this.size] = null;
        this.vals[this.size] = null;
    }

    public void remove(String key) {
        int i = this.indexOfKey(key);
        if (i != -1) {
            this.remove(i);
        }

    }

    public void removeIgnoreCase(String key) {
        int i = this.indexOfKeyIgnoreCase(key);
        if (i != -1) {
            this.remove(i);
        }

    }

    public boolean hasKey(String key) {
        return this.indexOfKey(key) != -1;
    }

    public boolean hasKeyIgnoreCase(String key) {
        return this.indexOfKeyIgnoreCase(key) != -1;
    }

    public int size() {
        return this.size;
    }

    public void addAll(Attributes incoming) {
        if (incoming.size() != 0) {
            this.checkCapacity(this.size + incoming.size);
            Iterator var2 = incoming.iterator();

            while(var2.hasNext()) {
                Attribute attr = (Attribute)var2.next();
                this.put(attr);
            }

        }
    }

    public Iterator<Attribute> iterator() {
        return new Iterator<Attribute>() {
            int i = 0;

            public boolean hasNext() {
                return this.i < Attributes.this.size;
            }

            public Attribute next() {
                Attribute attr = new Attribute(Attributes.this.keys[this.i], Attributes.this.vals[this.i], Attributes.this);
                ++this.i;
                return attr;
            }

            public void remove() {
                Attributes.this.remove(--this.i);
            }
        };
    }

    public List<Attribute> asList() {
        ArrayList<Attribute> list = new ArrayList(this.size);

        for(int i = 0; i < this.size; ++i) {
            Attribute attr = this.vals[i] == null ? new BooleanAttribute(this.keys[i]) : new Attribute(this.keys[i], this.vals[i], this);
            list.add(attr);
        }

        return Collections.unmodifiableList(list);
    }

    public Map<String, String> dataset() {
        return new Attributes.Dataset(this);
    }

    public String html() {
        StringBuilder sb = StringUtil.borrowBuilder();

        try {
            this.html(sb, (new Document("")).outputSettings());
        } catch (IOException var3) {
            throw new SerializationException(var3);
        }

        return StringUtil.releaseBuilder(sb);
    }

    final void html(Appendable accum, OutputSettings out) throws IOException {
        int sz = this.size;

        for(int i = 0; i < sz; ++i) {
            String key = this.keys[i];
            String val = this.vals[i];
            accum.append(' ').append(key);
            if (!Attribute.shouldCollapseAttribute(key, val, out)) {
                accum.append("=\"");
                Entities.escape(accum, val == null ? "" : val, out, true, false, false);
                accum.append('"');
            }
        }

    }

    public String toString() {
        return this.html();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Attributes that = (Attributes)o;
            if (this.size != that.size) {
                return false;
            } else {
                return !Arrays.equals(this.keys, that.keys) ? false : Arrays.equals(this.vals, that.vals);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.size;
        result = 31 * result + Arrays.hashCode(this.keys);
        result = 31 * result + Arrays.hashCode(this.vals);
        return result;
    }

    public Attributes clone() {
        Attributes clone;
        try {
            clone = (Attributes)super.clone();
        } catch (CloneNotSupportedException var3) {
            throw new RuntimeException(var3);
        }

        clone.size = this.size;
        this.keys = copyOf(this.keys, this.size);
        this.vals = copyOf(this.vals, this.size);
        return clone;
    }

    public void normalize() {
        for(int i = 0; i < this.size; ++i) {
            this.keys[i] = Normalizer.lowerCase(this.keys[i]);
        }

    }

    private static String dataKey(String key) {
        return "data-" + key;
    }

    private static class Dataset extends AbstractMap<String, String> {
        private final Attributes attributes;

        private Dataset(Attributes attributes) {
            this.attributes = attributes;
        }

        public Set<Entry<String, String>> entrySet() {
            return new Attributes.Dataset.EntrySet();
        }

        public String put(String key, String value) {
            String dataKey = Attributes.dataKey(key);
            String oldValue = this.attributes.hasKey(dataKey) ? this.attributes.get(dataKey) : null;
            this.attributes.put(dataKey, value);
            return oldValue;
        }

        private class DatasetIterator implements Iterator<Entry<String, String>> {
            private Iterator<Attribute> attrIter;
            private Attribute attr;

            private DatasetIterator() {
                this.attrIter = Dataset.this.attributes.iterator();
            }

            public boolean hasNext() {
                while(true) {
                    if (this.attrIter.hasNext()) {
                        this.attr = (Attribute)this.attrIter.next();
                        if (!this.attr.isDataAttribute()) {
                            continue;
                        }

                        return true;
                    }

                    return false;
                }
            }

            public Entry<String, String> next() {
                return new Attribute(this.attr.getKey().substring("data-".length()), this.attr.getValue());
            }

            public void remove() {
                Dataset.this.attributes.remove(this.attr.getKey());
            }
        }

        private class EntrySet extends AbstractSet<Entry<String, String>> {
            private EntrySet() {
            }

            public Iterator<Entry<String, String>> iterator() {
                return Dataset.this.new DatasetIterator();
            }

            public int size() {
                int count = 0;

                for(Attributes.Dataset.DatasetIterator iter = Dataset.this.new DatasetIterator(); iter.hasNext(); ++count) {
                }

                return count;
            }
        }
    }
}
