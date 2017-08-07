package com.zyouke.netty.handler;



import org.apache.commons.lang.time.DateFormatUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandlerPackage extends ChannelHandlerAdapter {
    
    private int count;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*ByteBuf buf = (ByteBuf) msg;
        byte[] byteArr = new byte[buf.readableBytes()];
        buf.readBytes(byteArr);
        String request = new String(byteArr,"UTF-8");*/
	String request = (String) msg;
        //request = request.substring(0,request.length() - System.getProperty("line.separator").length());
	System.out.println("客户端请求:" + request  + ";this is count: " + ++count);
	String formatDate = DateFormatUtils.format(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss sss") + System.getProperty("line.separator");
	ByteBuf  response = Unpooled.copiedBuffer(formatDate.getBytes());
	ctx.write(response);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	System.out.println(cause.getMessage());
	ctx.close();
    }
    
    
}
