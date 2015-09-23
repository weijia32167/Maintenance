package maintenance.agent.core;


import maintenance.agent.conf.Constants;
import maintenance.agent.core.lifecycle.AbsLifeCycle;
import maintenance.agent.core.lifecycle.LifeCycleException;
import maintenance.agent.core.zookeeper.CuratorZKClient;

public class Agent extends AbsLifeCycle {

	private String zookeeperURL = Constants.DEFAULT_ZOOKEEPER_URL;

	private CuratorZKClient zkClient;

	public Agent() {
		
	}

	public void setZookeeperURL(String zookeeperURL) {
		this.zookeeperURL = zookeeperURL;
	}

	@Override
	protected void doInit() throws LifeCycleException {
		zkClient = new CuratorZKClient(zookeeperURL);
	}

	@Override
	protected void doStart() throws LifeCycleException {
		zkClient.start();
	}

	@Override
	protected void doStop() throws LifeCycleException {
		zkClient.stop();
	}
	
	
}
