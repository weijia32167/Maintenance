package maintenance.agent.bootstrap;


import maintenance.agent.core.Agent;
import maintenance.agent.core.lifecycle.LifeCycleException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
	
	public static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String[] args) throws LifeCycleException, InterruptedException {
		Agent agent = new Agent();
		try {
			agent.start();
		} catch (LifeCycleException e) {
			e.printStackTrace();
			agent.stop();
		}
		Thread.sleep(500000);
	}
	
}
