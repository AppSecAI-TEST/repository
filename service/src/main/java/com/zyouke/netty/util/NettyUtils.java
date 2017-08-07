package com.zyouke.netty.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyUtils {

    // 服务端的绑定监听端口
    public static void bind(final ChannelInitializer initializer){
	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup(); 
	try {
	    ServerBootstrap b = new ServerBootstrap();
	    b.group(bossGroup,workerGroup)
	    .channel(NioServerSocketChannel.class)
	    .option(ChannelOption.SO_BACKLOG,1024)
	    .childHandler(initializer);
	    ChannelFuture future = b.bind(8080).sync();
	    future.channel().closeFuture().sync();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}finally{
	    bossGroup.shutdownGracefully();
	    workerGroup.shutdownGracefully();
	}
    }
    
    // 请求
    public static void request(final ChannelInitializer initializer) {
	EventLoopGroup group = new NioEventLoopGroup();
	try {
	    Bootstrap b = new Bootstrap();
	    b.group(group)
	    .channel(NioSocketChannel.class)
	    .option(ChannelOption.TCP_NODELAY,true)
	    .handler(initializer);
	    ChannelFuture future = b.connect("127.0.0.1",8080).sync();
	    future.channel().closeFuture().sync();
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    group.shutdownGracefully();
	}
    }
    
    
}
