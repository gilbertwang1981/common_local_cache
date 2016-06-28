package com.vip.local.cache.sdk;

import java.net.URLEncoder;
import java.util.HashMap;

import com.vip.local.cache.data.LocalCacheData;
import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.main.LocalCacheInitializer;
import com.vip.local.cache.param.LocalCacheParameter;
import com.vip.local.cache.worker.LocalCacheReplicaWorker;

public class LocalCacheSdk {
	private static LocalCacheSdk instance = null;
	
	public static LocalCacheSdk getInstance(){
		if (instance == null) {
			instance = new LocalCacheSdk();
		}
		
		return instance;
	}
	
	public void initialize(LocalCacheCallback callback , String hosts) throws NumberFormatException, InterruptedException{
		LocalCacheInitializer.getInstance().initialize(null , callback , hosts);
	}
	
	public String get(String key) {
		return LocalCacheData.getInstance().get(key);
	}
	
	public void set(String key , String value) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode());
		HashMap<String , String> data = new HashMap<String , String>();
		data.put("cache_key", key);
		data.put("cache_value", value);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void del(String key) {
		LocalCacheParameter param = new LocalCacheParameter();
		
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode());
		HashMap<String , String> data = new HashMap<String , String>();
		data.put("cache_key", key);
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
	
	public void flush(String parameter) throws NumberFormatException, Exception{
		LocalCacheParameter param = new LocalCacheParameter();
		param.setCode(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode());
		
		HashMap<String , String> data = new HashMap<String , String>();
		
		data.put("cache_key" , "flush_parameter_key");
		data.put("cache_value" , URLEncoder.encode(parameter , "UTF-8"));
		param.setParams(data);
		
		param.setParams(data);
		
		LocalCacheReplicaWorker.getInstance().addCommand(param);
	}
}