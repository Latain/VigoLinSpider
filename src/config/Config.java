package config;
import java.util.Properties;
import java.io.IOException; 
public class Config {
	//是否持久化到数据库
	public static boolean dbEnable;
	//登录豆瓣的邮箱
	public static String form_email;
	//登录豆瓣的密码
	public static String form_password;
	//序列化cookie的文件
	public static String cookiePath;
	//数据库主机
	public static String dbHost;
	//数据库用户
	public static String dbUser;
	//数据库用户密码
	public static String dbPassword;
	//保存验证码的文件路径
	public static String verifyCodePath;
	//数据库名
	public static String dbName;
    //下载线程数
	public static int downloadThreadSize;
	//下载网页数
	public static int downloadPageCount;
	//开始爬取的url
	public static String startUrl;
	//遇到禁止爬虫时的验证码
	public static String barrierCode;
	//指定标签电影数
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
