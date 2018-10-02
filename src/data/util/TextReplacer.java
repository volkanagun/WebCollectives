/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wolf
 */
public final class TextReplacer implements Serializable {
    private List<String[]> replaceList;

    public TextReplacer() {
        replaceList = new LinkedList<>();
    }

    public TextReplacer(List<String[]> replaceList) {
        this.replaceList = replaceList;
    }

    public TextReplacer(String[] replacements) {
        this();
        addReplacements(replacements);
    }

    public void addReplacement(String[] replacement) {
        replaceList.add(replacement);
    }
    public void addReplacements(String[] replacements) {if (replacements.length % 2 == 0) {for (int i = 0; i < replacements.length - 1; i += 2) {replaceList.add(new String[]{replacements[i], replacements[i + 1]});}}}
    public String rReplaceAll(String text) {String mtext = text;for (String[] replace : replaceList) {mtext = mtext.replaceAll(replace[0], replace[1]);}return mtext;}
}