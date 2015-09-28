package maintenance.agent.core.lifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import maintenance.agent.core.listener.EventSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsLifeCycle implements ILifeCycle{

	private Lock lock = new ReentrantLock();
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected volatile LifeCycleState state = LifeCycleState.NEW;
	
	protected EventSupport<ILifeCycle.Listener> eventSupport = new EventSupport<ILifeCycle.Listener>();

	public AbsLifeCycle() {
		super();
	}

	@Override
	public void init() throws LifeCycleException {

		try {
			lock.lock();
			if (state.equals(LifeCycleState.NEW)) {
				state = LifeCycleState.INITING;
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.NEW,LifeCycleState.INITING));
				doInit();
				state = LifeCycleState.INITED;
				long endTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.INITING,LifeCycleState.INITED,(endTime-startTime)));
			}  else if (state.equals(LifeCycleState.INITING) || state.equals(LifeCycleState.INITED)) {
				init();
			} else {
				LifeCycleException.throwException(null,"Current LifeCycle State is :" + state.toString());
			}
		} 
		finally{
			lock.unlock();
		}
	}

	@Override
	public void start() throws LifeCycleException {
		try{
			lock.lock();
			if(state.equals(LifeCycleState.NEW)){
				init();
				start();
			}else if(state.equals(LifeCycleState.INITING)){
				start();
			}else if(state.equals(LifeCycleState.INITED)){
				state = LifeCycleState.STARTING;
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.INITED,LifeCycleState.STARTING));
				doStart();
				long endTime = System.currentTimeMillis();
				state = LifeCycleState.STARTED;
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STARTING,LifeCycleState.STARTED,(endTime - startTime)));
			}else if(state.equals(LifeCycleState.STARTED)){
				log.debug(this+" is started!");
			}else if(state.equals(LifeCycleState.STARTED_PAUSE)){
				log.debug(this+" is paused,you can invoke resume to make it recovery!");
			}else if(state.equals(LifeCycleState.STOPPED)){
				log.debug(this+" is stopped!");
			}else if(state.equals(LifeCycleState.DESTORYED)){
				log.debug(this+" is destoryed!");
			}
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public void pause() throws LifeCycleException {
		try{
			lock.lock();
			log.debug(this+" method is support!");
		}finally{
			lock.unlock();
		}
		
	}

	@Override
	public void resume() throws LifeCycleException {
		try{
			lock.lock();
			log.debug(this+" method is support!");
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void stop() throws LifeCycleException {
		try{
			lock.lock();
			if(state.equals(LifeCycleState.NEW)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.equals(LifeCycleState.INITING)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.equals(LifeCycleState.INITED)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.equals(LifeCycleState.STARTED)){
				state = LifeCycleState.STOPPING;
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STARTED,LifeCycleState.STOPPING));
				doStop();
				long endTime = System.currentTimeMillis();
				state = LifeCycleState.STOPPED;
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STOPPING,LifeCycleState.STOPPED,(endTime - startTime)));
			}else if(state.equals(LifeCycleState.STARTED_PAUSE)){
				log.debug(this+" is paused,you can invoke resume to make it recovery!");
			}else if(state.equals(LifeCycleState.STOPPED)){
				log.debug(this+" is stopped!");
			}else if(state.equals(LifeCycleState.DESTORYED)){
				log.debug(this+" is destoryed!");
			}
		}finally{
			lock.unlock();
		}
	}



	@Override
	public void destory() throws LifeCycleException {

	}

	protected void doInit() throws LifeCycleException {
		log.debug("Default doInit() Impl is simple log,you can Impl ams.core.lifecycle.AbsMBeanLifeCycle.doInit() make it other useful function!");
	}

	protected void doStart() throws LifeCycleException {
		log.debug("Default doStart() Impl is simple log,you can Impl ams.core.lifecycle.AbsMBeanLifeCycle.doStart() make it other useful function!");
	}
	
	protected void doStop() throws LifeCycleException {
		log.debug("Default doStop() Impl is simple log,you can Impl ams.core.lifecycle.AbsMBeanLifeCycle.doStop() make it other useful function!");
	}

	@Override
	public boolean isInited() {
		return state.compareTo(LifeCycleState.INITED)==0;
	}
	
	@Override
	public boolean isStarted() {
		return state.compareTo(LifeCycleState.STARTED) == 0;
	}

	@Override
	public boolean isStarting() {
		return state.compareTo(LifeCycleState.STARTING) == 0;
	}

	@Override
	public boolean isStopping() {
		return false;
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean isFailed() {
		return false;
	}

	/**********************EventSupport proxy method***********************************/
	public void addListener(ILifeCycle.Listener listener){
		eventSupport.addListener(listener);
	}
	
	public void removeListener(ILifeCycle.Listener listener){
		eventSupport.removeListener(listener);
	}
	
	public void notifyListeners(LifeCycleEvent lifeCycleEvent){
		CompletableFuture.runAsync(()->{eventSupport.notifyListeners(lifeCycleEvent);}, ForkJoinPool.commonPool());
	}


	/**********************getters and setters***********************************/

	public EventSupport<ILifeCycle.Listener> getEventSupport() {
		return eventSupport;
	}

	public void setEventSupport(EventSupport<ILifeCycle.Listener> eventSupport) {
		this.eventSupport = eventSupport;
	}
	
}
