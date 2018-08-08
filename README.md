# WebCrawler

<li> Use Jsoup to parse the URLs and use Jsoup selector to find all 'a' tags with "href" attribute;

Crawl with multiple threads;

Save visited URLs into Neo4j, a graph database, so we can create a complte graph of a website;

Use Spring Data to interacti with Neo4j;

Use Spring Boot to wireup;

Use Mockito library and PowerMock library for unit test;

Use Java8 Predicates;


A Few Factors When Building a Web Crawler

# 1 – Basic solution
A basic web crawler can work like this:

1. Start with a URL pool that contains all the websites we want to crawl.
2. For each URL, issue a HTTP GET request to fetch the web page content.
3. Parse the content (usually HTML) and extract potential URLs that we want to crawl.
4. Add new URLs to the pool and keep crawling.
It depends on the specific problem, sometimes we may have a separate system that generates URLs to crawl. For instance, a program can keep listening to RSS feeds and for every new article, it can add the URL into the crawling pool. 

# 2 – Crawling frequency
How often will you crawl a website?

One approach is to follow the robot.txt of each site. robot.txt is a standard used by websites to communicate with web crawlers. It can specify things like what files should not be crawled and most web crawlers will follow the configuration.

# 3 – Remove duplicate URLs
In a distributed web crawler, how can we remove duplicate URLS?

One common approach is to use Bloom Filter. In a nutshell, a bloom filter is a space-efficient system that allows you to test if an element is in a set. However, it may have false positive. In other words, if a bloom filter can tell you either a URL is definitely not in the pool or it probably in the pool.

To briefly explain how bloom filter works, an empty bloom filter is a bit array of m bits (all 0). There are also k hash functions that map each element to one of the m bits. So when we add a new element (URL) into the bloom filter, we will get k bits from the hash functions and set all of them to 1. Thus, when we check the existence of an element, we first get the k bits for it and if any of them is not 1, we know immediately that the element doesn’t exist. However, if all of the k bits are 1, this can come from the combination of several other elements.


# 4 – Parsing
When parsing the data (usually HTML) to extract the information we care about, there might be many chanllenges. 

For instance, you may need to handle encode/decode issue when the HTML contains non-unicode characters. In addition, when the web page contains images, videos or even PDF, it can also cause weird behaviors.

In addition, some web pages are all rendered through Javascript like using AngularJS, your crawler may not be able
to get any content at all.

We need tons of robustness tests to make sure that it can work as expected. 

# 5 - detect loops. 
Many websites contain links like A→B->C->A and your crawler may end up running forever. Think about how to fix this?

We can maintain a set which contains visited urls, and only visit urls which are not contained in this set.

Another problem is DNS lookup. When the system get scaled to certain level, DNS lookup can be a bottleneck and you may build your own DNS server.

# [Reference]
http://blog.gainlo.co/index.php/2016/06/29/build-web-crawler/

