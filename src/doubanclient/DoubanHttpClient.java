package doubanclient;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.io.File;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;

import util.HttpClientUtil;
/*
 * �����Ĵ�����Ժ��ԣ�һ��ʼ��¼ʱ�׳�java.security.InvalidAlgorithmParameterException�쳣�������������Ե�
 * ��Ҫ����jdk��1.6���ϾͿ��Խ��������
 */
public class DoubanHttpClient {
	private static final String INDEX_URL="https://movie.douban.com/";
	private static final String SSLSOCKET_PATH="C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\sslsocket.keystore";
	private static final String TRUST_PATH="C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\trust.keystore";
	private static final String PASSWORD="changeit";
	public static CloseableHttpClient getDoubanHttpClient() throws Exception{
		KeyStore sslsocket=KeyStore.getInstance("JKS");
		FileInputStream in=new FileInputStream(new File(SSLSOCKET_PATH));
		try{
			sslsocket.load(in,PASSWORD.toCharArray());
		}
		finally{
			in.close();
		}
		KeyStore trustStore=KeyStore.getInstance("JKS");
		FileInputStream input=new FileInputStream(new File(TRUST_PATH));
		try{
			trustStore.load(input,PASSWORD.toCharArray());
		}
		finally{
			input.close();
		}
		KeyManagerFactory kmf=KeyManagerFactory.getInstance("SunX509");
		kmf.init(sslsocket,PASSWORD.toCharArray());
		KeyManager[] km=kmf.getKeyManagers();
		TrustManagerFactory tmf=TrustManagerFactory.getInstance("SunX509");
		tmf.init(trustStore);
		TrustManager[] tm=tmf.getTrustManagers();
		SSLContext sslContext=SSLContext.getInstance("SSL");
		sslContext.init(km, tm, new SecureRandom());
		SSLConnectionSocketFactory sslsf=new SSLConnectionSocketFactory(sslContext);
		Registry<ConnectionSocketFactory> registry=RegistryBuilder.<ConnectionSocketFactory>create()
				                                                  .register("https",sslsf)
				                                                  .build();
		PoolingHttpClientConnectionManager connManager=new PoolingHttpClientConnectionManager(registry);
		connManager.setMaxTotal(200);
		connManager.setDefaultMaxPerRoute(20);
		CloseableHttpClient httpclient=HttpClients.custom()
				                                  .setConnectionManager(connManager)
				                                  .build();
		return httpclient;
	}
	public static void main(String[] args) throws Exception {
		CloseableHttpClient httpclient=HttpClientUtil.getMyHttpClient();
		HttpGet httpget=new HttpGet(INDEX_URL);
		HttpClientContext context=HttpClientUtil.getMyContext();
		String result=HttpClientUtil.getWebPage(httpclient, httpget, context, "UTF-8", true);
	}
}
