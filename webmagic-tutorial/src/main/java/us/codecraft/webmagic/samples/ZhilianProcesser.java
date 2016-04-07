package cn.com.dhc.reptiles;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Administrator
 * 猎聘网信息抓取
 */
public class ZhilianProcesser implements PageProcessor{
	private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(500).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 5.1; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
		System.out.println("start");
        Spider.create(new ZhilianProcesser())
        	.addUrl("http://sou.zhaopin.com/jobs/searchresult.ashx?in=210500%3b160400%3b160000%3b160200%3b300100%3b160100%3b160600&jl=%E5%85%A8%E5%9B%BD&sm=0&sg=0a0412df72ec486781b6658f55452ea9&p=1")
        	.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
        	.thread(1).run();
        System.out.println("end");
	}
	
    public void process(Page page) {
//    	System.out.println(page.getUrl());
//    	http://jobs.zhaopin.com/190788127251152.htm?ssidkey=y&ss=201&ff=03
    	
        List<String> links = page.getHtml().links().regex("http://jobs.zhaopin.com/\\d+.htm").all();
        //?ssidkey=y&ss=201&ff=03
        if(links != null && links.size() > 0){
        	for(int i = 0;i<links.size();i++){
        		String url = links.get(i) + "?ssidkey=y&ss=201&ff=03";
        		links.set(i, url);
        	}
        }
        page.addTargetRequests(links);
        //分页信息
        links = page.getHtml().links().regex("http://sou.zhaopin.com/jobs/searchresult.ashx\\?in=210500%3b160400%3b160000%3b160200%3b300100%3b160100%3b160600&jl=%E5%85%A8%E5%9B%BD&sm=0&sg=0a0412df72ec486781b6658f55452ea9&p=\\d+").all();
        page.addTargetRequests(links);
        
        //公司信息
        page.putField("公司名", page.getHtml().xpath("//div[@class='top-fixed-box']/div[1]/div[1]/h2/a/text()").toString());
        //公司基本情况信息
        page.putField("行业", page.getHtml().xpath("//div[@class='terminalpage-right']/div[1]/ul/li[3]/strong/a/text()").toString());
        page.putField("规模", page.getHtml().xpath("//div[@class='terminalpage-right']/div[1]/ul/li[1]/strong/text()").toString());
        page.putField("性质", page.getHtml().xpath("//div[@class='terminalpage-right']/div[1]/ul/li[2]/strong/text()").toString());
        page.putField("地址", page.getHtml().xpath("//div[@class='terminalpage-right']/div[1]/ul/li[4]/strong/text()").toString());
        page.putField("公司介绍", page.getHtml().xpath("//div[@class='terminalpage-left']/div[1]/div/div[2]/allText()").toString());
        
        //职位信息
        page.putField("职位名", page.getHtml().xpath("//div[@class='top-fixed-box']/div[1]/div[1]/h1/text()").toString());
        page.putField("薪资", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[1]/strong/text()").toString());
        page.putField("工作地点", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[2]/strong/allText()").toString());
        page.putField("发布时间", page.getHtml().xpath("//span[@id='span4freshdate']/text()").toString());
        page.putField("工作经验", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[5]/strong/text()").toString());
        page.putField("最低学历", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[6]/strong/text()").toString());
        page.putField("招聘人数", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[7]/strong/text()").toString());
        page.putField("职位类别", page.getHtml().xpath("//div[@class='terminalpage-left']/ul/li[8]/strong/a/text()").toString());
        page.putField("职位描述", page.getHtml().xpath("//div[@class='terminalpage-left']/div[1]/div/div[1]/allText()").toString());
        page.putField("福利", page.getHtml().xpath("//div[@class='top-fixed-box']/div[1]/div[1]/div/allText()").toString());
    }

    public Site getSite() {
        return site;
    }

}
