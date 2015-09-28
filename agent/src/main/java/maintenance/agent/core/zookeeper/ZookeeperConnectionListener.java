package maintenance.agent.core.zookeeper;

import java.util.EventObject;

import maintenance.agent.core.listener.EventListener;


public abstract class ZookeeperConnectionListener implements EventListener{

	@Override
	public <T extends EventObject> void trigger(T event) {
		trigger((ZookeeperConnectionStateEvent)event);
	}

	public abstract void trigger(ZookeeperConnectionStateEvent event);
	
}
