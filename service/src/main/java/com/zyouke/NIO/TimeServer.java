package com.zyouke.NIO;


public class TimeServer {
    public static void main(String[] args) {
	new Thread(new TimeServerHandler(8080)).start();
    }
}
