The previous README file is deleted by mistake. For properties of this repo check : https://www.sciencedirect.com/science/article/pii/S2352711023002650 

# Overview
WebCollectives is a repository for crawling and extracting web pages through template based matching. Source code contains necessary examples in sites folder.
The example use LookupPattern to define hierarchical regex patterns and HTTP GET or a Selenium API to download or link seed urls. 
WebFlow is the main container of a template and crawler. The results of the WebTemplate is LookupResult is a hierarchical XML structure. 
It contains the hierarchy defined by LookupPattern. The results of a template can be pipelined to another template in WebFlow object through addNext method. 
Moreover for self loops addExtraTemplate can be used. 



