package douban;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import util.HttpClientUtil;
import util.MyLogger;
import entity.Page;

import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.ClientProtocolException; 
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
public abstract class HttpClient {
	private Logger logger=MyLogger.getLogger(HttpClient.class);
	protected CloseableHttpClient httpclient;
	protected HttpClientContext context;
	private CloseableHttpResponse response;
	public HttpClient(){
		this.httpclient=HttpClientUtil.getMyHttpClient();
		this.context=HttpClientUtil.getMyContext();
	}
	/*
	 * 执行相应的请求，获取响应
	 * @param request
	 * @return CloseableHttpResponse
	 */
	public CloseableHttpResponse getResponse(HttpRequestBase request){
		try{
			response=httpclient.execute(request,context);
			return response;
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * 执行相应的请求，将响应装换成流
	 * @param request
	 * @return InputStream
	 */
	public InputStream getWebPageInputStream(HttpRequestBase request){
		try{
			InputStream in=getResponse(request).getEntity().getContent();
			return in;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public InputStream getWebPageInputStream(String url){
		HttpGet httpget=new HttpGet(url);
		return getWebPageInputStream(httpget);
	}
	/*
	 * 执行请求，将响应转换成字符串
	 * @param url
	 * @return String
	 */
	public String getWebContent(String url){
		InputStream in=getWebPageInputStream(url);
		StringBuilder builder=new StringBuilder();
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line;
			while((line=reader.readLine())!=null){
				builder.append(line+"\n");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return builder.toString();
	}
	/*
	 * 执行请求，返回Page对象
	 * @param request
	 * @return Page
	 */
	public Page getWebPage(HttpRequestBase request){
		Page page=new Page();
		CloseableHttpResponse response=getResponse(request);
		page.setStatusCode(response.getStatusLine().getStatusCode());
		page.setUrl(request.getURI().toString());
		try{
			if(page.getStatusCode()==200){
				page.setHtml(IOUtils.toString(response.getEntity().getContent(),"UTF-8"));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			request.releaseConnection();
		}
		return page;
	}
	public Page getWebPage(String url){
		HttpGet httpget=new HttpGet(url);
		return getWebPage(httpget);
	}
	/*
	 * 反序列化CookieStore
	 * @param path
	 * @return boolean
	 */
	public boolean deserializeCookieStore(String path){
		try{
			CookieStore cookieStore=(CookieStore)HttpClientUtil.deserializeObject(path);
			context.setCookieStore(cookieStore);
		}catch(Exception e){
			logger.warn("CookieStore反序列化失败，找不到CookieStore文件");
			return false;
		}
		return true;
	}
	public CloseableHttpClient getCloseableHttpClient(){
		return httpclient;
	}
	public HttpClientContext getHttpClientContext(){
		return context;
	}
	public abstract void initHttpClient();
}
