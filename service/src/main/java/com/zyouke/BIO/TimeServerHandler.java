package com.zyouke.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeServerHandler implements Runnable {

    private Socket socket;
    public TimeServerHandler(Socket socket) {
	this.socket = socket;
    }
    public void run() {
	try {
	    // a.获得客户端的意图 request
	    InputStream is = socket.getInputStream();
	    // 字符流和字节流的转换
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);

	    // 存储读取的数据
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    // 读取数据
	    while ((line = br.readLine()) != null) {
		// 将数据存储在StringBuilder中
		sb.append(line);
	    }
	    System.out.println("服务器收到的请求：" + sb.toString());
	    // b.返回客户端数据
	    OutputStream os = socket.getOutputStream();
	    // 获得输出流
	    PrintWriter pw = new PrintWriter(os);
	    long millis = System.currentTimeMillis();
	    pw.write(String.valueOf(millis));
	    // 将数据发送
	    pw.flush();
	    // 关闭流
	    br.close();
	    pw.close();
	    socket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
