package maintenance.agent.core;

import java.lang.management.MemoryMXBean;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


import maintenance.agent.core.jmx.IMBeanService;

public class MBeanService implements IMBeanService{
	
	@Override
	public void getMemoryMXBean(int jmxPort){
		if(jmxPort<0 || jmxPort > 65535){
			//throw new RpcException("Parma error : jmxPort is must between [0,65535]!");
		}
		
		JMXServiceURL url = null;
		JMXConnector jmxc = null;
		try {
			url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:"+jmxPort+"/jmxrmi");
			jmxc = JMXConnectorFactory.connect(url, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			System.out.println("\nDomains:");
			String domains[] = mbsc.getDomains();
			Arrays.sort(domains);
			for (String domain : domains) {
			   System.out.println("\tDomain = " + domain);
			}
			System.out.println("\nMBeanServer default domain = " + mbsc.getDefaultDomain());
			System.out.println("\nMBean count = " +  mbsc.getMBeanCount());
			System.out.println("\nQuery MBeanServer MBeans:");
			Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));
			for (ObjectName name : names) {
				System.out.println("\tObjectName = " + name);
			}
			ObjectName mxbeanName = new ObjectName ("java.lang:type=Memory");
			MemoryMXBean mxbeanProxy = JMX.newMXBeanProxy(mbsc, mxbeanName,  MemoryMXBean.class);
			System.out.println(mxbeanProxy.getHeapMemoryUsage());
			System.out.println(mxbeanProxy.getNonHeapMemoryUsage());
			System.out.println(mxbeanProxy.getObjectPendingFinalizationCount());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(jmxc!=null){
					jmxc.close();
					}
			}catch(Exception e){
				
			}
		}
		
		//return ManagementFactory.getMemoryMXBean();
	}

	
}
