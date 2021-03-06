package com.sqider;

import com.main.Main;
import com.util.MyUtil;
import org.apache.log4j.Logger;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.LinkTag;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Google extends SearchEngine {
	static Logger logger = Logger.getLogger(Google.class);

	public List<String> search(String keys[],int page, int flag){
		String url="";
		List<String> urls = new ArrayList<String>();

		//poj%201400
/*		if(page == 0){ 
			url = "http://www.baidu.com/s?tn=sayh_1_dg&ie=utf-8&f=8&rsv_bp=1&rsv_spt=3&wd=";
			url += keys[0];	
			for(int i=1; i<keys.length; i++) url += "+" + keys[i];
				url += "&inputT=0";
		}else{
		}*/
		url="https://www.google.com.hk/search?num="+page+"&biw=1366&bih=344&q=" + URLEncoder.encode(keys[0]);
		for(int i=1; i<keys.length; i++) url += "+" + URLEncoder.encode(keys[i]);
		url += "&oq=" + URLEncoder.encode(keys[0]);
		for(int i=1; i<keys.length; i++) url += "+" + URLEncoder.encode(keys[i]);

		logger.info(url);
		PageData pd = MyUtil.getPage(url, false);
		//logger.info(pd.html);
		if(pd == null){
			logger.info("搜索失败!");
			return null;
		}
		/*List<LinkTag> list = MyUtil.parseTags(pd.html, LinkTag.class, "data-click", null);
		for(LinkTag link:list){
			//logger.info(link.getAttribute("href"));
			//logger.info(link.getLinkText().toLowerCase());
			urls.add(link.getAttribute("href"));
		}*/
		//logger.info(pd.html);
		List<HeadingTag> divs = MyUtil.parseTags(pd.html, HeadingTag.class, "class", "r");
		for(HeadingTag div:divs){
			List<LinkTag> links = MyUtil.parseTags(div.toHtml(), LinkTag.class, "target", "_blank");
			
			for(LinkTag link:links){
				if(link.getAttribute("onmousedown") != null && link.getAttribute("onmousedown").startsWith("return")){
					String title = link.getLinkText();
					if(flag == 0){
						if(MyUtil.rightTitle(title, keys))
							urls.add(link.getLink());
					}else if(flag == 1){
						if(MyUtil.rightTitle1(title, keys))
							urls.add(link.getLink());
					}
				}
			}
			
			//logger.info(link.getLink());
		}
		return urls;
	}
		
//		http://www.byywee.com/page/M0/S448/448552.html
//			http://hi.baidu.com/ygahgxcrlkbsuyq/item/6efa5c3711cf58faa884287d
//			http://2225377fjs.blog.163.com/blog/static/174629837201111203415428/
//			http://www.rsuntingying.com.cn/2014/1/20/00021.html
//			http://qianmacao.blog.163.com/blog/static/20339718020122268958598/
//			http://www.tanghuixia.com.cn/NC2014-3-24/ID4025174.html
//			http://www.niwozhi.net/demo_c61_i139934.html
//			http://www.xuebuyuan.com/1234756.html
//			http://www.csdn123.com/html/blogs/20130821/56251.htm
//			http://www.cfanz.cn/?c=article&amp;a=read&amp;id=33410
//			http://blog.csdn.net/oceanlight/article/details/7866759
//			http://www.haogongju.net/art/1648279
//			http://www.07net01.com/program/75365.html
//			http://www.niwozhi.net/demo_c63_i57869.html
//			http://www.leiliangcheng2.cn/news/2014/3/10/po4040823.html
//			http://mikewell.blog.163.com/blog/static/19291417220127175544688/
//			http://doc.okbase.net/caihongshijie6/archive/16152.html
//			http://blog.sina.com.cn/s/blog_63a2d3b30101czox.html
//			http://www.cnblogs.com/dream-wind/archive/2012/09/15/2685953.html
//			http://www.cppblog.com/kuangbin/archive/2011/08/25/154301.html
//			http://acm.hust.edu.cn/vjudge/problem/viewProblem.action?id=11912
//			http://blog.csdn.net/woshi250hua/article/details/7737001
//			http://www.rritw.com/a/JAVAbiancheng/ANT/20130302/273425.html
		
		public static void main(String[] args) {
			String keys[] = new String[]{"hdu","3152"};
			List<String> urls = new Google().search(keys,30);
			for(String str:urls) logger.info(str);
		}

		@Override
		public List<String> search(String[] keys, int page) {
			return search(keys, page, 0);
		}
}
