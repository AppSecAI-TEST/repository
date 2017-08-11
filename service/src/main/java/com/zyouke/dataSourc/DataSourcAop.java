package com.zyouke.dataSourc;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

import com.zyouke.bean.Area;

public class DataSourcAop {


    public void before(JoinPoint point) {
	Signature signature = point.getSignature();
	Area object = (Area) point.getArgs()[0];
	System.out.println(object.getCode());
    }
    
    public void after(){
	System.out.println("------------------");
    }
}
