package data.crawler.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WebTimeGenerator extends WebSuffixGenerator{
    private String suffix;
    private Date startDate, endDate;

    public WebTimeGenerator(String suffix, Date startDate, Date endDate) {

        this.suffix = suffix;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private Date increment(Date currentDate){
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    @Override
    public List<String> apply(String url) {
        List<String> seeds = new ArrayList<>();
        Date currentDate = startDate;
        while(currentDate.before(endDate)){
            String formattedDate = String.valueOf(currentDate.getTime());
            String seed = url + suffix+ formattedDate;
            seeds.add(seed);
            currentDate = increment(currentDate);
        }

        return seeds;
    }
}
