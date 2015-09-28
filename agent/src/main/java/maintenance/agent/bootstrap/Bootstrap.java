package maintenance.agent.bootstrap;


import maintenance.agent.core.Agent;
import maintenance.agent.core.lifecycle.ILifeCycle;
import maintenance.agent.core.lifecycle.ILifeCycle.LifeCycleEvent;
import maintenance.agent.core.lifecycle.LifeCycleException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	
	public static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	
	
	public static void main(String[] args) throws LifeCycleException, InterruptedException {
		Agent agent = new Agent();
		agent.addListener(new ILifeCycle.Listener(){

			@Override
			public void trigger(LifeCycleEvent event) {

				log.info(event.toString());
			}
		});
		try {
			agent.start();
		} catch (LifeCycleException e) {
			e.printStackTrace();
			agent.stop();
		}
		
		Thread.sleep(5000);
		agent.stop();
		
	}
	
}
