package douban;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import util.HttpClientUtil;
import util.MyLogger;
import config.Config;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import parser.UrlListParser;
public class ModelLoginDouban {
	private static final String INDEX_URL="https://www.douban.com/";
	private static final String LOGIN_URL="https://accounts.douban.com/login";
	private static Logger logger=MyLogger.getLogger(ModelLoginDouban.class);
	public void login(DoubanHttpClient doubanHttpClient) throws Exception {
		CloseableHttpClient httpclient=doubanHttpClient.getCloseableHttpClient();
		HttpClientContext context=doubanHttpClient.getHttpClientContext();
		Map<String,String> params=new HashMap<String,String>();
		params.put("source","index_nav");
		params.put("form_email",Config.form_email);
		params.put("form_password",Config.form_password);
		HttpGet httpget=new HttpGet(INDEX_URL);
		//���ʳ�ʼ��ҳ��
		String result=HttpClientUtil.getWebPage(httpclient, httpget, context, "UTF-8", false);
		Document doc=Jsoup.parse(result);
		Elements links=doc.select("img#captcha_image");
		String yzmUrl=links.attr("src");//��֤��url captcha-solution
		/*
		 * �жϳ�ʼ��ҳ����û����֤��
		 * ����У�����֤��һ��post
		 */
		if(yzmUrl!=""){
			int startIndex=yzmUrl.indexOf("=");
			int lastIndex=yzmUrl.indexOf("&");
			String str=yzmUrl.substring(startIndex+1,lastIndex);//captcha-id
			String yzm=verifiedCode(httpclient,context,yzmUrl);//��֤��
			params.put("captcha-solution",yzm);
			params.put("captcha-id",str);
		}
		
		HttpPost httppost=new HttpPost(LOGIN_URL);
	    HttpClientUtil.setHttpPost(httppost,params);
		HttpClientUtil.getWebPage(httpclient, httppost, context, "utf-8", false);//��¼
		httpget=new HttpGet("http://www.douban.com");
		String r=HttpClientUtil.getWebPage(httpclient,httpget,context,"utf-8",false);
		if(r.contains(Config.nickName)){
			logger.info("��½�ɹ�!");
			HttpClientUtil.serializeObject(context.getCookieStore(),Config.cookiePath);
		}else{
			logger.info("��¼ʧ�ܣ�");
			throw new RuntimeException("Login Failed!");
		}
	}
	public String verifiedCode(CloseableHttpClient httpclient,HttpClientContext context,String url){
		String path="C:\\Users\\Administrator\\Desktop\\yzm\\";
		String saveName="verify.gif";
		HttpClientUtil.downloadFile(httpclient, context, url, path, saveName, true);
		logger.info("������"+path+saveName+"�µ���֤��");
		Scanner scanner=new Scanner(System.in);
		String yzm=scanner.nextLine();
		return yzm;
	}
	public static void main(String[] args) throws Exception{
		ModelLoginDouban login=new ModelLoginDouban();
		login.login(DoubanHttpClient.getInstance());
		entity.Page page=DoubanHttpClient.getInstance().getWebPage("https://movie.douban.com/tag/%E7%88%B1%E6%83%85?start=20&type=T");
		System.out.println(UrlListParser.getInstance().parse(page));
	}
}
