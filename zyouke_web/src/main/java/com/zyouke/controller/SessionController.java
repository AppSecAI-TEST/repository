package com.zyouke.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/session")
public class SessionController {
    @RequestMapping(value = "/setSession.do")
    public void setSession(HttpServletRequest request, HttpServletResponse response) {
        String value = request.getParameter("value");
        request.getSession().setAttribute("test", value);
        System.out.println("sessionid=======" + request.getSession().getId());
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
