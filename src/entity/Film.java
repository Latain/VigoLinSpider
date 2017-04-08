package entity;

public class Film {
	private String filmName;
	private String director;
	private String actor;
	private String type;
	private String country;
	private String language;
	private String release_date;
	private String rating_num;
	private int rating_people;
	private String stars5;
	private String stars4;
	private String stars3;
	private String stars2;
	private String stars1;
	private String url;
	private String filmTime;
	public void setFilmName(String filmName){
		this.filmName=filmName;
	}
	public String getFilmName(){
		return filmName;
	}
	public void setDirector(String director){
		this.director=director;
	}
	public String getDirector(){
		return director;
	}
	public void setActor(String actor){
		this.actor=actor;
	}
	public String getActor(){
		return actor;
	}
	public void setType(String type){
		this.type=type;
	}
	public String getType(){
		return type;
	}
	public void setCountry(String country){
		this.country=country;
	}
	public String getCountry(){
		return country;
	}
	public void setLanguage(String language){
		this.language=language;
	}
	public String getLanguage(){
		return language;
	}
	public void setReleaseDate(String release_date){
		this.release_date=release_date;
	}
	public String getReleaseDate(){
		return release_date;
	}
	public void setRatingNum(String rating_num){
		this.rating_num=rating_num;
	}
	public String getRatingNum(){
		return rating_num;
	}
	public void setRatingPeople(int rating_people){
		this.rating_people=rating_people;
	}
	public int getRatingPeople(){
		return rating_people;
	}
	public void setStars5(String stars5){
		this.stars5=stars5;
	}
	public String getStars5(){
		return stars5;
	}
	public void setStars4(String stars4){
		this.stars4=stars4;
	}
	public String getStars4(){
		return stars4;
	}
	public void setStars3(String stars3){
		this.stars3=stars3;
	}
	public String getStars3(){
		return stars3;
	}
	public void setStars2(String stars2){
		this.stars2=stars2;
	}
	public String getStars2(){
		return stars2;
	}
	public void setStars1(String stars1){
		this.stars1=stars1;
	}
	public String getStars1(){
		return stars1;
	}
	public void setUrl(String url){
		this.url=url;
	}
	public String getUrl(){
		return url;
	}
	public void setFilmTime(String filmTime){
		this.filmTime=filmTime;
	}
	public String getFilmTime(){
		return filmTime;
	}
	@Override
	public String toString(){
		return "Film{"+"FilmName='"+filmName+'\''
				      + ",Director='"+director+'\''
				      +",Actor='"+actor+'\''
				      + ",Type='"+type+'\''
				      + ",Country='"+country+'\''
				      + ",Language='"+language+'\''
				      + ",ReleaseDate='"+release_date+'\''
				      + ",RatingNum='"+rating_num+'\''
				      + ",Rating_people='"+rating_people+'\''
				      + ",Stars5='"+stars5+'\''
				      + ",Stars4='"+stars4+'\''
				      + ",Stars3='"+stars3+'\''
				      + ",Stars2='"+stars2+'\''
				      + ",Stars1='"+stars1+'\''
				      +",Url='"+url+'\''
				      +"}";
	}
}
