package maintenance.agent.core.zookeeper;


import maintenance.agent.conf.Constants;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CuratorZKClient extends AbsZKClient{

	public static final Logger logger = LoggerFactory.getLogger(CuratorZKClient.class);
	
	public static final String DEFAULT_NAMESPACE = "Agents";
	
	private CuratorFramework curatorFramework;
	
	private RetryPolicy retryPolicy;
	
	private ACLProvider aclProvider;

	public CuratorZKClient(String connectionURL){
		aclProvider = new DefaultACLProvider();
		retryPolicy = new RetryNTimes(Integer.MAX_VALUE, 1000);
		Builder builder =  CuratorFrameworkFactory.builder()
						   .connectString(connectionURL)
						   .namespace(DEFAULT_NAMESPACE)
						   .aclProvider(aclProvider)
						   .retryPolicy(retryPolicy)
						   .connectionTimeoutMs(5000)
						   .sessionTimeoutMs(5000)
						   .authorization("digest", "agent".getBytes());
		curatorFramework = builder.build();
	}
	
	
	public CuratorZKClient(){
		aclProvider = new DefaultACLProvider();
		retryPolicy = new RetryNTimes(Integer.MAX_VALUE, 1000);
		Builder builder =  CuratorFrameworkFactory.builder().
						   connectString(Constants.Default_Register_Center).
						   namespace(DEFAULT_NAMESPACE).
						   aclProvider(aclProvider).
						   retryPolicy(retryPolicy).
						   connectionTimeoutMs(5000).
						   sessionTimeoutMs(1000).
						   authorization("digest", "agent".getBytes());
		curatorFramework = builder.build();
	}
	

	public void start() {
		curatorFramework.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
			@Override
			public void unhandledError(String message, Throwable e) {
				logger.info(message);
				e.printStackTrace();
			}
		});

		
		curatorFramework.getCuratorListenable().addListener(new CuratorListener() {
			
			@Override
			public void eventReceived(CuratorFramework client, CuratorEvent event)throws Exception {
				logger.info("abc:"+event.toString());
				CuratorEventType curatorEventType = event.getType();
				logger.info("abc:"+curatorEventType.toString());
				
				if(curatorEventType==CuratorEventType.WATCHED){
					WatchedEvent watchEvent = event.getWatchedEvent();
					logger.info(watchEvent.toString());
					KeeperState keeperState = watchEvent.getState();
				}else if(curatorEventType==CuratorEventType.CLOSING){
					client.getZookeeperClient().getZooKeeper().close();
					//client.getZookeeperClient().close();
					client.close();
					logger.info("org.apache.curator.framework.imps.CuratorFrameworkState:"+client.getState().toString());
					logger.info("org.apache.zookeeper.States:"+client.getZookeeperClient().getZooKeeper().getState().toString());
				}else if(curatorEventType==CuratorEventType.SYNC){
					
				}else{
					
				}
			}
		});
		
		
		curatorFramework.getConnectionStateListenable().addListener(
				(curatorFramework,state)->{
					if (state == ConnectionState.LOST) {
						CuratorZKClient.this.connectionState_LOST(curatorFramework);
					} else if (state == ConnectionState.CONNECTED) {
						CuratorZKClient.this.connectionState_CONNECTED(curatorFramework);
					} else if (state == ConnectionState.RECONNECTED) {
						CuratorZKClient.this.connectionState_RECONNECTED(curatorFramework);
					}
				}
			);
		
		if ((curatorFramework).getState().equals(CuratorFrameworkState.LATENT)) {
			curatorFramework.start();
			try {
				curatorFramework.blockUntilConnected();
			} catch (InterruptedException e) {
				stop();
			}
		}

	}
	
	public void stop() {
		if (isStarted()) {
			CloseableUtils.closeQuietly(curatorFramework);  
		}
	}

	private boolean isStarted(){
		return curatorFramework.getState().equals(CuratorFrameworkState.STARTED);
	}
	
	
	/*List<ACL> aclList = new ArrayList<>();
	aclList.add(new ACL(Perms.READ,Ids.AUTH_IDS));
	aclList.add(new ACL(Perms.READ,Ids.READ_ACL_UNSAFE));
	aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
	aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
	aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
	aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
	*/
	public ZNode getOrCreateZNode(String path, byte[] data,CreateMode createMode) throws Exception {
		if(isStarted()){
			Stat stat = curatorFramework.checkExists().forPath(path);
			if (stat == null) {
				curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, data);
				stat = curatorFramework.checkExists().forPath(path);
			}
			ZNode znode = new ZNode();
			znode.setData(data);
			znode.setNameSpace(curatorFramework.getNamespace());
			znode.setPath(path);
			znode.setStat(stat);
			znode.setAclList(curatorFramework.getACL().forPath(path));
			return znode;
			/*BackgroundPathable<byte[]> backgroundPathable = curatorFramework.getData().watched();
			curatorFramework.getData().usingWatcher(new RepeatWatcher(curatorFramework)).forPath(path);*/
		}else{
			throw new Exception("CuratorFramework is not Started!");
		}
	}

	public void deleteZNode(String path) throws Exception {
		curatorFramework.delete().forPath(path);
	}
	
	public void setData(String path,byte[] data) throws Exception {
		if(isStarted()){
			curatorFramework.setData().forPath(path, data);
		}
	}
	
	public byte[] getData(String path) throws Exception {
		if(isStarted()){
			return curatorFramework.getData().forPath(path);
		}else{
			throw new Exception("CuratorFramework is not Started!");
		}
	}



}
