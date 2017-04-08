package config;
import java.util.Properties;
import java.io.IOException; 
public class Config {
	//�Ƿ�־û������ݿ�
	public static boolean dbEnable;
	//��¼���������
	public static String form_email;
	//��¼���������
	public static String form_password;
	//���л�cookie���ļ�
	public static String cookiePath;
	//���ݿ�����
	public static String dbHost;
	//���ݿ��û�
	public static String dbUser;
	//���ݿ��û�����
	public static String dbPassword;
	//������֤����ļ�·��
	public static String verifyCodePath;
	//���ݿ���
	public static String dbName;
    //�����߳���
	public static int downloadThreadSize;
	//������ҳ��
	public static int downloadPageCount;
	//��ʼ��ȡ��url
	public static String startUrl;
	//������ֹ����ʱ����֤��
	public static String barrierCode;
	//ָ����ǩ��Ӱ��
	public static int tagFilmCount;
	public static String nickName;
	static{
		Properties p=new Properties();
		try{
			p.load(Config.class.getResourceAsStream("/property/config.properties"));
		}catch(IOException e){
			e.printStackTrace();
		}
		dbEnable= Boolean.parseBoolean(p.getProperty("db.enable"));
		form_email=p.getProperty("form_email");
		form_password=p.getProperty("form_password");
		cookiePath=p.getProperty("cookiepath");
		verifyCodePath=p.getProperty("verifycodepath");
		downloadThreadSize=Integer.parseInt(p.getProperty("downloadthreadsize"));
		downloadPageCount=Integer.parseInt(p.getProperty("downloadpagecount"));
		tagFilmCount=Integer.parseInt(p.getProperty("tagfilmcount"));
		startUrl=p.getProperty("start_url");
		barrierCode=p.getProperty("barriercode");
		nickName=p.getProperty("nick_name");
		if(dbEnable){
			dbName=p.getProperty("db.name");
			dbHost=p.getProperty("db.host");
			dbUser=p.getProperty("db.user");
			dbPassword=p.getProperty("db.password");
		}
	}
	public static void main(String[] args){
		System.out.println(dbUser);
		System.out.println(form_email);
	}
}
