package maintenance.agent.core.zookeeper;


import org.apache.curator.framework.CuratorFramework;

public abstract class AbsZKClient{
	
	
	
	
	/*连接状态断开*/
	public void connectionState_LOST(CuratorFramework curatorFramework){
		
	}
	/*连接建立*/
	public void connectionState_CONNECTED(CuratorFramework curatorFramework){
		
	}
	/*连接恢复*/
	public void connectionState_RECONNECTED(CuratorFramework curatorFramework){
		
	}
	
	
	
	
}
