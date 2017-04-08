package task;
import douban.DoubanHttpClient;
import entity.Page;
import entity.Film;
import parser.DoubanFilmIndexDetailPageParser;
import parser.UrlListParser;
import util.MyLogger;
import config.Config;
import sqlconnection.ConnectionManage;
import sqlconnection.SqlTool;
import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;
public class ParsePageTask implements Runnable{
	private static Logger logger=MyLogger.getLogger(ParsePageTask.class);
	private static DoubanHttpClient httpclient=DoubanHttpClient.getInstance();
	/*
	 * 原子类对象
	 * 统计所爬过的电影数
	 */
	public static AtomicInteger parseFilmCount=new AtomicInteger(0);
	public static volatile boolean isStopDownload=false;
	public static volatile boolean isStopParse=false;//当出现反爬虫页面时应挂起解析线程，直到验证之后才唤醒
	private Page page;
	public ParsePageTask(Page page){
		this.page=page;
	}
	@Override
	public void run(){
		parse();
	}
	private void parse(){
		String sql="select count(*) from href where href='"+page.getUrl()+"'";
		/*
		 * 若数据库中存在该链接，则放弃解析
		 */
		if(SqlTool.isContained(ConnectionManage.getConnection(), sql)){
			return;
		}else if(page.getUrl().contains("subject")){//若链接中包含subject则是电影主页链接，否则是电影标签链接
			Film film=DoubanFilmIndexDetailPageParser.getInstance().parse(page);
			if(film==null)
				return;
			if(Config.dbEnable){
				SqlTool.insertIntoDB(film);
				SqlTool.insertIntoHref(page.getUrl());
			}
			parseFilmCount.incrementAndGet();
			logger.info("解析成功:"+film.toString());
			return;
		}else if(!isStopDownload){
			if(Config.dbEnable)
			    SqlTool.insertIntoHref(page.getUrl());
			for(String url:UrlListParser.getInstance().parse(page)){
					httpclient.getDownloadThreadExecutor().execute(new DownloadTask(url));
			}
		}
	}
}
