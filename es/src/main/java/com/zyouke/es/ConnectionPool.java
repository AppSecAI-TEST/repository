package com.zyouke.es;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

// es ���ӳ�
public class ConnectionPool {

    private int initSize;
    // ���ӳ�
    private LinkedBlockingQueue<TransportClient> connectionPool = new LinkedBlockingQueue<TransportClient>();;
    public ConnectionPool(int initSize) {
	this.initSize = initSize;
	initConnectionPool();
    }
    
    private synchronized void initConnectionPool(){
	for (int i = 0; i < initSize; i++) {
	    TransportClient client = getClient();
	    if (client != null) {
		connectionPool.add(client);
	    }
	}
	    
    }
    
    public synchronized TransportClient getTransportClient(){
	if (connectionPool.size() <= 0){
	    long start = System.currentTimeMillis();
	    while (true) {
		try {
		    if(connectionPool.size() > 0){
			break;
		    }
		    Thread.sleep(3000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    System.out.println("�ȴ���......��ʱ" + (System.currentTimeMillis() - start) + "ms");
	}
	TransportClient client = connectionPool.poll();
	System.out.println("���ӳ���ʣ��������" + connectionPool.size());
	return client;
    }
    
    public void close(TransportClient client){
	connectionPool.add(client);
	client.close();
	System.out.println("�رպ����ӳ���ʣ��������" + connectionPool.size());
    }
    
    private TransportClient getClient() {
	try {
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("cluster.name", "zyouke_es");
	    Settings.Builder settings = Settings.builder().put(map);
	    TransportClient client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("103.82.53.221"), Integer.parseInt("9300")));
	    return client;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	 return null;
    }
    
    
    
    
}
