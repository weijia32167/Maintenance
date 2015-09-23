package dubbo.customer;

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class MBeanServiceTest {
	
	@Autowired
	private IMBeanService mBeanService;
	public static void echo(String str){
		System.out.println(str);
	}
	
	@Test
	public final void init() throws Exception {
		mBeanService.getMemoryMXBean(9999);
		/*MemoryMXBean memoryMXBean = mBeanService.getMemoryMXBean();
		System.out.println(memoryMXBean);*/
		/*JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		System.out.println("\nDomains:");
		String domains[] = mbsc.getDomains();
		Arrays.sort(domains);
		for (String domain : domains) {
		   System.out.println("\tDomain = " + domain);
		}
		echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());
		echo("\nMBean count = " +  mbsc.getMBeanCount());
		echo("\nQuery MBeanServer MBeans:");
		Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));
		for (ObjectName name : names) {
		    echo("\tObjectName = " + name);
		}
		ObjectName mxbeanName = new ObjectName ("java.lang:type=Memory");
		MemoryMXBean mxbeanProxy = JMX.newMXBeanProxy(mbsc, mxbeanName,  MemoryMXBean.class);
		System.out.println(mxbeanProxy.getHeapMemoryUsage());*/
		
	}
}
