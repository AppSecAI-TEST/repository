package com.zyouke.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class TimeServerHandler implements Runnable {

    private Selector selector;
    private ServerSocketChannel SSC;
    private boolean boo = true;

    public TimeServerHandler(int port) {
	try {
	    selector = Selector.open();
	    SSC = ServerSocketChannel.open();
	    SSC.configureBlocking(false);
	    SSC.socket().bind(new InetSocketAddress(port), 1024);
	    SSC.register(selector, SelectionKey.OP_ACCEPT);
	    System.out.println("端口" + port + "已启动");
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void run() {
	while (boo) {
	    try {
		selector.select(1000);
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> it = selectedKeys.iterator();
		SelectionKey sKey = null;
		while (it.hasNext()) {
		    sKey = it.next();
		    it.remove();
		    try {
			handleInput(sKey);
		    } catch (Exception e) {
			if (sKey != null) {
			    sKey.cancel();
			    if (sKey.channel() != null) {
				sKey.channel().close();
			    }
			}
			e.printStackTrace();
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	if (selector != null) {
	    try {
		selector.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private void handleInput(SelectionKey sKey) throws Exception {

	if (sKey.isValid()) {
	    if (sKey.isAcceptable()) {
		ServerSocketChannel ssc = (ServerSocketChannel) sKey.channel();
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
	    }
	    if (sKey.isReadable()) {
		SocketChannel sc = (SocketChannel) sKey.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		int readBytes = sc.read(readBuffer);
		if (readBytes > 0) {
		    readBuffer.flip();
		    byte[] byteArray = new byte[readBuffer.remaining()];
		    readBuffer.get(byteArray);
		    String body = new String(byteArray, "UTF-8");
		    System.out.println("接受服务端请求:" + body);
		    doWrite(sc, String.valueOf(System.currentTimeMillis()));
		}else if(readBytes < 0){
		    sKey.cancel();
		    sc.close();
		}
	    }
	}

    }

    private void doWrite(SocketChannel sc, String response) {
	if (StringUtils.isNotBlank(response)) {
	    byte[] bytes = response.getBytes();
	    ByteBuffer writeBuffer = ByteBuffer.allocate(response.length());
	    writeBuffer.put(bytes);
	    writeBuffer.flip();
	    try {
		sc.write(writeBuffer);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

}
