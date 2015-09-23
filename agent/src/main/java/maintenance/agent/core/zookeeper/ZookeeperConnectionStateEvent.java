package maintenance.agent.core.zookeeper;

import maintenance.agent.core.listener.StateEvent;

import org.apache.curator.framework.CuratorFramework;

public class ZookeeperConnectionStateEvent extends StateEvent<CuratorFramework, ZookeeperConnectionState>{

	private static final long serialVersionUID = 5611835809299708745L;

	public ZookeeperConnectionStateEvent(CuratorFramework source,ZookeeperConnectionState oldState,ZookeeperConnectionState newState, long time) {
		super(source, oldState, newState, time);
	}

	public ZookeeperConnectionStateEvent(CuratorFramework source,ZookeeperConnectionState oldState, ZookeeperConnectionState newState) {
		super(source, oldState, newState);
	}

}
