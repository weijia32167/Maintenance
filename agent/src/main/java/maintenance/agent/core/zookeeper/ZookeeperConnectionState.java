package maintenance.agent.core.zookeeper;

import java.util.EventListener;


public enum ZookeeperConnectionState {

	CONNECTED {
		public boolean isConnected() {
			return true;
		}
	},


	SUSPENDED {
		public boolean isConnected() {
			return false;
		}
	},

	RECONNECTED {
		public boolean isConnected() {
			return true;
		}
	},


	LOST {
		public boolean isConnected() {
			return false;
		}
	},


	READ_ONLY {
		public boolean isConnected() {
			return true;
		}
	};


	public abstract boolean isConnected();
	
	public interface Listener  extends EventListener{
		public void ZookeeperConnectionStateChange(ZookeeperConnectionStateEvent event);
	}

}
