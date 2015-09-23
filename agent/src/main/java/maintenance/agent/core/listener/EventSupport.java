package maintenance.agent.core.listener;

import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventSupport<T extends EventListener> implements IListenered<T>{

	protected List<T> listeners = new CopyOnWriteArrayList<>();
	
	public void notifyListeners(EventObject event){
		for(int i = 0 ; i < listeners.size() ; i++ ){
			EventListener listener = listeners.get(i);
			listener.trigger(event);
		}
	}
	
	public void addListener(T t){
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
	}
	
}
