package com.zyouke.netty.handler;

import org.apache.commons.lang.time.DateFormatUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandlerPackage extends ChannelHandlerAdapter {
    
    private int count;
    private byte[] bytes;

    public TimeClientHandlerPackage() {
	String request = "请求服务系统时间" + System.getProperty("line.separator");
	bytes = request.getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	System.out.println("TimeClient请求TimeServer.....");
	ByteBuf requestBuf = null;
	for (int i = 0; i < 1000; i++) {
	    requestBuf = Unpooled.buffer(bytes.length);
	    requestBuf.writeBytes(bytes);
	    ctx.writeAndFlush(requestBuf);
	}
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	System.out.println("TimeServer返回消息");
        /*ByteBuf buf = (ByteBuf) msg;
        byte[] byteArr = new byte[buf.readableBytes()];
        buf.readBytes(byteArr);
        String response = new String(byteArr,"UTF-8");*/
	String response = (String) msg;
	System.out.println("现在是:" + response + ";this is count " + ++count);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	System.out.println("处理异常...");
	System.out.println(cause.getMessage());
	ctx.close();
    }
}
