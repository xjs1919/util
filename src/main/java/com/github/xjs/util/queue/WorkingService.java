package com.github.xjs.util.queue;

public class WorkingService<T extends QueueAble> {  
    
    private WorkingQueue<T> workingQueue;  
  
    public WorkingService() {  
        workingQueue = new WorkingQueue<T>();  
    }  
  
    public void start() {  
        workingQueue.start();  
    }  
  
    public void execute(final T t, Callback<T> callback ) {  
        workingQueue.execute(t, callback);  
    }  
  
    public void stop() {  
        if (workingQueue != null) {  
            workingQueue.stop();  
        }  
    }  
}  