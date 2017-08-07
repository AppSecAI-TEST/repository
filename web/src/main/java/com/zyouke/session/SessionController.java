package com.zyouke.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/session")
public class SessionController {

    @RequestMapping(value = "/setSession.do",produces = "text/plain;charset=utf-8")
    public void setSession(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String value = request.getParameter("value");
        String sessionId = request.getSession().getId();
	System.out.println("------------" + value + "sessionId " + sessionId);
        request.getSession().setAttribute(name, value);
    }

    @RequestMapping(value = "/getSession.do")
    public void getInterestPro(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        System.out.println("------" + request.getSession().getAttribute(name));
    }

    @RequestMapping(value = "/removeSession.do")
    public void removeSession(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        request.getSession().removeAttribute(name);
    }
}
