package parser;
import entity.Film;
import entity.Page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class DoubanFilmIndexDetailPageParser extends DetailPageParser{
	private static DoubanFilmIndexDetailPageParser detailParser;
	public static DoubanFilmIndexDetailPageParser getInstance(){
		if(detailParser==null){
			detailParser=new DoubanFilmIndexDetailPageParser();
		}
		return detailParser;
	}
	@Override 
	public Film parse(Page page){
		//若包含episode_list标签即为连续剧，刷选
		if(page.getHtml().contains("episode_list"))
			return null;
		Document doc=Jsoup.parse(page.getHtml());
	    return parseFilmDetail(doc,page.getUrl());
	}
	private Film parseFilmDetail(Document doc,String url){
			Film film=new Film();
			try{
				Elements filmName=doc.select("title");
				film.setFilmName(filmName.text());//电影名字
				//电影信息标签，包括导演，主演，影片类型，片长等等
				Elements filmInformation=doc.select("div#info");
				Elements director=filmInformation.select("a[rel=v:directedBy]");
		        film.setDirector(director.text());//导演
		        Elements actor=filmInformation.select("span.actor").select("span.attrs");
		        film.setActor(actor.text());//主演
		        Elements type=filmInformation.select("span[property=v:genre]");
		        if(type.text().contains("真人秀") || type.text().contains("脱口秀")){
		        	return null;
		        }else{
		        	film.setType(type.text());//影片类型
		        }
		        //片长
		        try{
		        	if(filmInformation.select("span[property=v:runtime]").text().contains("2017"))
		        		return null;
		        	film.setFilmTime(filmInformation.select("span[property=v:runtime]").text());
		        }catch(Exception e){
		        	film.setFilmTime("Unknown");
		        }
		        String allInformation=filmInformation.text();
		        //影片所属国家
		        try{
		        	film.setCountry(allInformation.substring(allInformation.indexOf("制片国家/地区:")+8,allInformation.indexOf("语言:")));
		        }catch(Exception e){
		        	film.setCountry("Unknown");
		        }
		        //影片语言类型
		        try{
		        	film.setLanguage(allInformation.substring(allInformation.indexOf("语言:")+3,allInformation.indexOf("上映日期:")));
		        }catch(Exception e){
		        	film.setLanguage("Unknown");
		        }
		        //发行时间
		        Elements releaseDate=filmInformation.select("span[ property=v:initialReleaseDate]");
		        film.setReleaseDate(releaseDate.text());
		        //评分标签
		        Elements selection=doc.select("div#interest_sectl");
		        Elements rating_num=selection.select("strong[property=v:average]");
		        film.setRatingNum(rating_num.text());//评分
		        Elements rating_people=selection.select("span[property=v:votes]");
		        try{
		        	film.setRatingPeople(Integer.parseInt(rating_people.text()));//评分人数
		        }catch(Exception e){
		        	film.setRatingPeople(0);
		        }
		        Elements vote=selection.select("span.rating_per");
		        try{
		        	film.setStars5(vote.get(0).text());//5星占比
			        film.setStars4(vote.get(1).text());//4星占比
			        film.setStars3(vote.get(2).text());//3星占比
			        film.setStars2(vote.get(3).text());//2星占比
			        film.setStars1(vote.get(4).text());//1星占比
		        }catch(Exception e){
		        	//
		        }
		        film.setUrl(url);
			}catch(Exception e){
				e.printStackTrace();
			}
	        return film;
	}
}
