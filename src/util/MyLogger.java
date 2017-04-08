package util;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
public class MyLogger extends Logger{
	public MyLogger(String name){
		super(name);
	}
	public static Properties getProperties(){
		Properties prop=new Properties();
		prop.setProperty("log4j.rootLogger","INFO,console");
		prop.setProperty("log4j.appender.console","org.apache.log4j.ConsoleAppender");
		prop.setProperty("log4j.appender.console.layout","org.apache.log4j.PatternLayout");
		prop.setProperty("log4j.appender.console.layout.ConversionPattern","[%r][%t][%p]-%c-%l-%m%n");
		//prop.setProperty("log4j.appender.file","org.apache.log4j.FileAppender");
		//prop.setProperty("log4j.appender.file.File","C:\\Users\\Administrator\\Desktop\\log.txt");
		//prop.setProperty("log4j.appender.file.layout","org.apache.log4j.TTCCLayout");
		return prop;
	}
	public static Logger getLogger(Class<?> type){
		Logger logger=Logger.getLogger(type);
		PropertyConfigurator.configure(getProperties());
		return logger;
	}
}
