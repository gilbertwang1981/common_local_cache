package com.vip.local.cache.cmd;

import java.util.concurrent.ConcurrentHashMap;

import com.vip.local.cache.define.LocalCacheCmdType;
import com.vip.local.cache.param.LocalCacheParameter;

public class LocalCacheCommandMgr {
	private static LocalCacheCommandMgr instance = null;
	private ConcurrentHashMap<Integer , LocalCacheCommand> commands = new 
			ConcurrentHashMap<Integer , LocalCacheCommand>();
	
	public static LocalCacheCommandMgr getInstance() {
		if (instance == null) {
			instance = new LocalCacheCommandMgr();
		}
		
		return instance;
	}
	
	private LocalCacheCommandMgr(){
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_DEL.getCode() , 
				new DelLocalCacheCommand());
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_FLUSH.getCode() , 
				new FlushLocalCacheCommand());
		commands.put(LocalCacheCmdType.LOCAL_CACHE_CMD_TYPE_SET.getCode() , 
				new SetLocalCacheCommand());
	}
	
	public boolean execute(int code , LocalCacheParameter paramter) {
		LocalCacheCommand localCacheCommand = commands.get(code);
		if (localCacheCommand == null) {
			return false;
		}
		
		return localCacheCommand.execute(paramter);
	}
}