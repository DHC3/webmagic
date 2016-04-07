package cn.com.dhc.reptiles;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Administrator
 * IT168 文库中会议信息抓取
 */
public class IT168huiyiProcesser implements PageProcessor{
	private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(500).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8");
	
	public static void main(String[] args) {
		System.out.println("start");
        Spider.create(new IT168huiyiProcesser())
        	.addUrl("http://wenku.it168.com/huiyi/search.aspx?_125=all&_99=1")
        	.addPipeline(new FilePipeline("D:\\ENV\\webMagic\\"))
//        	.addPipeline(new JsonFilePipeline("D:\\ENV\\webMagic\\"))
        	.thread(1).run();
        System.out.println("end");
	}
	
    public void process(Page page) {
    	System.out.println(page.getUrl());
    	
        List<String> links = page.getHtml().links().regex("http://wenku.it168.com/huiyi/\\d+").all();
        page.addTargetRequests(links);
        //分页信息
        links = page.getHtml().links().regex("http://wenku.it168.com/huiyi/search.aspx\\?_125=all&_99=\\d+").all();
        page.addTargetRequests(links);
        
		// 会议名称、会议演讲题目、职务、公司、人名
		String businessName = page.getHtml().xpath("//div[@class='tit1']/h1/a/text()").toString();
		String businessTime = page.getHtml().xpath("//div[@class='tit1']/text()").toString();
		//获取页面中table数量
		List<String> tableMessage = page.getHtml().xpath("//table").all();
		
		if(tableMessage != null && tableMessage.size() == 1){
			//http://wenku.it168.com/huiyi/2533 参照
			// 获取table下全部tr信息
			List<String> trMessage = page.getHtml().xpath("//table[@class='tab1']/tbody/tr").all();
			if (trMessage != null && trMessage.size() > 0) {
				List<String> tdMessage = page.getHtml().xpath("//table[@class='tab1']/tbody/tr[2]/td").all();
				if (tdMessage.size() == 4) {
					page.putField("会议主题", businessName);
					page.putField("会议时间", businessTime);
					for (int i = 1; i < trMessage.size(); i++) {
						page.putField("演讲主题" + i,page.getHtml().xpath("//table[@class='tab1']/tbody/tr["+ String.valueOf(i + 1)+ "]/td[1]/allText()").toString());
						page.putField("职务" + i,page.getHtml().xpath("//table[@class='tab1']/tbody/tr["+ String.valueOf(i + 1)+ "]/td[2]/allText()").toString());
						page.putField("公司" + i,page.getHtml().xpath("//table[@class='tab1']/tbody/tr["+ String.valueOf(i + 1)+ "]/td[3]/allText()").toString());
						page.putField("演讲人" + i,page.getHtml().xpath("//table[@class='tab1']/tbody/tr["+ String.valueOf(i + 1)+ "]/td[4]/allText()").toString());
					}
				}
			}
		}else if(tableMessage != null && tableMessage.size() > 1){
			page.putField("会议主题", businessName);
			page.putField("会议时间", businessTime);
			//获取分论坛信息
			List<String> subCount = page.getHtml().xpath("//div[@class='l1']/p").all();
			boolean isOthers = false;
			if(subCount != null && subCount.size() == tableMessage.size() + 1){
				//http://wenku.it168.com/huiyi/2529 参照
				isOthers = true;
			}
			//http://wenku.it168.com/huiyi/2118  参照
			//遍历table信息
			for(int j =0;j<tableMessage.size();j++){
				// 获取table下全部tr信息
				List<String> trMessage = page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1)+"]/tbody/tr").all();
				//获取分论坛信息
				String subItem = page.getHtml().xpath("//div[@class='l1']/h2["+ String.valueOf(j+1) + "]/text()").toString();
				if(isOthers){
					subItem = subItem + ":" + page.getHtml().xpath("//div[@class='l1']/p["+ String.valueOf(j+2) + "]/text()").toString();
				}
				
				page.putField("分论坛" + j,subItem);
				if (trMessage != null && trMessage.size() > 0) {
					List<String> tdMessage = page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1) + "]/tbody/tr[2]/td").all();
					if (tdMessage.size() == 4) {
						for (int i = 1; i < trMessage.size(); i++) {
							page.putField("演讲主题" + i,page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1) + "]/tbody/tr["+ String.valueOf(i + 1)+ "]/td[1]/allText()").toString());
							page.putField("职务" + i,page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1) + "]/tbody/tr["+ String.valueOf(i + 1)+ "]/td[2]/allText()").toString());
							page.putField("公司" + i,page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1) + "]/tbody/tr["+ String.valueOf(i + 1)+ "]/td[3]/allText()").toString());
							page.putField("演讲人" + i,page.getHtml().xpath("//div[@class='l1']/table[" + String.valueOf(j+1) + "]/tbody/tr["+ String.valueOf(i + 1)+ "]/td[4]/allText()").toString());
						}
					}
				}
			}
		}
    }

    public Site getSite() {
        return site;
    }

}
