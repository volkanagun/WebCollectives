package data.crawler.web;

import data.crawler.sites.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wolf on 02.07.2015.
 */
public class WebFlow implements Serializable, Callable<Boolean> {
    private String folder;
    private WebTemplate mainTemplate;

    public WebFlow() {
    }

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

    public WebFlow setMainTemplate(WebTemplate mainTemplate) {
        this.mainTemplate = mainTemplate;
        return this;
    }

    public String getFolder() {
        return folder;
    }

    public WebTemplate getMainTemplate() {
        return mainTemplate;
    }

    @Override
    public Boolean call() throws Exception {
        execute();
        return true;
    }



    public void doDeleteStart(int maxDepth){

        List<String> deleteFilenamePatterns = new ArrayList<>();
        WebTemplate crrTemplate = mainTemplate;
        int depth = 0;

        while(crrTemplate!=null && depth < maxDepth){

            depth++;

            if(crrTemplate.getDoDeleteStart()){
                deleteDocuments(crrTemplate);
            }

            Iterator<String> nextLinks = crrTemplate.getNextIterator();
            if(nextLinks.hasNext()) crrTemplate = crrTemplate.getNextMap(nextLinks.next());
            else crrTemplate = null;
        }


    }

    public void deleteDocuments(WebTemplate webTemplate){
        String pattern = webTemplate.getName();
        String folder = webTemplate.getFolder();
        File folderFile =  new File(folder);
        if(folderFile.exists()) {
            File[] deleteFiles = folderFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(pattern);
                }
            });

            for (File f : deleteFiles) f.delete();
        }
    }

    public WebDocument execute() {


        //doDeleteStart(2);
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

    public static void waiting(ExecutorService service){
        while(!service.isTerminated() && !service.isShutdown()){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void batchSubmit(ExecutorService service, List<WebFlow> webFlows) {
        try {
            service.invokeAll(webFlows);
            service.shutdown();
            waiting(service);
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
        WebFlow flowBoingBlogList = BoingBlogList.build(LookupOptions.BLOGENGDIRECTORY);
        WebFlow flowMashableBlogList = MashableList.build(LookupOptions.BLOGENGDIRECTORY);
        WebFlow flowEngadetList = EngadetList.build(LookupOptions.BLOGENGDIRECTORY);
        WebFlow flowHuffingtonList = HuffingtonPost.build(LookupOptions.BLOGENGDIRECTORY);


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
