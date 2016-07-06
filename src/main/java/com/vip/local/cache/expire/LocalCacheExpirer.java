package com.vip.local.cache.expire;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.define.LocalCacheConst;

public class LocalCacheExpirer extends Thread {
	private static LocalCacheExpirer instance = null;
	
	private BlockingQueue<LocalCacheExpiredData> expireQueue = new ArrayBlockingQueue<LocalCacheExpiredData>(
				new Integer(LocalCacheConst.LOCAL_CACHE_EXPIRE_QUEUE_SIZE.getDefinition()));
	
	public static LocalCacheExpirer getInstance(){
		if (instance == null) {
			instance = new LocalCacheExpirer();
		}
		
		return instance;
	}
	
	public void expire(String key , Long timestamp) {
		try {
			LocalCacheExpiredData expiredData = new LocalCacheExpiredData();
			expiredData.setExpiredKey(key);
			expiredData.setExpiredTime(timestamp);
			
			expireQueue.put(expiredData);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while (true) {
			try {
				LocalCacheExpiredData expiredData = expireQueue.poll(
						new Integer(LocalCacheConst.LOCAL_CACHE_EXPIRE_QUEUE_TMO.getDefinition()) , TimeUnit.MILLISECONDS);
				if (expiredData == null) {
					continue;
				}
				
				Long current = System.currentTimeMillis();
				if (expiredData.getExpiredTime().longValue() <= current.longValue()){
					if (LocalCacheData.getInstance().getExpired(expiredData.getExpiredKey()).longValue() <= expiredData.getExpiredTime().longValue()) {
						System.out.println("delete the data:" + expiredData.getExpiredTime() + " " + 
								current + " " + LocalCacheData.getInstance().getExpired(expiredData.getExpiredKey()));
						
						LocalCacheData.getInstance().del(expiredData.getExpiredKey());
					}
				} else {
					expireQueue.put(expiredData);
					
					Thread.sleep(100);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
