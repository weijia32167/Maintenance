package maintenance.agent.core.zookeeper;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import maintenance.agent.core.listener.EventSupport;

import org.apache.curator.framework.CuratorFramework;

public abstract class AbsZKClient{
	
	protected EventSupport<ZookeeperConnectionListener> eventSupport = new EventSupport<>();
	
	
	/**********************EventSupport proxy method***********************************/
	public void addListener(ZookeeperConnectionListener listener){
		eventSupport.addListener(listener);
	}
	
	public void removeListener(ZookeeperConnectionListener listener){
		eventSupport.removeListener(listener);
	}
	
	private void notifyListeners(ZookeeperConnectionStateEvent zookeeperConnectionStateEvent){
		CompletableFuture.runAsync(()->{eventSupport.notifyListeners(zookeeperConnectionStateEvent);}, ForkJoinPool.commonPool());
	}

	
	/*连接建立*/
	public void connectionState_CONNECTED(CuratorFramework curatorFramework){
		ZookeeperConnectionStateEvent event = new ZookeeperConnectionStateEvent(curatorFramework,ZookeeperConnectionState.CONNECTED);
		notifyListeners(event);
	}
	
	/*连接状态断开*/
	public void connectionState_LOST(CuratorFramework curatorFramework){
		ZookeeperConnectionStateEvent event = new ZookeeperConnectionStateEvent(curatorFramework,ZookeeperConnectionState.CONNECTED,ZookeeperConnectionState.LOST);
		notifyListeners(event);
	}
	
	/*连接恢复*/
	public void connectionState_RECONNECTED(CuratorFramework curatorFramework){
		ZookeeperConnectionStateEvent event = new ZookeeperConnectionStateEvent(curatorFramework,ZookeeperConnectionState.SUSPENDED,ZookeeperConnectionState.RECONNECTED);
		notifyListeners(event);
	}
	
	
	
	
}
