package com.github.xjs.util.queue;

public class WorkingService<T extends QueueAble> {  
    
    private WorkingQueue<T> workingQueue;  
    

    public WorkingService() {  
        this.workingQueue = new WorkingQueue<T>();  
    }  
  
    public void start() {  
        workingQueue.start();  
    }  
  
    public void execute(final T t, Callback<T> callback ) {  
        this.execute(t, callback, false);
    }  
    
    public void execute(final T t, Callback<T> callback, boolean callbackExecuteParallel) {  
        workingQueue.execute(t, callback, callbackExecuteParallel);  
    }  
  
    public void stop() {  
        if (workingQueue != null) {  
            workingQueue.stop();  
        }  
    }  
}  