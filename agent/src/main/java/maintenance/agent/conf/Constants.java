package maintenance.agent.conf;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import maintenance.agent.core.context.AgentConext;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	
	public static final Logger logger = LoggerFactory.getLogger(Constants.class);
	/**
	 * 连接到local zookeeper server的字符串
	 */
	public static final String Default_Register_Center = "127.0.0.1:2181";
	
	public static String RegisterCenter; 
	
	public static final String SIGAR_LIB_NATIVE_ROOT = "sigar-lib";
	
	public static Sigar sigar;
	
	public static final String MonitorPath = "Monitor";
	
	public static final String CPUPath = "CPUs";
	
	public static final String SessionPath = "Sessions";
	
	public static long Default_Monitor_Cycle=1000;
	
	public static long Monitor_Cycle; 

	static{
		Properties prop = new Properties();
			try {
				prop.load(Constants.class.getClassLoader().getResourceAsStream("conf.properties"));
				RegisterCenter = prop.getProperty("RegisterCenter", Default_Register_Center);
				Monitor_Cycle = Long.parseLong(prop.getProperty("MonitorCycle", Default_Monitor_Cycle+""));
			} catch (IOException e) {
				logger.error("conf.properties error,System apply default Configurtion!");
				RegisterCenter= Default_Register_Center;
				Monitor_Cycle= Default_Monitor_Cycle;
			}
			logger.debug("RegisterCenter URL:"+RegisterCenter);
			String libpath = Constants.SIGAR_LIB_NATIVE_ROOT+ System.getProperty("file.separator")+ getSigarNativeFileName();
			String rootPath = AgentConext.class.getClassLoader().getResource("").getPath();
			File file = new File(rootPath, libpath);
			Runtime.getRuntime().load(file.getAbsolutePath());
			sigar = new  Sigar();
	}
	
	public static final String getSigarNativeFileName() {
		String OSName = System.getProperty("os.name");
		String OSArch = System.getProperty("os.arch");
		StringBuffer sb = new StringBuffer();
		sb.append("sigar-");
		sb.append(OSArch + "-");
		if (OSName.contains("freebsd-6")) {
			sb.append("freebad-6.so");
		} else if (OSName.contains("linux")) {
			sb.append("linux.so");
		} else if (OSName.contains("solaris")) {
			sb.append("solaris.so");
		} else if (OSName.contains("hpux-11")) {
			sb.append("hpux-11.sl");
		} else if (OSName.contains("aix-5")) {
			sb.append("aix-5.so");
		} else if (OSName.contains("macosx")) {
			sb.append("macosx.dylib");
		} else if (OSName.contains("freebsd-6")) {
			sb.append("freebsd-5.so");
		} else if (OSName.contains("Windows")) {
			sb.append("winnt.dll");
		}
		return sb.toString();
	}
	
	
}
