/**
 * 
 */
package com.github.xjs.util.retry.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xjs.util.retry.RetryAble;
import com.github.xjs.util.retry.RetryTask;

/**
 * @author 605162215@qq.com
 *
 * 2016年8月15日 上午10:24:21
 */
public class RetryPersistService implements PersistService<RetryTask>{
	
	private Map<String, String> db = new HashMap<String, String>();
	
	@Override
	public void save(RetryTask retryService) {
		System.out.println("[RetryPersistService]save:"+retryService);
		String uuid = retryService.getUuid();
		int interval = retryService.getInterval();
		db.put(uuid, "{\"clazz\":\""+retryService.getTask().getName()+"\",\"interval\":\""+interval+"\"}");
	}
	
	@Override
	public void delete(RetryTask retryService) {
		System.out.println("[RetryPersistService]delete:"+retryService);
		String uuid = retryService.getUuid();
		db.remove(uuid);
	}

	@Override
	public List<RetryTask> getAll() {
		System.out.println("[RetryPersistService]getAll");
		List<RetryTask> list = new ArrayList<RetryTask>();
		for(Map.Entry<String, String> entry : db.entrySet()){
			String uuid = entry.getKey();
			String json = entry.getValue();
			JSONObject jo = JSON.parseObject(json);
			String clazzName = jo.getString("clazz");
			int interval = jo.getIntValue("interval");
			RetryTask task = new RetryTask(getClass(clazzName), interval);
			task.setUuid(uuid);
			list.add(task);
		}
		return list;
	}
	
	@Override
	public int size(){
		return db.size();
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends RetryAble> getClass(String className){
		try{
			return (Class<? extends RetryAble>)Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
