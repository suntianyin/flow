package com.apabi.flow.ip.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author pipi
 * @Date 2018/9/18 17:50
 **/
public class TelnetUtil {
    public static boolean connect(String ip, Integer port) {
        boolean flag = false;
        Socket socket = new Socket();
        try {
            // 设置连接超时时间
            socket.connect(new InetSocketAddress(ip, port), 10000);
            // 设置读操作超时时间
            socket.setSoTimeout(10000);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        boolean flag = TelnetUtil.connect("183.129.244.17", 21213);
        System.out.println(flag);
    }
}
