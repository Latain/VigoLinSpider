package sqlconnection;
import util.MyLogger;
import org.apache.log4j.Logger;
import config.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionManage {
	private static Logger logger=MyLogger.getLogger(ConnectionManage.class);
	private static Connection conn;
	public static Connection createConnection(){
		Connection con=null;
		String url="jdbc:mysql://"+Config.dbHost+":3306/"+Config.dbName+"?autoReconnect=true&useSSL=false";
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection(url,Config.dbUser,Config.dbPassword);
			//logger.info("Success!");
		}catch(ClassNotFoundException e){
			logger.error("ClassNotFoundException",e);
		}catch(SQLException e){
			logger.error("SQLException",e);
		}
		return con;
	}
	public static synchronized Connection getConnection(){
		try{
			if(conn==null || conn.isClosed()){
				conn=createConnection();
			}else{
				return conn;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return conn;
	}
	public static void close(){
		if(conn!=null){
			try{
				conn.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
}
