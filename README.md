### WebCrawler
基于WebMagic框架开发的网络爬虫,爬取[这个网站](http://www.mzitu.com/)的图片,并存储到本地文件系统.

#### 开发过程中的问题
+ 图片的下载      
发现使用WebMagic的Pipeline无法顺利下载文件,看了源代码,发现是直接把PageProcess的ResultItem中的内容做的存储,但是又无法直接获得Response的实体.于是选择了使用HttpClient进行图片的下载,可能以后会优化.

+ 网页链接的获取与下载       
发现WebMagic是把所有要下载的链接加入到了一个数组中,本想要直接获得一个网页中的图片然后直接下载,发现好像不太可行,于是把所有的链接加入到待爬取数组中,然后用正则表达式分别判断.

以上的问题可能是自己不太熟悉WebMagic和Java所致. (/(ㄒoㄒ)/~~)


#### FAQ
*为什么使用Java而不用Python*?        
**实践理论知识**       
*为什么爬取这个网站的图片*?       
**反爬虫机制简单,而且可以方便地提取链接**      

#### License
**MIT**

