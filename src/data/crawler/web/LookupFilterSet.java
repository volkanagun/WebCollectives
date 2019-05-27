package data.crawler.web;

import java.util.HashSet;
import java.util.Set;

public class LookupFilterSet extends LookupFilter{
    private Set<String> acceptSet = new HashSet<>();

    public LookupFilterSet(String[] setMembers) {
        for(String item:setMembers){
            acceptSet.add(item);
        }
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
