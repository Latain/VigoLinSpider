package parser;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import entity.Page;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonIOException;

import org.apache.log4j.Logger;
import util.MyLogger;
public class JsonDataParser extends ListPageParser{
	private static Logger logger=MyLogger.getLogger(JsonDataParser.class);
	private static JsonDataParser jsonParser;
	public static JsonDataParser getInstance(){
		if(jsonParser==null){
			jsonParser=new JsonDataParser();
		}
		return jsonParser;
	}
	@Override
	public List<String> parse(Page page){
		List<String> listUrl=new ArrayList<String>();
		try{
			JsonParser jParser=new JsonParser();
			JsonObject jObject=(JsonObject)jParser.parse(page.getHtml());
			JsonArray jArray=jObject.get("subjects").getAsJsonArray();
			for(int i=0;i<jArray.size();i++){
				JsonObject object=jArray.get(i).getAsJsonObject();
				listUrl.add(object.get("url").getAsString());
			}
		}catch(JsonIOException e){
			logger.error("JsonIOException",e);
		}catch(JsonSyntaxException e){
			logger.error("JsonSyntaxException",e);
		}
		return listUrl;
	}
	public static void main(String[] args) throws Exception{
		BufferedReader buffer=new BufferedReader(new FileReader(new File("C:\\Users\\Administrator\\Desktop\\test.txt")));
		StringBuilder result=new StringBuilder();
		String line;
		while((line=buffer.readLine())!=null){
			result.append(line);
		}
		buffer.close();
		Page page=new Page();
		page.setHtml(result.toString());
		for(String url:getInstance().parse(page)){
			logger.info(url);
		}
		//System.out.println("...");
	}
}
