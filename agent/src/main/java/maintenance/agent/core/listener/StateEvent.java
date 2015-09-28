package maintenance.agent.core.listener;

import java.util.EventObject;


public abstract class StateEvent<Source,State> extends EventObject{

	private static final long serialVersionUID = 4629854684099048473L;
	
	protected long time;
	
	protected State oldState;
	
	protected State newState;
	
	public StateEvent(Source source,State oldState) {
		super(source);
		this.oldState = oldState;
	}
	
	
	public StateEvent(Source source,State oldState,State newState,long time) {
		super(source);
		this.oldState = oldState;
		this.newState = newState;
		this.time = time;
	}
	
	public StateEvent(Source source,State oldState,State newState) {
		super(source);
		this.oldState = oldState;
		this.newState = newState;
	}
	
	@Override
	public String toString() {
		if(newState == null){
			return "["+source+"]"+"["+oldState+"]";
		}else{
			return "["+source+"]"+" ["+oldState +"->"+ newState + "]["+time+"ms]";
		}
		
		
		
	}

}
