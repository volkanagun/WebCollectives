# WebCollectives

Web collectives is a web content extraction and crawler libray focused on simplicity and ease of use. The majoirty of the code is written in Java 11 syntax, the inference package contains Scala codes.

The crawler is integrated into the content extraction process. The user specify the rules to extract the links and later the rule templates are used to
extract structured XML document from the HTML content without using DOM. Each domain has its own set of rules. These rules can be extracted
through statistical inference without any supervision. In terms of simlicity, the tool uses file system for storing the contents, and 
it can not be compared with large scale models such as Nutch, Lucene or Solr. The WebCollectives library can be integrated as a data gathering process for different tools. It depends on Scala 2.11, Apache FluentHC, HTTPClient, Lucene and JSON4s libraries.   

# Directory Structure

The base and core libraries are located in web folder.

- data
  - crawler
    -web

The examples are located at sites folder. Each example contains a main method and runs separetely. 

- data
  -crawler
    -sites

Other than codes a resources folder is required to run the program. The required folders in resources folder are as follows.

- resources
  - selenium
  - htmls

Selenium is not required for all examples. But some examples traverse the web domain through javascript calls. These repeated calls are necessary in 
navigating through pages and finding the necessary HTML content. Selenium must be compatible to either chrome or firefox depends on the example. It sets  firefox by doFirefox(true) method in some examples otherwise the Chrome compatible version is called. 


# Quick Examples

WebTemplate sets the content directory, seeds and the domain. Seets are the url links for downloading html content. The seeds can be generated from a baseline seed by setPageSuffix method. This method adds new urls through generating the main seed in a predefined boundary.

```java
WebTemplate template = new WebTemplate("resources/blogs/", "blog-links", "mashable.com");
template.addSeed("https://mashable.com/review");
template.setNextPageSuffix("?page=");
template.setNextPageStart(3);
template.setNextPageSize(2);
```

In the above examples the following urls are generated and traversed by the template
- https://mashable.com/review?page=3
- https://mashable.com/review?page=4

Each template contains a regex tree pattern. The regex tree pattern extracts and labels all the sub-patterns that it contains. The subpatterns are defined as a tree structure and should not contain recursions. 

```java
LookupPattern linkPattern = new LookupPattern(LookupOptions.URL, LookupOptions.CONTAINER, "<div class="justify-center(.*?)>","</div>")
                  .setStartEndMarker("<div","</div>")
                  .add(new LookupPattern(LookupOptions.URL, LookupOptions.ARTICLELINK, "<a class="block(.*?)href=\"","\""));

template.setMainPattern(linkPattern);
```
In the above code block each pattern states the starting and ending boundaries. In this respect, the pattern given above has the `<div` start marker. The pattern captures the first starting point of the <div class=justify-center block and inside the element it searchers all the `<a class=block` patterns. The text content of this block contains all the links.     

Each template can be linked by addNext method. This method must state the label that holds the links to be followed by the next template.

```java
template.addNext(articleTemplate, LookupOptions.ARTICLELINK);

```
In the above code block addNext methods forwards the extracted template content which is labelled by the ARTICLELINK String to the articleTemplate. All the links are downleded and the HTML content is forwareded to the articleTemplate. The lookup pattern of the articleTemplate is used to extract the new content.

In the final stage, a WebFlow object is generated and executed by defining the starting template.

```java
WebFlow webFlow = new WebFlow(template);
webFlow.execute();
```




    


