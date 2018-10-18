package com.apabi.flow.config;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author guanpp
 * @date 2018/7/31 17:53
 * @description
 */
@WebServlet(
        urlPatterns = {"/druid/*"},
        initParams = {
                @WebInitParam(name = "loginUsername", value = "admin"),
                @WebInitParam(name = "loginPassword", value = "admin"),
                @WebInitParam(name = "resetEnable", value = "false")
//      @WebInitParam(name = "allow", value = "127.0.0.1")
        }
)
public class DruidStatViewServlet extends StatViewServlet {
}
