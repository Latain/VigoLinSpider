package parser;
import java.util.List;
import java.util.ArrayList;
import entity.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class UrlListParser extends ListPageParser{
	private static UrlListParser urlListParser;
	public static UrlListParser getInstance(){
		if(urlListParser==null)
			urlListParser=new UrlListParser();
		return urlListParser;
	} 
	public List<String> parse(Page page){
		List<String> urlList=new ArrayList<String>();
		String entity=page.getHtml();
		if(entity!=null){
			Document doc=Jsoup.parse(entity);
			Elements elements=doc.select("a.nbg");
			for(int i=0;i<elements.size();i++){
				urlList.add(elements.get(i).attr("href"));
			}
		}
		return urlList;
	}
}
