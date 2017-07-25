package com.zyouke.dataSourc;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import com.zyouke.bean.Area;

public class DataSourcAop {


    // 在数据插入之前,判断数据插入的库
    public void before(JoinPoint point) {
	Signature signature = point.getSignature();
	Area object = (Area) point.getArgs()[0];
	System.out.println(object.getCode());
    }
    
    public void after(){
	System.out.println("------------------");
    }
}
