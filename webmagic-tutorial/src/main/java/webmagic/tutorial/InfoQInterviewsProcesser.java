package cn.com.dhc.reptiles;

import java.util.Date;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Administrator
 * InfoQ访谈网站信息抓取
 */
public class InfoQInterviewsProcesser implements PageProcessor{
	private Site site = Site.me()
			.setDomain("www.infoq.com")
            .setUserAgent("User-Agent: Mozilla/5.0 (Windows NT 5.1; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
        System.out.println("访谈信息抓取开始" + new Date());
        Spider.create(new InfoQInterviewsProcesser())
    		.addUrl("http://www.infoq.com/cn/interviews/")
    		.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\interviews\\"))
    		.thread(1).run();
        System.out.println("访谈信息抓取结束" + new Date());
	}
	
    public void process(Page page) {
    	System.out.println(page.getUrl());
    	//访谈分页信息
        List<String> links = page.getHtml().links().regex("http://www.infoq.com/cn/interviews/\\d+").all();
        page.addTargetRequests(links);
    	
    	for(int i=1;i<16;i++){
    		//第死个DIV是空白，无需处理
        	int currentRow = i%4;
        	if(currentRow == 0){
        		continue;
        	}
        	page.putField("演讲主题"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(2+i) +"]/h2/a/text()").toString());
        	page.putField("演讲人"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(2+i) +"]/span/a[1]/text()").toString());
        	String time = page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(2+i) +"]/span/text()").toString();
        	if(time != null){
        		time = time.replace("受访者  发布于", "").trim();
        	}
        	page.putField("发布时间"+i, time);
        	page.putField("演讲概要"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(2+i) +"]/p/text()").toString());
        }
    }

    public Site getSite() {
        return site;
    }

}
