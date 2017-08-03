package com.zyouke.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {

    private Selector selector;
    private SocketChannel SC;
    private boolean boo = true;

    public TimeClientHandler() {
	try {
	    selector = Selector.open();
	    SC = SocketChannel.open();
	    SC.configureBlocking(false);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public void run() {
	try {
	    doConnect();
	} catch (Exception e1) {
	    e1.printStackTrace();
	    System.exit(1);
	}
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
			System.exit(1);
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
		System.exit(1);
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
	    SocketChannel sc = (SocketChannel) sKey.channel();
	    if (sKey.isConnectable()) {
		if (sc.finishConnect()) {
		    sc.register(selector, SelectionKey.OP_READ);
		    doWrite(sc);
		} else {
		    System.exit(1);
		}
	    }
	    if (sKey.isReadable()) {
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		int readBytes = sc.read(readBuffer);
		if (readBytes > 0) {
		    readBuffer.flip();
		    byte[] byteArray = new byte[readBuffer.remaining()];
		    readBuffer.get(byteArray);
		    String body = new String(byteArray, "UTF-8");
		    System.out.println("客户端获取的信息:" + body);
		    doWrite(sc);
		} else if (readBytes < 0) {
		    sKey.cancel();
		    sc.close();
		}
	    }
	}
    }

    private void doWrite(SocketChannel sc) throws Exception {
	String response = "请求系统时间.....";
	byte[] bytes = response.getBytes();
	ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
	writeBuffer.put(bytes);
	writeBuffer.flip();
	sc.write(writeBuffer);
	if (!writeBuffer.hasRemaining()) {
	    System.out.println("请求两秒后成功");
	}
    }

    private void doConnect() throws Exception {
	try {
	    if (SC.connect(new InetSocketAddress("127.0.0.1", 8080))) {
		SC.register(selector, SelectionKey.OP_READ);
		doWrite(SC);
	    }else {
		SC.register(selector,SelectionKey.OP_CONNECT);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
