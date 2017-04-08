package monitor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
public class ThreadPoolMonitor implements Runnable {
	private static Logger logger=util.MyLogger.getLogger(ThreadPoolMonitor.class);
	private ThreadPoolExecutor exe;
	private String name;
	public volatile static boolean isStopMonitor=false;
	public ThreadPoolMonitor(ThreadPoolExecutor exe,String name){
		this.exe=exe;
		this.name=name;
	}
	public void run(){
		while(!isStopMonitor){
			logger.info(name+":"+String.format("[Monitor][%d/%d] Active:%d, completed:%d, queueSize:%d, task:%d, isShutdown:%s, isTerminate:%s",
					                            exe.getPoolSize(),exe.getCorePoolSize(),exe.getActiveCount(),exe.getCompletedTaskCount(),
					                            exe.getQueue().size(),exe.getTaskCount(),exe.isShutdown(),exe.isTerminated()));
			try{
				TimeUnit.MILLISECONDS.sleep(100);
			}catch(InterruptedException e){
				e.printStackTrace();
				logger.error("InterruptedException",e);
			}
		}
	}
}