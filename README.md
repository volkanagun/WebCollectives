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
template.setNextPageStart(2);
template.setNextPageSize(5);
```

Each template can be linked by addNext method. This method must state the label that holds the links to be followed by the next template.

Each template contains a LookupPattern. Lookup pattern is a hierarchical  



    


