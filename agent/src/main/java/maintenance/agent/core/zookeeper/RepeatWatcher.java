package maintenance.agent.core.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

public class RepeatWatcher implements CuratorWatcher{

	private CuratorFramework curatorFramework;
	
	public RepeatWatcher(CuratorFramework curatorFramework) {
		super();
		this.curatorFramework = curatorFramework;
	}

	@Override
	public void process(WatchedEvent event) throws Exception {
		System.out.println(event.getType());
		byte[] bytes = curatorFramework.getData().forPath(event.getPath());
		String result = new String(bytes);
		//ZookeeperSupport.BUFF = result;
		//ZookeeperSupport.logger.info(ZookeeperSupport.BUFF);
		curatorFramework.getData().usingWatcher(this).forPath(event.getPath());
	}

	
	
	
	
}
