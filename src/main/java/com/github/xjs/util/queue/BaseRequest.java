package com.github.xjs.util.queue;

public class BaseRequest {  
	  
    @SuppressWarnings("rawtypes")  
    private LazyExecutable executor;  
  
    @SuppressWarnings("rawtypes")  
    public void setLazyExecutor(LazyExecutable callback) {  
        this.executor = callback;  
    }  
      
    @SuppressWarnings("rawtypes")  
    public LazyExecutable getLazyExecutor() {  
        return executor;  
    }  
}  