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
		//������episode_list��ǩ��Ϊ�����磬ˢѡ
		if(page.getHtml().contains("episode_list"))
			return null;
		Document doc=Jsoup.parse(page.getHtml());
	    return parseFilmDetail(doc,page.getUrl());
	}
	private Film parseFilmDetail(Document doc,String url){
			Film film=new Film();
			try{
				Elements filmName=doc.select("title");
				film.setFilmName(filmName.text());//��Ӱ����
				//��Ӱ��Ϣ��ǩ���������ݣ����ݣ�ӰƬ���ͣ�Ƭ���ȵ�
				Elements filmInformation=doc.select("div#info");
				Elements director=filmInformation.select("a[rel=v:directedBy]");
		        film.setDirector(director.text());//����
		        Elements actor=filmInformation.select("span.actor").select("span.attrs");
		        film.setActor(actor.text());//����
		        Elements type=filmInformation.select("span[property=v:genre]");
		        if(type.text().contains("������") || type.text().contains("�ѿ���")){
		        	return null;
		        }else{
		        	film.setType(type.text());//ӰƬ����
		        }
		        //Ƭ��
		        try{
		        	if(filmInformation.select("span[property=v:runtime]").text().contains("2017"))
		        		return null;
		        	film.setFilmTime(filmInformation.select("span[property=v:runtime]").text());
		        }catch(Exception e){
		        	film.setFilmTime("Unknown");
		        }
		        String allInformation=filmInformation.text();
		        //ӰƬ��������
		        try{
		        	film.setCountry(allInformation.substring(allInformation.indexOf("��Ƭ����/����:")+8,allInformation.indexOf("����:")));
		        }catch(Exception e){
		        	film.setCountry("Unknown");
		        }
		        //ӰƬ��������
		        try{
		        	film.setLanguage(allInformation.substring(allInformation.indexOf("����:")+3,allInformation.indexOf("��ӳ����:")));
		        }catch(Exception e){
		        	film.setLanguage("Unknown");
		        }
		        //����ʱ��
		        Elements releaseDate=filmInformation.select("span[ property=v:initialReleaseDate]");
		        film.setReleaseDate(releaseDate.text());
		        //���ֱ�ǩ
		        Elements selection=doc.select("div#interest_sectl");
		        Elements rating_num=selection.select("strong[property=v:average]");
		        film.setRatingNum(rating_num.text());//����
		        Elements rating_people=selection.select("span[property=v:votes]");
		        try{
		        	film.setRatingPeople(Integer.parseInt(rating_people.text()));//��������
		        }catch(Exception e){
		        	film.setRatingPeople(0);
		        }
		        Elements vote=selection.select("span.rating_per");
		        try{
		        	film.setStars5(vote.get(0).text());//5��ռ��
			        film.setStars4(vote.get(1).text());//4��ռ��
			        film.setStars3(vote.get(2).text());//3��ռ��
			        film.setStars2(vote.get(3).text());//2��ռ��
			        film.setStars1(vote.get(4).text());//1��ռ��
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
