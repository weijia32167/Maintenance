package maintenance.agent.core.lifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import maintenance.agent.core.listener.EventSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsLifeCycle implements ILifeCycle{

	private Lock lock = new ReentrantLock();
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected volatile AtomicReference<LifeCycleState> state = new AtomicReference<LifeCycleState>();
	
	protected EventSupport<ILifeCycle.Listener> eventSupport = new EventSupport<ILifeCycle.Listener>();

	public AbsLifeCycle() {
		super();
		state.set(LifeCycleState.NEW);
	}

	@Override
	public final void init() throws LifeCycleException {

		try {
			lock.lock();
			if (state.get().equals(LifeCycleState.NEW)) {
				state.compareAndSet(LifeCycleState.NEW, LifeCycleState.INITING);
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.NEW,LifeCycleState.INITING));
				doInit();
				state.compareAndSet(LifeCycleState.INITING, LifeCycleState.INITED);
				long endTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.INITING,LifeCycleState.INITED,(endTime-startTime)));
			}  else if (state.get().equals(LifeCycleState.INITING) || state.equals(LifeCycleState.INITED)) {
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
	public final void start() throws LifeCycleException {
		try{
			lock.lock();
			if(state.get().equals(LifeCycleState.NEW)){
				init();
				start();
			}else if(state.get().equals(LifeCycleState.INITING)){
				start();
			}else if(state.get().equals(LifeCycleState.INITED)){
				state.compareAndSet(LifeCycleState.INITED, LifeCycleState.STARTING);
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.INITED,LifeCycleState.STARTING));
				doStart();
				long endTime = System.currentTimeMillis();
				state.compareAndSet(LifeCycleState.STARTING, LifeCycleState.STARTED);
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STARTING,LifeCycleState.STARTED,(endTime - startTime)));
			}else if(state.get().equals(LifeCycleState.STARTED)){
				log.debug(this+" is started!");
			}else if(state.get().equals(LifeCycleState.STARTED_PAUSE)){
				log.debug(this+" is paused,you can invoke resume to make it recovery!");
			}else if(state.get().equals(LifeCycleState.STOPPED)){
				log.debug(this+" is stopped!");
			}else if(state.get().equals(LifeCycleState.DESTORYED)){
				log.debug(this+" is destoryed!");
			}
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public final void pause() throws LifeCycleException {
		try{
			lock.lock();
			log.debug(this+" method is support!");
		}finally{
			lock.unlock();
		}
		
	}

	@Override
	public final void resume() throws LifeCycleException {
		try{
			lock.lock();
			log.debug(this+" method is support!");
		}finally{
			lock.unlock();
		}
	}

	@Override
	public final void stop() throws LifeCycleException {
		try{
			lock.lock();
			if(state.get().equals(LifeCycleState.NEW)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.get().equals(LifeCycleState.INITING)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.get().equals(LifeCycleState.INITED)){
				log.debug(this+" is not started,don't need stop!");
			}else if(state.get().equals(LifeCycleState.STARTED)){
				state.compareAndSet(LifeCycleState.STARTED, LifeCycleState.STOPPING);
				long startTime = System.currentTimeMillis();
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STARTED,LifeCycleState.STOPPING));
				doStop();
				long endTime = System.currentTimeMillis();
				state.compareAndSet(LifeCycleState.STOPPING, LifeCycleState.STOPPED);
				notifyListeners(new LifeCycleEvent(this,LifeCycleState.STOPPING,LifeCycleState.STOPPED,(endTime - startTime)));
			}else if(state.get().equals(LifeCycleState.STARTED_PAUSE)){
				log.debug(this+" is paused,you can invoke resume to make it recovery!");
			}else if(state.get().equals(LifeCycleState.STOPPED)){
				log.debug(this+" is stopped!");
			}else if(state.get().equals(LifeCycleState.DESTORYED)){
				log.debug(this+" is destoryed!");
			}
		}finally{
			lock.unlock();
		}
	}



	@Override
	public final void destory() throws LifeCycleException {

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
		return state.get().compareTo(LifeCycleState.INITED)==0;
	}
	
	@Override
	public boolean isStarted() {
		return state.get().compareTo(LifeCycleState.STARTED) == 0;
	}

	@Override
	public boolean isStarting() {
		return state.get().compareTo(LifeCycleState.STARTING) == 0;
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
