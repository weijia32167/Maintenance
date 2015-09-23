package maintenance.agent.core.lifecycle;

public class LifeCycleException extends Exception {

	private static final long serialVersionUID = -6439103942373119991L;

	public LifeCycleException() {
		super();
	}

	public LifeCycleException(String message, Throwable cause,boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LifeCycleException(String message, Throwable cause) {
		super(message, cause);
	}

	public LifeCycleException(String message) {
		super(message);
	}

	public LifeCycleException(Throwable cause) {
		super(cause);
	}

	public static final void throwException(Throwable exception,String message) throws LifeCycleException{
		LifeCycleException lifeCycleException = null;
		if(exception == null && message == null){
			lifeCycleException = new LifeCycleException();
		}else if(exception != null && message == null){
			lifeCycleException = new LifeCycleException(exception);
		}else if(exception == null && message != null){
			lifeCycleException = new LifeCycleException(message);
		}else if(exception != null && message != null){
			lifeCycleException = new LifeCycleException(message,exception);
		}
		throw lifeCycleException;
	}
}
