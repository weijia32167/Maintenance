package maintenance.agent.core.lifecycle;


import maintenance.agent.core.lifecycle.ILifeCycle.LifeCycleEvent;
import maintenance.agent.core.listener.EventSupport;


/*public class LifeCycleEventSupport<T extends ILifeCycle.Listener> implements IListenered<T>{*/
public class LifeCycleEventSupport extends EventSupport<ILifeCycle.Listener>{
	
	//private List<T> listeners = new LinkedList<>();
	
/*	public void notifyListeners(LifeCycleEvent event){
		for(int i = 0 ; i < listeners.size() ; i++ ){
			ILifeCycle.Listener listener = listeners.get(i);
			listener.trigger(event);
		}
	}*/
	
/*	public void addListener(T t){
		listeners.add(t);
	}
	
	public void removeListener(T t){
		listeners.remove(t);
	}

	public List<T> getListeners() {
		return listeners;
	}

	public void setListeners(List<T> listeners) {
		this.listeners = listeners;
	}*/
	
	
}
