package com.vip.local.cache.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.handler.LocalCachePeerHandler;
import com.vip.local.cache.proto.CommonLocalCache;
import com.vip.local.cache.proto.CommonLocalCache.CacheCommand;
import com.vip.local.cache.util.LocalCacheUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class LocalCacheClientInitializer extends Thread{
	private static LocalCacheClientInitializer instance = null;
	
	private Bootstrap bootstrap = null;
	
	private ConcurrentHashMap<String , Channel> channels = 
			new ConcurrentHashMap<String , Channel>();
	
	private List<String> channelKeys = new ArrayList<String>();
	
	private List<String> retryConnects = new ArrayList<String>();
	
	public static LocalCacheClientInitializer getInstance(){
		if (instance == null) {
			instance = new LocalCacheClientInitializer();
		}
		
		return instance;
	}
	
	public void initialize(){
		EventLoopGroup group = new NioEventLoopGroup();
		
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class);
		
		bootstrap.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 5 , 0, 4, 0, 4));  
				pipeline.addLast(new ProtobufDecoder(CommonLocalCache.CacheCommand.getDefaultInstance()));
				pipeline.addLast(new LengthFieldPrepender(4));  
				pipeline.addLast(new ProtobufEncoder());
				pipeline.addLast(new LocalCachePeerHandler());
			}
		});
		
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.SO_REUSEADDR , true);
		bootstrap.option(ChannelOption.SO_SNDBUF , 1024 * 1024 * 2);
		bootstrap.option(ChannelOption.SO_RCVBUF , 1024 * 1024 * 2);
	}
	
	private Channel getChannel(String host , int port){
		Channel channel = channels.get(host + ":" + port);
		if (channel == null) {
			try {
				channel = bootstrap.connect(host, port).sync().channel();
			} catch (Exception e) {
				return null;
			}
			
			channels.put(host + ":" + port , channel);
			channelKeys.add(host + ":" + port);
		}
		
		return channel;
	}
	
	public boolean replicate(String host , CacheCommand msg) {
		Channel channel = this.getChannel(host , new Integer(
				LocalCacheConst.LOCAL_CACHE_SERVER_PORT.getDefinition()));
		
		if(channel != null) {
			try {
				channel.writeAndFlush(msg);
				
				return true;
			} catch (Exception e) {
				retryConnects.add(host + ":" + LocalCacheConst.LOCAL_CACHE_SERVER_PORT.getDefinition());
				channelKeys.remove(host + ":" + LocalCacheConst.LOCAL_CACHE_SERVER_PORT.getDefinition());
				channels.remove(host + ":" + LocalCacheConst.LOCAL_CACHE_SERVER_PORT.getDefinition());

				return false;
			}
		}else{
			return false;
		}
	}
	
	public void run(){
		while (true) {
			try {
				Thread.sleep(new Integer(
						LocalCacheConst.LOCAL_CACHE_HC_TMO.getDefinition()));
				
				for (String retry : retryConnects) {
					List<String> address = LocalCacheUtil.tokenizer(retry , ":");
					Channel channel = this.getChannel(
							address.get(0) , new Integer(address.get(1)));
					if (channel != null) {
						retryConnects.remove(retry);
						channelKeys.add(retry);
						channels.put(retry , channel);
						
						break;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}