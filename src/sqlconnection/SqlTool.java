package sqlconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import util.MyLogger;
import entity.Film;
public class SqlTool {
	private static Logger logger=MyLogger.getLogger(SqlTool.class);
	/*
	 * 判断某电影是否已存入数据库中
	 * @param conn
	 * @param sql sql语句查询
	 * @return 
	 */
	public static synchronized boolean isContained(Connection conn,String sql){
		try{
			PreparedStatement state=conn.prepareStatement(sql);
			ResultSet result=state.executeQuery();
			int num;
			while(result.next()){
				num=result.getInt("count(*)");
				result.close();
				state.close();
				conn.close();
				if(num==0){
					return false;
				}else{
					return true;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	public static synchronized boolean insertIntoDB(Film film){
		Connection conn=ConnectionManage.getConnection();
	    String column="film_name,director,actor,type,country,language,release_date,rating_people,stars5,stars4,stars3,stars2,stars1,url,rating_num,film_time";
		String values="?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		String sql="insert into film_compatable("+column+") values("+values+")";
		PreparedStatement state;
		try{
			state=conn.prepareStatement(sql);
			state.setString(1,film.getFilmName());
			state.setString(2,film.getDirector());
			state.setString(3,film.getActor());
			state.setString(4,film.getType());
			state.setString(5,film.getCountry());
			state.setString(6,film.getLanguage());
			state.setString(7,film.getReleaseDate());
			state.setString(8,String.valueOf(film.getRatingPeople()));
			state.setString(9,film.getStars5());
			state.setString(10,film.getStars4());
			state.setString(11,film.getStars3());
			state.setString(12,film.getStars2());
			state.setString(13,film.getStars1());
			state.setString(14,film.getUrl());
			state.setString(15,film.getRatingNum());
			state.setString(16,film.getFilmTime());
			state.executeUpdate();
			state.close();
			conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	/*
	 * 将访问过的url插入Href表
	 * @param url
	 * @return 
	 */
	public static synchronized boolean insertIntoHref(String url){
		Connection conn=ConnectionManage.getConnection();
		try{
			String sql="insert into href (href) VALUES('"+url+"')";
			Statement state=conn.createStatement();
			state.executeUpdate(sql);
			state.close();
			conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return true;
	}
	/*
	 * 清空Href表
	 * @param
	 * @return
	 */
	public static synchronized void deleteHrefTable(){
		String sql="DELETE FROM href";
		Connection conn=ConnectionManage.getConnection();
		try{
			PreparedStatement state=conn.prepareStatement(sql);
			state.executeUpdate();
			state.close();
			conn.close();
			logger.info("删除表成功!");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
