package maintenance.agent.conf;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	
	public static final Logger logger = LoggerFactory.getLogger(Constants.class);
	/**
	 * 连接到local zookeeper server的字符串
	 */
	public static final String DEFAULT_Register_Center = "127.0.0.1:2181";
	
	public static String RegisterCenter; 
	
	public static final String SIGAR_LIB_NATIVE_ROOT = "sigar-lib";
	
	static{
		Properties prop = new Properties();
			try {
				prop.load(Constants.class.getClassLoader().getResourceAsStream("conf.properties"));
				RegisterCenter = prop.getProperty("RegisterCenter", DEFAULT_Register_Center);
			} catch (IOException e) {
				e.printStackTrace();
				RegisterCenter= DEFAULT_Register_Center;
			}
			logger.debug("RegisterCenter URL:"+RegisterCenter);
			
	}
	
	
}
