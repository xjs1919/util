package com.github.xjs.util.queue;

public class WorkingService<T extends BaseRequest> {  
    
    private WorkingQueue<T> workingQueue;  
  
    public WorkingService() {  
        workingQueue = new WorkingQueue<T>();  
    }  
  
    public void start() {  
        workingQueue.start();  
    }  
  
    public void execute(final T t, LazyExecutable<T> callback ) {  
        t.setLazyExecutor(callback);  
        workingQueue.execute(t);  
    }  
  
    public void stop() {  
        if (workingQueue != null) {  
            workingQueue.stop();  
        }  
    }  
}  