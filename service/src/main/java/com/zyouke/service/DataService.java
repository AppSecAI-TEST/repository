package com.zyouke.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zyouke.bean.Area;
import com.zyouke.dao.HibernateDao;

public class DataService {

    public static List<Area> list = Collections.synchronizedList(new ArrayList<Area>());

    // 解析html
    public static void parsingHtml(String url, String selector) {
	List<String> urls = new ArrayList<String>();
	ExecutorService executorService = Executors.newFixedThreadPool(20);
	try {
	    Connection connection = Jsoup.connect(url);
	    connection.timeout(5000);
	    Document document = connection.get();
	    Elements elements = document.select(selector);
	    for (Element element : elements) {
		if (selector.equals("table .provincetable a")) { // 省份
		    String href = element.attr("href");
		    String value = element.text();
		    String code = href.replace(".html", "").trim() + "0000000000";
		    value = value.replace("<br/>", "").trim();
		    Area area = new Area();
		    area.setCode(code);
		    area.setValue(value);
		    list.add(area);
		    int index = StringUtils.lastIndexOf(url, "/");
		    String newUrl = url.substring(0, index + 1);
		    urls.add(newUrl + href);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    try {
		FileWriter fileWritter = new FileWriter("E:/work_doc/demo_file/area_file/error.txt", true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(url + "\r\n");
		bufferWritter.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
	for (String urlStr : urls) {
	    final String urlTemp = urlStr;
	    executorService.execute(new Runnable() {
		public void run() {
		    parsingHtml(urlTemp, "tr .citytr", false);
		}
	    });
	}
	executorService.shutdown();
	while (!executorService.isTerminated());
    }
    
    // 解析html
    public static void parsingHtml(String url, String selector, boolean boo) {
	try {
	    Connection connection = Jsoup.connect(url);
	    connection.timeout(5000);
	    Document document = connection.get();
	    Elements elements = document.select(selector);
	    
	    for (Element element : elements) {
		if (selector.equals("table .provincetable a")) { // 省份
		    String href = element.attr("href");
		    String value = element.text();
		    String code = href.replace(".html", "").trim() + "0000000000";
		    value = value.replace("<br/>", "").trim();
		    Area area = new Area();
		    area.setCode(code);
		    area.setValue(value);
		    add(area);
		    int index = StringUtils.lastIndexOf(url, "/");
		    String newUrl = url.substring(0, index + 1);
		    parsingHtml(newUrl + href, "tr .citytr", boo);
		} else {// 其他级别
		    Elements elementsTag = element.getElementsByTag("a");
		    if (elementsTag.size() == 0) {// 最后一级没有标签
			elementsTag = element.getElementsByTag("td");
			if (elementsTag.size() == 2) {
			    String code = elementsTag.get(0).text();
			    String text = elementsTag.get(1).text();
			    Area area = new Area();
			    area.setCode(code);
			    area.setValue(text);
			    add(area);
			} else {
			    String text = elementsTag.get(2).text();
			    String code = elementsTag.get(0).text();
			    Area area = new Area();
			    area.setCode(code);
			    area.setValue(text);
			    add(area);
			}
		    } else {
			String href = elementsTag.get(0).attr("href");
			String value = elementsTag.get(1).text();
			String code = elementsTag.get(0).text();
			Area area = new Area();
			area.setCode(code);
			area.setValue(value);
			add(area);
			int index = StringUtils.lastIndexOf(url, "/");
			String newUrl = url.substring(0, index + 1);
			if (selector.equals("tr .citytr")) {
			    parsingHtml(newUrl + href, "tr .countytr", boo);
			} else if (selector.equals("tr .countytr")) {
			    parsingHtml(newUrl + href, "tr .towntr", boo);
			} else if (selector.equals("tr .towntr")) {
			    parsingHtml(newUrl + href, "tr .villagetr", boo);
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    try {
		FileWriter fileWritter = new FileWriter("E:/work_doc/demo_file/area_file/error.txt", true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		if (boo) {
		    bufferWritter.write(url + "_error\r\n");
		} else {
		    bufferWritter.write(url + "\r\n");
		}
		bufferWritter.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
    }

    public synchronized static void add(Area area) {
	list.add(area);
	if(list.size() > 1000){
	    HibernateDao dao = new HibernateDao();
	    dao.addAllSql(list);
	    list.clear();
	}
    }
    
    
    
    
    
    
    public static void parsingHtmlByError() {
	try {
	    File file = new File("E:/work_doc/demo_file/area_file/error.txt");
	    InputStreamReader read = new InputStreamReader(new FileInputStream(file));// 考虑到编码格式
	    BufferedReader bufferedReader = new BufferedReader(read);
	    String lineTxt = null;
	    while ((lineTxt = bufferedReader.readLine()) != null) {
		if (!lineTxt.contains("_error")) {
		    int index = StringUtils.lastIndexOf(lineTxt, "/");
		    int newLineTxtLength = lineTxt.substring(index + 1, lineTxt.length()).replace(".html", "").trim().length();
		    if (newLineTxtLength == 9) {
			parsingHtml(lineTxt, "tr .villagetr", true);
		    } else if (newLineTxtLength == 6) {
			parsingHtml(lineTxt, "tr .towntr", true);
		    } else if (newLineTxtLength == 4) {
			parsingHtml(lineTxt, "tr .countytr", true);
		    }
		}
	    }
	    read.close();
	    if (list.size() > 0) {
		HibernateDao dao = new HibernateDao();
		dao.addAllSql(list);
		list.clear();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void updateSql(int start,List<Area> queryList) {
	HibernateDao dao = new HibernateDao();
	final List<Area> queryListTwo = dao.queryListByLimit(start);
	ExecutorService executorService = Executors.newFixedThreadPool(20);
	for (final Area area : queryList) {
	    executorService.execute(new Runnable() {

		public void run() {
		    String prefix = area.getCode().substring(0, 9);
		    if (queryListTwo != null && queryListTwo.size() > 0) {
			ArrayList<Area> areas = new ArrayList<Area>();
			for (Area areaTwo : queryListTwo) {
			    if (areaTwo.getParent() == null && areaTwo.getCode().startsWith(prefix)) {
				areaTwo.setParent(area.getCode());
				areaTwo.setLevel(area.getLevel() + 1);
				areaTwo.setFullName(area.getFullName() + areaTwo.getValue());
				areas.add(areaTwo);
			    }
			}
			HibernateDao dao = new HibernateDao();
			dao.updateSql(areas);
		    }
		}
	    });

	}
	executorService.shutdown();
	while (!executorService.isTerminated());
    }

    public static void mysqlDataToText() {
	ExecutorService executorService = Executors.newFixedThreadPool(25);
	for (int i = 0; i < 80; i++) {
	    int startTemp = 0;
	    if(i > 0){
		startTemp = (i * 10000) + 1;
	    }
	    final int start = startTemp;
	    executorService.execute(new Runnable() {
		public void run() {
		    HibernateDao dao = new HibernateDao();
		    List<Area> queryList = dao.queryListByLimit(start);
		    writer(queryList);
		}
	    });
	}

	executorService.shutdown();
	while (!executorService.isTerminated());
    }
    
    public synchronized static void writer(List<Area> queryList){
	System.out.println("开始写...............");
	try {
	    BufferedWriter buf = new BufferedWriter(new FileWriter("E:/work_doc/demo_file/area_file/mysqlDataToText.txt",true));
	    for (Area area : queryList) {
	        buf.write(area.toString()+"\r\n");
	    }
	    buf.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
    
    
}
