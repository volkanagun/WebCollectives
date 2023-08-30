package data.crawler.web;

public class WebPageLengthFilter extends WebPageFilter{
    private int length;

    public WebPageLengthFilter(int length) {
        this.length = length;
    }

    @Override
    public boolean isOk(String html) {
        int newLength = html.length();
        if(newLength > length){
            length = length + newLength;
            return true;
        }
        else{
            return false;
        }
    }

}
