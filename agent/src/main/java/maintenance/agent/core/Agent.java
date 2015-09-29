package maintenance.agent.core;


import maintenance.agent.conf.Constants;
import maintenance.agent.core.lifecycle.AbsLifeCycle;
import maintenance.agent.core.lifecycle.ILifeCycle;
import maintenance.agent.core.lifecycle.LifeCycleException;
import maintenance.agent.core.monitor.MonitorInfoCollect;
import maintenance.agent.core.zookeeper.CuratorZKClient;
import maintenance.agent.core.zookeeper.ZNode;
import maintenance.agent.core.zookeeper.ZookeeperConnectionListener;
import maintenance.agent.core.zookeeper.ZookeeperConnectionStateEvent;

import org.apache.zookeeper.CreateMode;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Agent extends AbsLifeCycle {

	private String zookeeperURL = Constants.RegisterCenter;
	
	private MonitorInfoCollect monitorInfoCollect;

	private CuratorZKClient zkClient;
	
	private ZNode sessionZnode;
	
	@Override
	protected void doInit() throws LifeCycleException {
		zkClient = new CuratorZKClient(zookeeperURL);
		zkClient.addListener(new ZookeeperConnectionListener() {
			@Override
			public void trigger(ZookeeperConnectionStateEvent event) {
				log.info(event.toString());
			}
		});
		this.addListener(new ILifeCycle.Listener(){

			@Override
			public void trigger(LifeCycleEvent event) {

				log.info(event.toString());
			}
		});
	}

	@Override
	protected void doStart() throws LifeCycleException {
		zkClient.start();
		try {
			ZNode sessionRootZNnode = getOrCreateSessionRootZNdode();
			ZNode fqdnZNode = registerFQDN(Constants.sigar);
			/*很奇怪的问题,似乎需要阻塞一会等待某种资源的建立完成，否则后续的ZNode创建失败*/
			Thread.sleep(1000);
			String sessionPath = sessionRootZNnode.getPath()+fqdnZNode.getPath();
			System.out.println(1);
			sessionZnode = zkClient.getOrCreateZNode(sessionPath, null, CreateMode.EPHEMERAL);
			monitorInfoCollect = new MonitorInfoCollect(fqdnZNode,zkClient,Constants.Monitor_Cycle);
			monitorInfoCollect.start();
		} catch (SigarException e) {
			throw new LifeCycleException(e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new LifeCycleException(e);
		}
	}

	@Override
	protected void doStop() throws LifeCycleException {
		zkClient.stop();
	}


	private ZNode registerFQDN(Sigar sigar) throws SigarException,Exception{
		String FQDN = sigar.getFQDN();
		return zkClient.getOrCreateZNode("/"+FQDN, null, CreateMode.PERSISTENT);
	}
	
	
	private ZNode getOrCreateSessionRootZNdode() throws Exception{
		return zkClient.getOrCreateZNode("/"+Constants.SessionPath, null, CreateMode.PERSISTENT);
	}
	
}
