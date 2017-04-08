package entity;

public class Page {
	private int statusCode;
	private String url;
	private String html;
	public void setUrl(String url){
		this.url=url;
	}
	public void setStatusCode(int statusCode){
		this.statusCode=statusCode;
	}
	public void setHtml(String html){
		this.html=html;
	}
	public int getStatusCode(){
		return statusCode;
	}
	public String getUrl(){
		return url;
	}
	public String getHtml(){
		return html;
	}
}
