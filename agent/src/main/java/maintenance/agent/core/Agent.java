package maintenance.agent.core;


import org.hyperic.sigar.SigarException;

import maintenance.agent.conf.Constants;
import maintenance.agent.core.lifecycle.AbsLifeCycle;
import maintenance.agent.core.lifecycle.LifeCycleException;
import maintenance.agent.core.monitor.CPUMonitor;
import maintenance.agent.core.zookeeper.CuratorZKClient;
import maintenance.agent.core.zookeeper.ZookeeperConnectionListener;
import maintenance.agent.core.zookeeper.ZookeeperConnectionStateEvent;

public class Agent extends AbsLifeCycle implements CPUMonitor{

	private String zookeeperURL = Constants.RegisterCenter;

	private CuratorZKClient zkClient;
	
	@Override
	protected void doInit() throws LifeCycleException {
		zkClient = new CuratorZKClient(zookeeperURL);
		zkClient.addListener(new ZookeeperConnectionListener() {
			@Override
			public void trigger(ZookeeperConnectionStateEvent event) {
				log.info(event.toString());
			}
		});
	}

	@Override
	protected void doStart() throws LifeCycleException {
		zkClient.start();
		
	}

	@Override
	protected void doStop() throws LifeCycleException {
		zkClient.stop();
	}

	@Override
	public void registerCPUList() throws SigarException {
		
	}

	@Override
	public void registerCpuInfoList() throws SigarException {
		
	}

	@Override
	public void registerCpuPercList() throws SigarException {
		
	}

	
	

	
}
