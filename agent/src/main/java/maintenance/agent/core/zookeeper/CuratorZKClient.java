package maintenance.agent.core.zookeeper;

import maintenance.agent.conf.Constants;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
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
						   .sessionTimeoutMs(6000)
						   .authorization("digest", "agent".getBytes());
		curatorFramework = builder.build();
	}
	
	
	public CuratorZKClient(){
		aclProvider = new DefaultACLProvider();
		retryPolicy = new RetryNTimes(Integer.MAX_VALUE, 1000);
		Builder builder =  CuratorFrameworkFactory.builder().
						   connectString(Constants.DEFAULT_ZOOKEEPER_URL).
						   namespace(DEFAULT_NAMESPACE).
						   aclProvider(aclProvider).
						   retryPolicy(retryPolicy).
						   connectionTimeoutMs(5000).
						   sessionTimeoutMs(6000).
						   authorization("digest", "agent".getBytes());
		curatorFramework = builder.build();
	}
	

	public void start() {
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
				CloseableUtils.closeQuietly(curatorFramework);  
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
	
	public void createZnode(String path, byte[] data,CreateMode createMode) throws Exception {
		if(isStarted()){
			Stat stat = curatorFramework.checkExists().forPath(path);
			if (stat == null) {
				/*List<ACL> aclList = new ArrayList<>();
				aclList.add(new ACL(Perms.READ,Ids.AUTH_IDS));
				aclList.add(new ACL(Perms.READ,Ids.READ_ACL_UNSAFE));
				aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
				aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
				aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));
				aclList.add(new ACL(Perms.READ,new Id("ip","192.168.12.201")));*/
				String operateString =curatorFramework.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, data);
				System.out.println(operateString);
			}
			
			BackgroundPathable<byte[]> backgroundPathable = curatorFramework.getData().watched();
			curatorFramework.getData().usingWatcher(new RepeatWatcher(curatorFramework)).forPath(path);
		}
	}

	public void deleteZnode(String path) throws Exception {
		curatorFramework.delete().forPath(path);
	}

	public static void main(String[] args) throws Exception {
		CuratorZKClient zookeeperSupport = new CuratorZKClient("192.168.12.201:2182,192.168.12.201:2183,192.168.12.201:2181");
		zookeeperSupport.start();
	}




}
