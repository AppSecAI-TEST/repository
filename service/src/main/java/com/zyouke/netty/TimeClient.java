package com.zyouke.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import com.zyouke.netty.handler.TimeClientHandlerPackage;
import com.zyouke.netty.util.NettyUtils;



public class TimeClient {
    public static void main(String[] args) throws Exception {
	//NettyUtils.request(new TimeClientHandler());
	
	NettyUtils.request(new ChannelInitializer<SocketChannel>() {
		@Override
		protected void initChannel(SocketChannel sc) throws Exception {
		    sc.pipeline().addLast(new LineBasedFrameDecoder(1024));
		    sc.pipeline().addLast(new StringDecoder());
		    sc.pipeline().addLast(new TimeClientHandlerPackage());
		}
	    });
    }
}
