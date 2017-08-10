package com.zyouke.netty.handler;

import org.apache.commons.lang.time.DateFormatUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {
    
    private  ByteBuf requestBuf;

    public TimeClientHandler() {
	String request = "请求服务系统时间";
	requestBuf = Unpooled.buffer(request.getBytes().length);
	requestBuf.writeBytes(request.getBytes());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
	System.out.println("TimeClient请求TimeServer.....");
        ctx.writeAndFlush(requestBuf);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
	System.out.println("TimeServer返回消息");
        ByteBuf buf = (ByteBuf) msg;
        byte[] byteArr = new byte[buf.readableBytes()];
        buf.readBytes(byteArr);
        String response = new String(byteArr,"UTF-8");
	System.out.println("现在是:" + DateFormatUtils.format(Long.valueOf(response),"yyyy-MM-dd HH:mm:ss sss"));
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	System.out.println("处理异常...");
	ctx.close();
    }
}
