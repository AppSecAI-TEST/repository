package com.data.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.zyouke.bean.Area;
import com.zyouke.dao.HibernateDao;
import com.zyouke.service.DataService;

public class DataTest {

    @Test
    public void test1() {
	DataService.parsingHtml("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/index.html", "table .provincetable a",false);
	if (DataService.list.size() > 0) {
	    HibernateDao.addAllSql(DataService.list);
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
	List<Area> queryList = HibernateDao.queryList(map);
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
	List<Area> queryList = HibernateDao.queryList(map);
	List<Area> list = HibernateDao.queryList();
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
	HibernateDao.updateSql(areas);
    }
    
    
    @Test
    public void test5() {
	DataService.mysqlDataToText();
    }
    
}
