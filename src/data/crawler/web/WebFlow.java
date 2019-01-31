package data.crawler.web;

import data.crawler.sites.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wolf on 02.07.2015.
 */
public class WebFlow implements Serializable, Callable<Boolean> {
    private String folder;
    private WebTemplate mainTemplate;

    public WebFlow(WebTemplate mainTemplate) {
        this.mainTemplate = mainTemplate;
        this.folder = mainTemplate.getFolder();

    }

    public WebFlow(WebTemplate mainTemplate, String folder) {
        this.mainTemplate = mainTemplate;
        this.folder = folder;
        this.mainTemplate.setFolder(folder + mainTemplate.getFolder());
    }

    public WebFlow setMainLookComplete(boolean lookComplete){
        mainTemplate.setLookComplete(lookComplete);
        return this;
    }

    public WebFlow setMainDirectory(String directory){
        mainTemplate.setFolder(directory);
        return this;
    }

    @Override
    public Boolean call() throws Exception {
        execute();
        return true;
    }

    public WebDocument execute() {
        //Download template
        //Extract patterns
        //Pass seedlist to next template as urls if it is a url
        WebDocument mainDocument = mainTemplate.execute();

        return mainDocument;
    }



    public static void submit(ExecutorService service, final WebFlow flow) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                flow.execute();
            }
        });
    }

    public static void batchSubmit(ExecutorService service, List<WebFlow> webFlows) {
        try {
            service.invokeAll(webFlows);
            service.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(5);

        WebFlow flowWebrazzi = Webrazzi.build();
        WebFlow flowBesHarfliler = BesHarfliler.build();
        WebFlow flowGezenler = GezenlerKulubu.build();
        WebFlow flowHayrola = Hayrola.build();
        WebFlow flowSonBirseyler = SonBirseyler.build();
        WebFlow flowEgonomic = Ergonomic.build();
        WebFlow flowAcikEconomy = AcikEkonomi.build();
        WebFlow flowAvaz = AvazAvaz.build();
        WebFlow flowMilliyet = MilliyetBlog.build();
        WebFlow flowMilliyetBlogListe = MilliyetBlogList.build();
        WebFlow flowGazeteOku = GazeteOku.build();

        WebFlow panLargeTest = PAN.buildForPAN2011LargeTest();
        WebFlow panSmallTest = PAN.buildForPAN2011SmallTest();
        WebFlow panSmallTrain = PAN.buildForPAN2011SmallTrain();
        WebFlow flowBoingBlogList = BoingBlogList.build();
        WebFlow flowMashableBlogList = MashableList.build();
        WebFlow flowEngadetList = EngadetList.build();
        WebFlow flowHuffingtonList = HuffingtonPost.build();


        /*submit(service, panSmallTest);
        submit(service, panSmallTrain);*/

        //submit(service,flowMilliyetBlogListe);
        //submit(service, flowMilliyetBlogListe);
        batchSubmit(service, Arrays.asList(flowMilliyetBlogListe, flowBoingBlogList, flowMashableBlogList, flowEngadetList, flowHuffingtonList));
        //submit(service,flowHuffingtonList);
        //submit(service,flowBoingBlogList);
        //submit(service,flowBesHarfliler);
        //submit(service,flowGezenler);
        //submit(service, flowHayrola);
        //submit(service,flowEgonomic);
        //submit(service, flowAcikEconomy);
        ///submit(service, flowAvaz);
        //submit(service, flowMilliyet);
        //submit(service, flowSonBirseyler);
        //submit(service, flowGazeteOku);

        //submit(service, flowMilliyetBlogListe);
        service.shutdown();


        /*List<WebFlow> flows = Arrays.asList(
                //flowWebrazzi,
                //flowBesHarfliler,
                //flowGezenler,
                //flowHayrola,
                //flowSonBirseyler,
                //flowEgonomic,
                //flowAvaz,
                //flowMilliyet,
                flowGazeteOku);*/

        //batchSubmit(service, flows);

        /*WebFlow flowTrain = buildForPAN2011Small();
        flowTrain.execute();*/

        /*WebFlow flowTest = buildForPAN2011LargeTest();
        flowTest.execute();

        WebFlow flowTrain = buildForPAN2011LargeTrain();
        flowTrain.execute();*/

       /* WebFlow flowGazeteOku = buildGazeteBatchFlow();
        flowGazeteOku.execute();*/

    }

}
