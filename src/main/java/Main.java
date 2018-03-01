import cn.mrsiz.WebCrawler.MztPageProcessor;
import us.codecraft.webmagic.Spider;

public class Main {
    public static void main(String[] args) {
        Spider.create(new MztPageProcessor())
                .addUrl("http://www.mzitu.com/page/2")
//                .addUrl("http://i.meizitu.net/2018/02/28c09.jpg")
                //开启8个线程抓取
                .thread(8)
                //启动爬虫
                .run();
    }
}
