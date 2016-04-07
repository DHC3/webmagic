package cn.com.dhc.reptiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Administrator
 * 拉勾网信息抓取
 */
public class LagouProcesser implements PageProcessor{
	private Site site = Site.me().setSleepTime(500).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
        System.out.println("start");
        Spider.create(new LagouProcesser())
        	.addUrl("http://www.lagou.com/zhaopin/")
        	.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
        	.thread(1).run();
        System.out.println("end");
	}
	
    private static HtmlPage getNextPage(HtmlPage currentPage,List<String> acturlLinks) throws IOException {
    	//查找所有链接
		List<HtmlAnchor> links = currentPage.getAnchors();
		if(links != null && links.size() > 0){
			for(HtmlAnchor anchor:links){
				String link = anchor.getAttribute("href");
				//对链接进行正则匹配
				//职位链接http://www.lagou.com/jobs/1634552.html
				Pattern pattern = Pattern.compile("http://www.lagou.com/jobs/\\d+\\.html");
				Matcher matcher = pattern.matcher(link);
				if(matcher.matches()){
					acturlLinks.add(link);
				}
			}
		}
		//定位下一页链接
		HtmlDivision divNode = currentPage.getFirstByXPath("//div[@id='positionHead']/ul[@id='order']/li/div[4]/div[2]");
		HtmlPage nextPage = divNode.click();
		return nextPage;
	}

	public void process(Page page) {
		List<String> links = new ArrayList<String>();
		//根据HtmlUnit解析链接
		if("http://www.lagou.com/zhaopin/".equals(page.getUrl().toString())){
			//使用FireFox读取网页
	        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
	        try {
	            //htmlunit 对css和javascript的支持不好，所以请关闭之
	            webClient.getOptions().setJavaScriptEnabled(true);
	            webClient.getOptions().setCssEnabled(false);
	        	//打开网址
				HtmlPage currentPage=webClient.getPage("http://www.lagou.com/zhaopin/");
				//线程等待加载JS
				webClient.waitForBackgroundJavaScript(1000*3);
				//拉勾网显示30页数据
				for(int i =1;i<31;i++){
					//获取下一页链接
					currentPage = getNextPage(currentPage,links);
				}
				//追加待分析页面URL
				page.addTargetRequests(links);
			} catch (FailingHttpStatusCodeException e) {
				System.out.println("打开失败：  " + "");
			} catch (MalformedURLException e) {
				System.out.println("打开失败：  " + "");
			} catch (IOException e) {
				System.out.println("打开失败：  " + "");
			}finally{
				//关闭webclient
		        webClient.close();
			}
	        //关闭webclient
	        webClient.close();
		}else{
			//http://www.lagou.com/jobs/1639109.html
	        links = page.getHtml().links().regex("http://www.lagou.com/jobs/\\d+\\.html").all();
	        page.addTargetRequests(links);
	        
	        //公司信息
	        page.putField("公司名", page.getHtml().xpath("//dl[@id='job_detail']/dt/h1/div/text()").toString());
	        //公司基本情况信息
	        page.putField("领域", page.getHtml().xpath("//div[@id='container']/div[2]/dl/dd/ul[1]/li[1]/text()").toString());
	        page.putField("规模", page.getHtml().xpath("//div[@id='container']/div[2]/dl/dd/ul[1]/li[2]/text()").toString());
	        page.putField("发展阶段", page.getHtml().xpath("//div[@id='container']/div[2]/dl/dd/ul[2]/li/text()").toString());
	        page.putField("地址", page.getHtml().xpath("//div[@id='container']/div[2]/dl/dd/div[1]/text()").toString());
	        page.putField("公司主页", page.getHtml().xpath("//div[@id='container']/div[2]/dl/dd/ul[1]/li[3]/a/text()").toString());
	        
	        //职位信息
	        page.putField("职位名", page.getHtml().xpath("//dl[@id='job_detail']/dt/h1/text()").toString());
	        page.putField("薪资", page.getHtml().xpath("//dl[@id='job_detail']/dd[1]/p[1]/span[1]/text()").toString());
	        page.putField("发布时间", page.getHtml().xpath("//dl[@id='job_detail']/dd[1]/p[3]/text()").toString());
	        page.putField("要求", page.getHtml().xpath("//dl[@id='job_detail']/dd[1]/p[1]/allText()").toString());
	        page.putField("福利", page.getHtml().xpath("//dl[@id='job_detail']/dd[1]/p[2]/text()").toString());
	        page.putField("职位描述", page.getHtml().xpath("//dl[@id='job_detail']/dd[2]/allText()").toString());
	        page.putField("发布者", page.getHtml().xpath("//dl[@id='job_detail']/dd[3]/div/div[1]/allText()").toString());
		}
    }

    public Site getSite() {
        return site;
    }

}
