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
	 * ��ȡ��ҳ������ҳ��Ϣת����String
	 * @param httpclient �ͻ���
	 * @param request ����
	 * @param context ִ��������
	 * @param encoding ������ʽ
	 * @param printToConsole �Ƿ��ӡ������̨
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
	 * ���ÿͻ��˵�cookie����
	 * @param 
	 * @return CloseableHttpClient
	 */
	public static CloseableHttpClient getMyHttpClient(){
		CloseableHttpClient httpclient=null;
		LaxRedirectStrategy redirectStrategy=new LaxRedirectStrategy();
		RequestConfig config=RequestConfig.custom()
				                          .setCookieSpec("browser_compatibility")
				                          .build();
		//���ӹ����
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
	 * ��ȡ���Ƶ�ִ��������
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
	 * ���л������ƶ������ļ���
	 * @param object �����л�����
	 * @param path �ļ�·��
	 * @return void
	 */
	public static void serializeObject(Object object,String path){
		ObjectOutputStream out=null;
		try{
			BufferedOutputStream buffered=new BufferedOutputStream(new FileOutputStream(new File(path)));
			out=new ObjectOutputStream(buffered);
			out.writeObject(object);
			logger.info("���л��ɹ�");
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
	 * ��ָ���ļ��з����л�����
	 * @param path �ƶ��ļ�·��
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
	 * ������֤��
	 * @param httpclient �ͻ���
	 * @param context ִ��������
	 * @param fileUrl ��֤��·��
	 * @param path ��֤��洢·��
	 * @param saveName ��֤��洢�ļ���
	 * @param isReplaceFile �Ƿ񸲸�ԭ���ļ�
	 * @return 
	 */
	public static void downloadFile(CloseableHttpClient httpclient,HttpClientContext context,String fileUrl,String path,String saveName,boolean isReplaceFile){
		try{
			HttpGet httpget=new HttpGet(fileUrl);
			CloseableHttpResponse response=httpclient.execute(httpget);
			logger.info("StatusCode:"+response.getStatusLine().getStatusCode());
			File directory=new File(path);
			if(!directory.exists() && !directory.isDirectory()){
				logger.info("�ļ��в����ڣ����ڴ���������");
				directory.mkdir();
			}else{
				logger.info("�ļ����Ѵ���");
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
						logger.info("�ļ����������С�����");
					}
					in.close();
					buffered.close();
					logger.info("�ļ��ɹ�������"+path);
					httpget.releaseConnection();
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				logger.info("�ļ��Ѵ���");
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
	 * ���cookie
	 * @param store cookie�̵�
	 * @return
	 */
	public static void getCookies(CookieStore store){
		List<Cookie> list=store.getCookies();
		if(list!=null){
			for(int i=0;i<list.size();i++){
				logger.info("Cookie:"+list.get(i).getName()+":"+list.get(i).getValue()+"\n����ʱ��:"+list.get(i).getExpiryDate()+"\nComment:"+list.get(i).getComment()
						    +"\nCommentURL:"+list.get(i).getCommentURL()+"\nDomain:"+list.get(i).getDomain()+"\nPorts:"+list.get(i).getPorts()+"\n\n");
			}
		}else{
			logger.info("��CookieStore��Cookie");
		}
	}
	/*
	 * ��ȡͷ��Ϣ
	 * @param headers ͷ��Ϣ����
	 * @return 
	 */
	public static void getHeaders(Header[] headers){
		if(headers==null){
			logger.info("��headers��Header");
		}else{
			logger.info("......ͷ��Ϣ��ʼ.....");
			for(int i=0;i<headers.length;i++){
				logger.info(headers[i]);
			}
			logger.info(".......ͷ��Ϣ����.......");
		}
	}
	/*
	 * ��InputStreamװ����String
	 * @param in ������
	 * @param encoding �ƶ�������ʽ
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
	 * ����Post�������
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
