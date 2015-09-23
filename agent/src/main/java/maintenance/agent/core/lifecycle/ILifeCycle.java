package maintenance.agent.core.lifecycle;


import java.util.EventObject;

import maintenance.agent.core.listener.EventListener;
import maintenance.agent.core.listener.IListenered;
import maintenance.agent.core.listener.StateEvent;

public interface ILifeCycle extends IListenered<ILifeCycle.Listener>{
	
	public void init() throws LifeCycleException;
	
	public void start() throws LifeCycleException;
	
	public void pause() throws LifeCycleException;
	
	public void resume() throws LifeCycleException;
	
	public void stop() throws LifeCycleException;
	
	public void destory() throws LifeCycleException;
	
	public boolean isInited();
	public boolean isStarted();     
	public boolean isStarting();  
	public boolean isStopping();      
	public boolean isStopped();  
	public boolean isFailed(); 
	
	public abstract class Listener  implements EventListener{
		//public void LifeCycleStateChange(LifeCycleEvent event);
		
		
		public abstract void trigger(LifeCycleEvent event);

		@Override
		public <T extends EventObject> void trigger(T event) {
			trigger((LifeCycleEvent)event);
		}
	}
	
	public class LifeCycleEvent extends StateEvent<ILifeCycle,LifeCycleState>{

		private static final long serialVersionUID = 7625782425467617083L;

		public LifeCycleEvent(ILifeCycle source, LifeCycleState oldState,LifeCycleState newState, long time) {
			super(source, oldState, newState, time);
		}

		public LifeCycleEvent(ILifeCycle source, LifeCycleState oldState,LifeCycleState newState) {
			super(source, oldState, newState);
		}
	}
	
}
