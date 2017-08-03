package com.zyouke.BIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeServer {

    public static void main(String[] args) {

	// 1.创建serverSocket
	ServerSocket serverSocket = null;
	ExecutorService executorService = Executors.newFixedThreadPool(50);
	try {
	    serverSocket = new ServerSocket();

	    // 2.设置端口号
	    serverSocket.bind(new InetSocketAddress(8080));

	    System.out.println("监听端口为8080的服务器");

	    // 3.获取报文
	    while (true) {
	        Socket socket = serverSocket.accept();
	        executorService.execute(new TimeServerHandler(socket));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    try {
		serverSocket.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}
