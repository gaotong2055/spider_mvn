package com.sqider;

import com.model.*;
import com.util.Init;
import com.util.MyUtil;
import com.util.PojUtil;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.tags.Div;
import org.htmlparser.util.ParserException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Spider4PojJava {

	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Spider4PojJava.class);

	
	public static String qnamediv = "<div ><h1 class=\"mytitle mybigtile\">";
	public static String qnamediv2="</h1></div>";
	
	public static String titledes = "<div><p class=\"mytitle\"> 问题描述 :</p></div>";
	public static String titleInput = "<div><p class=\"mytitle\"> 输入:</p></div>";
	public static String titledOutput = "<div><p class=\"mytitle\"> 输出:</p></div>";
	public static String titledSamInput = "<div><p class=\"mytitle\"> 样例输入:</p></div>";
	public static String titledSamOutput = "<div><p class=\"mytitle\"> 样例输出:</p></div>";
	public static String titledHint = "<div><p class=\"mytitle\"> 温馨提示:</p></div>";
	public static String titledThink = "<div><p class=\"mytitle\"> 解题思路:</p></div>";

	public static String titlecode = "<div><p class=\"mytitle\"> 解题代码:</p></div>";
	public static WpTermTaxonomyDAO wtdao = new WpTermTaxonomyDAO();
	
	//POJ Tag
	public static WpTermTaxonomy termtax01 =wtdao.findById(10L);
	
	//Java Tag
	public static	WpTermTaxonomy termtax02 =wtdao.findById(66L);
	//logger.info(termtax01.getTerm().getName());
	
	//Poj  分类
	public static	WpTermTaxonomy termtax03 =wtdao.findById(31L);
	//Java 解POJ 分类
	public static	WpTermTaxonomy termtax04 =wtdao.findById(82L);
	
	//C++
	public static	WpTermTaxonomy termtaxcpp =wtdao.findById(65L);
	/**
	 * 获取所有文章的链接
	 * @return
	 */
	public static List<String> getUrls(){
		List<String> urls = new ArrayList<String>();
		PageData pd = MyUtil.getPage("http://www.java3z.com/cwbwebhome/article/article17/POJ3.html", false);
		Scanner scan = new Scanner(pd.html);
		String line = null;
		while(scan.hasNext()){
			line = scan.nextLine();
			if(line.startsWith("href=")){
				urls.add(line);
			}
		}
		return urls;
	}
	
	/**
	 * 并存储该文章到数据库
	 * @param url 要解析的url,
	 * @return
	 * @throws Exception
	 */
	public static WpPosts parseUrl(String url) throws Exception{
		String surl = "http://www.java3z.com/cwbwebhome/article/article17/" +
		StringUtils.substringBetween(url, "href=\"", "\">");
		String num = StringUtils.substringBetween(url, ">", "<");
		logger.info(surl + "   " + num);
		
		
		//获取代码页面 数据
		PageData pd = MyUtil.getPage(surl, false, "gb2312", null, null);
		Parser parser = new Parser();
		try {
			parser.setInputHTML(pd.html);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String qName="";
		try {
			//先从POJ 获取题目的信息
			ProblemData pdata = PojUtil.getPorblemStr(num, true);
			String postText = pdata.text;
			
			List<Div> code = MyUtil.parseTags(pd.html, Div.class, "class", "entry");
			
			String lang = "java";
			//解析代码
			String codeString = null;
			for(Div d:code){
				
				String tmpStr = d.getStringText();
				
				
				if(tmpStr.contains("static") && tmpStr.contains("class") && tmpStr.contains("public"))
				{
					codeString = tmpStr; 
				}else if(tmpStr.contains("include") || tmpStr.contains("scanf") || tmpStr.contains("namespace")){
					codeString = tmpStr;
					lang = "cpp";
				}
				
				if(codeString != null) break;
				
			}
			
			if(codeString == null){
				throw new Exception();
			}
			
			codeString = codeString.replaceAll("<pre>", "").replaceAll("</pre>", "").trim();
			int last = codeString.lastIndexOf('}');
				
			codeString = codeString.substring(0,last+1);
			
			postText += titlecode;
			int codeStyle = 1; //使用那个代码高亮插件
			if(codeStyle == 0){
				postText += "<code lang=\"java\">";
				postText += codeString;
				postText += "</code>";
			}else{
				postText += "<pre class=\"brush:" + lang + " \">";
				postText += codeString;
				postText += "</pre>";
			}
			
		
			//logger.info(postText);
			
			WpPosts wpPosts = new WpPosts();
			WpPostsDAO pdao = new WpPostsDAO();
			
			wpPosts.setPostAuthor(1L);
			Timestamp tm = new Timestamp(new Date().getTime());
			Timestamp tm2 = new Timestamp(tm.getTime() -  60 * 8 * 60 * 1000);
			
			wpPosts.setPostDate(tm);
			wpPosts.setPostDateGmt(new Timestamp(new Date().getTime()));
			wpPosts.setPostContent(postText);
			
			wpPosts.setPostTitle("POJ " + num + " " + pdata.title + " [解题报告] Java");
			if(lang.equals("cpp")) wpPosts.setPostTitle("POJ " + num + " " + pdata.title + " [解题报告] C++");
			wpPosts.setPostExcerpt(wpPosts.getPostTitle());
			wpPosts.setPostStatus("publish");
			wpPosts.setCommentStatus("open");
			wpPosts.setPingStatus("open");
			wpPosts.setPostName("POJ-" + num + "-" + MyUtil.clearTitleToUrl(pdata.title)+ "-blog");
			wpPosts.setToPing("");
			wpPosts.setPinged("");
			wpPosts.setPostModified(tm);
			wpPosts.setPostModifiedGmt(new Timestamp(new Date().getTime()));
			wpPosts.setPostContentFiltered("");
			wpPosts.setPostParent(0L);
			wpPosts.setPostPassword("");
			wpPosts.setGuid("");
			wpPosts.setPostMimeType("");
			
			wpPosts.setMenuOrder(0);
			wpPosts.setPostType("post");
			wpPosts.setCommentCount(0L);
			
			
			pdao.save(wpPosts);
			//logger.info(wpPosts.getId());
			
			wpPosts.setGuid(Init.host + "/?p=" + wpPosts.getId());
			wpPosts.getTerms().add(termtax01);
			
			wpPosts.getTerms().add(termtax03);
			if(lang.equals("java")){
			wpPosts.getTerms().add(termtax02);
			wpPosts.getTerms().add(termtax04);
			}else if(lang.equals("cpp"))
				wpPosts.getTerms().add(termtaxcpp);
			//codeString 可能会包含分类信息
			getTerms(wpPosts, codeString, lang);
			
			pdao.save(wpPosts);
		
			Log log = new Log();
			log.setFlag(1); //未完成
			log.setUrl(surl);
			log.setName("poj "+num);
			log.setTime(new Timestamp(new Date().getTime()));
			LogDAO lDao = new LogDAO();
			log.setOther(wpPosts.getId() + "");
			lDao.save(log);
			
		} catch (Exception e) {
			Log log = new Log();
			log.setFlag(0); //未完成
			log.setUrl(surl);
			log.setName("poj "+num);
			log.setTime(new Timestamp(new Date().getTime()));
			LogDAO lDao = new LogDAO();
			lDao.save(log);
			e.printStackTrace();
			throw e;
		}
		
		return null;
	}
	
	private static void getTerms(WpPosts wpPosts, String text,String lang) {
		//logger.info("getTerms :");
		//logger.info(codeString);
		for(String catKey: Init.catKeySet){
			String tkey = catKey.toLowerCase();
			if(text.toLowerCase().contains(tkey)){
				if(tkey.equals("java") ||tkey.equals("poj") ||tkey.equals("pku") ||tkey.equals("dp") ||tkey.equals("hash") || tkey.equals("sort")) continue; //Java标签不能重复添加

				if(lang.equals("java")){
					if(tkey.endsWith("hash") && text.contains("HashTable")) continue;
					else if(tkey.endsWith("queue") && text.contains("Queue")) continue;
				}
				WpTermTaxonomy tax = Init.catTermTaxMap.get(catKey);
				wpPosts.getTerms().add(tax);
				logger.info("add new cat: " + catKey);
			}
		}
		for(String tagString: Init.tagKeySet){
			String tkey = tagString.toLowerCase();
			if(text.toLowerCase().contains(tagString.toLowerCase())){
				
				if(tkey.equals("java") ||tkey.equals("poj") ||tkey.equals("pku")  ||tkey.equals("dp") || tkey.equals("hash") || tkey.equals("sort")) continue; //Java标签不能重复添加
				
				WpTermTaxonomy tax = Init.tagTermTaxMap.get(tagString);
				wpPosts.getTerms().add(tax);
				logger.info("add new tag: " + tagString);
			}
		}
		
		
	}

	public static void main(String[] args) {
		Init.init();
		//parseUrl("href=\"acm888.html\">1191</A>&nbsp;&nbsp;&nbsp;&nbsp;");
		/*String url = "";
		boolean test = false;
		if(args.length == 4){
			test = true;
			url = args[3];
		}*/
		
		
		
		boolean test = false;
		if(test){
			try {
				parseUrl("href=\"acm231.html\">1316</A>&nbsp;&nbsp;&nbsp;&nbsp;");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			List<String> urls = getUrls();
			int start = 0,end=1; 
			int sleepTime = 2000;
			
			if(args.length == 3){
				//logger.info("wrong args!"); return;
				start = Integer.parseInt(args[0]);
				end = Integer.parseInt(args[1]);
				sleepTime = Integer.parseInt(args[2]);
			}
			
			if(end == -1) end = urls.size();
			int i=start;
			for( i=start; i < urls.size(); i++){
				if(urls.get(i).contains("acm603")){
					start = i; 
					logger.info(urls.get(i));
					break;
				}
			}
			
			if(Init.isWindows)
				end = start + 20;
			
			for( i=start; i<end; i++){
				try {
					parseUrl(urls.get(i));
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
		}
		
		
//		Timestamp tm = new Timestamp(new Date().getTime());
//		Timestamp tm2 = new Timestamp(tm.getTime() -  60 * 8 * 60 * 1000);
//		logger.info(tm.toLocaleString());
//		logger.info(tm2.toLocaleString());
	}
	
}
