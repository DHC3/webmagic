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
 * 猎聘网信息抓取
 */
public class LiepinProcesser implements PageProcessor{
	private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(500).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
//		System.out.println("猎聘网抓取开始" + new Date());
//        Spider.create(new LiepinProcesser())
//        	.addUrl("http://www.liepin.com/zhaopin/?pubTime=&salary=&searchType=1&clean_condition=&jobKind=&isAnalysis=&init=-1&sortFlag=15&searchField=1&key=&industries=040%2C420%2C010%2C030&jobTitles=&dqs=&compscale=&compkind=&ckid=962906c5c4c96df7")
////        	.addUrl("http://job.liepin.com/490_4905795/?ckid=962906c5c4c96df7&pageNo=0&pageIdx=0&totalIdx=0&imscid=R000000071")
//        	.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
//        	.thread(1).run();
//        System.out.println("猎聘网抓取结束" + new Date());
        System.out.println("智联抓取开始" + new Date());
        Spider.create(new ZhilianProcesser())
    		.addUrl("http://sou.zhaopin.com/jobs/searchresult.ashx?in=210500%3b160400%3b160000%3b160200%3b300100%3b160100%3b160600&jl=%E5%85%A8%E5%9B%BD&sm=0&sg=0a0412df72ec486781b6658f55452ea9&p=1")
    		.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
    		.thread(1).run();
        System.out.println("智联网抓取结束" + new Date());
	}
	
    public void process(Page page) {
    	System.out.println(page.getUrl());
    	//http://job.liepin.com/475_4751866/?ckid=962906c5c4c96df7&pageNo=8&pageIdx=0&totalIdx=320&imscid=R000000071
        List<String> links = page.getHtml().links().regex("http://job.liepin.com/\\d{3}_\\d{7}/\\?ckid=962906c5c4c96df7&pageNo=\\d+&pageIdx=\\d+&totalIdx=\\d+&imscid=R000000071").all();
        page.addTargetRequests(links);
        //分页信息
        links = page.getHtml().links().regex("http://www.liepin.com/zhaopin/\\?pubTime=&salary=&searchType=1&clean_condition=&jobKind=&isAnalysis=&init=-1&sortFlag=15&searchField=1&key=&industries=040%2C420%2C010%2C030&jobTitles=&dqs=&compscale=&compkind=&ckid=962906c5c4c96df7&curPage=\\d+").all();
        page.addTargetRequests(links);
        
        //公司基本情况信息
        String companyInfo = page.getHtml().xpath("//div[@class='right-post-top']/div[@class='content content-word']/allText()").toString();
        if(companyInfo != null){
        	//公司信息
            page.putField("公司名", page.getHtml().xpath("//div[@class='right-post-top']/p[@class='post-top-p']/a/text()").toString());
        	String arrayCompany[] = companyInfo.split("：");
            page.putField("行业", arrayCompany[1].replace("规模", "").trim());
            page.putField("规模", arrayCompany[2].replace("性质", "").trim());
            page.putField("性质", arrayCompany[3].replace("地址", "").trim());
            page.putField("地址", arrayCompany[4].trim());
            page.putField("企业介绍", page.getHtml().xpath("//div[@class='title']/div[5]/div/text()").toString());
            
            //职位信息
            page.putField("职位名", page.getHtml().xpath("//div[@class='title']/div[1]/h1/text()").toString());
            page.putField("薪资", page.getHtml().xpath("//div[@class='job-title-left']/p[1]/text()").toString());
            page.putField("工作地点", page.getHtml().xpath("//div[@class='job-title-left']/p[2]/span[1]/text()").toString());
            page.putField("发布时间", page.getHtml().xpath("//div[@class='job-title-left']/p[2]/span[2]/text()").toString());
            page.putField("要求", page.getHtml().xpath("//div[@class='resume clearfix']/allText()").toString());
            page.putField("福利", page.getHtml().xpath("//div[@class='tag-list clearfix']/allText()").toString());
            page.putField("职位描述", page.getHtml().xpath("//div[@class='title']/div[3]/div/text()").toString());
            page.putField("其他信息", page.getHtml().xpath("//div[@class='title']/div[4]/div/allText()").toString());
        }
    }

    public Site getSite() {
        return site;
    }

}
