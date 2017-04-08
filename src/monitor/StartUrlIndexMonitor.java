package monitor;
import config.Config;
import douban.DoubanHttpClient;
import task.DownloadTask;
import java.util.concurrent.TimeUnit;
public class StartUrlIndexMonitor implements Runnable {
	public static boolean isStopMonitor=false;
	private static DoubanHttpClient httpclient=DoubanHttpClient.getInstance();
	@Override
	public void run(){
		try{
			for(int i=0;i<=Config.tagFilmCount;i+=20){
				if(!isStopMonitor){
					String url=Config.startUrl.replace("*",String.valueOf(i));
					httpclient.getDownloadThreadExecutor().execute(new DownloadTask(url));
					TimeUnit.MILLISECONDS.sleep(0);
				}
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}