package data.crawler.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WebDateGenerator extends WebSuffixGenerator{

    private String dateFormat;
    private Date startDate, endDate;
    private SimpleDateFormat formatting;
    public WebDateGenerator(String dateFormat, Date startDate, Date endDate) {
        this.dateFormat = dateFormat;
        this.startDate = startDate;
        this.endDate = endDate;
        this.formatting = new SimpleDateFormat(dateFormat);
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
           String formattedDate = formatting.format(currentDate);
           String seed = url + formattedDate;
           seeds.add(seed);
           currentDate = increment(currentDate);
        }

        return seeds;
    }
}
