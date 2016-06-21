package com.vip.local.cache.main;

import org.apache.commons.lang3.StringUtils;

import com.vip.local.cache.define.LocalCacheConst;
import com.vip.local.cache.init.LocalCacheServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class LocalCacheInitializer {
	
	private static LocalCacheInitializer instance = null;
	
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
	private EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	public static LocalCacheInitializer getInstance() {
		if (instance == null) {
			instance = new LocalCacheInitializer();
		}
		
		return instance;
	}
	
	public void initialize(String port) throws NumberFormatException, InterruptedException {
		if (StringUtils.isEmpty(port) || !StringUtils.isNumeric(port)) {
			port = LocalCacheConst.LOCAL_CACHE_SERVER_PORT.getDefinition();
		}
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup);
		
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new LocalCacheServerInitializer());
		
		ChannelFuture cf = bootstrap.bind(new Integer(port)).sync();
		
		cf.channel().closeFuture().sync();
		
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
	}
}