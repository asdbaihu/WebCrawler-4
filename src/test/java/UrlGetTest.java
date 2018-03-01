import cn.mrsiz.WebCrawler.MztPageProcessor;
import us.codecraft.webmagic.Spider;

public class UrlGetTest {
    public static void main(String[] args) {
        Spider.create(new MztPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.mzitu.com/page/1")
//                .addUrl("http://i.meizitu.net/2018/02/28c09.jpg")
                //开启5个线程抓取
                .thread(8)
                //启动爬虫
                .run();
    }
}
