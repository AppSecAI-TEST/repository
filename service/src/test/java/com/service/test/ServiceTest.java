package com.service.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zyouke.bean.Area;
import com.zyouke.dao.HibernateDao;
import com.zyouke.service.DataService;

@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration("classpath:applicationContext.xml") 
public class ServiceTest {

    @Autowired
    private HibernateDao hibernateDao;
    @Test
    public void test1() {
	System.out.println(System.getProperty("line.separator").length());
    }
    
    @Test
    public void test2() {
	DataService.parsingHtmlByError();
    }
    
    @Test
    public void test3() {
	Map<String, String> map = new HashMap<String, String>();
	map.put("LEVEL", "4");
	List<Area> queryList = hibernateDao.queryList(map);
	for (int i = 0; i < 8; i++) {
	    if(i == 0){
		DataService.updateSql(i,queryList);
	    }else{
		DataService.updateSql((i * 100000) + 1,queryList);
	    }
	}
    }
    
    @Test
    public void test4() {
	Map<String, String> map = new HashMap<String, String>();
	map.put("LEVEL", "4");
	List<Area> queryList = hibernateDao.queryList(map);
	List<Area> list = hibernateDao.queryList();
	ArrayList<Area> areas = new ArrayList<Area>();
	for (Area area1 : list) {
	    for (Area area2 : queryList) {
		if(area1.getCode().startsWith(area2.getCode().substring(0, 9))){
		    area1.setParent(area2.getCode());
		    area1.setLevel(area2.getLevel() + 1);
		    area1.setFullName(area2.getFullName() + area1.getValue());
		    areas.add(area1);
		}
	    }
	}
	hibernateDao.updateSql(areas);
    }
    
    
    @Test
    public void test5() {
	DataService.mysqlDataToText();
    }
    

    @Test
    public void test6() {
	ExecutorService executorService = Executors.newFixedThreadPool(50);
	try {
	    BufferedReader reader = new BufferedReader(new FileReader("E:/work_doc/demo_file/area_file/mysqlDataToText.txt"));
	    String line = null;
	    List<Area> list = new ArrayList<Area>();
	    while ((line = reader.readLine()) != null) {
		Area area = new Area();
		String[] lineArr = line.split(",");
		area.setCode(lineArr[1].split("=")[1]);
		area.setValue(lineArr[2].split("=")[1]);
		area.setParent(lineArr[3].split("=")[1]);
		area.setLevel(Integer.valueOf(lineArr[4].split("=")[1]));
		area.setFullName(lineArr[5].split("=")[1]);
		list.add(area);
		if (list.size() > 200) {
		    final List<Area> listTemp = new ArrayList<Area>();
		    listTemp.addAll(list);
		    list.clear();
		    executorService.execute(new Runnable() {
			public void run() {
			    hibernateDao.addAllSql(listTemp);
			}
		    });
		}
	    }
	    executorService.shutdown();
	    while (!executorService.isTerminated());
	    if (list.size() > 0){
		hibernateDao.addAllSql(list);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void test7() {
	long start = System.currentTimeMillis();
	try {
	    BufferedReader reader = new BufferedReader(new FileReader("E:/work_doc/demo_file/area_file/mysqlDataToText.txt"));
	    String line = null;
	    List<Area> list = new ArrayList<Area>();
	    while ((line = reader.readLine()) != null) {
		System.out.println("******" + line);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("耗时" + (System.currentTimeMillis() - start) + "ms");
    }
}
