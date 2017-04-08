package task;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.Map;
import java.util.HashMap;

import douban.DoubanHttpClient;
import util.HttpClientUtil;
import util.MyLogger;
import org.apache.log4j.Logger;
import entity.Page;
import config.Config;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class DownloadTask implements Runnable {
	public static CyclicBarrier cyclic=new CyclicBarrier(Config.downloadThreadSize,new Runnable(){
		@Override
		public void run(){
			String indexPage=barrierCode();
			if(indexPage!=null && indexPage.contains("VigoLin")){
				logger.info("Success!");
			}else{
				return;
			}
		}
	});
	private static DoubanHttpClient httpclient=DoubanHttpClient.getInstance();
	private static Logger logger=MyLogger.getLogger(DownloadTask.class);
	private String url;
	public DownloadTask(String url){
		this.url=url;
	}
	@Override
	public void run(){
		downloadPage();
	}
	/*
	 * 下载网页
	 * @param
	 * @return
	 */
	private void downloadPage(){
		try{
			Page page=null;
			page=httpclient.getWebPage(url);
			int status=page.getStatusCode();
			logger.info(Thread.currentThread().getName()+" executing request "+page.getUrl()+"  status:"+status);
			if(status==HttpStatus.SC_OK){
				httpclient.getParseThreadExecutor().execute(new ParsePageTask(page));
				return;
			}else if(status==502 || status==504 || status==500 || status==429){
				TimeUnit.MILLISECONDS.sleep(100);
				httpclient.getDownloadThreadExecutor().execute(new DownloadTask(url));//重新执行此下载任务
				return;
			}else if(status==403){
					cyclic.await();
					downloadPage();
			}
		}catch(InterruptedException e){
			logger.error("InterruptedException",e);
		}catch(NullPointerException e){
			logger.error("NullPointerException",e);
		}catch(BrokenBarrierException e){
			logger.error("BrokenBarrierException",e);
		}catch(Exception e){
			//logger.error("Exception",e);
		}
	}
	/*
	 * 当触发反爬虫机制时，获取拦截验证码
	 * @param
	 * @return String
	 */
	private static String barrierCode(){
		String indexPage=null;
		try{
			HttpGet httpget=new HttpGet(Config.barrierCode);
			String result=HttpClientUtil.getWebPage(httpclient.getCloseableHttpClient(),httpget,httpclient.getHttpClientContext(),"utf-8",false);
			Document doc=Jsoup.parse(result);
			String ck=doc.select("input[name=ck]").attr("value");
			String barrierUrl=doc.select("img[alt=captcha]").attr("src");//验证码链接
			String captcha_id=barrierUrl.substring(barrierUrl.indexOf("=")+1);//captcha_id
			//障碍验证码识别处
		    logger.info("请输入"+barrierUrl+"链接的验证码");
			Scanner scanner=new Scanner(System.in);
			String barriercode=scanner.nextLine();
			HttpPost httppost=new HttpPost("https://www.douban.com/misc/sorry");//post
			Map<String,String> params=new HashMap<String,String>();//params
			params.put("ck", ck);
			params.put("captcha-solution", barriercode);
			params.put("captcha-id", captcha_id);
			params.put("original-url","https://www.douban.com/");
		    HttpClientUtil.setHttpPost(httppost, params);
			indexPage=HttpClientUtil.getWebPage(httpclient.getCloseableHttpClient(),httppost, httpclient.getHttpClientContext(), "utf-8", false);
		}catch(Exception e){
			logger.info("Exception",e);
		}
		return indexPage;
	}
}
