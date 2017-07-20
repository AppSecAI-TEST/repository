package com.zyouke.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.zyouke.bean.Area;
import com.zyouke.es.ConnectionPool;
import com.zyouke.es.Es;

public class EsTest {

    @Test
    public void test1(){
	ExecutorService executorService = Executors.newFixedThreadPool(25);
	final ConnectionPool pool = new ConnectionPool(10);
	Es.deleteIndex(pool);
	try {
	    BufferedReader reader = new BufferedReader(new FileReader("E:/work_doc/demo_file/area_file/mysqlDataToText.txt"));
	    String line = null;
	    List<Area> list = new ArrayList<Area>();
	    while ((line = reader.readLine()) != null) {
		Area area = new Area();
		String[] lineArr = line.split(",");
		area.setId(Long.valueOf(lineArr[0].split("=")[1]));
		area.setCode(lineArr[1].split("=")[1]);
		area.setValue(lineArr[2].split("=")[1]);
		area.setParent(lineArr[3].split("=")[1]);
		area.setLevel(Integer.valueOf(lineArr[4].split("=")[1]));
		area.setFullName(lineArr[5].split("=")[1]);
		list.add(area);
		if (list.size() > 1000) {
		    final List<Area> listTemp = new ArrayList<Area>();
		    listTemp.addAll(list);
		    list.clear();
		    executorService.execute(new Runnable() {
			public void run() {
			    Es.creatIndexByEs(listTemp,pool);
			}
		    });
		}
	    }
	    executorService.shutdown();
	    while (!executorService.isTerminated());
	    if (list.size() > 0){
		Es.creatIndexByEs(list,pool);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void test2(){
	ConnectionPool pool = new ConnectionPool(1);
	Es.search(pool);
    }
    
    @Test
    public void test3(){
	ConnectionPool pool = new ConnectionPool(1);
	Es.search2(pool,"±±¾©");
    }
}
