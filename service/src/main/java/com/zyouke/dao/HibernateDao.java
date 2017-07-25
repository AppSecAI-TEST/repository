package com.zyouke.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.zyouke.bean.Area;

// ≤‚ ‘ π”√
public class HibernateDao {

    private static SessionFactory sessionFactory = null;
    private static SessionFactory sessionFactory3307 = null;
    static {
	sessionFactory = new Configuration().configure().buildSessionFactory();
	sessionFactory3307 = new Configuration().configure("/hibernate.cfg_3307.xml").buildSessionFactory();
    }

    public void add(Area area) {
	
	System.out.println(area);
	/*
	Session session = sessionFactory.openSession();
	Transaction tx = session.beginTransaction();
	session.save(area);
	tx.commit();
	session.close();
    */}

    public void addAll(List<Area> areas) {
	Session session = sessionFactory.openSession();
	Session session3307 = sessionFactory3307.openSession();
	Transaction tx = session.beginTransaction();
	Transaction tx3307 = session3307.beginTransaction();
	for (Area area : areas) {
	    if(area.getId() < 350000){
		session3307.save(area);
	    }else{
		session.save(area);
	    }
	}
	session.flush();
	session.clear();
	tx.commit();
	session.close();
	session3307.flush();
	session3307.clear();
	tx3307.commit();
	session3307.close();
    }

     public synchronized  void addAllSql(List<Area> areas) {
	System.out.println("----------->" + areas.get(0).getCode());
	String sqlInsert = "INSERT INTO t_area (CODE,VALUE) values ";
	for (int i = 0; i < areas.size(); i++) {
	    Area area = areas.get(i);
	    if (i == areas.size() - 1) {
		sqlInsert = sqlInsert + "('" + area.getCode() + "'"+",'" + area.getValue() + "');";
	    } else {
		sqlInsert = sqlInsert + "('" + area.getCode() + "'"+",'" + area.getValue() + "'),";
	    }
	}
	try {
	    FileWriter fileWritter = new FileWriter("E:/work_doc/demo_file/area_file/t_area.txt", true);
	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    bufferWritter.write(sqlInsert + "\r\n");
	    bufferWritter.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public List<Area> queryList() {
	Session session = sessionFactory.openSession();
	Transaction tx = session.beginTransaction();
	String sqlInsert = "select * from t_area where PARENT is null LIMIT 0,5300";
	Query query = session.createSQLQuery(sqlInsert).addEntity(Area.class);
	List<Area> list = query.list();
	tx.commit();
	session.close();
	return list;
    }

    public List<Area> queryList(Map<String, String> map) {
	Session session = sessionFactory.openSession();
	Transaction tx = session.beginTransaction();
	String sqlSelect = "select * from t_area where ";
	for (String key : map.keySet()) {
	    sqlSelect = sqlSelect + key + "=" + map.get(key);
	}
	Query query = session.createSQLQuery(sqlSelect).addEntity(Area.class);
	List<Area> list = query.list();
	tx.commit();
	session.close();
	return list;
    }

    public List<Area> queryListByLimit(int start) {
	Session session = sessionFactory.openSession();
	Transaction tx = session.beginTransaction();
	String sqlSelect = "select * from t_area  LIMIT "+start+",10000";
	Query query = session.createSQLQuery(sqlSelect).addEntity(Area.class);
	List<Area> list = query.list();
	tx.commit();
	session.close();
	return list;
    }

    public synchronized void updateSql(List<Area> areas) {
	try {
	    FileWriter fileWritter = new FileWriter("E:/work_doc/demo_file/area_file/update.txt", true);
	    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    for (Area area : areas) {
		String sqlInsert = "UPDATE t_area SET PARENT = " + area.getParent() + ",LEVEL = " + area.getLevel() + ",FULL_NAME = '" + area.getFullName() + "' WHERE ID = " + area.getId();
		System.out.println("-------->" + area.getId());
		bufferWritter.write(sqlInsert + ";\r\n");
	    }
	    bufferWritter.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
