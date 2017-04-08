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
	 * ԭ�������
	 * ͳ���������ĵ�Ӱ��
	 */
	public static AtomicInteger parseFilmCount=new AtomicInteger(0);
	public static volatile boolean isStopDownload=false;
	public static volatile boolean isStopParse=false;//�����ַ�����ҳ��ʱӦ��������̣߳�ֱ����֤֮��Ż���
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
		 * �����ݿ��д��ڸ����ӣ����������
		 */
		if(SqlTool.isContained(ConnectionManage.getConnection(), sql)){
			return;
		}else if(page.getUrl().contains("subject")){//�������а���subject���ǵ�Ӱ��ҳ���ӣ������ǵ�Ӱ��ǩ����
			Film film=DoubanFilmIndexDetailPageParser.getInstance().parse(page);
			if(film==null)
				return;
			if(Config.dbEnable){
				SqlTool.insertIntoDB(film);
				SqlTool.insertIntoHref(page.getUrl());
			}
			parseFilmCount.incrementAndGet();
			logger.info("�����ɹ�:"+film.toString());
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
