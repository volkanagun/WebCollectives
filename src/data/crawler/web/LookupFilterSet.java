package data.crawler.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LookupFilterSet extends LookupFilter{
    private final Set<String> acceptSet = new HashSet<>();

    public LookupFilterSet(String[] setMembers) {
        Collections.addAll(acceptSet, setMembers);
    }

    @Override
    public String accept(String text) {
        for(String item:acceptSet){
            if(text.contains(item)) {
                return item;
            }
        }

        return null;
    }
}
