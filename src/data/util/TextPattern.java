/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wolf
 */
public class TextPattern {

    /////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Matching">


    public static int[] indexOfRegex(int from, String regex, String text, int nth) {
        int[] result = new int[]{-1, 0};
        for (int i = 0; i < nth; i++) {
            if (from != -1) {
                result = indexOfRegex(from, regex, text);
                from = result[0] + result[1];
            }
            else{
                break;
            }

        }
        return result;
    }

    public static int[] indexOfRegex(int from, int fromLength, String regex, String text, int nth) {
        int[] result = new int[]{from, fromLength};
        for (int i = 0; i < nth; i++) {
            if (from != -1) {
                result = indexOfRegex(from, regex, text);
                from = result[0] + result[1];
            }

        }
        return result;
    }


    public static int countOfRegexUntil(int from, int until, String regex, String text){
        int count = 0;
        int[] crrIndex = indexOfRegex(from, regex, text);
        int startIndex = crrIndex[0] + crrIndex[1];

        while(until >= startIndex && startIndex!=-1){
            crrIndex = indexOfRegex(startIndex, regex, text);
            startIndex = crrIndex[0] + crrIndex[1];
            count+=1;
        }

        return count;
    }

    public static int[] indexOfRegex(int from, String regex, String text) {
        int index = -1;
        int len = 0;
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (from < text.length() && from != -1 && matcher.find(from)) {
            index = matcher.start();
            len = matcher.group().length();
        }
        return new int[]{index, len};
    }

    public static int[] indexOfStartEnd(int index, String markerStart, String markerEnd, String text) {
        int[] endIndex = indexOfRegex(index, markerEnd, text);
        int subStartIndex = index;
        int subEndIndex = endIndex[0] + endIndex[1];
        int count = 0;
        while (subStartIndex != -1 && subEndIndex > subStartIndex) {
            int[] recStartIndex = indexOfRegex(subStartIndex, markerStart, text);

            subStartIndex = recStartIndex[0] + recStartIndex[1];

            count++;
        }
        if (index != -1) {
            endIndex = indexOfRegex(subEndIndex, markerEnd, text, count - 1);
        }

        if (endIndex[0] != -1 && index != -1) {
            return new int[]{index, endIndex[0] + endIndex[1]};
        } else {
            return new int[]{-1, 0};
        }
    }

    public static int[] indexOfStartEnd(int index, String start, String end, String markerStart, String markerEnd, String text) {
        int[] startIndex = indexOfRegex(index, start, text);
        int[] endIndex = indexOfRegex(startIndex[0], end, text);
        int[] markerStartIndex = startIndex;
        int[] markerEndIndex = endIndex;
        int count = 0;
        while (startIndex[0] != -1 && endIndex[0] != -1) {

            count = countOfRegexUntil(markerStartIndex[0] + markerStartIndex[1], markerEndIndex[0], markerStart, text);
            if (count > 0) {
                markerStartIndex = markerEndIndex;
                markerEndIndex = indexOfRegex(markerEndIndex[0] + markerEndIndex[1],0, markerEnd, text, count);
                //markerEndIndex = indexOfRegex(markerStartIndex[0] + markerStartIndex[1], markerEnd, text);
            }
            else{
                break;
            }
            /*count--;
            if(count > 0){
                markerStartIndex = indexOfRegex(markerEndIndex[0] + markerEndIndex[1], markerEnd, text, count);
                markerEndIndex = indexOfRegex(markerStartIndex[0] + markerStartIndex[1], markerEnd, text);
            }
            else if(count == 0){
                int startLook = markerEndIndex[0] + markerEndIndex[1];
                markerStartIndex = markerEndIndex;
                markerEndIndex = indexOfRegex(startLook, markerEnd, text);

            }*/
            endIndex = markerEndIndex;

        }

        return new int[]{startIndex[0] + startIndex[1], endIndex[0]};

    }

    public static int[] indexOfStartEnd(int from, String startRegex, String endRegex,
                                        String startMarker, String endMarker, String text, int nth) {
        int startIndex = -1;
        int endIndex = -1;
        int endLength = 0;
        int iindex = -1;
        int[] index = indexOfRegex(from, startRegex, text, nth);
        startIndex = index[0] + index[1];
        endIndex = startIndex;
        iindex = startIndex;
        boolean found = false;

        if (startIndex == -1) {
            return new int[]{startIndex, endIndex, endLength};
        }

        int endMarkerIndex = -1;
        int endMarkerLength = 0;

        if (startMarker != null & endMarker != null) {

            Pattern startPattern = Pattern.compile(startMarker);
            Matcher startMatcher = startPattern.matcher(text);
            Pattern endPattern = Pattern.compile(endMarker);
            Matcher endMatcher = endPattern.matcher(text);

            iindex = startIndex;

            Stack<Integer> stackPosition = new Stack<>();
            boolean popMode = false;

            endMarkerIndex = endMatcher.find(iindex) ? endMatcher.end() : -1;
            if (endMarkerIndex == -1) {

                return new int[]{startIndex, text.length(), 0};
            }
            endMarkerLength = endMatcher.group().length();
            int markerIndex = -1;

            //Open  Marker Tags to push
            while (true) {
                if (!popMode) {
                    markerIndex = startMatcher.find(iindex) ? startMatcher.end() : -1;
                    if (markerIndex > endMarkerIndex) {
                        iindex = endMarkerIndex;
                        popMode = true;
                    } else if (markerIndex != -1) {
                        stackPosition.push(markerIndex);
                        iindex = markerIndex;
                    } else {
                        break;
                    }
                } else {
                    markerIndex = endMatcher.find(iindex) ? endMatcher.end() : -1;
                    if (markerIndex != -1 && !stackPosition.isEmpty()) {

                        stackPosition.pop();
                        endMarkerIndex = markerIndex;
                        endIndex = endMarkerIndex;
                        endMarkerLength = endMatcher.group().length();

                        popMode = false;

                    } else if (stackPosition.isEmpty()) {
                        found = true;

                        break;
                    } else {
                        break;
                    }
                }
            }

        }

        if (!found) {
            index = indexOfRegex(endIndex, endRegex, text);
            endIndex = index[0];
            endLength = index[1];
        } else {
            endIndex = endMarkerIndex - endMarkerLength;
            endLength = endMarkerLength;
        }
        return new int[]{startIndex, endIndex, endLength};
    }

    public static int[] indexOfFirst(int from, String regex, String text, StringBuffer buffer) {
        int index = -1;
        int len = 0;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find(from)) {
            index = matcher.start();
            len = matcher.group().length();
            buffer.append(matcher.group());
        }
        return new int[]{index, len};
    }

    public static int[] indexOfLast(int from, String regex, String text, StringBuffer buffer) {
        int index = -1;
        int len = 0;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        String group = "";
        while (matcher.find(from)) {

            index = matcher.start();
            len = matcher.group().length();
            group = matcher.group();
            from = ++index;
        }

        buffer.append(group);
        return new int[]{index, len};
    }

    public static boolean matchesList(List<String> regexList, String group) {
        boolean isMatch = false;

        for (String regex : regexList) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(group);
            if (matcher.find()) {
                isMatch = true;
                break;
            }
        }

        return isMatch;
    }

    public static boolean containsList(List<String> charseqList, String group) {
        boolean isMatch = false;
        for (String charSeq : charseqList) {
            if (group.contains(charSeq)) {
                isMatch = true;
                break;
            }
        }

        return isMatch;
    }

    public static boolean containsRegex(String text, String regex) {
        boolean isMatch = false;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        isMatch = matcher.find();

        return isMatch;

    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Cleaning">
    private String clearUnwantedPattern(String text, String regex) {
        if (text == null) {
            return "";
        } else {

            String result = text.replaceAll(regex, "");
            return result.trim();
        }
    }

    public static String clearAllTags(String text) {
        String regex = "<((.|\n|\r)*?)>";
        return text.replaceAll(regex, "");
    }

    public static String clearExtra(String text) {
        String regexSpace = "\\s+";
        String regexLine = "\\n+";
        String regexReturn = "\\r+";

        text = text.replaceAll(regexLine, " ");
        text = text.replaceAll(regexReturn, " ");
        text = text.replaceAll(regexSpace, " ");

        return text;
    }

    public static String clearAllTagsWithExtra(String text) {
        String result = clearAllTags(text);
        return clearExtra(result).trim();
    }

    public static String clearTagsExcept(List<String> exceptTags, String text) {

        try {
            String regex = "<(.|\n|\r)*?>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            List<String> replaceList = new LinkedList<String>();
            while (matcher.find()) {
                String group = matcher.group();
                if (!matchesList(exceptTags, group)) {
                    replaceList.add(group);
                }
            }

            for (String replace : replaceList) {
                text = text.replaceFirst(replace, "");
            }
        } catch (Exception ex) {
        }

        return text;
    }

    public static String clearTagsBetween(String startRegex, String endRegex, String text) {
        StringBuffer startBuffer = new StringBuffer();
        StringBuffer endBuffer = new StringBuffer();

        int[] startIndex = indexOfFirst(0, startRegex, text, startBuffer);
        int[] endIndex = indexOfLast(0, endRegex, text, endBuffer);

        if (startIndex[0] > -1 && endIndex[0] > -1) {

            String innerText = text.substring(startIndex[0] + startIndex[1], endIndex[0]);
            innerText = clearAllTags(innerText);
            String beforeText = text.substring(0, startIndex[0]);
            String afterText = text.substring(endIndex[0] + endIndex[1]);
            String newText = beforeText + "\n"
                    + startBuffer + innerText + "\n"
                    + endBuffer + "\n" + afterText;
            return newText;
        } else {
            return text;
        }
    }

    public static String clearRange(String startRegex, String endRegex, String text) {
        StringBuffer startBuffer = new StringBuffer();
        StringBuffer endBuffer = new StringBuffer();

        int[] startIndex = indexOfFirst(0, startRegex, text, startBuffer);
        int[] endIndex = indexOfLast(0, endRegex, text, endBuffer);

        if (startIndex[0] > -1 && endIndex[0] > -1) {
            String untilFirst = text.substring(0, startIndex[0]);
            String afterLast = text.substring(endIndex[0] + endIndex[1] - 1);
            return untilFirst + afterLast;
        } else {
            return text;
        }

    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Removing">
    public static String removeBetween(String regexStart, String regexEnd, String text) {

        int[] start = indexOfFirst(0, regexStart, text, new StringBuffer());
        int[] end = indexOfFirst(0, regexEnd, text, new StringBuffer());

        if (start[0] > -1) {

            String newText = text.substring(0, start[0]) + " " + text.substring(end[0] + end[1]);
            return newText;
        }

        return text;
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Obtaining Patterns">
    public static String replaceAllWith(String text, String[] correction) {
        Pattern pattern = Pattern.compile(correction[0]);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(correction[1]);
    }

    public static String replaceAllWith(String text, List<String[]> corrections) {
        String result = text;
        for (String[] correction : corrections) {
            result = replaceAllWith(result, correction);
        }
        return result.trim();
    }

    public static String extractRestAfterLast(String regex, int groupID, String text) {
        int[] index = indexOfLast(0, regex, text, new StringBuffer(text));

        if (index[0] > 0 && index[0] + index[1] < text.length()) {
            return text.substring(index[0] + index[1]).trim();
        } else {
            return "";
        }
    }

    public static String obtainTillFirst(int from, String regex, String text) {
        int[] index = indexOfFirst(from, regex, text, new StringBuffer());
        if (index[0] == -1) {
            return null;
        } else {
            return text.substring(0, index[0]);
        }
    }

    public static String obtainFirstPattern(String regex, int groupID, boolean clearTags, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        String group = null;
        if (matcher.find()) {
            group = matcher.group(groupID);
            if (clearTags) {
                group = clearAllTags(group);
            }

        }

        if (group == null) {
            return "";
        } else {
            return clearExtra(group).trim();
        }
    }

    public static String obtainFirstPattern(String regex, int[] groupIDs, boolean forceAll, String combineDelimiter, String text) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        String finalGroup = "";
        if (matcher.find()) {

            for (int groupID : groupIDs) {
                String group = matcher.group(groupID);
                if (forceAll) {
                    if (group == null) {
                        return "";
                    } else {
                        finalGroup += combineDelimiter + group;
                    }
                }
            }

        }

        if (finalGroup.isEmpty()) {
            return "";
        } else {
            return clearExtra(finalGroup.substring(1)).trim();
        }
    }

    public static List<String> obtainPatterns(Pattern pattern, String text, int group) {

        List<String> patternList = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String groupString = matcher.group(group);
            patternList.add(groupString);
        }

        return patternList;

    }

    public static List<String[]> obtainPatterns(Pattern pattern, String text, int[] groups) {

        List<String[]> patternList = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String[] values = new String[groups.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = matcher.group(groups[i]);
            }

            patternList.add(values);
        }

        return patternList;

    }

    public static int countPattern(String pattern, String text){
        int si=0;
        int fi = 0;
        int count = 0;
        while(si > 0){
            si = text.indexOf(pattern, fi);
            fi = si + pattern.length();
            count++;
        }

        return count;
    }

    public static int stateIndex(int maxCount, String pattern, String text){
        int si=0;
        int fi = 0;
        int count= 0;
        while(si > 0 && count < maxCount){
            si = text.indexOf(pattern, fi);
            fi = si + pattern.length();
            count++;
        }

        return fi;
    }

    public static void containPatterns(String regex,String startRegex, String endRegex, int groupID, String text, List<String> patternList) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            int mstart = matcher.start();
            int mend = matcher.end();
            String rest = text.substring(mend);
            String group = matcher.group(groupID);
            patternList.add(clearExtra(group).trim());
        }
    }

    public static void obtainPatterns(String regex, int groupID, boolean clearTags, String text, List<String> patternList) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(groupID);
            if (clearTags) {
                group = clearAllTags(group);
            }
            patternList.add(clearExtra(group).trim());
        }
    }

    public static void obtainPatterns(String regex, int[] groupIDs, boolean clearTags, String text, List<String[]> patternList) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String[] result = new String[groupIDs.length];
            for (int i = 0; i < groupIDs.length; i++) {
                String group = matcher.group(groupIDs[i]);
                if (clearTags && group != null) {
                    group = clearAllTags(group);
                }

                if (group != null) {
                    group = clearExtra(group);
                }

                result[i] = group;

            }
            patternList.add(result);
        }
    }

    public static void obtainPatterns(String regex, int groupID, boolean clearTags, String text, Map<Integer[], String> patternList) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(groupID);
            Integer[] region = new Integer[]{matcher.start(groupID), matcher.end(groupID)};
            if (clearTags) {
                group = clearAllTags(group);
            }
            patternList.put(region, clearExtra(group).trim());
        }
    }

    public static String obtainPatterns(String start, String end, boolean clearTags, String text) {
        int[] theIndex = indexOfRegex(0, start, text);
        int endIndex;
        int startIndex = theIndex[0];
        int length = theIndex[1];
        String result = "";
        while (startIndex != -1) {

            theIndex = indexOfRegex(startIndex, end, text);
            endIndex = theIndex[0];

            if (endIndex != -1) {
                String group = text.substring(startIndex + length, endIndex);
                if (clearTags) {
                    group = clearAllTags(group);
                }

                result += group;

            } else {

                endIndex = text.length();
            }

            theIndex = indexOfRegex(endIndex, start, text);

            startIndex = theIndex[0];
            length = theIndex[1];
        }

        return result;
    }

    public static String obtainFirstPattern(String start, String end, boolean clearTags, String text) {
        int[] theIndex = indexOfRegex(0, start, text);
        int endIndex;
        int startIndex = theIndex[0];
        int length = theIndex[1];
        String result = "";
        if (startIndex != -1) {

            theIndex = indexOfRegex(startIndex, end, text);
            endIndex = theIndex[0];

            if (endIndex != -1) {
                String group = text.substring(startIndex + length, endIndex);
                if (clearTags) {
                    group = clearAllTags(group);
                }

                result += group;

            } else {

                endIndex = text.length();
            }

            theIndex = indexOfRegex(endIndex, start, text);

        }

        return result;
    }

    public static void obtainPatterns(String start, String end, boolean clearTags, String text, Map<Integer[], String> patternMap) {
        int[] theIndex = indexOfRegex(0, start, text);
        int endIndex;
        int startIndex = theIndex[0];
        int length = theIndex[1];
        while (startIndex != -1) {

            theIndex = indexOfRegex(startIndex, end, text);
            endIndex = theIndex[0];

            if (endIndex != -1) {
                String group = text.substring(startIndex + length, endIndex);
                if (clearTags) {
                    group = clearAllTags(group);
                }
                patternMap.put(new Integer[]{startIndex + length, endIndex}, group.trim());

            } else {

                endIndex = text.length();
            }

            theIndex = indexOfRegex(endIndex, start, text);

            startIndex = theIndex[0];
            length = theIndex[1];
        }
    }

    public static void obtainPatterns(String start, String end, boolean clearTags, String text, List<String> patternList) {
        int[] theIndex = indexOfRegex(0, start, text);
        int endIndex;
        int startIndex = theIndex[0];
        int length = theIndex[1];
        while (startIndex != -1) {

            theIndex = indexOfRegex(startIndex + length, end, text);
            endIndex = theIndex[0];

            if (endIndex != -1) {

                if (startIndex + length < endIndex) {
                    String group = text.substring(startIndex + length, endIndex);
                    if (clearTags) {
                        group = clearAllTags(group);
                    }

                    patternList.add(group.trim());
                } else {
                    int debug = 0;
                }
            } else {
                endIndex = text.length();
            }

            theIndex = indexOfRegex(endIndex, start, text);
            startIndex = theIndex[0];
            length = theIndex[1];

        }
    }

    public static void obtainPatterns(String start, String end, String startMarker, String endMarker, String text, List<String> patternList) {

        int[] theIndex = indexOfStartEnd(0, start, end, startMarker, endMarker, text);
        int startIndex = theIndex[0];
        int endIndex = theIndex[1];

        while (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            String subText = text.substring(startIndex, endIndex);
            patternList.add(subText);
            startIndex = endIndex;
            theIndex = indexOfStartEnd(startIndex, start, end, startMarker, endMarker, text);
            startIndex = theIndex[0];
            endIndex = theIndex[1];
        }
    }

    public static void obtainPatterns(String start, String end, boolean clearTags, String clearPatterns, String text, List<String> patternList) {
        int[] theIndex = indexOfRegex(0, start, text);
        int endIndex;
        int startIndex = theIndex[0];
        int length = theIndex[1];
        while (startIndex != -1) {

            theIndex = indexOfRegex(startIndex, end, text);
            endIndex = theIndex[0];

            if (endIndex != -1) {

                String group = text.substring(startIndex + length, endIndex).replaceAll(clearPatterns, "");
                if (clearTags) {
                    group = clearAllTags(group);
                }

                patternList.add(group.trim());

            } else {
                endIndex = text.length();
            }

            theIndex = indexOfRegex(endIndex, start, text);
            startIndex = theIndex[0];
            length = theIndex[1];
        }
    }

    public static String[] obtainFirstPatternsByCheck(Pattern pattern, String text, String[] checks, int checkGroup, int getGroup) {
        Matcher matcher = pattern.matcher(text);
        String[] values = new String[checks.length];

        while (matcher.find()) {
            String property = matcher.group(checkGroup);
            String value = matcher.group(getGroup);

            for (int i = 0; i < checks.length; i++) {
                if (property.equals(checks[i])) {
                    values[i] = value;
                    break;
                }
            }
        }
        return values;
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Substring Building">
    public static String buildFromSubstring(String input, List<Integer> start, List<Integer> end, List<String[]> markers) {
        String outString = "";

        int length = input.length();

        for (int i = 0; i < length; i++) {
            int sIndex = start.indexOf(i);
            int eIndex = end.indexOf(i);
            String putMarker = "";
            if (eIndex != -1) {
                putMarker = markers.get(eIndex)[1];
                outString += putMarker + input.charAt(i);
            }

            if (sIndex != -1) {
                putMarker = markers.get(sIndex)[0];
                outString += putMarker + input.charAt(i);
            }

            if (eIndex == -1 && sIndex == -1) {
                outString += input.charAt(i);
            }

        }

        return outString;
    }

    public static String substring(String input, int start, int end) {

        if (start < 0) {
            return null;
        } else if (end < 0) {
            int mstart = input.length() + end;
            return input.substring(mstart);
        } else {
            return input.substring(start, end);
        }
    }

    //</editor-fold>
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Correct For Turkish">
    public static String correctTurkishCharacters(String text) {
        String resultText = text;

        resultText = resultText.replaceAll("û", "u");
        resultText = resultText.replaceAll("â", "a");
        resultText = resultText.replaceAll("î", "ı");
        resultText = resultText.replaceAll("Û", "U");
        resultText = resultText.replaceAll("Î", "I");
        resultText = resultText.replaceAll("Â", "A");
        resultText = resultText.replaceAll("ý", "ı");
        resultText = resultText.replaceAll("Ý", "İ");
        resultText = resultText.replaceAll("ð", "ğ");

        return resultText;

    }
    //</editor-fold>
    ///////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Combinatorial Production">
    public static class ProduceThread implements Callable<Set<String>> {

        private Set<String> results;
        private final String[] text;
        private final int limit;

        public ProduceThread(String[] text, int limit) {

            this.text = text;
            this.limit = limit;
        }

        @Override
        public Set<String> call() throws Exception {
            results = new HashSet<>();
            produceLimitBy(results, text, limit);
            return results;
        }
    }

    public static void sleep(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(TextPattern.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void produceLimitBy(Set<String> results, List<String[]> texts, int limit) {
        List<ProduceThread> tasks = new ArrayList<>();
        for (String[] text : texts) {
            ProduceThread task = new ProduceThread(text, limit);
            tasks.add(task);
        }

        ExecutorService service = Executors.newCachedThreadPool();
        List<Future<Set<String>>> result = null;
        try {
            result = service.invokeAll(tasks);
        } catch (InterruptedException ex) {
            Logger.getLogger(TextPattern.class.getName()).log(Level.SEVERE, null, ex);
        }

        service.shutdown();

        while (!service.isTerminated()) {
            sleep(1000);
        }

        for (Future<Set<String>> item : result) {
            try {
                if (item.isDone()) {
                    results.addAll(item.get());
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(TextPattern.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void produceLimitBy(List<String> results, String[] text, int limit) {

        int i;
        for (i = 0; i < text.length - limit; i++) {
            String current = text[i];
            String combined = "";

            results.add(current);

            for (int j = i; j <= limit; j++) {
                combined += text[j];
                results.add(combined);
            }

        }

        for (; i < text.length; i++) {
            String combined = text[i];
            results.add(combined);
            for (int j = i + 1; j < text.length; j++) {
                combined += " " + text[j];
                results.add(combined.trim());
            }
        }
    }

    public static void produceLimitBy(Set<String> results, String[] text, int limit) {

        int i;
        for (i = 0; i < text.length - limit; i++) {
            String current = text[i];
            String combined = "";

            results.add(current);

            for (int j = i; j <= limit; j++) {
                combined += text[j];
                results.add(combined);
            }

        }

        for (; i < text.length; i++) {
            String combined = text[i];
            results.add(combined);
            for (int j = i + 1; j < text.length; j++) {
                combined += " " + text[j];
                results.add(combined.trim());
            }
        }
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    public static void main(String[] args) {
        String text = "<open>" +
                "<open id=5>" + "<open></close><open></close>" + "</close>" +
                "<open id=5>" + "<open></close>" + "</close>" +
                "<open></close><open></close>" +
                "<open id=5>" + "<open></close>" + "</close>"
                + "</close><open><close>";

        List<String> obtainedList = new ArrayList<>();
        obtainPatterns("<open\\sid\\=5>", "</close>", "<open>", "</close>", text, obtainedList);
    }
}
