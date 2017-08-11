package com.zyouke.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import com.zyouke.netty.handler.HttpFileServerHandler;
import com.zyouke.netty.util.NettyUtils;


public class TimeServer {
    
    public static void main(final String[] args) {
	NettyUtils.bind(new ChannelInitializer<SocketChannel>() {
	    @Override
	    protected void initChannel(SocketChannel sc) throws Exception {
		sc.pipeline().addLast("http_decoder",new HttpRequestDecoder());
		sc.pipeline().addLast("http_aggregator",new HttpObjectAggregator(65536));
		sc.pipeline().addLast("http_encoder",new HttpResponseEncoder());
		sc.pipeline().addLast("http_chunked",new ChunkedWriteHandler());
		sc.pipeline().addLast("http_file",new HttpFileServerHandler(args[0]));
	    }
	});
	
    }
}
