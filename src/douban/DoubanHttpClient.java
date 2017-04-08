package douban;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

import task.ParsePageTask;
import util.MyLogger;
import org.apache.log4j.Logger;
import config.Config;
public class DoubanHttpClient extends HttpClient{
	private Logger logger=MyLogger.getLogger(DoubanHttpClient.class);
	/*
	 * 下载线程池
	 */
	private ThreadPoolExecutor downloadThreadExecutor;
	/*
	 * 解析网页线程池
	 */
	private ThreadPoolExecutor parseThreadExecutor;
	public static class DoubanProvider{
		private static final DoubanHttpClient INSTANCE=new DoubanHttpClient();
	}
	public static DoubanHttpClient getInstance(){
		return DoubanProvider.INSTANCE;
	}
	public DoubanHttpClient(){
		initHttpClient();
		initThreadPoolExecutor();
		//创建一个监控下载线程池的线程
		//new Thread(new ThreadPoolMonitor(downloadThreadExecutor,"downloadThreadExecutor")).start();
	    //创建一个监控解析线程池的线程
		//new Thread(new ThreadPoolMonitor(parseThreadExecutor,"parseThreadExecutor")).start();
	}
	public void startSpider(){
		new Thread(new monitor.StartUrlIndexMonitor()).start();
		manageDoubanHttpClient();
	}
	public void manageDoubanHttpClient(){
		while(true){
			long taskCount=downloadThreadExecutor.getTaskCount();
			/*
			 * 如果下载网页的数量超过预定数量，同时下载线程池没有关闭
			 * 则shutdown下载线程池
			 */
			if(taskCount>Config.downloadPageCount && !DoubanHttpClient.getInstance().getDownloadThreadExecutor().isShutdown()){
				ParsePageTask.isStopDownload=true;
				DoubanHttpClient.getInstance().getDownloadThreadExecutor().shutdown();
			}
			/*
			 * 如果下载线程池处于停止状态，但解析线程池没有关闭
			 * 则shutdown解析线程池
			 */
			if(DoubanHttpClient.getInstance().getDownloadThreadExecutor().isTerminated() &&
					!DoubanHttpClient.getInstance().getParseThreadExecutor().isShutdown()){
				DoubanHttpClient.getInstance().getParseThreadExecutor().shutdown();
			}
			/*
			 * 如果解析线程池暂停
			 * 则显示爬取电影数量
			 */
			if(DoubanHttpClient.getInstance().getParseThreadExecutor().isTerminated()){
				monitor.ThreadPoolMonitor.isStopMonitor=true;
				monitor.StartUrlIndexMonitor.isStopMonitor=true;
				logger.info("爬取电影数量:"+ParsePageTask.parseFilmCount.get());
				break;
			}
		}
	}
	@Override
	public void initHttpClient(){
		if(!deserializeCookieStore(Config.cookiePath)){
			try{
				new ModelLoginDouban().login(this);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void initThreadPoolExecutor(){
		downloadThreadExecutor=new ThreadPoolExecutor(Config.downloadThreadSize,Config.downloadThreadSize,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
		parseThreadExecutor=new ThreadPoolExecutor(1,1,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
	}
	public ThreadPoolExecutor getDownloadThreadExecutor(){
		return downloadThreadExecutor;
	}
	public ThreadPoolExecutor getParseThreadExecutor(){
		return parseThreadExecutor;
	}
}
