package Main;
import douban.DoubanHttpClient;
public class Main {
	public static void main(String[] args){
		DoubanHttpClient.getInstance().startSpider();
	}
}
