package maintenance.agent.core.listener;

public interface IListenered<T> {
	
	public void addListener(T t);
	
	public void removeListener(T t);

}
