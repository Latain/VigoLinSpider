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
	 * �����̳߳�
	 */
	private ThreadPoolExecutor downloadThreadExecutor;
	/*
	 * ������ҳ�̳߳�
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
		//����һ����������̳߳ص��߳�
		//new Thread(new ThreadPoolMonitor(downloadThreadExecutor,"downloadThreadExecutor")).start();
	    //����һ����ؽ����̳߳ص��߳�
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
			 * ���������ҳ����������Ԥ��������ͬʱ�����̳߳�û�йر�
			 * ��shutdown�����̳߳�
			 */
			if(taskCount>Config.downloadPageCount && !DoubanHttpClient.getInstance().getDownloadThreadExecutor().isShutdown()){
				ParsePageTask.isStopDownload=true;
				DoubanHttpClient.getInstance().getDownloadThreadExecutor().shutdown();
			}
			/*
			 * ��������̳߳ش���ֹͣ״̬���������̳߳�û�йر�
			 * ��shutdown�����̳߳�
			 */
			if(DoubanHttpClient.getInstance().getDownloadThreadExecutor().isTerminated() &&
					!DoubanHttpClient.getInstance().getParseThreadExecutor().isShutdown()){
				DoubanHttpClient.getInstance().getParseThreadExecutor().shutdown();
			}
			/*
			 * ��������̳߳���ͣ
			 * ����ʾ��ȡ��Ӱ����
			 */
			if(DoubanHttpClient.getInstance().getParseThreadExecutor().isTerminated()){
				monitor.ThreadPoolMonitor.isStopMonitor=true;
				monitor.StartUrlIndexMonitor.isStopMonitor=true;
				logger.info("��ȡ��Ӱ����:"+ParsePageTask.parseFilmCount.get());
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
