package com.vip.local.cache.define;

public enum LocalCacheConst {
	LOCAL_CACHE_SERVER_PORT("10012" , "服务器端口"),
	LOCAL_CACHE_MAX_FRAME_SIZE("10240" , "最大针大小"),
	LOCAL_CACHE_CMD_QUEUE_SIZE("819200" , "命令队列大小"),
	LOCAL_CACHE_CMD_QUEUE_TMO("1000" , "命令队列超时"),
	LOCAL_CACHE_RETRANS_QUEUE_TMO("200" , "超时队列超时"),
	LOCAL_CACHE_RETRANS_TMO("300000" , "超时队列超时"),
	LOCAL_CACHE_HC_TMO("1000" , "健康检查超时时间"),
	LOCAL_CACHE_EXPIRE_QUEUE_SIZE("8192000" , "超时队列容量"),
	LOCAL_CACHE_RETRANS_QUEUE_SIZE("1000000" , "重传队列大小"),
	LOCAL_CACHE_EXPIRE_QUEUE_TMO("100" , "队列超时");
	
	private String definition;
	private String desc;
	
	private LocalCacheConst(String definition , String desc){
		this.setDefinition(definition);
		this.setDesc(desc);
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
