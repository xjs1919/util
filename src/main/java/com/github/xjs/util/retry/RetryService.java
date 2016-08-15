/**
 * 
 */
package com.github.xjs.util.retry;

import java.util.List;
import java.util.concurrent.DelayQueue;

import com.github.xjs.util.retry.RetryAble;
import com.github.xjs.util.retry.RetryTask;
import com.github.xjs.util.retry.RetryService.OnRetryListener;
import com.github.xjs.util.retry.persist.PersistService;
import com.github.xjs.util.ThreadPoolUtil;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:22:05
 */
public class RetryService {
private static final int[] DEFAULT_INTERVALS = new int[]{1*60, 5*60, 10*60, 30*60, 60*60, 2*3600, 5*3600, 10*600, 15*3600, 24*3600};
	
	private DelayQueue<RetryTask> delayQueue = new DelayQueue<RetryTask>();
	
	private boolean start = false;
	
	private int[] intervals;
	
	private OnRetryListener retryListener;
	private PersistService<RetryTask> persistService;
	
	public RetryService(){
		this(DEFAULT_INTERVALS);
	}
	
	public RetryService(int[] intervals){
		this(intervals, null);
	}
	
	public RetryService(int[] intervals, PersistService<RetryTask> persistService){
		this.intervals = intervals;
		this.persistService = persistService;
	}
	
	public static interface OnRetryListener{
        public void onRetryArrived(RetryTask retryTask);
        public void onRetryFailed(RetryTask retryTask);
        public void onRetrySuccessed(RetryTask retryTask);
    }
	
	/**
     * 应用启动以后，由Controller调用启动，只调用一次
     * */
    public void start(final OnRetryListener listener){
        if(start){
            return;
        }
        System.out.println("[RetryService] start....");
        start = true;
        retryListener = listener;
        new Thread(new Runnable(){
            public void run(){
            	//找到数据库中还需要重试的那些任务
            	ThreadPoolUtil.execute(new Runnable(){
					@Override
					public void run() {
						System.out.println("[RetryService]find task need to run");
						if(persistService == null){
							return;
						}
						List<RetryTask> oldTasks = persistService.getAll();
						System.out.println("[RetryService]find task need to run:"+oldTasks);
						if(oldTasks == null || oldTasks.size() <= 0){
							return;
						}
						for(RetryTask task : oldTasks){
							//不需要写数据库了
							delayQueue.put(task);
						}
					}
            	});
            	//等待从重试任务
                try{
                    while(true){
                    	//出对
                    	RetryTask task = delayQueue.take();
                    	//同时要删掉数据库
                    	if(persistService != null){
                    		persistService.delete(task);
                    	}
                    	ThreadPoolUtil.execute(new Runnable(){
                    		public void run(){
                    			retry(task);
                    		}
                    	});
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void retry(RetryTask task){
		System.out.println("[retry]do retry:"+task);
		boolean success = false;
		try{
			Class<? extends RetryAble> taskClass = task.getTask();
			RetryAble retryAble = taskClass.newInstance();
			success = retryAble.retryAble();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(!success){
			RetryTask next = getNextTask(task);
			if(next != null){
				System.out.println("[retry]will retry next:"+next);
				add(next);
				if(retryListener != null){
		        	retryListener.onRetryArrived(task);
		        }
			}else{
				System.out.println("[retry]sorry, i have tried all my best,abondon:"+task);
				remove(task);
				if(retryListener != null){
					retryListener.onRetryFailed(task);
				}
			}
		}else{
			System.out.println("[retry]success!"+task);
			remove(task);
			if(retryListener != null){
				retryListener.onRetrySuccessed(task);
			}
		}
	}
    
    public void add(Class<? extends RetryAble> task){
    	RetryTask retryTask = new RetryTask(task, intervals[0]);
    	add(retryTask);
    }
    
    private void add(final RetryTask retryTask){
        ThreadPoolUtil.execute(new Runnable(){
            public void run(){
                // 入队
                delayQueue.put(retryTask);
                // 做持久化
                if(persistService != null){
                	persistService.save(retryTask);
                }
                System.out.println("[retry]delayQueue.size："+delayQueue.size()+",persistService.size:"+persistService.size());
            }
        });
    }

    public void remove(final RetryTask target){
        ThreadPoolUtil.execute(new Runnable() {
            public void run() {
                if (target == null) {
                    return;
                }
                // 出队
                delayQueue.remove(target);
                // 从持久化删除
                if(persistService != null){
                	persistService.delete(target);
                }
                System.out.println("[retry]delayQueue.size："+delayQueue.size()+",persistService.size:"+persistService.size());
            }
        });
    }
    
    public RetryTask getNextTask(RetryTask task){
        int nextInterval = 0;
        for(int i=0; i<intervals.length-1; i++){
            int iv = intervals[i];
            if(iv == task.getInterval()){
                nextInterval = intervals[i+1];
                break;
            }
        }
        if(nextInterval <= 0){
            return null;
        }
        return new RetryTask(task.getTask(), nextInterval);
    }
    
}
