package com.github.xjs.util.delay;

import java.util.concurrent.DelayQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xjs.util.ThreadPoolUtil;
	
public class DelayedService {
	
	private static Logger log = LoggerFactory.getLogger(DelayedService.class);
	
    private boolean start ;     
    
    private DelayQueue<BaseDelayed<?>> delayQueue = new DelayQueue<BaseDelayed<?>>();  
      
    public static interface OnDelayedListener{  
        public <T extends BaseDelayed<?>> void onDelayedArrived(T delayed);  
    }  
    
    public static interface OnStartListener{
    	public void onStart();
    }
  
    public <T> void start(final OnStartListener onStartListener, final OnDelayedListener listener){  
        if(start){  
            return;  
        }  
        log.debug("DelayService start...");
        start = true;  
        //启动DelayQueue
        new Thread(new Runnable(){  
            public void run(){  
                try{  
                    while(true){  
                    	BaseDelayed<?> baseDelayed = delayQueue.take();  
                    	log.debug("DelayService baseDelayed:"+baseDelayed);
                        if(listener != null){ 
                        	ThreadPoolUtil.execute(new Runnable(){
                        		public void run(){
                        			listener.onDelayedArrived(baseDelayed);  
                        		}
                        	});
                        }  
                    }  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
            }  
        }).start();
        //回调出去
        if(onStartListener != null){
        	ThreadPoolUtil.execute(new Runnable(){
				@Override
				public void run() {
					onStartListener.onStart();
				}
        	});
        }
    }  
      
    public void add(BaseDelayed<?> baseDelayed){  
        delayQueue.put(baseDelayed);  
    }  

	@SuppressWarnings("unchecked")
	public <T, D extends BaseDelayed<?>> D remove(Class<D>clazz, T value){  
    	BaseDelayed<?>[] array = delayQueue.toArray(new BaseDelayed<?>[]{});  
		if(array == null || array.length <= 0){  
			return null;  
		}  
		D target = null;  
		for(BaseDelayed<?> delayed : array){  
			if(clazz != delayed.getClass()){
				continue;
			}
			if(delayed.getValue().equals(value)){  
				target = (D)delayed;  
				break;  
			}  
		}  
		if(target != null){  
		      delayQueue.remove(target);  
		}  
		return target;
    } 
}
