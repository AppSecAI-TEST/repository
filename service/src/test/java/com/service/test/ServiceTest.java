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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
    public void test1_1(){
	for (int i = 0; i < 100000; i++) {
	    System.out.println("aaaaaaaaa");
	}
    }  
      
    /** 
     * 测试添加 
     */
    @Test
    public void test2_2(){
	Area area = new Area();
	area.setId(11111l);
	area.setCode("100000");
	area.setValue("aaaaa");
	area.setParent("0");
	area.setLevel(1);
	area.setFullName("aaaaabbbbb");
	hibernateDao.add(area);
    }  
      
    /** 
     * 测试使用对象包含taobaoId属性的进行查找 
     * 使用这种方式可以根据taobaoId分库查找 
     */  
    public void test3_3(){
	
    }  

    @Test
    public void test1() {
	DataService.parsingHtml("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/index.html", "table .provincetable a",false);
	if (DataService.list.size() > 0) {
	    hibernateDao.addAllSql(DataService.list);
	    DataService.list.clear();
	}
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

	ExecutorService executorService = Executors.newFixedThreadPool(30);
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
		if (list.size() > 1000) {
		    final List<Area> listTemp = new ArrayList<Area>();
		    listTemp.addAll(list);
		    list.clear();
		    executorService.execute(new Runnable() {
			public void run() {
			    hibernateDao.addAll(listTemp);
			}
		    });
		}
	    }
	    executorService.shutdown();
	    while (!executorService.isTerminated());
	    if (list.size() > 0){
		hibernateDao.addAll(list);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    @Test
    public void test7() {
	BeanFactory factory=new ClassPathXmlApplicationContext("spring_aop.xml");
	HibernateDao hibernateDao =(HibernateDao) factory.getBean("hibernateDao");
	Area area = new Area();
	area.setId(11111l);
	area.setCode("100000");
	area.setValue("aaaaa");
	area.setParent("0");
	area.setLevel(1);
	area.setFullName("aaaaabbbbb");
	hibernateDao.add(area);
    }
}
