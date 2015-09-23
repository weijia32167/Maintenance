package maintenance.agent.core.listener;

import java.util.EventObject;


public interface EventListener {
	 public <T extends EventObject> void trigger(T event);
}
