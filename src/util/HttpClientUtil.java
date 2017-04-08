package util;
import org.apache.log4j.Logger;
import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.*;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import config.Config;
public class HttpClientUtil {
	private static Logger logger=MyLogger.getLogger(HttpClientUtil.class);
	/*
	 * 获取网页，将网页信息转换成String
	 * @param httpclient 客户端
	 * @param request 请求
	 * @param context 执行上下文
	 * @param encoding 编码样式
	 * @param printToConsole 是否打印到控制台
	 * @return String
	 */
	public static String getWebPage(CloseableHttpClient httpclient,HttpRequestBase request,HttpClientContext context,String encoding,boolean printToConsole){
		CloseableHttpResponse response=null;
		try{
			response=httpclient.execute(request,context);
		}catch(ClientProtocolException e){
			e.printStackTrace();
			logger.error("ClientProtocolException",e);
		}catch(IOException e){
			e.printStackTrace();
			logger.error("IOException",e);
		}
		logger.info("StatusCode:"+response.getStatusLine().getStatusCode());
		StringBuilder builder=new StringBuilder();
		BufferedReader reader=null;
		String line;
		try{
			reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent(),encoding));
			while((line=reader.readLine())!=null){
				builder.append(line+"\n");
				if(printToConsole){
					logger.info(line);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			logger.error("IOException",e);
		}
		finally{
			request.releaseConnection();
		}
		return builder.toString();
	}
	/*
	 * 设置客户端的cookie策略
	 * @param 
	 * @return CloseableHttpClient
	 */
	public static CloseableHttpClient getMyHttpClient(){
		CloseableHttpClient httpclient=null;
		LaxRedirectStrategy redirectStrategy=new LaxRedirectStrategy();
		RequestConfig config=RequestConfig.custom()
				                          .setCookieSpec("browser_compatibility")
				                          .build();
		//连接管理池
		PoolingHttpClientConnectionManager manager=new PoolingHttpClientConnectionManager();
		manager.setMaxTotal(200);
		manager.setDefaultMaxPerRoute(20);
		httpclient=HttpClients.custom()
		              .setDefaultRequestConfig(config)
		              .setConnectionManager(manager)
		              .setRedirectStrategy(redirectStrategy)
		              .build();
		
		return httpclient;
	}
	/*
	 * 获取定制的执行上下文
	 * @param
	 * @return HttpClientContext
	 */
	public static HttpClientContext getMyContext(){
		CookieStore store=(CookieStore)deserializeObject(Config.cookiePath);
		HttpClientContext context=HttpClientContext.create();
		Lookup<CookieSpecProvider> lookup=RegistryBuilder.<CookieSpecProvider>create()
				                                             .register("best_match",new BestMatchSpecFactory())
				                                             .register("browser_compatibility",new BrowserCompatSpecFactory())
				                                             .build();
		context.setCookieSpecRegistry(lookup);
		//if(store!=null)
			//context.setCookieStore(store);
		return context;
	}
	/*
	 * 序列化对象到制定个的文件中
	 * @param object 被序列化对象
	 * @param path 文件路径
	 * @return void
	 */
	public static void serializeObject(Object object,String path){
		ObjectOutputStream out=null;
		try{
			BufferedOutputStream buffered=new BufferedOutputStream(new FileOutputStream(new File(path)));
			out=new ObjectOutputStream(buffered);
			out.writeObject(object);
			logger.info("序列化成功");
			out.flush();
			buffered.close();
			out.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/*
	 * 从指定文件中反序列化对象
	 * @param path 制定文件路径
	 * @return object
	 */
	public static Object deserializeObject(String path){
		Object object=null;
		try{
			BufferedInputStream buffered=new BufferedInputStream(new FileInputStream(new File(path)));
			ObjectInputStream input=new ObjectInputStream(buffered);
		    object=input.readObject();
		    buffered.close();
		    input.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return object;
	}
	/*
	 * 下载验证码
	 * @param httpclient 客户端
	 * @param context 执行上下文
	 * @param fileUrl 验证码路径
	 * @param path 验证码存储路径
	 * @param saveName 验证码存储文件名
	 * @param isReplaceFile 是否覆盖原有文件
	 * @return 
	 */
	public static void downloadFile(CloseableHttpClient httpclient,HttpClientContext context,String fileUrl,String path,String saveName,boolean isReplaceFile){
		try{
			HttpGet httpget=new HttpGet(fileUrl);
			CloseableHttpResponse response=httpclient.execute(httpget);
			logger.info("StatusCode:"+response.getStatusLine().getStatusCode());
			File directory=new File(path);
			if(!directory.exists() && !directory.isDirectory()){
				logger.info("文件夹不存在，正在创建。。。");
				directory.mkdir();
			}else{
				logger.info("文件夹已存在");
			}
			File file=new File(path+saveName);
			if(!file.exists() || isReplaceFile){
				try{
					BufferedOutputStream buffered=new BufferedOutputStream(new FileOutputStream(file));
					InputStream in=response.getEntity().getContent();
					byte[] buff=new byte[(int)response.getEntity().getContentLength()];
					while(true){
						int readed=in.read(buff);
						if(readed==-1){
							break;
						}
						byte[] temp=new byte[readed];
						System.arraycopy(buff,0,temp,0,readed);
						buffered.write(temp);
						logger.info("文件正在下载中。。。");
					}
					in.close();
					buffered.close();
					logger.info("文件成功下载至"+path);
					httpget.releaseConnection();
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				logger.info("文件已存在");
			}
		}catch(ClientProtocolException e){
			e.printStackTrace();
			logger.error("ClientProtocolException ",e);
		}catch(IOException e){
			e.printStackTrace();
			logger.error("IOException",e);
		}
	}
	/*
	 * 输出cookie
	 * @param store cookie商店
	 * @return
	 */
	public static void getCookies(CookieStore store){
		List<Cookie> list=store.getCookies();
		if(list!=null){
			for(int i=0;i<list.size();i++){
				logger.info("Cookie:"+list.get(i).getName()+":"+list.get(i).getValue()+"\n过期时间:"+list.get(i).getExpiryDate()+"\nComment:"+list.get(i).getComment()
						    +"\nCommentURL:"+list.get(i).getCommentURL()+"\nDomain:"+list.get(i).getDomain()+"\nPorts:"+list.get(i).getPorts()+"\n\n");
			}
		}else{
			logger.info("该CookieStore无Cookie");
		}
	}
	/*
	 * 获取头信息
	 * @param headers 头信息数组
	 * @return 
	 */
	public static void getHeaders(Header[] headers){
		if(headers==null){
			logger.info("该headers无Header");
		}else{
			logger.info("......头信息开始.....");
			for(int i=0;i<headers.length;i++){
				logger.info(headers[i]);
			}
			logger.info(".......头信息结束.......");
		}
	}
	/*
	 * 将InputStream装换成String
	 * @param in 输入流
	 * @param encoding 制定编码形式
	 * @return String
	 */
	public static String transferToString(InputStream in,String encoding){
		String line;
		StringBuilder builder=new StringBuilder();
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(in,encoding));
			while((line=reader.readLine())!=null){
				builder.append(line);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return builder.toString();
	}
	/*
	 * 设置Post请求参数
	 * @param httppost
	 * @param map
	 * @return
	 */
	public static void setHttpPost(HttpPost httppost,Map<String,String> map) throws Exception{
		List<NameValuePair> list=new ArrayList<NameValuePair>();
		for(Map.Entry<String,String> entry:map.entrySet()){
			list.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
		}
		UrlEncodedFormEntity entity=new UrlEncodedFormEntity(list);
		httppost.setEntity(entity);
	}
	
	public static void main(String[] args){
		//String url="https://movie.douban.com/j/search_subjects?type=movie&tag=%E6%9C%80%E6%96%B0&page_limit=20&page_start=0";
	    douban.DoubanHttpClient douBan=douban.DoubanHttpClient.getInstance();
		entity.Page page=douBan.getWebPage(config.Config.startUrl);
		logger.info(page.getStatusCode());
		logger.info(config.Config.startUrl);
	}
}
