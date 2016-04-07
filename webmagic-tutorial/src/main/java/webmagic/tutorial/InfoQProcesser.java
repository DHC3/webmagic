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
 * InfoQ网站信息抓取
 */
public class InfoQProcesser implements PageProcessor{
	private Site site = Site.me()
			.setDomain("www.infoq.com")
            .setUserAgent("User-Agent: Mozilla/5.0 (Windows NT 5.1; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
        System.out.println("演讲信息抓取开始" + new Date());
        Spider.create(new InfoQProcesser())
    		.addUrl("http://www.infoq.com/cn/presentations/")
    		.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
    		.thread(1).run();
        System.out.println("演讲信息抓取结束" + new Date());
	}
	
    public void process(Page page) {
    	System.out.println(page.getUrl());
    	//http://www.infoq.com/cn/presentations/jingdong-elastic-computing-practice
//        List<String> links = page.getHtml().links().regex("http://job.liepin.com/\\d{3}_\\d{7}/\\?ckid=962906c5c4c96df7&pageNo=\\d+&pageIdx=\\d+&totalIdx=\\d+&imscid=R000000071").all();
//        page.addTargetRequests(links);
    	//演讲分页信息
        List<String> links = page.getHtml().links().regex("http://www.infoq.com/cn/presentations/\\d+").all();
        page.addTargetRequests(links);
    	
    	//第一页特殊处理
    	if("http://www.infoq.com/cn/presentations/".equals(page.getUrl().toString())){
    		for(int i=1;i<18;i++){
    			//第三个DIV是空白，无需处理
        		int currentRow = i%3;
        		if(currentRow == 0){
        			continue;
        		}
        		page.putField("演讲主题"+i, page.getHtml().xpath("//div[@id='content']/div[3]/div[" + String.valueOf(1+i) +"]/h2/a/text()").toString());
        		page.putField("演讲人"+i, page.getHtml().xpath("//div[@id='content']/div[3]/div[" + String.valueOf(1+i) +"]/span/a/text()").toString());
        		String time = page.getHtml().xpath("//div[@id='content']/div[3]/div[" + String.valueOf(1+i) +"]/span/text()").toString();
        		if(time != null){
        			time = time.replace("作者   发布于", "").trim();
        		}
        		page.putField("发布时间"+i, time);
        		page.putField("演讲概要"+i, page.getHtml().xpath("//div[@id='content']/div[3]/div[" + String.valueOf(1+i) +"]/p/text()").toString());
        	}
    	}else{
    		for(int i=1;i<16;i++){
    			//第死个DIV是空白，无需处理
        		int currentRow = i%4;
        		if(currentRow == 0){
        			continue;
        		}
        		page.putField("演讲主题"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(3+i) +"]/h2/a/text()").toString());
        		page.putField("演讲人"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(3+i) +"]/span/a[1]/text()").toString());
        		String time = page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(3+i) +"]/span/text()").toString();
        		if(time != null){
        			time = time.replace("作者   发布于", "").trim();
        		}
        		page.putField("发布时间"+i, time);
        		page.putField("演讲概要"+i, page.getHtml().xpath("//div[@id='content']/div[" + String.valueOf(3+i) +"]/p/text()").toString());
        	}
    	}
    }

    public Site getSite() {
        return site;
    }

}
