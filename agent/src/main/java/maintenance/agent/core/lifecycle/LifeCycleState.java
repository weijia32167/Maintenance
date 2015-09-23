package maintenance.agent.core.lifecycle;

public enum LifeCycleState {

	NEW(1, "new"), 
	INITING(2, "initing"), 
	INITED(3, "inited"), 
	STARTING(4,"starting"), 
	STARTED(5, "started"), 
	STARTED_PAUSE(6, "pause"), 
	STOPPING(7, "stopping"), 
	STOPPED(8, "stopped"), 
	DESTORYINH(9, "destorying"), 
	DESTORYED(10, "destoryed");

	private int code;
	private String desc;

	private LifeCycleState(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public int getCode() {
		return code;
	}

}