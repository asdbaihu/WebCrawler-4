package cn.mrsiz.WebCrawler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import javax.xml.ws.spi.http.HttpContext;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MztPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(5).setSleepTime(1000);

    //匹配图片专辑列表的正则表达式
    public static final String URL_LIST = "http://www\\.mzitu\\.com/page/\\d";
    //匹配图片专辑的表达式,由这个连接获得具体的1~n张图的连接
    public static final String URL_PIC = "http://www\\.mzitu\\.com/\\d";
    //所要下载的图片的连接
    public static final String URL_CONT = "http://www\\.mzitu\\.com/\\d+/\\d";

    public Site getSite() {
        return site;
    }

    public void process(Page page) {

            //要从这里获得下载图片来源链接的链接
            if(page.getUrl().regex(URL_CONT).match()) {
                try {
                    String picSrc = page.getHtml().xpath("//div[@class=\"main-image\"]/p/a/img/@src").all().get(0);

                    //使用HttpClient下载图片
                    HttpClient client = HttpClients.createDefault();
                    HttpGet get = new HttpGet(picSrc);

                    //get的请求头可以用google浏览器看请求格式,然后复制粘贴下来
                    get.addHeader("Host", "i.meizitu.net");
                    get.addHeader("Paragma", "no-cache");
                    get.addHeader("Accept-Encoding","gzip, deflate");
                    get.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
                    get.addHeader("Cache-Control", "no-cache");
                    get.addHeader("Connection", "keep-alive");
                    get.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
                    get.addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

                    //模拟站内点击
                    get.addHeader("Referer", picSrc);

                    //获得目录名
                    String title = page.getHtml().xpath("//h2[@class=\"main-title\"]/text()").all().get(0);
                    int index = title.indexOf("（");
                    if (index != -1) {
                        title = title.substring(0, index);
                    }

                    //发起连接请求
                    HttpResponse httpResponse = client.execute(get);

                    //判断服务器是否正常响应
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        byte[] data = EntityUtils.toByteArray(entity);
                        System.out.println("write file into the dir: " + title);
                        FileOutputStream fout = new FileOutputStream(title + "/" + title + picSrc.substring(picSrc.length() - 6));
                        fout.write(data);
                        fout.close();
                    }else {
                        System.out.println("the server return code is not 200");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (page.getUrl().regex(URL_PIC).match()) { //每张图片的第一张连接,以此来获得其他1~n张的连接
                String url = page.getUrl().toString();
                //图片的最大数目
                String maxCnt = null;
                //要创建的目录名
                String title = null;
                try {
                    //获得这个专辑下的图片数目
                    List<String> listString = page.getHtml().xpath("//div[@class=\"pagenavi\"]/a/span/text()").all();
                    maxCnt = listString.get(listString.size() - 2);

                    //获得目录名
                    title = page.getHtml().xpath("//h2[@class=\"main-title\"]/text()").all().get(0);

                    //创建目录
                    File dir = new File(title);
                    if (dir.exists()) {
                        System.out.println("dir + " + title + " exist");
                    }else {
                        if (dir.mkdirs()) {
                            System.out.println("create a directory " + title);
                        }else {
                            System.out.println("faile to create the directory");
                        }
                    }

                    //把这些连接加到要访问的数组中去
                    for (int i = 1; i <= Integer.parseInt(maxCnt); ++i) {
                        String webUrl = url + "/" + i;
                        page.addTargetRequest(webUrl);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else {

                //获得页面下所有图片专辑的连接
                //一开始就是这个
                page.addTargetRequests(page.getHtml().xpath("//ul[@id=\"pins\"]/li/a/@href").all());
            }

    }
}
