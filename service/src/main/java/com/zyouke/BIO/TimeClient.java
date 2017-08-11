package com.zyouke.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TimeClient {

    public static void main(String[] args) throws Exception {
	ExecutorService executorService = Executors.newFixedThreadPool(500);
	for (int i = 0; i < 10000; i++) {
	    executorService.execute(new Runnable() {
		public void run() {
		    try {
			// 1.创建Socket
			Socket socket = new Socket();
			// 2.连接服务器
			socket.connect(new InetSocketAddress("127.0.0.1", 8080));
			// a.发送数据到服务器
			OutputStream os = socket.getOutputStream();
			// 获得输出流
			PrintWriter pw = new PrintWriter(os);
			pw.write("请求系统时间");
			// 将数据发送
			pw.flush();
			// 告知服务器已经到了流的结尾
			socket.shutdownOutput();

			// b.获得服务器的回应
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
			// 打印服务器回应数据
			System.out.println(sb.toString());

			// 关闭流
			br.close();
			pw.close();
			socket.close();
		    } catch (IOException e) {
			e.printStackTrace();
		    }

		}
	    });
	}
	executorService.shutdown();
	while (!executorService.isTerminated());
    }
}
