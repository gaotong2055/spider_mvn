package com.test;

import com.sqider.PageData;
import com.util.HibernateSessionFactory;
import com.util.MyUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VisitAllPages {


	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(VisitAllPages.class);


	public static List<String> allurls;
	static{
		allurls = getAllPages();
	}
	
	public static void main(String[] args) throws SQLException {
	
		//session.close();
		visitAll();
	}
	
	public static List<String> getAllPublisPosts(){
		Session session = HibernateSessionFactory.sessionFactory.getCurrentSession();
		Transaction tran = session.beginTransaction();
		List posts = session.createQuery("select * from WpPosts post where post.postStatus='publish' and " +
				"post.postType='post'").list();

		logger.info(posts.size());
		tran.commit();
		return null;
	}

	private static void visitAll() {
		int cnt = 0;
		for(String url : allurls) {

			PageData pda = MyUtil.getPage(url);
			if(pda!=null){
				logger.info(cnt + "  ok url! " + url);
			}
			cnt ++;
			
			try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static List<String> getAllPages(){
		PageData pd = MyUtil.getPage("http://www.acmerblog.com/sitemap.xml");
		Scanner scan = new Scanner(pd.html);
		String line = null;
		int cnt = 0;
		List<String> urls = new ArrayList<String>();
		while(scan.hasNextLine() && (line = scan.nextLine()) != null){
			if(line.contains("<loc>")){
			String url = org.apache.commons.lang3.StringUtils.substringBetween(line, "<loc>", "</loc>");
			urls.add(url);
			}
		}
		return urls;
	}
	
	
	public static String getUrl(String oj,String problem){
		for(String url:allurls){
			url = url.toUpperCase();
			oj = oj.toUpperCase();
			if(url.matches(oj + "{-}*" + problem)){
				return url;
			}
		}
		return null;
	}
	
	
}
