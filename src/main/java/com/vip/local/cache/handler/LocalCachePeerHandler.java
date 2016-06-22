package com.vip.local.cache.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LocalCachePeerHandler extends SimpleChannelInboundHandler<String>{

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String arg1) throws Exception {
		System.out.println(arg1);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }
}