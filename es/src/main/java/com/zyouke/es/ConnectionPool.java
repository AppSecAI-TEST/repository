package com.zyouke.es;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.zyouke.bean.Node;

// es 连接池
public class ConnectionPool {

    private int initSize;
    // 连接池
    private LinkedBlockingQueue<TransportClient> connectionPoolNode1 = new LinkedBlockingQueue<TransportClient>();
    private LinkedBlockingQueue<TransportClient> connectionPoolNode2 = new LinkedBlockingQueue<TransportClient>();
    public ConnectionPool(int initSize) {
	this.initSize = initSize;
	initConnectionPool();
    }
    
    private synchronized void initConnectionPool(){
	for (int i = 0; i < initSize; i++) {
	    TransportClient clientNode1 = getClient("node1");
	    TransportClient clientNode2 = getClient("node2");
	    if (clientNode1 != null) {
		System.out.println("为连接池connectionPoolNode1添加连接");
		connectionPoolNode1.add(clientNode1);
	    }
	    if (clientNode2 != null) {
		System.out.println("为连接池connectionPoolNode2添加连接");
		connectionPoolNode2.add(clientNode2);
	    }
	}
	    
    }
    
    public synchronized int getPollSize(String node) {
	if (node.equals(Node.NODE1.getValue())) {
	    return connectionPoolNode1.size();
	}
	if (node.equals(Node.NODE2.getValue())) {
	    return connectionPoolNode2.size();
	}
	return 0;
    }
    
    public synchronized Map<String, Object> getTransportClient(String node) {
	Map<String, Object> map = new HashMap<String, Object>();
	if (StringUtils.isNotBlank(node)) {
	    if (StringUtils.equals(node, Node.NODE1.getValue())) {
		if (connectionPoolNode1.size() <= 0) {
		    long start = System.currentTimeMillis();
		    while (true) {
			try {
			    if (connectionPoolNode1.size() > 0) {
				break;
			    }
			    Thread.sleep(3000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		    }
		}
		TransportClient client = connectionPoolNode1.poll();
		map.put("client", client);
		map.put("node", node);
		return map;
	    } else if (StringUtils.equals(node, Node.NODE2.getValue())) {

		if (connectionPoolNode2.size() <= 0) {
		    while (true) {
			try {
			    if (connectionPoolNode2.size() > 0) {
				break;
			    }
			    Thread.sleep(3000);
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		    }
		}
		TransportClient client = connectionPoolNode2.poll();
		map.put("client", client);
		map.put("node", node);
		return map;
	    }
	} else {
	    while (true) {
		TransportClient client = connectionPoolNode1.poll();
		if (client != null) {
		    map.put("client", client);
		    map.put("node", Node.NODE1.getValue());
		    break;
		}
		client = connectionPoolNode2.poll();
		if (client != null) {
		    map.put("client", client);
		    map.put("node", Node.NODE2.getValue());
		    break;
		}
	    }
	}
	System.out.println(map.toString());
	return map;
    }
    
    public void close(TransportClient client, String node) {
	if (node.equals(Node.NODE1.getValue())) {
	    connectionPoolNode1.add(client);
	    System.out.println("关闭后连接池connectionPoolNode1中剩余连接数" + connectionPoolNode1.size());
	}
	if (node.equals(Node.NODE2.getValue())) {
	    connectionPoolNode2.add(client);
	    System.out.println("关闭后连接池connectionPoolNode2中剩余连接数" + connectionPoolNode2.size());
	}
    }
    
    private TransportClient getClient(String node) {
	try {
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("cluster.name", "zyouke_es");
	    Settings.Builder settings = Settings.builder().put(map);
	    TransportClient client = TransportClient.builder().settings(settings).build();
	    if(node.equals(Node.NODE1.getValue())){
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("103.82.53.221"), Integer.parseInt("9300")));
	    }else if(node.equals(Node.NODE2.getValue())){
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("103.82.53.221"), Integer.parseInt("9301")));
	    }
	    return client;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	 return null;
    }
    
    
    
    
}
